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
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.fold.condfac.StackConditionFactoryFacade;
import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.domain.fold.origeom.EstimationResult;
import oripa.domain.fold.origeom.OverlapRelation;
import oripa.domain.fold.stackcond.StackConditionOf3Faces;
import oripa.domain.fold.stackcond.StackConditionOf4Faces;
import oripa.domain.fold.subface.SubFace;
import oripa.domain.fold.subface.SubFacesFactory;
import oripa.util.IntPair;
import oripa.util.Pair;
import oripa.util.StopWatch;

/**
 * @author OUCHI Koji
 *
 */
class LayerOrderEnumerator {

	public static class Result {
		private final List<OverlapRelation> overlapRelations;
		private final List<SubFace> subfaces;
		private final EstimationResultRules rules;

		private Result(final List<OverlapRelation> overlapRelations, final List<SubFace> subfaces,
				final EstimationResultRules rules) {
			this.overlapRelations = overlapRelations;
			this.subfaces = subfaces;
			this.rules = rules;
		}

		public List<OverlapRelation> getOverlapRelations() {
			return overlapRelations;
		}

		public List<SubFace> getSubfaces() {
			return subfaces;
		}

		public EstimationResultRules getRules() {
			return rules;
		}

		public boolean isEmpty() {
			return overlapRelations.isEmpty();
		}
	}

	private final static Logger logger = LoggerFactory.getLogger(LayerOrderEnumerator.class);

	private AtomicInteger callCount;
	private AtomicInteger localLayerOrderCount;

	private final SubFacesFactory subfacesFactory;

	private final boolean shouldLogStats;

	private boolean firstOnly;

	public LayerOrderEnumerator(final SubFacesFactory subfacesFactory, final boolean shouldLogStats) {
		this.subfacesFactory = subfacesFactory;
		this.shouldLogStats = shouldLogStats;
	}

	/**
	 * @param origamiModel
	 *            half-edge based data for origami model after moving faces.
	 * @param eps
	 *            max value of computation error.
	 * @param firstOnly
	 *            true for only one state.
	 */
	public Result enumerate(final OrigamiModel origamiModel, final double eps, final boolean firstOnly) {
		var faces = origamiModel.getFaces();
		var edges = origamiModel.getEdges();

		this.firstOnly = firstOnly;

		// construct the subfaces
		final double paperSize = origamiModel.getPaperSize();
		var subfaces = subfacesFactory.createSubFaces(faces, paperSize, paperSize * eps * 10);

		logger.debug("#subface={}", subfaces.size());
		// addKeyValue() seems not to work... See:
		// https://jira.qos.ch/browse/SLF4J-600
//		logger.atDebug().addKeyValue("subfaces.size()", subfaces.size()).log();

		// Set overlap relations based on valley/mountain folds information
		OverlapRelation overlapRelation;
		var result = new OverlapRelationFactory().createOverlapRelationByLineType(faces, eps);
		overlapRelation = result.getOverlapRelation();
		var rules = result.getRules();

		if (rules.isUnfoldable()) {
			return new Result(List.of(), List.of(), rules);
		}

		var watch = new StopWatch(true);

		var conditionFactory = new StackConditionFactoryFacade(faces, edges, overlapRelation, subfaces, eps);

		var overlappingFaceIndexIntersections = conditionFactory.getOverlappingFaceIndexIntersections();
		var faceIndicesOnHalfedge = conditionFactory.getFaceIndicesOnHalfedge();

		logger.debug("preprocessing time = {}[ms]", watch.getMilliSec());

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

		if (estimationResult.isUnfoldable()) {
			logger.info("found unfoldable before searching.");
			return new Result(List.of(), List.of(), estimationResult);
		}

		var undefinedRelationCount = countUndefinedRelations(overlapRelation);
		logger.debug("#undefined = {}", undefinedRelationCount);

		watch.start();

		// heuristic: apply the heuristic in local layer ordering to global
		// subface ordering.
		var scores2Faces = new HashMap<SubFace, Double>();
		var scores3Faces = new HashMap<SubFace, Double>();
		var scores4Faces = new HashMap<SubFace, Double>();
		for (var sub : subfaces) {
			scores2Faces.put(sub, sub.getAllCountOfConditionsOf2Faces(overlapRelation)
					/ (double) sub.getParentFaceCount());
			scores3Faces.put(sub, sub.getAllCountOfConditionsOf3Faces(overlapRelation)
					/ (double) sub.getParentFaceCount());
			scores4Faces.put(sub, sub.getAllCountOfConditionsOf4Faces(overlapRelation)
					/ (double) sub.getParentFaceCount());
		}
		var sortedSubfaces = subfaces.stream()
				.sorted(Comparator
						.comparingDouble((final SubFace sub) -> scores2Faces.get(sub))
						.thenComparingDouble((final SubFace sub) -> scores3Faces.get(sub))
						.thenComparingDouble((final SubFace sub) -> scores4Faces.get(sub))
						.reversed())
				.toList();
		logger.debug("subface ordering = {}[ms]", watch.getMilliSec());

		var overlapRelations = new ConcurrentLinkedQueue<OverlapRelation>();

		watch.start();

		callCount = new AtomicInteger();
		localLayerOrderCount = new AtomicInteger();
		findAnswer(faces, sortedSubfaces, overlapRelation, overlapRelations);
		var time = watch.getMilliSec();

		logger.debug("#call = {}", callCount);
		logger.debug("#LLO = {}", localLayerOrderCount);
		logger.debug("time = {}[ms]", time);

		if (shouldLogStats) {
			logStats(sortedSubfaces, overlapRelation);
		}

		return new Result(new ArrayList<>(overlapRelations), sortedSubfaces,
				new EstimationResultRules());
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
	 * @param subfaces
	 *            the subfaces to be used.
	 * @param overlapRelation
	 *            overlap relation matrix.
	 * @param overlapRelations
	 *            an object to store the result.
	 */
	private int findAnswer(
			final List<OriFace> faces,
			final List<SubFace> subfaces,
			final OverlapRelation overlapRelation,
			final Collection<OverlapRelation> overlapRelations) {
		callCount.incrementAndGet();

		if (firstOnly && !overlapRelations.isEmpty()) {
			return 0;
		}

		if (subfaces.isEmpty()) {
			var answer = overlapRelation.clone();
			overlapRelations.add(answer);

			return 1;
		}

		SubFace sub = subfaces.get(0);

		var localLayerOrders = sub.createLocalLayerOrders(faces, overlapRelation, false);

		if (localLayerOrders == null) {
			var nextSubfaces = popAndSort(subfaces);
			return findAnswer(faces, nextSubfaces, overlapRelation, overlapRelations);
		}

		localLayerOrderCount.addAndGet(localLayerOrders.size());

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
					// therefore a face with smaller index i on local layer
					// order should be UPPER than a face with index j on local
					// layer order.
					var result = nextOverlapRelation.setUpperIfPossible(index_i, index_j);
					if (result == EstimationResult.UNFOLDABLE) {
						return;
					}
				}
			}

