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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.fold.DeterministicLayerOrderEstimator.EstimationResult;
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

/**
 * @author OUCHI Koji
 *
 */
public class LayerOrderEnumerator {
	private static final Logger logger = LoggerFactory.getLogger(LayerOrderEnumerator.class);

//	private HashSet<StackConditionOf4Faces> condition4s;
	private List<SubFace> subFaces;

	private AtomicInteger callCount;
	private AtomicInteger penetrationTestCallCount;
	private AtomicInteger penetrationCount;

	private final SubFacesFactory subFacesFactory;

	public LayerOrderEnumerator(final SubFacesFactory subFacesFactory) {
		this.subFacesFactory = subFacesFactory;
	}

	public List<OverlapRelation> enumerate(final OrigamiModel origamiModel) {
		var faces = origamiModel.getFaces();
		var edges = origamiModel.getEdges();

		// construct the subfaces
		final double paperSize = origamiModel.getPaperSize();
		final double eps = OriGeomUtil.pointEps(paperSize);
		subFaces = subFacesFactory.createSubFaces(faces, paperSize, eps);
		logger.debug("subFaces.size() = " + subFaces.size());

		// Set overlap relations based on valley/mountain folds information
		OverlapRelation overlapRelation = new OverlapRelationFactory().createOverlapRelationByLineType(faces, eps);

		var watch = new StopWatch(true);

		logger.debug("preprocessing time = {}[ms]", watch.getMilliSec());

		var overlappingFaceIndexIntersections = createOverlappingFaceIndexIntersections(faces, overlapRelation);
		var faceIndicesOnHalfEdge = createFaceIndicesOnHalfEdge(faces, eps);
		holdCondition3s(faces, overlapRelation, overlappingFaceIndexIntersections, faceIndicesOnHalfEdge);

		var condition4s = holdCondition4s(faces, edges, overlapRelation, eps);

		var estimator = new DeterministicLayerOrderEstimator(
				faces, subFaces,
				overlappingFaceIndexIntersections,
				faceIndicesOnHalfEdge,
				condition4s);
		var estimationResult = estimator.estimate(overlapRelation, eps);

		if (estimationResult == EstimationResult.UNFOLDABLE) {
			logger.debug("found unfoldable before searching.");
			return List.of();
		}

		var undefinedRelationCount = countUndefinedRelations(overlapRelation);
		logger.debug("#undefined = {}", undefinedRelationCount);

		watch.start();
		var localLayerOrderCountMap = new HashMap<SubFace, Integer>();
		subFaces.stream().forEach(sub -> {
			var localLayerOrderCount = sub.countLocalLayerOrders(faces, overlapRelation, true);
			localLayerOrderCountMap.put(sub, localLayerOrderCount);
		});
		logger.debug("local layer ordering time = {}[ms]", watch.getMilliSec());
		logger.debug("max #localLayerOrder {}",
				localLayerOrderCountMap.values().stream().mapToInt(i -> i).max().getAsInt());
		logger.debug("average #localLayerOrder {}",
				localLayerOrderCountMap.values().stream().mapToInt(i -> i == -1 ? 1 : i).average().getAsDouble());
		logger.debug("max #parentFace {}",
				subFaces.stream().mapToInt(SubFace::getParentFaceCount).max().getAsInt());

		// heuristic: fewer local layer orders mean the search on the subface
		// has more possibility to be correct. Such confident search node should
		// be consumed at early stage.
		subFaces = localLayerOrderCountMap.entrySet().stream()
				.sorted(Comparator.comparing(Entry::getValue))
				.map(Entry::getKey)
				.collect(Collectors.toList());

		var overlapRelations = Collections.synchronizedList(new ArrayList<OverlapRelation>());

		watch.start();

		callCount = new AtomicInteger();
		penetrationTestCallCount = new AtomicInteger();
		penetrationCount = new AtomicInteger();
		findAnswer(faces, overlapRelations, 0, overlapRelation);
		var time = watch.getMilliSec();

		logger.debug("#call = {}", callCount);
		logger.debug("#penetrationTest = {}", penetrationTestCallCount);
		logger.debug("#penetration = {}", penetrationCount);
		logger.debug("time = {}[ms]", time);

		return overlapRelations;
	}

