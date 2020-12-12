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
package oripa.domain.cptool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.geom.GeomUtil;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class OverlappingLineExtractor {
	private static final Logger logger = LoggerFactory.getLogger(OverlappingLineExtractor.class);

	private boolean isOverlap(final OriLine line0, final OriLine line1) {
		var overlapCount = GeomUtil.distinguishLineSegmentsOverlap(
				line0.p0, line0.p1, line1.p0, line1.p1);
		if (overlapCount >= 3) {
			return true;
		}
		if (overlapCount == 2) {
			if (GeomUtil.distance(line0.p0, line1.p0) < GeomUtil.EPS) {
				return false;
			} else if (GeomUtil.distance(line0.p0, line1.p1) < GeomUtil.EPS) {
				return false;
			} else if (GeomUtil.distance(line0.p1, line1.p0) < GeomUtil.EPS) {
				return false;
			} else if (GeomUtil.distance(line0.p1, line1.p1) < GeomUtil.EPS) {
				return false;
			} else {
				return true;
			}
		}

		return false;
	}

	private static class AngleLinePair {
		private final double angle;
		private final OriLine line;

		public AngleLinePair(final double angle, final OriLine line) {
			this.angle = angle;
			this.line = line;
		}

		/**
		 * @return angle
		 */
		public double getAngle() {
			return angle;
		}

		/**
		 * @return line
		 */
		public OriLine getLine() {
			return line;
		}
	}

	/**
	 * sort lines by angle for creating hash easily.
	 *
	 * @param lineArray
	 * @return
	 */
	private ArrayList<AngleLinePair> createPairs(final ArrayList<OriLine> lineArray) {
		return lineArray.parallelStream()
				.map(line -> new AngleLinePair(
						Math.atan2(line.p1.y - line.p0.y, line.p1.x - line.p0.x), line))
				.map(pair -> pair.getAngle() <= 0
						? new AngleLinePair(pair.getAngle() + Math.PI, pair.getLine())
						: pair)
				.sorted(Comparator.comparing(AngleLinePair::getAngle))
				.collect(Collectors.toCollection(() -> new ArrayList<>()));
	}

	/**
	 * make a hash table whose keys are integers for angles of lines. if angles
	 * are equal, then lines can overlap.
	 *
	 * @param angleLinePairs
	 *            should be sorted by angle and the angle should be between 0 to
	 *            PI.
	 * @return
	 */
	private ArrayList<ArrayList<AngleLinePair>> createHash(
			final ArrayList<AngleLinePair> angleLinePairs) {

		final double EPS = 1e-5;

		var pairsSplitByAngle = new ArrayList<ArrayList<AngleLinePair>>();
		int split_i = 0;
		pairsSplitByAngle.add(new ArrayList<AngleLinePair>());
		pairsSplitByAngle.get(split_i).add(angleLinePairs.get(0));
		for (int i = 1; i < angleLinePairs.size(); i++) {
			var pair1 = angleLinePairs.get(i);
			var pair0 = pairsSplitByAngle.get(split_i).get(0);
			if (pair1.getAngle() - pair0.getAngle() > EPS) {
				// a line with angle PI is the same as one with angle 0.
				if (Math.PI - pair1.getAngle() < EPS) {
					split_i = 0;
				} else {
					split_i++;
					pairsSplitByAngle.add(new ArrayList<AngleLinePair>());
				}
			}
			pairsSplitByAngle.get(split_i).add(pair1);
		}

		return pairsSplitByAngle;
	}

	/**
	 * extracts all possible overlapping lines.
	 *
	 * @param lines
	 * @return all overlapping lines.
	 */
	public Collection<OriLine> extract(final Collection<OriLine> lines) {
		var startTime = System.currentTimeMillis();

		// convert collection to ensure fast access to lines.
		var lineArray = new ArrayList<OriLine>(lines);

		// make a data structure for fast computation.
		var angleLinePairs = createPairs(lineArray);
		var pairsSplitByAngle = createHash(angleLinePairs);

		var overlappingLines = new ConcurrentLinkedDeque<OriLine>();

		// for each angle, try all pairs of lines and find overlaps.
		IntStream.range(0, pairsSplitByAngle.size()).parallel().forEach(k -> {
			var pairs = pairsSplitByAngle.get(k);
			IntStream.range(0, pairs.size()).parallel().forEach(i -> {
				var line0 = pairs.get(i).getLine();
				IntStream.range(i + 1, pairs.size()).parallel().forEach(j -> {
					var line1 = pairs.get(j).getLine();
					if (isOverlap(line0, line1)) {
						overlappingLines.add(line0);
						overlappingLines.add(line1);
					}
				});
			});
		});

		var endTime = System.currentTimeMillis();
		logger.debug("extract(): " + (endTime - startTime) + "[ms]");

		return overlappingLines;
	}

	/**
	 *
	 * @param lines
	 * @param target
	 * @return overlapping lines of {@code target}. {@code target} is not
	 *         contained.
	 */
	public Collection<OriLine> extract(final Collection<OriLine> lines, final OriLine target) {
		return lines.parallelStream()
				.filter(l -> isOverlap(l, target) && !l.equals(target))
				.collect(Collectors.toList());
	}
}
