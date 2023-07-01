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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OriHalfedge;
import oripa.domain.fold.origeom.EstimationResult;
import oripa.domain.fold.origeom.OverlapRelation;
import oripa.domain.fold.stackcond.StackConditionOf4Faces;
import oripa.domain.fold.subface.SubFace;
import oripa.util.StopWatch;

/**
 * @author OUCHI Koji
 *
 */
class DeterministicLayerOrderEstimator {
	private static final Logger logger = LoggerFactory.getLogger(DeterministicLayerOrderEstimator.class);

	private final List<OriFace> faces;
	private final List<SubFace> subfaces;
	private final List<Integer>[][] overlappingFaceIndexIntersections;
	private final Map<OriHalfedge, Set<Integer>> faceIndicesOnHalfedge;
	private final List<StackConditionOf4Faces> condition4s;

	/**
	 *
	 * @param faces
	 * @param subFaces
	 * @param overlappingFaceIndexIntersections
	 *            [FaceID1][FaceID2]
	 * @param faceIndicesOnHalfEdge
	 *            Key: halfedge, value: set of indices of faces that are on the
	 *            halfedge.
	 *
	 */
	public DeterministicLayerOrderEstimator(
			final List<OriFace> faces,
			final List<SubFace> subFaces,
			final List<Integer>[][] overlappingFaceIndexIntersections,
			final Map<OriHalfedge, Set<Integer>> faceIndicesOnHalfedge,
			final List<StackConditionOf4Faces> condition4s) {
		this.faces = faces;
		this.subfaces = subFaces;
		this.overlappingFaceIndexIntersections = overlappingFaceIndexIntersections;
		this.faceIndicesOnHalfedge = faceIndicesOnHalfedge;
		this.condition4s = condition4s;
	}

	/**
	 * Determines overlap relations by necessary conditions.
	 *
	 * @param overlapRelation
	 *            overlap relation matrix
	 */
	public EstimationResult estimate(
			final OverlapRelation overlapRelation,
			final double eps) {

		int estimationLoopCount = 0;

		var watch = new StopWatch(true);

		logger.trace("initial state" + System.lineSeparator() + overlapRelation.toString());

		var atomicOverlapRelation = overlapRelation.cloneAtomic();

		EstimationResult changed;
		do {
			changed = EstimationResult.NOT_CHANGED;

			var result = estimateBy3FaceCover(atomicOverlapRelation);
			changed = result.or(changed);

			logger.trace("3 face cover" + System.lineSeparator() + atomicOverlapRelation.toString());

			result = estimateBy3FaceTransitiveRelation(atomicOverlapRelation);
			changed = result.or(changed);

			logger.trace("transitivity" + System.lineSeparator() + atomicOverlapRelation.toString());

			result = estimateBy4FaceStackCondition(atomicOverlapRelation);
			changed = result.or(changed);

			logger.trace("4 face condition" + System.lineSeparator() + atomicOverlapRelation.toString());

			result = estimateBy4FaceCover(atomicOverlapRelation);
			changed = result.or(changed);

			logger.trace("4 face cover" + System.lineSeparator() + atomicOverlapRelation.toString());

			if (!isCorrect(atomicOverlapRelation)) {
				return EstimationResult.UNFOLDABLE;
			}

			estimationLoopCount++;
		} while (changed == EstimationResult.CHANGED);

		atomicOverlapRelation.copyTo(overlapRelation);

		logger.debug("#estimation = {}", estimationLoopCount);
		logger.debug("estimation time {}[ms]", watch.getMilliSec());

		return changed;
	}

	private boolean isCorrect(final OverlapRelation overlapRelation) {

		// check transitivity
		var correct = IntStream.range(0, faces.size()).parallel().allMatch(i -> {
			for (int j = 0; j < faces.size(); j++) {
				if (i == j) {
					continue;
				}

				var overlaps = overlappingFaceIndexIntersections[i][j];

				for (int k : overlaps) {
					if (overlapRelation.isUpper(i, k) && overlapRelation.isUpper(k, j)) {
						if (overlapRelation.isLower(i, j)) {
							return false;
						}
					}
				}
			}
			return true;
		});

		// and others as well?

		return correct;
	}

