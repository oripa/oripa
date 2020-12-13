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
import java.util.function.Function;
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
	private static final double EPS = 1e-5;

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

	private static class AnalyticLine {
		private final OriLine line;
		private final double angle;
		private final double intercept;

		public AnalyticLine(final OriLine line) {
			this.line = line;

			var p0 = line.p0;
			var p1 = line.p1;

			var angle = Math.atan2(p1.y - p0.y, p1.x - p0.x);
			// limit the angle 0 to PI.
			if (angle < 0) {
				angle += Math.PI;
			}
			// a line with angle PI is the same as one with angle 0.
			if (Math.PI - angle < EPS) {
				angle = 0;
			}
			this.angle = angle;

			// vertical line doesn't have intercept.
			if (Math.abs(Math.PI / 2 - angle) < EPS) {
				intercept = Double.MAX_VALUE;
			} else {
				intercept = p0.y - (p1.y - p0.y) / (p1.x - p0.x) * p0.x;
			}
		}

		/**
		 * @return line
		 */
		public OriLine getLine() {
			return line;
		}

		/**
		 * @return angle
		 */
		public double getAngle() {
			return angle;
		}

		/**
		 * @return intercept
		 */
		public double getIntercept() {
			return intercept;
		}
	}

	/**
	 * sort lines by angle for creating hash easily.
	 *
	 * @param lineArray
	 * @return
	 */
	private ArrayList<AnalyticLine> createAnalyticLines(final ArrayList<OriLine> lineArray) {
		return lineArray.parallelStream()
				.map(line -> new AnalyticLine(line))
				.sorted(Comparator.comparing(AnalyticLine::getAngle))
				.collect(Collectors.toCollection(() -> new ArrayList<>()));
	}

	private ArrayList<ArrayList<AnalyticLine>> createHash(
			final ArrayList<AnalyticLine> sortedLines,
			final Function<AnalyticLine, Double> keyExtractor) {
		var hash = new ArrayList<ArrayList<AnalyticLine>>();

		int split_i = 0;
		hash.add(new ArrayList<AnalyticLine>());
		hash.get(split_i).add(sortedLines.get(0));
		for (int i = 1; i < sortedLines.size(); i++) {
			var line1 = sortedLines.get(i);
			var line0 = hash.get(split_i).get(0);
			if (keyExtractor.apply(line1) - keyExtractor.apply(line0) > EPS) {
				split_i++;
				hash.add(new ArrayList<AnalyticLine>());
			}
			hash.get(split_i).add(line1);
		}

		return hash;

	}

	/**
	 * make a hash table whose keys are index of lines ordered by angle. if
	 * angles are equal, then lines can overlap.
	 *
	 * @param sortedLines
	 *            should be sorted by angle.
	 * @return a hash table whose keys are index of lines ordered by angle.
	 */
	private ArrayList<ArrayList<AnalyticLine>> createAngleHash(
			final ArrayList<AnalyticLine> sortedLines) {

		return createHash(sortedLines, AnalyticLine::getAngle);
	}

	/**
	 * create hash tables by intercept for each angle.
	 *
	 * @param angleHash
	 *            a hash table created by {@link #createAngleHash(ArrayList)}.
	 * @return 3D hash table, e.g., hash[angle][intercept][lineIndex].
	 */
	private ArrayList<ArrayList<ArrayList<AnalyticLine>>> createInterceptHash(
			final ArrayList<ArrayList<AnalyticLine>> angleHash) {

		var hash = new ArrayList<ArrayList<ArrayList<AnalyticLine>>>();

		for (int i = 0; i < angleHash.size(); i++) {
			var byAngle = angleHash.get(i).stream()
					.sorted(Comparator.comparing(AnalyticLine::getIntercept))
					.collect(Collectors.toCollection(() -> new ArrayList<>()));

			var byIntercept = createHash(byAngle, AnalyticLine::getIntercept);

			hash.add(byIntercept);

		}
		return hash;
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
		var analyticLines = createAnalyticLines(lineArray);
		var angleHash = createAngleHash(analyticLines);
		var hash = createInterceptHash(angleHash);

		var overlappingLines = new ConcurrentLinkedDeque<OriLine>();

		// for each angle and intercept, try all pairs of lines and find
		// overlaps.
		IntStream.range(0, hash.size()).parallel().forEach(angle_i -> {
			var byAngle = hash.get(angle_i);
			IntStream.range(0, byAngle.size()).parallel().forEach(intercept_i -> {
				var byIntercept = byAngle.get(intercept_i);
				// for each line
				IntStream.range(0, byIntercept.size()).parallel().forEach(i -> {
					var line0 = byIntercept.get(i).getLine();
					// search another line of overlapping
					IntStream.range(i + 1, byIntercept.size()).parallel().forEach(j -> {
						var line1 = byIntercept.get(j).getLine();
						if (isOverlap(line0, line1)) {
							overlappingLines.add(line0);
							overlappingLines.add(line1);
						}
					});
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
