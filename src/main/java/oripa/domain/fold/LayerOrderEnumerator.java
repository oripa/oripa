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

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
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
import oripa.util.IntPair;
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
	private List<Integer>[][] overlappingFaceIndexIntersections;

	/**
	 * Key: halfedge, value: set of indices which are under the halfedge.
	 */
	private Map<OriHalfedge, Set<Integer>> faceIndicesOnHalfEdge;

	private AtomicInteger callCount;
	private AtomicInteger penetrationTestCallCount;
	private AtomicInteger penetrationCount;

	private final SubFacesFactory subFacesFactory;

	public LayerOrderEnumerator(final SubFacesFactory subFacesFactory) {
		this.subFacesFactory = subFacesFactory;
	}

	public OverlapRelationList enumerate(final OrigamiModel origamiModel) {
		var faces = origamiModel.getFaces();
		var edges = origamiModel.getEdges();

		// construct the subfaces
		double paperSize = origamiModel.getPaperSize();
		subFaces = subFacesFactory.createSubFaces(faces, paperSize, eps(paperSize));
		logger.debug("subFaces.size() = " + subFaces.size());

		OverlapRelation overlapRelation = createOverlapRelation(faces, paperSize);

		// Set overlap relations based on valley/mountain folds information
		determineOverlapRelationByLineType(faces, overlapRelation);

		var watch = new StopWatch(true);

		overlappingFaceIndexIntersections = createOverlappingFaceIndexIntersections(faces, paperSize);
		faceIndicesOnHalfEdge = createFaceIndicesOnHalfEdge(faces, paperSize);

		logger.debug("preprocessing time = {}[ms]", watch.getMilliSec());

		holdCondition3s(faces, overlapRelation);

		condition4s = new HashSet<>();
		holdCondition4s(faces, edges, overlapRelation);

		estimate(faces, overlapRelation);

		watch.start();
		for (SubFace sub : subFaces) {
			sub.buildLocalLayerOrders(faces, overlapRelation);
		}
		logger.debug("local layer ordering time = {}[ms]", watch.getMilliSec());

		// heuristic: fewer local layer orders mean the search on the subface
		// has more possibility to be correct. Such confident search node should
		// be consumed at early stage.
		subFaces = subFaces.stream()
				.sorted(Comparator.comparing(SubFace::getLocalLayerOrderCount))
				.collect(Collectors.toList());

		var overlapRelationList = new OverlapRelationList();

		watch.start();

		var changedFaceIDs = faces.stream().map(OriFace::getFaceID).collect(Collectors.toSet());
		callCount = new AtomicInteger();
		penetrationTestCallCount = new AtomicInteger();
		penetrationCount = new AtomicInteger();
		findAnswer(faces, overlapRelationList, 0, overlapRelation, changedFaceIDs);
		var time = watch.getMilliSec();

		logger.debug("#call = {}", callCount.get());
		logger.debug("#penetrationTest = {}", penetrationTestCallCount);
		logger.debug("#penetration = {}", penetrationCount);
		logger.debug("time = {}[ms]", time);

		return overlapRelationList;
	}

	private double eps(final double paperSize) {
		return paperSize * 0.00001;
	}

	@SuppressWarnings("unchecked")
	private List<Integer>[][] createOverlappingFaceIndexIntersections(final List<OriFace> faces,
			final double paperSize) {
		List<Set<Integer>> indices = IntStream.range(0, faces.size())
				.mapToObj(i -> new HashSet<Integer>())
				.collect(Collectors.toList());

		final double EPS = eps(paperSize);

		// prepare pair indices of overlapping faces.
		for (var face : faces) {
			for (var other : faces) {
				if (face.getFaceID() == other.getFaceID()) {
					continue;
				}
				if (OriGeomUtil.isFaceOverlap(face, other, EPS)) {
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

		final double EPS = eps(paperSize);
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
					if (OriGeomUtil.isLineCrossFace(other, halfedge, EPS)) {
						indexSet.add(other.getFaceID());
					}
				}
			}
		}

		return indices;
	}

	private class IndexPair extends IntPair {
		public IndexPair(final int i, final int j) {
			super(i, j);
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
		callCount.incrementAndGet();

		if (!changedFaceIDs.isEmpty()) {
			penetrationTestCallCount.incrementAndGet();
			if (detectPenetrationBy3faces(faces, changedFaceIDs, overlapRelation)) {
				penetrationCount.incrementAndGet();
				return;
			}
			if (detectPenetrationBy4faces(overlapRelation)) {
				penetrationCount.incrementAndGet();
				return;
			}
		}

		if (subFaceIndex == subFaces.size()) {
			var answer = overlapRelation.clone();
			overlapRelationList.add(answer);
			return;
		}

		SubFace sub = subFaces.get(subFaceIndex);

		if (sub.isLocalLayerOrderDeterminedByGlobal()) {
			findAnswer(faces, overlapRelationList, subFaceIndex + 1, overlapRelation,
					new HashSet<>());
			return;
		}

		var correctLocalLayerOrders = sub.localLayerOrdersStream().parallel()
				.filter(localLayerOrder -> isCorrectLayerOrder(localLayerOrder, overlapRelation))
				.collect(Collectors.toList());

		// Parallel search. It is fast but can exceed memory for
		// complex model because of copying overlapRelation (a large matrix).
		correctLocalLayerOrders.parallelStream().forEach(localLayerOrder -> {
			int size = localLayerOrder.size();
			var nextChangedFaceIDs = new HashSet<Integer>();
			var nextOverlapRelation = overlapRelation.clone();

			// determine overlap relations according to local layer order
			IntStream.range(0, size).forEach(i -> {
				int index_i = localLayerOrder.get(i).getFaceID();
				for (int j = i + 1; j < size; j++) {
					int index_j = localLayerOrder.get(j).getFaceID();
					if (nextOverlapRelation.isUndefined(index_i, index_j)) {
						nextOverlapRelation.setUpper(index_i, index_j);

						nextChangedFaceIDs.add(index_i);
						nextChangedFaceIDs.add(index_j);
					}
				}
			});

			findAnswer(faces, overlapRelationList, subFaceIndex + 1,
					nextOverlapRelation, nextChangedFaceIDs);
		});

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

				var penetrates = overlappingFaceIndexIntersections[index_i][index_j].parallelStream()
						.anyMatch(index_k -> {
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
			if (!cond.isDetermined(overlapRelation)) {
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
	 * Whether the order of faces in {@code localLayerOrder} is correct or not
	 * according to {@code overlapRelation}.
	 *
	 * @param localLayerOrder
	 *            stack of faces including the same subface.
	 * @param overlapRelation
	 *            overlap relation matrix.
	 * @return true if the order is correct.
	 */
	private boolean isCorrectLayerOrder(final List<OriFace> localLayerOrder, final OverlapRelation overlapRelation) {
		int size = localLayerOrder.size();

		return IntStream.range(0, size).allMatch(i -> {
			final int index_i = localLayerOrder.get(i).getFaceID();
			return IntStream.range(i + 1, size).allMatch(j -> {
				final int index_j = localLayerOrder.get(j).getFaceID();
				// if index of local layer order is 0, the face is at the top of
				// layer order (looking down the folded model on a table).
				// therefore a face with smaller index i on layer order should
				// be UPPER than a face with index j on layer order.
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
	private void estimate(final List<OriFace> faces, final OverlapRelation overlapRelation) {
		int estimationLoopCount = 0;

		var watch = new StopWatch(true);
		boolean changed;
		do {
			changed = false;
			changed |= estimateBy3FaceCover(faces, overlapRelation);
			changed |= estimateBy3FaceTransitiveRelation(overlapRelation);
			changed |= estimateBy4FaceStackCondition(overlapRelation);
			estimationLoopCount++;
		} while (changed);
		logger.debug("#estimation = {}", estimationLoopCount);
		logger.debug("estimation time {}[ms]", watch.getMilliSec());
	}

	/**
	 * Creates 3-face condition and sets to subfaces: If face[i] and face[j]
	 * touching edge are covered by face[k] then OR[i][k] = OR[j][k]
	 *
	 * @param faces
	 *            all faces of the model
	 * @param overlapRelation
	 *            overlap relation matrix
	 */
	private void holdCondition3s(
			final List<OriFace> faces, final OverlapRelation overlapRelation) {

		int conditionCount = 0;

		var watch = new StopWatch(true);

		var subFacesOfEachFace = createSubFacesOfEachFace(faces);

		for (OriFace f_i : faces) {
			var index_i = f_i.getFaceID();
			for (OriHalfedge he : f_i.halfedgeIterable()) {
				var pair = he.getPair();
				if (pair == null) {
					continue;
				}

				OriFace f_j = pair.getFace();
				var index_j = f_j.getFaceID();
				if (!overlapRelation.isLower(index_i, index_j)) {
					continue;
				}

				var intersectionSubFaces = subFacesOfEachFace.get(f_i).stream()
						.filter(s -> subFacesOfEachFace.get(f_j).contains(s))
						.collect(Collectors.toList());

				var indices = overlappingFaceIndexIntersections[index_i][index_j];
				for (var index_k : indices) {
					if (index_i == index_k || index_j == index_k) {
						continue;
					}
					if (!faceIndicesOnHalfEdge.get(he).contains(index_k)) {
						continue;
					}
					OriFace f_k = faces.get(index_k);

					StackConditionOf3Faces cond = new StackConditionOf3Faces();
					cond.upper = index_i;
					cond.lower = index_j;
					cond.other = index_k;

					var filteredSubFaces = intersectionSubFaces.stream()
							.filter(s -> subFacesOfEachFace.get(f_k).contains(s))
							.collect(Collectors.toList());
					// Add condition to all subfaces of the 3 faces
					for (SubFace sub : filteredSubFaces) {
						sub.addStackConditionOf3Faces(cond);
						conditionCount++;
					}
				}
			}
		}

		logger.debug("#condition3 = {}", conditionCount);
		logger.debug("condition3s computation time {}[ms]", watch.getMilliSec());
	}

	/**
	 * Creates 4-face condition and sets to subfaces.
	 *
	 * @param faces
	 *            all faces fo the model
	 * @param edges
	 *            all edges of the model
	 * @param overlapRelation
	 *            overlap relation matrix
	 */
	private void holdCondition4s(final List<OriFace> faces,
			final List<OriEdge> edges, final OverlapRelation overlapRelation) {

		int edgeNum = edges.size();
		logger.debug("edgeNum = " + edgeNum);

		var watch = new StopWatch(true);

		var subFacesOfEachFace = createSubFacesOfEachFace(faces);

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

				var intersectionSubfaces = subFacesOfEachFace.get(e0LeftFace).stream()
						.filter(s -> subFacesOfEachFace.get(e0RightFace).contains(s))
						.filter(s -> subFacesOfEachFace.get(e1LeftFace).contains(s))
						.filter(s -> subFacesOfEachFace.get(e1RightFace).contains(s))
						.collect(Collectors.toList());

				if (intersectionSubfaces.isEmpty()) {
					continue;
				}

				StackConditionOf4Faces cond = new StackConditionOf4Faces();

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

				for (var sub : intersectionSubfaces) {
					sub.addStackConditionOf4Faces(cond);
				}
				condition4s.add(cond);
			}
		}

		logger.debug("#condition4 = {}", condition4s.size());
		logger.debug("condition4s computation time {}[ms]", watch.getMilliSec());

	}

	private Map<OriFace, Set<SubFace>> createSubFacesOfEachFace(final List<OriFace> faces) {
		Map<OriFace, Set<SubFace>> subFacesOfEachFace = new HashMap<>();

		faces.forEach(face -> subFacesOfEachFace.put(face, new HashSet<>()));

		for (var subFace : subFaces) {
			for (var face : subFace.ParentFacesIterable()) {
				subFacesOfEachFace.get(face).add(subFace);
			}
		}

		return subFacesOfEachFace;
	}

	/**
	 * Determines overlap relation using 4-face condition.
	 *
	 * @param overlapRelation
	 * @return
	 */
	private boolean estimateBy4FaceStackCondition(final OverlapRelation overlapRelation) {

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
	private boolean estimateBy3FaceTransitiveRelation(final OverlapRelation overlapRelation) {
		boolean bChanged = false;

		for (SubFace sub : subFaces) {
			while (updateOverlapRelationBy3FaceTransitiveRelation(sub, overlapRelation)) {
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
	private boolean updateOverlapRelationBy3FaceTransitiveRelation(final SubFace sub,
			final OverlapRelation overlapRelation) {

		for (int i = 0; i < sub.getParentFaceCount(); i++) {
			for (int j = i + 1; j < sub.getParentFaceCount(); j++) {

				// search for undetermined relations
				int index_i = sub.getParentFace(i).getFaceID();
				int index_j = sub.getParentFace(j).getFaceID();

				if (overlapRelation.isNoOverlap(index_i, index_j)) {
					continue;
				}
				if (!overlapRelation.isUndefined(index_i, index_j)) {
					continue;
				}
				// Find the intermediary face
				for (int k = 0; k < sub.getParentFaceCount(); k++) {
					if (k == i || k == j) {
						continue;
					}

					int index_k = sub.getParentFace(k).getFaceID();

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
	private boolean estimateBy3FaceCover(
			final List<OriFace> faces,
			final OverlapRelation overlapRelation) {

		boolean changed = false;
		for (OriFace f_i : faces) {
			int index_i = f_i.getFaceID();
			for (OriHalfedge he : f_i.halfedgeIterable()) {
				var pair = he.getPair();
				if (pair == null) {
					continue;
				}
				int index_j = pair.getFace().getFaceID();

				var indices = overlappingFaceIndexIntersections[index_i][index_j];
				for (var index_k : indices) {
					if (index_i == index_k || index_j == index_k) {
						continue;
					}
					if (!faceIndicesOnHalfEdge.get(he).contains(index_k)) {
						continue;
					}
					if (!overlapRelation.isUndefined(index_i, index_k)
							&& overlapRelation.isUndefined(index_j, index_k)) {
						overlapRelation.set(index_j, index_k, overlapRelation.get(index_i, index_k));
						changed = true;
					} else if (!overlapRelation.isUndefined(index_j, index_k)
							&& overlapRelation.isUndefined(index_i, index_k)) {
						overlapRelation.set(index_i, index_k, overlapRelation.get(index_j, index_k));
						changed = true;
					}
				}
			}
		}

		return changed;

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

		int countOfZeros = 0;
		for (int i = 0; i < size; i++) {
			overlapRelation.setNoOverlap(i, i);
			for (int j = i + 1; j < size; j++) {
				if (OriGeomUtil.isFaceOverlap(faces.get(i), faces.get(j), eps(paperSize))) {
					overlapRelation.setUndefined(i, j);
				} else {
					overlapRelation.setNoOverlap(i, j);
					countOfZeros++;
				}
			}
		}

		if (((double) countOfZeros) / (size * size) > 0.75) {
			logger.debug("use sparse matrix for overlap relation.");
			overlapRelation.switchToSparseMatrix();
		}

		return overlapRelation;
	}
}