	/**
	 * Determines overlap relation using 4-face condition.
	 *
	 * @param overlapRelation
	 * @return whether overlapRelation is changed or not, or the model is
	 *         unfoldable.
	 */
	private EstimationResult estimateBy4FaceStackCondition(
			final OverlapRelation overlapRelation) {

		var changed = EstimationResult.NOT_CHANGED;

		for (StackConditionOf4Faces cond : condition4s) {

			// if: lower1 > upper2, then: upper1 > upper2, upper1 > lower2,
			// lower1 > lower2
			if (overlapRelation.isLower(cond.lower1, cond.upper2)) {
				var result = overlapRelation.setLowerIfPossible(cond.upper1, cond.upper2);
				changed = result.or(changed);

				result = overlapRelation.setLowerIfPossible(cond.upper1, cond.lower2);
				changed = result.or(changed);

				result = overlapRelation.setLowerIfPossible(cond.lower1, cond.lower2);
				changed = result.or(changed);
			}
			// if: lower2 > upper1, then: upper2 > upper1, upper2 > lower1,
			// lower2 > lower1
			else if (overlapRelation.isLower(cond.lower2, cond.upper1)) {
				var result = overlapRelation.setLowerIfPossible(cond.upper2, cond.upper1);
				changed = result.or(changed);

				result = overlapRelation.setLowerIfPossible(cond.upper2, cond.lower1);
				changed = result.or(changed);

				result = overlapRelation.setLowerIfPossible(cond.lower2, cond.lower1);
				changed = result.or(changed);
			}
			// if: upper1 > upper2 > lower1, then: upper1 > lower2, lower2 >
			// lower1
			else if (overlapRelation.isLower(cond.upper1, cond.upper2)
					&& overlapRelation.isLower(cond.upper2, cond.lower1)) {
				var result = overlapRelation.setLowerIfPossible(cond.upper1, cond.lower2);
				changed = result.or(changed);

				result = overlapRelation.setLowerIfPossible(cond.lower2, cond.lower1);
				changed = result.or(changed);
			}
			// if: upper1 > lower2 > lower1, then: upper1 > upper2, upper2 >
			// lower1
			else if (overlapRelation.isLower(cond.upper1, cond.lower2)
					&& overlapRelation.isLower(cond.lower2, cond.lower1)) {
				var result = overlapRelation.setLowerIfPossible(cond.upper1, cond.upper2);
				changed = result.or(changed);

				result = overlapRelation.setLowerIfPossible(cond.upper2, cond.lower1);
				changed = result.or(changed);
			}
			// if: upper2 > upper1 > lower2, then: upper2 > lower1, lower1 >
			// lower2
			else if (overlapRelation.isLower(cond.upper2, cond.upper1)
					&& overlapRelation.isLower(cond.upper1, cond.lower2)) {
				var result = overlapRelation.setLowerIfPossible(cond.upper2, cond.lower1);
				changed = result.or(changed);

				result = overlapRelation.setLowerIfPossible(cond.lower1, cond.lower2);
				changed = result.or(changed);
			}
			// if: upper2 > lower1 > lower2, then: upper2 > upper1, upper1 >
			// lower2
			else if (overlapRelation.isLower(cond.upper2, cond.lower1)
					&& overlapRelation.isLower(cond.lower1, cond.lower2)) {
				var result = overlapRelation.setLowerIfPossible(cond.upper2, cond.upper1);
				changed = result.or(changed);

				result = overlapRelation.setLowerIfPossible(cond.upper1, cond.lower2);
				changed = result.or(changed);
			}

			if (changed == EstimationResult.UNFOLDABLE) {
				return EstimationResult.UNFOLDABLE;
			}

		}

		return changed;
	}

