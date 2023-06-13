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
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.fold.condfac.StackConditionFactoryFacade;
import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.domain.fold.origeom.EstimationResult;
import oripa.domain.fold.origeom.OriGeomUtil;
import oripa.domain.fold.origeom.OverlapRelation;
import oripa.domain.fold.stackcond.StackConditionOf3Faces;
import oripa.domain.fold.stackcond.StackConditionOf4Faces;
import oripa.domain.fold.subface.SubFace;
import oripa.domain.fold.subface.SubFacesFactory;
import oripa.util.IntPair;
import oripa.util.StopWatch;

/**
 * @author OUCHI Koji
 *
 */
public class LayerOrderEnumerator {

	public static class Result {
		private final List<OverlapRelation> overlapRelations;
		private final List<SubFace> subfaces;

		private Result(final List<OverlapRelation> overlapRelations, final List<SubFace> subfaces) {
			this.overlapRelations = overlapRelations;
			this.subfaces = subfaces;
		}

		public List<OverlapRelation> getOverlapRelations() {
			return overlapRelations;
		}

		public List<SubFace> getSubfaces() {
			return subfaces;
		}

		public boolean isEmpty() {
			return overlapRelations.isEmpty();
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(LayerOrderEnumerator.class);

	private AtomicInteger callCount;

	private final SubFacesFactory subfacesFactory;

	private final boolean shouldLogStats;

	public LayerOrderEnumerator(final SubFacesFactory subfacesFactory, final boolean shouldLogStats) {
		this.subfacesFactory = subfacesFactory;
		this.shouldLogStats = shouldLogStats;
	}

	public Result enumerate(final OrigamiModel origamiModel) {
		var faces = origamiModel.getFaces();
		var edges = origamiModel.getEdges();

		// construct the subfaces
		final double paperSize = origamiModel.getPaperSize();
		final double eps = OriGeomUtil.pointEps(paperSize);
		var subfaces = subfacesFactory.createSubFaces(faces, paperSize, eps);
		logger.debug("subFaces.size() = " + subfaces.size());

		// Set overlap relations based on valley/mountain folds information
		OverlapRelation overlapRelation;
		try {
			overlapRelation = new OverlapRelationFactory().createOverlapRelationByLineType(faces, eps);
		} catch (Exception e) {
			logger.debug("found unfoldable when constructing overlap relation.");
			return new Result(List.of(), List.of());
		}

		var watch = new StopWatch(true);

		logger.debug("preprocessing time = {}[ms]", watch.getMilliSec());

		var conditionFactory = new StackConditionFactoryFacade(faces, edges, overlapRelation, subfaces, eps);

		var overlappingFaceIndexIntersections = conditionFactory.getOverlappingFaceIndexIntersections();
		var faceIndicesOnHalfedge = conditionFactory.getFaceIndicesOnHalfedge();

		var condition3s = conditionFactory.create3FaceConditions();
		setConditionOf3facesToSubfaces(condition3s, subfaces);

		var condition4s = conditionFactory.create4FaceCondtions();
		setConditionOf4facesToSubfaces(condition4s, subfaces);

		var estimator = new DeterministicLayerOrderEstimator(
				faces, subfaces,
				overlappingFaceIndexIntersections,
				faceIndicesOnHalfedge,
				condition4s);
		var estimationResult = estimator.estimate(overlapRelation, eps);

		if (estimationResult == EstimationResult.UNFOLDABLE) {
			logger.debug("found unfoldable before searching.");
			return new Result(List.of(), List.of());
		}

		var undefinedRelationCount = countUndefinedRelations(overlapRelation);
		logger.debug("#undefined = {}", undefinedRelationCount);

		watch.start();
		// heuristic: apply the heuristic in local layer ordering to global
		// subface ordering.
		subfaces = subfaces.stream()
				.sorted(Comparator
						.comparingInt((final SubFace sub) -> sub.getAllCountOfConditionsOf2Faces(overlapRelation))
						.thenComparingInt((final SubFace sub) -> sub.getAllCountOfConditionsOf3Faces(overlapRelation))
						.thenComparingInt((final SubFace sub) -> sub.getAllCountOfConditionsOf4Faces(overlapRelation))
						.reversed())
				.collect(Collectors.toList());
		logger.debug("subface ordering = {}[ms]", watch.getMilliSec());

		var overlapRelations = Collections.synchronizedList(new ArrayList<OverlapRelation>());

		watch.start();

		callCount = new AtomicInteger();
		findAnswer(faces, overlapRelations, subfaces, overlapRelation);
		var time = watch.getMilliSec();

		logger.debug("#call = {}", callCount);
		logger.debug("time = {}[ms]", time);

		if (shouldLogStats) {
			logStats(subfaces, overlapRelation);
		}

		return new Result(overlapRelations, subfaces);
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
	private int findAnswer(
			final List<OriFace> faces,
			final List<OverlapRelation> overlapRelations,
			final List<SubFace> subfaces,
			final OverlapRelation overlapRelation) {
		callCount.incrementAndGet();

		if (subfaces.isEmpty()) {
			var answer = overlapRelation.clone();
			overlapRelations.add(answer);

			return 1;
		}

		SubFace sub = subfaces.get(0);

		var localLayerOrders = sub.createLocalLayerOrders(faces, overlapRelation, false);

		if (localLayerOrders == null) {
			var nextSubfaces = popAndSort(subfaces);
			return findAnswer(faces, overlapRelations, nextSubfaces, overlapRelation);
		}

		var successCount = new AtomicInteger();

		// Parallel search. It is fast but can exceed memory for
		// complex model because of copying overlapRelation (a large matrix).
		localLayerOrders.parallelStream().forEach(localLayerOrder -> {
			int size = localLayerOrder.size();
			var nextSubfaces = popAndSort(subfaces);
			var nextOverlapRelation = overlapRelation.clone();

			// determine overlap relations according to local layer order
			for (int i = 0; i < size; i++) {
				int index_i = localLayerOrder.get(i).getFaceID();
				for (int j = i + 1; j < size; j++) {
					int index_j = localLayerOrder.get(j).getFaceID();
					// if index on local layer order is 0, the face is at
					// the top of layer order (looking down the folded model
					// on a table).
					// therefore a face with smaller index i on layer order
					// should be UPPER than a face with index j on layer
					// order.
					var result = nextOverlapRelation.setUpperIfPossible(index_i, index_j);
					if (result == EstimationResult.UNFOLDABLE) {
						return;
					}
				}
			}

			sub.incrementCallCount();
			successCount.addAndGet(findAnswer(faces, overlapRelations, nextSubfaces, nextOverlapRelation));
		});

		sub.addSuccessCount(successCount.get());

		return successCount.get();
	}

	private List<SubFace> popAndSort(final List<SubFace> subfaces) {
		var sublist = new ArrayList<>(subfaces.subList(1, subfaces.size()));

		// sort sublist for speeding up
		sublist.sort(Comparator.comparing(SubFace::getSuccessRate).reversed());

		return sublist;
	}

	private void setConditionOf3facesToSubfaces(
			final List<StackConditionOf3Faces> conditions,
			final List<SubFace> subfaces) {

		int count = 0;

		for (var subface : subfaces) {
			for (var condition : conditions) {
				if (subface.isRelatedTo(condition)) {
					subface.addStackConditionOf3Faces(condition);
					count++;
				}
			}
		}

		logger.debug("condtion3 set count ={}", count);
	}

	private void setConditionOf4facesToSubfaces(
			final List<StackConditionOf4Faces> conditions,
			final List<SubFace> subfaces) {

		int count = 0;

		for (var subface : subfaces) {
			for (var condition : conditions) {
				if (subface.isRelatedTo(condition)) {
					subface.addStackConditionOf4Faces(condition);
					count++;
				}
			}
		}

		logger.debug("condtion4 set count ={}", count);
	}

	private void logStats(final List<SubFace> subfaces, final OverlapRelation overlapRelation) {

		// logConditionCountDistribution(subfaces.get(0), overlapRelation);

		for (int i = 0; i < subfaces.size(); i++) {
			var subface = subfaces.get(i);

			if (subface.getSuccessCount() == 0) {
				continue;
			}

			var mostUsedFirstFaceEntry = subface.getFirstFaceCounts().entrySet().stream()
					.max(Comparator.comparing(entry -> entry.getValue().get()))
					.get();
			var firstFace = mostUsedFirstFaceEntry.getKey();

			logger.debug("@{} #face {}", i, subface.getParentFaceCount());
			logger.debug("@{} (firstFace, count, #2FacesCondition) = ({}, {}, {})", i,
					mostUsedFirstFaceEntry.getKey().getFaceID(),
					mostUsedFirstFaceEntry.getValue(),
					subface.getCountOfConditionsOf2Faces(firstFace, overlapRelation));
			logger.debug("@{} success rate {}", i, subface.getSuccessRate());
			logger.debug("@{} #2FacesFailure {}", i, subface.getFailureCountOf2Faces());
			logger.debug("@{} #3FacesFailure {}", i, subface.getFailureCountOf3Faces());
			logger.debug("@{} #4FacesFailure {}", i, subface.getFailureCountOf4Faces());
		}

	}

	private void logConditionCountDistribution(final SubFace subface, final OverlapRelation overlapRelation) {
		var faceIndexAndCountPairs = new ArrayList<IntPair>();

		for (int i = 0; i < subface.getParentFaceCount(); i++) {
			var face = subface.getParentFace(i);
			var pair = new IntPair(face.getFaceID(),
					subface.getCountOfConditionsOf2Faces(face, overlapRelation));

			faceIndexAndCountPairs.add(pair);
		}

		faceIndexAndCountPairs.sort(Comparator.comparing(IntPair::getV2));

		faceIndexAndCountPairs.forEach(pair -> logger.debug("@{} (face, #2FacesCondition) = ({}, {})", 0,
				pair.getV1(),
				pair.getV2()));

	}
}
