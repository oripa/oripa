/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package oripa.domain.fold;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.fold.halfedge.OriEdge;
import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OriHalfedge;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.domain.fold.origeom.OriGeomUtil;
import oripa.domain.fold.origeom.OverlapRelation;
import oripa.domain.fold.stackcond.StackConditionOf3Faces;
import oripa.domain.fold.stackcond.StackConditionOf4Faces;
import oripa.domain.fold.subface.SubFace;
import oripa.domain.fold.subface.SubFacesFactory;
import oripa.geom.GeomUtil;
import oripa.util.StopWatch;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class LayerOrderEnumerator {
	private static final Logger logger = LoggerFactory.getLogger(LayerOrderEnumerator.class);

	private HashSet<StackConditionOf4Faces> condition4s;
	private List<SubFace> subFaces;

	/**
	 * [faceID1][faceID2]
	 */
	private List<Integer>[][] faceOverlappingIndexIntersections;

	/**
	 * Key: halfedge, value: set of indices which are under the halfedge.
	 */
	private Map<OriHalfedge, Set<Integer>> faceIndicesOnHalfEdge;

	private int callCount;
	private int penetrationTestCallCount;
	private int penetrationCount;

	private final SubFacesFactory subFacesFactory;

	public LayerOrderEnumerator(final SubFacesFactory subFacesFactory) {
		this.subFacesFactory = subFacesFactory;
	}

	public OverlapRelationList enumerate(final OrigamiModel origamiModel) {
		var faces = origamiModel.getFaces();
		var edges = origamiModel.getEdges();

		// construct the subfaces
		double paperSize = origamiModel.getPaperSize();
		subFaces = subFacesFactory.createSubFaces(faces, paperSize);
		logger.debug("subFaces.size() = " + subFaces.size());

		OverlapRelation overlapRelation = createOverlapRelation(faces, paperSize);

		// Set overlap relations based on valley/mountain folds information
		determineOverlapRelationByLineType(faces, overlapRelation);

		holdCondition3s(faces, paperSize, overlapRelation);

		condition4s = new HashSet<>();
		holdCondition4s(edges, overlapRelation);

		estimation(faces, overlapRelation);

		for (SubFace sub : subFaces) {
			sub.sortFaceOverlapOrder(faces, overlapRelation);
		}

		// heuristic: fewer answer stacks mean the search on the subface has
		// more possibility to be correct. Such confident search node should be
		// consumed at early stage.
		subFaces = subFaces.stream()
				.sorted(Comparator.comparing(SubFace::answerStackCount))
				.collect(Collectors.toList());

		var watch = new StopWatch(true);

		faceOverlappingIndexIntersections = createFaceOverlappingIndexIntersections(faces, paperSize);
		faceIndicesOnHalfEdge = createFaceIndicesOnHalfEdge(faces, paperSize);
		logger.debug("preprocessing time = {}[ms]", watch.getMilliSec());

		var overlapRelationList = new OverlapRelationList();

		watch.start();

		var changedFaceIDs = faces.stream().map(OriFace::getFaceID).collect(Collectors.toSet());
		callCount = 0;
		penetrationTestCallCount = 0;
		penetrationCount = 0;
		findAnswer(faces, overlapRelationList, 0, overlapRelation, changedFaceIDs);
		var time = watch.getMilliSec();

		logger.debug("#call = {}", callCount);
		logger.debug("#penetrationTest = {}", penetrationTestCallCount);
		logger.debug("#penetration = {}", penetrationCount);
		logger.debug("time = {}[ms]", time);

		return overlapRelationList;
	}

	private double eps(final double paperSize) {
		return paperSize * 0.00001;
	}

	@SuppressWarnings("unchecked")
	private List<Integer>[][] createFaceOverlappingIndexIntersections(final List<OriFace> faces,
			final double paperSize) {
		List<Set<Integer>> indices = IntStream.range(0, faces.size())
				.mapToObj(i -> new HashSet<Integer>())
				.collect(Collectors.toList());

		// prepare pair indices of overlapping faces.
		for (var face : faces) {
			for (var other : faces) {
				if (face.getFaceID() == other.getFaceID()) {
					continue;
				}
				if (OriGeomUtil.isFaceOverlap(face, other, eps(paperSize))) {
					indices.get(face.getFaceID()).add(other.getFaceID());
				}
			}
		}

		// extract overlapping-face indices shared by face pair.
		var indexIntersections = new List[faces.size()][faces.size()];
		for (var face : faces) {
			for (var other : faces) {
				var index_i = face.getFaceID();
				var index_j = other.getFaceID();

				if (index_i == index_j) {
					continue;
				}

				var overlappingFaces_i = indices.get(index_i);
				var overlappingFaces_j = indices.get(index_j);

				indexIntersections[index_i][index_j] = overlappingFaces_i.stream()
						.filter(index -> overlappingFaces_j.contains(index))
						.collect(Collectors.toList());
			}
		}

		return indexIntersections;
	}

	private Map<OriHalfedge, Set<Integer>> createFaceIndicesOnHalfEdge(
			final List<OriFace> faces, final double paperSize) {

		Map<OriHalfedge, Set<Integer>> indices = new HashMap<>();

		for (var face : faces) {
			for (var halfedge : face.halfedgeIterable()) {
				Set<Integer> indexSet = new HashSet<Integer>();
				indices.put(halfedge, indexSet);
			}
		}
		for (var face : faces) {
			for (var halfedge : face.halfedgeIterable()) {
				var indexSet = indices.get(halfedge);
				for (var other : faces) {
					if (other == face) {
						continue;
					}
					if (OriGeomUtil.isLineCrossFace4(other, halfedge, paperSize)) {
						indexSet.add(other.getFaceID());
					}
				}
			}
		}

		return indices;
	}

	private class IndexPair {
		private final int i;
		private final int j;

		public IndexPair(final int i, final int j) {
			this.i = i;
			this.j = j;
		}

		@Override
		public boolean equals(final Object obj) {
			if (!(obj instanceof IndexPair)) {
				return false;
			}
			var o = (IndexPair) obj;
			return i == o.i && j == o.j;
		}

		@Override
		public int hashCode() {
			return Objects.hash(i, j);
		}
	}

	/**
	 * Determines overlap relations which are left uncertain after using
	 * necessary conditions.
	 *
	 * @param faces
	 *            all faces of the origami model.
	 * @param overlapRelationList
	 *            an object to store the result
	 * @param subFaceIndex
	 *            the index of subface to be used
	 * @param overlapRelation
	 *            overlap relation matrix
	 * @param changedFaceIDs
	 *            IDs of faces whose overlap relation changed. should contain
	 *            all face IDs for the first call.
	 */
	private void findAnswer(
			final List<OriFace> faces,
			final OverlapRelationList overlapRelationList, final int subFaceIndex,
			final OverlapRelation overlapRelation,
			final Set<Integer> changedFaceIDs) {
		callCount++;

		var foldableOverlapRelations = overlapRelationList.getFoldableOverlapRelations();

		if (!changedFaceIDs.isEmpty()) {
			penetrationTestCallCount++;
			if (detectPenetrationBy3faces(faces, changedFaceIDs, overlapRelation)) {
				penetrationCount++;
				return;
			}
			if (detectPenetrationBy4faces(overlapRelation)) {
				penetrationCount++;
				return;
			}
		}

		if (subFaceIndex == subFaces.size()) {
			var answer = OverlapRelation.clone(overlapRelation);
			foldableOverlapRelations.add(answer);
			return;
		}

		SubFace sub = subFaces.get(subFaceIndex);

		if (sub.allFaceOrderDecided) {
			findAnswer(faces, overlapRelationList, subFaceIndex + 1, overlapRelation,
					new HashSet<>());
			return;
		}

		for (var answerStack : sub.answerStacks) {
			int size = answerStack.size();
			if (!isCorrectStackOrder(answerStack, overlapRelation)) {
				continue;
			}
			var changedIndexPairs = new ArrayList<IndexPair>();
			var nextChangedFaceIDs = new HashSet<Integer>();

			// determine overlap relations according to stack
			for (int i = 0; i < size; i++) {
				int index_i = answerStack.get(i).getFaceID();
				for (int j = i + 1; j < size; j++) {
					int index_j = answerStack.get(j).getFaceID();
					if (overlapRelation.isUndefined(index_i, index_j)) {
						overlapRelation.setUpper(index_i, index_j);

						changedIndexPairs.add(new IndexPair(index_i, index_j));
						nextChangedFaceIDs.add(index_i);
						nextChangedFaceIDs.add(index_j);
					}
				}
			}

			findAnswer(faces, overlapRelationList, subFaceIndex + 1,
					overlapRelation, nextChangedFaceIDs);

			// get back
			changedIndexPairs.forEach(pair -> {
				overlapRelation.setUndefined(pair.i, pair.j);
			});
		}
	}

	/**
	 * Detects penetration. For face_i and its neighbor face_j, face_k
	 * penetrates the sheet of paper if face_k is between face_i and face_j in
	 * the folded state and if the connection edge of face_i and face_j is on
	 * face_k.
	 *
	 * @param faces
	 *            all faces.
	 * @param changedFaceIDs
	 *            IDs of faces whose overlap relation changed.
	 * @param overlapRelation
	 *            overlap relation matrix.
	 * @return true if there is a face which penetrates the sheet of paper.
	 */
	private boolean detectPenetrationBy3faces(final List<OriFace> faces, final Set<Integer> changedFaceIDs,
			final OverlapRelation overlapRelation) {
		var checked = new HashSet<IndexPair>();

		for (var faceID : changedFaceIDs) {
			var face = faces.get(faceID);
//		for (var face : faces) {
			for (var he : face.halfedgeIterable()) {
				var pair = he.getPair();
				if (pair == null) {
					continue;
				}

				var index_i = he.getFace().getFaceID();
				var index_j = pair.getFace().getFaceID();

				if (checked.contains(new IndexPair(index_i, index_j))) {
					continue;
				}

				if (!overlapRelation.isLower(index_i, index_j) &&
						!overlapRelation.isUpper(index_i, index_j)) {
					checked.add(new IndexPair(index_i, index_j));
					checked.add(new IndexPair(index_j, index_i));
					continue;
				}

				var penetrates = faceOverlappingIndexIntersections[index_i][index_j].parallelStream()
						.anyMatch(k -> {
							var face_k = faces.get(k);
							var index_k = face_k.getFaceID();
							if (index_i == index_k || index_j == index_k) {
								return false;
							}
							if (!faceIndicesOnHalfEdge.get(he).contains(index_k)) {
								return false;
							}
							if (overlapRelation.isLower(index_i, index_j) &&
									overlapRelation.isLower(index_i, index_k) &&
									overlapRelation.isUpper(index_j, index_k)) {
								return true;
							} else if (overlapRelation.isUpper(index_i, index_j) &&
									overlapRelation.isUpper(index_i, index_k) &&
									overlapRelation.isLower(index_j, index_k)) {
								return true;
							}

							return false;
						});
				if (penetrates) {
					return true;
				}

				checked.add(new IndexPair(index_i, index_j));
				checked.add(new IndexPair(index_j, index_i));
			}
		}

		return false;
	}

	/**
	 * Tests all cases of 4-face layer ordering condition.
	 *
	 * @param overlapRelation
	 *            overlap relation matrix
	 * @return {@code true} if penetration occurs, i.e., 4-face layer ordering
	 *         condition is not satisfied.
	 */
	private boolean detectPenetrationBy4faces(final OverlapRelation overlapRelation) {
		boolean correct = true;
		for (var cond : condition4s) {
			if (!cond.isDetermined(overlapRelation.get())) {
				continue;
			}

			// if: lower1 > upper2, then: upper1 > upper2, upper1 > lower2,
			// lower1 > lower2
			if (overlapRelation.isLower(cond.lower1, cond.upper2)) {
				correct &= overlapRelation.isLower(cond.upper1, cond.upper2);
				correct &= overlapRelation.isLower(cond.upper1, cond.lower2);
				correct &= overlapRelation.isLower(cond.lower1, cond.lower2);
			}

			// if: lower2 > upper1, then: upper2 > upper1, upper2 > lower1,
			// lower2 > lower1
			if (overlapRelation.isLower(cond.lower2, cond.upper1)) {
				correct &= overlapRelation.isLower(cond.upper2, cond.upper1);
				correct &= overlapRelation.isLower(cond.upper2, cond.lower1);
				correct &= overlapRelation.isLower(cond.lower2, cond.lower1);
			}

			// if: upper1 > upper2 > lower1, then: upper1 > lower2, lower2 >
			// lower1
			if (overlapRelation.isLower(cond.upper1, cond.upper2)
					&& overlapRelation.isLower(cond.upper2, cond.lower1)) {
				correct &= overlapRelation.isLower(cond.upper1, cond.lower2);
				correct &= overlapRelation.isLower(cond.lower2, cond.lower1);
			}

			// if: upper1 > lower2 > lower1, then: upper1 > upper2, upper2 >
			// lower1
			if (overlapRelation.isLower(cond.upper1, cond.lower2)
					&& overlapRelation.isLower(cond.lower2, cond.lower1)) {
				correct &= overlapRelation.isLower(cond.upper1, cond.upper2);
				correct &= overlapRelation.isLower(cond.upper2, cond.lower1);
			}

			// if: upper2 > upper1 > lower2, then: upper2 > lower1, lower1 >
			// lower2
			if (overlapRelation.isLower(cond.upper2, cond.upper1)
					&& overlapRelation.isLower(cond.upper1, cond.lower2)) {
				correct &= overlapRelation.isLower(cond.upper2, cond.lower1);
				correct &= overlapRelation.isLower(cond.lower1, cond.lower2);
			}

			// if: upper2 > lower1 > lower2, then: upper2 > upper1, upper1 >
			// lower2
			if (overlapRelation.isLower(cond.upper2, cond.lower1)
					&& overlapRelation.isLower(cond.lower1, cond.lower2)) {
				correct &= overlapRelation.isLower(cond.upper2, cond.upper1);
				correct &= overlapRelation.isLower(cond.upper1, cond.lower2);
			}

			if (!correct) {
				return true;
			}
		}

		return false;

	}

	/**
	 * Whether the order of faces in {@code answerStack} is correct or not
	 * according to {@code overlapRelation}.
	 *
	 * @param answerStack
	 *            stack of faces including the same subface.
	 * @param overlapRelation
	 *            overlap relation matrix.
	 * @return true if the order is correct.
	 */
	private boolean isCorrectStackOrder(final List<OriFace> answerStack, final OverlapRelation overlapRelation) {
		int size = answerStack.size();

		return IntStream.range(0, size).allMatch(i -> {
			final int index_i = answerStack.get(i).getFaceID();
			return IntStream.range(i + 1, size).allMatch(j -> {
				final int index_j = answerStack.get(j).getFaceID();
				// stack_index = 0 means the top of stack (looking down
				// the folded model on a table).
				// therefore a face with smaller stack_index i should be
				// UPPER than stack_index j.
				if (overlapRelation.isLower(index_i, index_j)) {
					return false;
				}
				return true;
			});
		});
	}

	/**
	 * Determines overlap relations by necessary conditions.
	 *
	 * @param faces
	 *            all faces.
	 * @param overlapRelation
	 *            overlap relation matrix
	 */
	private void estimation(final List<OriFace> faces, final OverlapRelation overlapRelation) {
		boolean changed;
		do {
			changed = false;
			changed |= estimate_by3faces(faces, overlapRelation);
			changed |= estimate_by3faces2(overlapRelation);
			changed |= estimate_by4faces(overlapRelation);
		} while (changed);
	}

	/**
	 * Creates 3-face condition and sets to subfaces: If face[i] and face[j]
	 * touching edge are covered by face[k] then OR[i][k] = OR[j][k]
	 *
	 * @param faces
	 *            all faces of the model
	 * @param paperSize
	 *            paper size before fold
	 * @param overlapRelation
	 *            overlap relation matrix
	 */
	private void holdCondition3s(
			final List<OriFace> faces, final double paperSize, final OverlapRelation overlapRelation) {

		for (OriFace f_i : faces) {
			for (OriHalfedge he : f_i.halfedgeIterable()) {
				var pair = he.getPair();
				if (pair == null) {
					continue;
				}

				OriFace f_j = pair.getFace();
				if (!overlapRelation.isLower(f_i.getFaceID(), f_j.getFaceID())) {
					continue;
				}
				for (OriFace f_k : faces) {
					if (f_k == f_i || f_k == f_j) {
						continue;
					}
					if (!OriGeomUtil.isLineCrossFace4(f_k, he, paperSize)) {
						continue;
					}
					StackConditionOf3Faces cond = new StackConditionOf3Faces();
					cond.upper = f_i.getFaceID();
					cond.lower = f_j.getFaceID();
					cond.other = f_k.getFaceID();

					// Add condition to all subfaces of the 3 faces
					for (SubFace sub : subFaces) {
						if (sub.parentFaces.contains(f_i) && sub.parentFaces.contains(f_j)
								&& sub.parentFaces.contains(f_k)) {
							sub.condition3s.add(cond);
						}
					}

				}
			}
		}
	}

	/**
	 * Creates 4-face condition and sets to subfaces.
	 *
	 * @param edges
	 *            all eges of the model
	 * @param paperSize
	 *            paper size before fold
	 * @param overlapRelation
	 *            overlap relation matrix
	 */
	private void holdCondition4s(
			final List<OriEdge> edges, final OverlapRelation overlapRelation) {

		int edgeNum = edges.size();
		logger.debug("edgeNum = " + edgeNum);

		for (int i = 0; i < edgeNum; i++) {
			OriEdge e0 = edges.get(i);
			var e0Left = e0.getLeft();
			var e0Right = e0.getRight();

			if (e0Left == null || e0Right == null) {
				continue;
			}

			for (int j = i + 1; j < edgeNum; j++) {
				OriEdge e1 = edges.get(j);
				var e1Left = e1.getLeft();
				var e1Right = e1.getRight();
				if (e1Left == null || e1Right == null) {
					continue;
				}

				if (!GeomUtil.isLineSegmentsOverlap(e0Left.getPosition(),
						e0Left.getNext().getPosition(),
						e1Left.getPosition(), e1Left.getNext().getPosition())) {
					continue;
				}

				var e0LeftFace = e0Left.getFace();
				var e0RightFace = e0Right.getFace();
				var e1LeftFace = e1Left.getFace();
				var e1RightFace = e1Right.getFace();

				StackConditionOf4Faces cond = new StackConditionOf4Faces();
				// Add condition to all subfaces of the 4 faces
				boolean bOverlap = false;
				for (SubFace sub : subFaces) {
					if (sub.parentFaces.contains(e0LeftFace)
							&& sub.parentFaces.contains(e0RightFace)
							&& sub.parentFaces.contains(e1LeftFace)
							&& sub.parentFaces.contains(e1RightFace)) {
						sub.condition4s.add(cond);
						bOverlap = true;
					}
				}

				var e0LeftFaceID = e0LeftFace.getFaceID();
				var e0RightFaceID = e0RightFace.getFaceID();
				var e1LeftFaceID = e1LeftFace.getFaceID();
				var e1RightFaceID = e1RightFace.getFaceID();

				if (overlapRelation.isUpper(e0LeftFaceID, e0RightFaceID)) {
					cond.upper1 = e0RightFaceID;
					cond.lower1 = e0LeftFaceID;
				} else {
					cond.upper1 = e0LeftFaceID;
					cond.lower1 = e0RightFaceID;
				}
				if (overlapRelation.isUpper(e1LeftFaceID, e1RightFaceID)) {
					cond.upper2 = e1RightFaceID;
					cond.lower2 = e1LeftFaceID;
				} else {
					cond.upper2 = e1LeftFaceID;
					cond.lower2 = e1RightFaceID;
				}

				if (bOverlap) {
					condition4s.add(cond);
				}
			}
		}
	}

	/**
	 * Determines overlap relation using 4-face condition.
	 *
	 * @param overlapRelation
	 * @return
	 */
	private boolean estimate_by4faces(final OverlapRelation overlapRelation) {

		boolean changed = false;

		for (StackConditionOf4Faces cond : condition4s) {

			// if: lower1 > upper2, then: upper1 > upper2, upper1 > lower2,
			// lower1 > lower2
			if (overlapRelation.isLower(cond.lower1, cond.upper2)) {
				changed |= overlapRelation.setLowerIfUndefined(cond.upper1, cond.upper2);
				changed |= overlapRelation.setLowerIfUndefined(cond.upper1, cond.lower2);
				changed |= overlapRelation.setLowerIfUndefined(cond.lower1, cond.lower2);
			}

			// if: lower2 > upper1, then: upper2 > upper1, upper2 > lower1,
			// lower2 > lower1
			if (overlapRelation.isLower(cond.lower2, cond.upper1)) {
				changed |= overlapRelation.setLowerIfUndefined(cond.upper2, cond.upper1);
				changed |= overlapRelation.setLowerIfUndefined(cond.upper2, cond.lower1);
				changed |= overlapRelation.setLowerIfUndefined(cond.lower2, cond.lower1);
			}

			// if: upper1 > upper2 > lower1, then: upper1 > lower2, lower2 >
			// lower1
			if (overlapRelation.isLower(cond.upper1, cond.upper2)
					&& overlapRelation.isLower(cond.upper2, cond.lower1)) {
				changed |= overlapRelation.setLowerIfUndefined(cond.upper1, cond.lower2);
				changed |= overlapRelation.setLowerIfUndefined(cond.lower2, cond.lower1);
			}

			// if: upper1 > lower2 > lower1, then: upper1 > upper2, upper2 >
			// lower1
			if (overlapRelation.isLower(cond.upper1, cond.lower2)
					&& overlapRelation.isLower(cond.lower2, cond.lower1)) {
				changed |= overlapRelation.setLowerIfUndefined(cond.upper1, cond.upper2);
				changed |= overlapRelation.setLowerIfUndefined(cond.upper2, cond.lower1);
			}

			// if: upper2 > upper1 > lower2, then: upper2 > lower1, lower1 >
			// lower2
			if (overlapRelation.isLower(cond.upper2, cond.upper1)
					&& overlapRelation.isLower(cond.upper1, cond.lower2)) {
				changed |= overlapRelation.setLowerIfUndefined(cond.upper2, cond.lower1);
				changed |= overlapRelation.setLowerIfUndefined(cond.lower1, cond.lower2);
			}

			// if: upper2 > lower1 > lower2, then: upper2 > upper1, upper1 >
			// lower2
			if (overlapRelation.isLower(cond.upper2, cond.lower1)
					&& overlapRelation.isLower(cond.lower1, cond.lower2)) {
				changed |= overlapRelation.setLowerIfUndefined(cond.upper2, cond.upper1);
				changed |= overlapRelation.setLowerIfUndefined(cond.upper1, cond.lower2);
			}
		}

		return changed;
	}

	/**
	 * If the subface a>b and b>c then a>c
	 *
	 * @param overlapRelation
	 *            overlap-relation matrix
	 * @return whether overlapRelation is changed or not.
	 */
	private boolean estimate_by3faces2(final OverlapRelation overlapRelation) {
		boolean bChanged = false;

		for (SubFace sub : subFaces) {
			while (updateOverlapRelationBy3FaceStack(sub, overlapRelation)) {
				bChanged = true;
			}
		}
		return bChanged;
	}

	/**
	 * Updates {@code overlapRelation} by 3-face stack condition.
	 *
	 * @param sub
	 *            subface.
	 * @param overlapRelation
	 *            overlap relation matrix.
	 * @return true if an update happens.
	 */
	private boolean updateOverlapRelationBy3FaceStack(final SubFace sub, final OverlapRelation overlapRelation) {

		for (int i = 0; i < sub.parentFaces.size(); i++) {
			for (int j = i + 1; j < sub.parentFaces.size(); j++) {

				// search for undetermined relations
				int index_i = sub.parentFaces.get(i).getFaceID();
				int index_j = sub.parentFaces.get(j).getFaceID();

				if (overlapRelation.isNoOverlap(index_i, index_j)) {
					continue;
				}
				if (!overlapRelation.isUndefined(index_i, index_j)) {
					continue;
				}
				// Find the intermediary face
				for (int k = 0; k < sub.parentFaces.size(); k++) {
					if (k == i || k == j) {
						continue;
					}

					int index_k = sub.parentFaces.get(k).getFaceID();

					if (overlapRelation.isUpper(index_i, index_k)
							&& overlapRelation.isUpper(index_k, index_j)) {
						overlapRelation.setUpper(index_i, index_j);
						return true;
					}
					if (overlapRelation.isLower(index_i, index_k)
							&& overlapRelation.isLower(index_k, index_j)) {
						overlapRelation.setLower(index_i, index_j);
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * If face[i] and face[j] touching edge is covered by face[k] then
	 * overlapRelation[i][k] = overlapRelation[j][k].
	 *
	 * @param faces
	 *            all faces of the model
	 * @param overlapRelation
	 *            overlap relation matrix
	 * @return whether overlapRelation is changed or not.
	 */
	private boolean estimate_by3faces(
			final List<OriFace> faces,
			final OverlapRelation overlapRelation) {

		boolean bChanged = false;
		for (OriFace f_i : faces) {
			int index_i = f_i.getFaceID();
			for (OriHalfedge he : f_i.halfedgeIterable()) {
				var pair = he.getPair();
				if (pair == null) {
					continue;
				}
				OriFace f_j = pair.getFace();
				int index_j = f_j.getFaceID();

				for (OriFace f_k : faces) {
					int index_k = f_k.getFaceID();
					if (f_k == f_i || f_k == f_j) {
						continue;
					}
					if (!OriGeomUtil.isLineCrossFace(f_k, he, 0.0001)) {
						continue;
					}
					if (!overlapRelation.isUndefined(index_i, index_k)
							&& overlapRelation.isUndefined(index_j, index_k)) {
						overlapRelation.set(index_j, index_k, overlapRelation.get(index_i, index_k));
						bChanged = true;
					} else if (!overlapRelation.isUndefined(index_j, index_k)
							&& overlapRelation.isUndefined(index_i, index_k)) {
						overlapRelation.set(index_i, index_k, overlapRelation.get(index_j, index_k));
						bChanged = true;
					}
				}
			}
		}

		return bChanged;
	}

	/**
	 * Determines the overlap relations by mountain/valley.
	 *
	 * @param faces
	 *            all faces of the model
	 * @param overlapRelation
	 *            overlap relation matrix
	 */
	private void determineOverlapRelationByLineType(
			final List<OriFace> faces, final OverlapRelation overlapRelation) {

		for (OriFace face : faces) {
			for (OriHalfedge he : face.halfedgeIterable()) {
				var pair = he.getPair();
				if (pair == null) {
					continue;
				}
				OriFace pairFace = pair.getFace();
				var faceID = face.getFaceID();
				var pairFaceID = pairFace.getFaceID();

				// If the relation is already decided, skip
				if (overlapRelation.isUpper(faceID, pairFaceID)
						|| overlapRelation.isLower(faceID, pairFaceID)) {
					continue;
				}

				if ((face.isFaceFront() && he.getType() == OriLine.Type.MOUNTAIN.toInt())
						|| (!face.isFaceFront() && he.getType() == OriLine.Type.VALLEY.toInt())) {
					overlapRelation.setUpper(faceID, pairFaceID);
				} else {
					overlapRelation.setLower(faceID, pairFaceID);
				}
			}
		}
	}

	/**
	 * creates the matrix overlapRelation and fills it with "no overlap" or
	 * "undefined"
	 *
	 * @param faces
	 *            all faces of the model
	 * @param paperSize
	 *            paper size before fold
	 * @return initialized overlap relation matrix
	 */
	private OverlapRelation createOverlapRelation(final List<OriFace> faces, final double paperSize) {

		int size = faces.size();
		OverlapRelation overlapRelation = new OverlapRelation(size);

		for (int i = 0; i < size; i++) {
			overlapRelation.setNoOverlap(i, i);
			for (int j = i + 1; j < size; j++) {
				if (OriGeomUtil.isFaceOverlap(faces.get(i), faces.get(j), eps(paperSize))) {
					overlapRelation.setUndefined(i, j);
				} else {
					overlapRelation.setNoOverlap(i, j);
				}
			}
		}

		return overlapRelation;
	}
}