	@SuppressWarnings("unchecked")
	private List<Integer>[][] createOverlappingFaceIndexIntersections(
			final List<OriFace> faces,
			final OverlapRelation overlapRelation) {
		List<Set<Integer>> indices = IntStream.range(0, faces.size())
				.mapToObj(i -> new HashSet<Integer>())
				.collect(Collectors.toList());

		// prepare pair indices of overlapping faces.
		for (var face : faces) {
			for (var other : faces) {
				var index_i = face.getFaceID();
				var index_j = other.getFaceID();
				if (!overlapRelation.isNoOverlap(index_i, index_j)) {
					indices.get(index_i).add(index_j);
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
			final List<OriFace> faces,
			final double eps) {

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
					if (OriGeomUtil.isLineCrossFace(other, halfedge, eps)) {
						indexSet.add(other.getFaceID());
					}
				}
			}
		}

		return indices;
	}

	private int countUndefinedRelations(final OverlapRelation overlapRelation) {
		int size = overlapRelation.getSize();

		int count = 0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (overlapRelation.isUndefined(i, j)) {
					count++;
				}
			}
		}

		return count;
	}

	/**
	 * Determines overlap relations which are left uncertain after using
	 * necessary conditions.
	 *
	 * @param faces
	 *            all faces of the origami model.
	 * @param overlapRelations
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
			final List<OverlapRelation> overlapRelations, final int subFaceIndex,
			final OverlapRelation overlapRelation) {
		callCount.incrementAndGet();

		if (subFaceIndex == subFaces.size()) {
			var answer = overlapRelation.clone();
			overlapRelations.add(answer);
			return;
		}

		SubFace sub = subFaces.get(subFaceIndex);

		var localLayerOrders = sub.createLocalLayerOrders(faces, overlapRelation, false);

		if (localLayerOrders == null) {
			findAnswer(faces, overlapRelations, subFaceIndex + 1, overlapRelation);
			return;
		}

		// Parallel search. It is fast but can exceed memory for
		// complex model because of copying overlapRelation (a large matrix).
		localLayerOrders.parallelStream().forEach(localLayerOrder -> {
			int size = localLayerOrder.size();
			var nextOverlapRelation = overlapRelation.clone();

			// determine overlap relations according to local layer order
			IntStream.range(0, size).forEach(i -> {
				int index_i = localLayerOrder.get(i).getFaceID();
				for (int j = i + 1; j < size; j++) {
					int index_j = localLayerOrder.get(j).getFaceID();
					if (nextOverlapRelation.isUndefined(index_i, index_j)) {
						// if index on local layer order is 0, the face is at
						// the top of layer order (looking down the folded model
						// on a table).
						// therefore a face with smaller index i on layer order
						// should be UPPER than a face with index j on layer
						// order.
						nextOverlapRelation.setUpper(index_i, index_j);
					}
				}
			});

			findAnswer(faces, overlapRelations, subFaceIndex + 1,
					nextOverlapRelation);
		});

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
			final List<OriFace> faces, final OverlapRelation overlapRelation,
			final List<Integer>[][] overlappingFaceIndexIntersections,
			final Map<OriHalfedge, Set<Integer>> faceIndicesOnHalfEdge) {

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
	 *            all faces of the model
	 * @param edges
	 *            all edges of the model
	 * @param overlapRelation
	 *            overlap relation matrix
	 */
	private Set<StackConditionOf4Faces> holdCondition4s(final List<OriFace> faces,
			final List<OriEdge> edges, final OverlapRelation overlapRelation, final double eps) {
		var condition4s = new HashSet<StackConditionOf4Faces>();

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

				if (!GeomUtil.isOverlap(e0.toSegment(), e1.toSegment(), eps)) {
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

		return condition4s;
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
}