	/**
	 * If the subface a>b and b>c then a>c
	 *
	 * @param overlapRelation
	 *            overlap-relation matrix
	 * @return whether overlapRelation is changed or not, or the model is
	 *         unfoldable.
	 */
	private EstimationResult estimateBy3FaceTransitiveRelation(final OverlapRelation overlapRelation) {

		var results = new ArrayList<EstimationResult>();
		for (int i = 0; i < subfaces.size(); i++) {
			results.add(EstimationResult.NOT_CHANGED);
		}

		IntStream.range(0, subfaces.size()).parallel().forEach(i -> {
			var sub = subfaces.get(i);

			EstimationResult result;
			while (true) {
				result = updateOverlapRelationBy3FaceTransitiveRelation(sub, overlapRelation);
				if (result != EstimationResult.CHANGED) {
					break;
				}
			}
			results.set(i, result);
		});

		var changed = results.parallelStream().reduce(EstimationResult::or).get();

		return changed;
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
	private EstimationResult updateOverlapRelationBy3FaceTransitiveRelation(final SubFace sub,
			final OverlapRelation overlapRelation) {

		for (int i = 0; i < sub.getParentFaceCount(); i++) {
			for (int j = i + 1; j < sub.getParentFaceCount(); j++) {

				// search for undetermined relations
				int index_i = sub.getParentFace(i).getFaceID();
				int index_j = sub.getParentFace(j).getFaceID();

				if (overlapRelation.isNoOverlap(index_i, index_j)) {
					continue;
				}

				// Find the middle face
				for (int k = 0; k < sub.getParentFaceCount(); k++) {
					if (k == i || k == j) {
						continue;
					}

					int index_k = sub.getParentFace(k).getFaceID();

					if (overlapRelation.isUpper(index_i, index_k)
							&& overlapRelation.isUpper(index_k, index_j)) {
						return overlapRelation.setUpperIfPossible(index_i, index_j);
					}
					if (overlapRelation.isLower(index_i, index_k)
							&& overlapRelation.isLower(index_k, index_j)) {
						return overlapRelation.setLowerIfPossible(index_i, index_j);
					}
				}
			}
		}
		return EstimationResult.NOT_CHANGED;
	}

	/**
	 * If face[i] and face[j] touching edge is covered by face[k] then
	 * overlapRelation[i][k] = overlapRelation[j][k].
	 *
	 * @param overlapRelation
	 *            overlap relation matrix
	 * @return whether overlapRelation is changed or not, or the model is
	 *         unfoldable.
	 */
	private EstimationResult estimateBy3FaceCover(final OverlapRelation overlapRelation) {

		var results = new ArrayList<EstimationResult>();
		for (int i = 0; i < faces.size(); i++) {
			results.add(EstimationResult.NOT_CHANGED);
		}

		faces.parallelStream().forEach(f_i -> results.set(f_i.getFaceID(), updateBy3FaceCover(f_i, overlapRelation)));

		var changed = results.parallelStream().reduce(EstimationResult::or).get();

		return changed;
	}

	private EstimationResult updateBy3FaceCover(
			final OriFace f_i,
			final OverlapRelation overlapRelation) {
		int index_i = f_i.getFaceID();

		var changed = EstimationResult.NOT_CHANGED;
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
				if (!faceIndicesOnHalfedge.get(he).contains(index_k)) {
					continue;
				}
				if (!overlapRelation.isUndefined(index_i, index_k)) {
					var result = overlapRelation.setIfPossible(index_j, index_k, overlapRelation.get(index_i, index_k));
					changed = result.or(changed);
				}
				if (!overlapRelation.isUndefined(index_j, index_k)) {
					var result = overlapRelation.setIfPossible(index_i, index_k, overlapRelation.get(index_j, index_k));
					changed = result.or(changed);
				}

				if (changed == EstimationResult.UNFOLDABLE) {
					return EstimationResult.UNFOLDABLE;
				}

			}
		}

		return changed;
	}

	private EstimationResult estimateBy4FaceCover(final OverlapRelation overlapRelation) {

		var results = new ArrayList<EstimationResult>();
		for (int i = 0; i < faces.size(); i++) {
			results.add(EstimationResult.NOT_CHANGED);
		}

		faces.parallelStream().forEach(f_i -> results.set(f_i.getFaceID(), updateBy4FaceCover(f_i, overlapRelation)));

		var changed = results.parallelStream().reduce(EstimationResult::or).get();

		return changed;
	}

	/**
	 * Assuming 3 face cover relation is determined, this method determines
	 * X-ABC relation where X is a face independent of ABC and ABC are faces
	 * connected by edges.
	 *
	 * @param f_i
	 * @param overlapRelation
	 * @return
	 */
	private EstimationResult updateBy4FaceCover(
			final OriFace f_i,
			final OverlapRelation overlapRelation) {
		int index_i = f_i.getFaceID();

		var changed = EstimationResult.NOT_CHANGED;
		for (OriHalfedge he : f_i.halfedgeIterable()) {
			var pair = he.getPair();
			if (pair == null) {
				continue;
			}
			int index_j = pair.getFace().getFaceID();

			for (OriHalfedge he2 : pair.getFace().halfedgeIterable()) {
				var pair2 = he2.getPair();
				if (pair2 == null) {
					continue;
				}
				int index_k = pair2.getFace().getFaceID();
				if (index_i == index_k || index_j == index_k) {
					continue;
				}

				var indices = new HashSet<>(overlappingFaceIndexIntersections[index_i][index_j]);
				var indices2 = new HashSet<>(overlappingFaceIndexIntersections[index_j][index_k]);

				// apply an AND operation
				indices.retainAll(indices2);

				for (var index_x : indices) {
					if (index_i == index_x || index_j == index_x || index_k == index_x) {
						continue;
					}
					if (!faceIndicesOnHalfedge.get(he).contains(index_x)) {
						continue;
					}
					if (!faceIndicesOnHalfedge.get(he2).contains(index_x)) {
						continue;
					}

					if (!overlapRelation.isUndefined(index_i, index_x)) {
						var result = overlapRelation.setIfPossible(index_k, index_x,
								overlapRelation.get(index_i, index_x));
						changed = result.or(changed);
					}

					if (changed == EstimationResult.UNFOLDABLE) {
						return EstimationResult.UNFOLDABLE;
					}
				}
			}
		}

		return changed;
	}

}
