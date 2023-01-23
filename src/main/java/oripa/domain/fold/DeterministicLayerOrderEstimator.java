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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OriHalfedge;
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

	enum EstimationResult {
		NOT_CHANGED(0),
		CHANGED(1),
		UNFOLDABLE(2);

		private EstimationResult(final int order) {
			this.order = order;
		}

		private final int order;

		private int getOrder() {
			return order;
		}

		public static EstimationResult selectStronger(final EstimationResult a, final EstimationResult b) {
			return Stream.of(a, b)
					.sorted(Comparator.comparing(EstimationResult::getOrder).reversed())
					.findFirst()
					.get();
		}
	}

	private final List<OriFace> faces;
	private final List<SubFace> subFaces;
	private final List<Integer>[][] overlappingFaceIndexIntersections;
	private final Map<OriHalfedge, Set<Integer>> faceIndicesOnHalfedge;
	private final Set<StackConditionOf4Faces> condition4s;

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
			final Set<StackConditionOf4Faces> condition4s) {
		this.faces = faces;
		this.subFaces = subFaces;
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

		EstimationResult changed;
		do {
			changed = EstimationResult.NOT_CHANGED;

			var result = estimateBy3FaceCover(overlapRelation);
			changed = EstimationResult.selectStronger(result, changed);

			result = estimateBy3FaceTransitiveRelation(overlapRelation);
			changed = EstimationResult.selectStronger(result, changed);

			result = estimateBy4FaceStackCondition(overlapRelation);
			changed = EstimationResult.selectStronger(result, changed);

			estimationLoopCount++;
		} while (changed == EstimationResult.CHANGED);
		logger.debug("#estimation = {}", estimationLoopCount);
		logger.debug("estimation time {}[ms]", watch.getMilliSec());

		return changed;
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
				var result = setLowerIfPossible(overlapRelation, cond.upper1, cond.upper2);
				changed = EstimationResult.selectStronger(result, changed);

				result = setLowerIfPossible(overlapRelation, cond.upper1, cond.lower2);
				changed = EstimationResult.selectStronger(result, changed);

				result = setLowerIfPossible(overlapRelation, cond.lower1, cond.lower2);
				changed = EstimationResult.selectStronger(result, changed);
			}

			// if: lower2 > upper1, then: upper2 > upper1, upper2 > lower1,
			// lower2 > lower1
			if (overlapRelation.isLower(cond.lower2, cond.upper1)) {
				var result = setLowerIfPossible(overlapRelation, cond.upper2, cond.upper1);
				changed = EstimationResult.selectStronger(result, changed);

				result = setLowerIfPossible(overlapRelation, cond.upper2, cond.lower1);
				changed = EstimationResult.selectStronger(result, changed);

				result = setLowerIfPossible(overlapRelation, cond.lower2, cond.lower1);
				changed = EstimationResult.selectStronger(result, changed);
			}

			// if: upper1 > upper2 > lower1, then: upper1 > lower2, lower2 >
			// lower1
			if (overlapRelation.isLower(cond.upper1, cond.upper2)
					&& overlapRelation.isLower(cond.upper2, cond.lower1)) {
				var result = setLowerIfPossible(overlapRelation, cond.upper1, cond.lower2);
				changed = EstimationResult.selectStronger(result, changed);

				result = setLowerIfPossible(overlapRelation, cond.lower2, cond.lower1);
				changed = EstimationResult.selectStronger(result, changed);
			}

			// if: upper1 > lower2 > lower1, then: upper1 > upper2, upper2 >
			// lower1
			if (overlapRelation.isLower(cond.upper1, cond.lower2)
					&& overlapRelation.isLower(cond.lower2, cond.lower1)) {
				var result = setLowerIfPossible(overlapRelation, cond.upper1, cond.upper2);
				changed = EstimationResult.selectStronger(result, changed);

				result = setLowerIfPossible(overlapRelation, cond.upper2, cond.lower1);
				changed = EstimationResult.selectStronger(result, changed);
			}

			// if: upper2 > upper1 > lower2, then: upper2 > lower1, lower1 >
			// lower2
			if (overlapRelation.isLower(cond.upper2, cond.upper1)
					&& overlapRelation.isLower(cond.upper1, cond.lower2)) {
				var result = setLowerIfPossible(overlapRelation, cond.upper2, cond.lower1);
				changed = EstimationResult.selectStronger(result, changed);

				result = setLowerIfPossible(overlapRelation, cond.lower1, cond.lower2);
				changed = EstimationResult.selectStronger(result, changed);
			}

			// if: upper2 > lower1 > lower2, then: upper2 > upper1, upper1 >
			// lower2
			if (overlapRelation.isLower(cond.upper2, cond.lower1)
					&& overlapRelation.isLower(cond.lower1, cond.lower2)) {
				var result = setLowerIfPossible(overlapRelation, cond.upper2, cond.upper1);
				changed = EstimationResult.selectStronger(result, changed);

				result = setLowerIfPossible(overlapRelation, cond.upper1, cond.lower2);
				changed = EstimationResult.selectStronger(result, changed);
			}

			if (changed == EstimationResult.UNFOLDABLE) {
				return EstimationResult.UNFOLDABLE;
			}

		}

		return changed;
	}

	private EstimationResult setLowerIfPossible(final OverlapRelation overlapRelation, final int i, final int j) {

		if (overlapRelation.setLowerIfUndefined(i, j)) {
			return EstimationResult.CHANGED;
		}
		if (overlapRelation.isUpper(i, j)) {
			// conflict.
			return EstimationResult.UNFOLDABLE;
		}

		return EstimationResult.NOT_CHANGED;
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
		var changed = EstimationResult.NOT_CHANGED;

		for (SubFace sub : subFaces) {
			while (updateOverlapRelationBy3FaceTransitiveRelation(sub, overlapRelation)) {
				changed = EstimationResult.CHANGED;
			}
		}
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
	 * @param overlapRelation
	 *            overlap relation matrix
	 * @return whether overlapRelation is changed or not, or the model is
	 *         unfoldable.
	 */
	private EstimationResult estimateBy3FaceCover(final OverlapRelation overlapRelation) {

		var changed = EstimationResult.NOT_CHANGED;
		for (OriFace f_i : faces) {
			var updateResult = updateBy3FaceCover(f_i, overlapRelation);
			changed = EstimationResult.selectStronger(updateResult, changed);
			if (changed == EstimationResult.UNFOLDABLE) {
				return EstimationResult.UNFOLDABLE;
			}
		}

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
					var result = setIfPossible(
							overlapRelation, index_j, index_k, overlapRelation.get(index_i, index_k));
					changed = EstimationResult.selectStronger(result, changed);
				}
				if (!overlapRelation.isUndefined(index_j, index_k)) {
					var result = setIfPossible(
							overlapRelation, index_i, index_k, overlapRelation.get(index_j, index_k));
					changed = EstimationResult.selectStronger(result, changed);
				}

				if (changed == EstimationResult.UNFOLDABLE) {
					return EstimationResult.UNFOLDABLE;
				}

			}
		}

		return changed;
	}

	private EstimationResult setIfPossible(final OverlapRelation overlapRelation,
			final int i, final int j, final byte value) {

		if (overlapRelation.setIfUndefined(i, j, value)) {
			return EstimationResult.CHANGED;
		}
		if (overlapRelation.get(i, j) != value) {
			// conflict.
			return EstimationResult.UNFOLDABLE;
		}

		return EstimationResult.NOT_CHANGED;
	}
}
