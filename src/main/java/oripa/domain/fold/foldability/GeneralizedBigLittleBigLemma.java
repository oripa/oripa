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
package oripa.domain.fold.foldability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.fold.halfedge.OriEdge;
import oripa.domain.fold.halfedge.OriVertex;
import oripa.domain.fold.origeom.OriGeomUtil;
import oripa.util.collection.CollectionUtil;
import oripa.util.rule.AbstractRule;

/**
 * For a sequence of equal angles, the number of M and V on the bounding edges
 * should be equal if the number of such edges is even, and otherwise |#M - #V|
 * = 1.
 *
 * @author OUCHI Koji
 *
 */
public class GeneralizedBigLittleBigLemma extends AbstractRule<OriVertex> {
	private static final Logger logger = LoggerFactory
			.getLogger(GeneralizedBigLittleBigLemma.class);

	private static final double EPS = 1e-5;

	private class Range {
		public int begin;
		public int end;

		public Range(final int b, final int e) {
			begin = b;
			end = e;
		}
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.util.collection.Rule#holds(java.lang.Object)
	 */
	@Override
	public boolean holds(final OriVertex vertex) {
		if (vertex.edgeStream().anyMatch(e -> e.isBoundary())) {
			return true;
		}

		var ranges = findMinimalAngleSequences(vertex);

		for (var range : ranges) {
			logger.trace("range =[" + range.begin + ", " + range.end + ")");
			int valleyCount = 0;

			BiFunction<OriEdge, Integer, Integer> incrementIfValley = (edge, count) -> {
				return (edge.isValley()) ? count + 1 : count;
			};

			for (int i = range.begin; i != range.end; i++) {
				valleyCount = incrementIfValley.apply(vertex.getEdge(i), valleyCount);
			}
			valleyCount = incrementIfValley.apply(vertex.getEdge(range.end), valleyCount);

			int edgeCount = range.end - range.begin + 1;
			int mountainCount = edgeCount - valleyCount;

			logger.trace("#edge in the range = " + edgeCount);
			logger.trace("#V in the range = " + valleyCount);
			logger.trace("#M in the range = " + mountainCount);

			if (edgeCount % 2 == 0) {
				if (valleyCount != mountainCount) {
					logger.trace("failed (even #edge)");
					return false;
				}
			} else if (Math.abs(valleyCount - mountainCount) != 1) {
				logger.trace("failed (odd #edge)");
				return false;
			}
		}

		return true;
	}

	private Collection<Range> findMinimalAngleSequences(final OriVertex vertex) {
		var ranges = new ArrayList<Range>();
		var edgeNum = vertex.edgeCount();

		final List<Double> angles = IntStream.range(0, edgeNum)
				.mapToObj(i -> OriGeomUtil.getAngleDifference(
						vertex.getOppositeVertex(i),
						vertex,
						vertex.getOppositeVertex(i + 1)))
				.collect(Collectors.toList());

		logger.trace("angles = "
				+ String.join(",", angles.stream()
						.map(a -> Double.toString(Math.toDegrees(a)))
						.collect(Collectors.toList())));

		Function<Integer, Double> getAngle = i -> CollectionUtil.getCircular(angles, i);

		var maxBounds = new ArrayList<Integer>();
		for (int i = 0; i < edgeNum; i++) {
			if (getAngle.apply(i + 1) - getAngle.apply(i) > EPS) {
				maxBounds.add(i + 1);
			}
		}

		for (int i = 0; i < maxBounds.size(); i++) {
			int maxBound = maxBounds.get(i);
			int minBound = maxBound - 1;

			while (Math.abs(getAngle.apply(minBound) - getAngle.apply(maxBound - 1)) <= EPS) {
				// minBound should not exceeds the previous maxBound
				if (minBound == CollectionUtil.getCircular(maxBounds, i - 1)) {
					break;
				}
				minBound--;
			}
			// minBound angle is larger than the sequential equal angles.
			if (getAngle.apply(minBound) - getAngle.apply(maxBound - 1) > EPS) {
				ranges.add(new Range(minBound + 1, maxBound));
			}
		}

		return ranges;
	}
}