			sub.incrementCallCount();
			successCount.addAndGet(findAnswer(faces, nextSubfaces, nextOverlapRelation, overlapRelations));
		});

		sub.addSuccessCount(successCount.get());

		return successCount.get();
	}

	private List<SubFace> popAndSort(final List<SubFace> subfaces) {
		return subfaces.subList(1, subfaces.size()).stream()
				// parallel processing causes different score values on the same
				// subface.
				// copy the pairs of subface and score to the temporary to fix
				// the score.
				.map(subface -> new Pair<Double, SubFace>(score(subface), subface))
				// sort sublist for speeding up
				.sorted(Comparator.comparing((final Pair<Double, SubFace> pair) -> pair.v1())
						.reversed())
				.map(Pair::v2)
				.toList();
	}

	private double score(final SubFace subface) {
		return subface.getSuccessRate();
	}

	private void setConditionOf3facesToSubfaces(
			final List<StackConditionOf3Faces> conditions,
			final List<SubFace> subfaces) {

		var count = new AtomicInteger();

		subfaces.parallelStream().forEach(subface -> {
			for (var condition : conditions) {
				if (subface.isRelatedTo(condition)) {
					subface.addStackConditionOf3Faces(condition);
					count.incrementAndGet();
				}
			}
		});

		logger.debug("condtion3 set count ={}", count);
	}

	private void setConditionOf4facesToSubfaces(
			final List<StackConditionOf4Faces> conditions,
			final List<SubFace> subfaces) {

		var count = new AtomicInteger();

		subfaces.parallelStream().forEach(subface -> {
			for (var condition : conditions) {
				if (subface.isRelatedTo(condition)) {
					subface.addStackConditionOf4Faces(condition);
					count.incrementAndGet();
				}
			}
		});

		logger.debug("condtion4 set count ={}", count);
	}

	private void logStats(final List<SubFace> subfaces, final OverlapRelation overlapRelation) {

		// logConditionCountDistribution(subfaces.get(0), overlapRelation);

		var sortedSubfaces = subfaces.stream()
				.sorted(Comparator.comparing(SubFace::getSuccessRate).reversed())
				.toList();

		for (int i = 0; i < sortedSubfaces.size(); i++) {
			var subface = sortedSubfaces.get(i);

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

		faceIndexAndCountPairs.sort(Comparator.comparing(IntPair::v2));

		faceIndexAndCountPairs.forEach(pair -> logger.debug("@{} (face, #2FacesCondition) = ({}, {})", 0,
				pair.v1(),
				pair.v2()));

	}
}
