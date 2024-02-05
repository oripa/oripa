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
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.cptool.compgeom.AnalyticLineHashFactory;
import oripa.geom.GeomUtil;
import oripa.util.StopWatch;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class OverlappingLineExtractor {
	private static final Logger logger = LoggerFactory.getLogger(OverlappingLineExtractor.class);

	/**
	 * Returns a collection of lines grouped by their support lines, i.e., lines
	 * are in the same group if the angle and intercept are respectively the
	 * same among the lines.
	 *
	 * @param lines
	 * @param pointEps
	 * @return grouping by support line
	 */
	public Collection<Collection<OriLine>> extractOverlapsGroupedBySupport(final Collection<OriLine> lines,
			final double pointEps) {
		// make a data structure for fast computation.
		var hashFactory = new AnalyticLineHashFactory(pointEps);
		var hash = hashFactory.create(lines);

		var overlapGroups = new ConcurrentLinkedQueue<Collection<OriLine>>();

		// for each angle and intercept, try all pairs of lines and find
		// overlaps.
		IntStream.range(0, hash.size()).parallel().forEach(angle_i -> {
			var byAngle = hash.get(angle_i);
			IntStream.range(0, byAngle.size()).parallel().forEach(intercept_i -> {
				var byIntercept = byAngle.get(intercept_i);
				Collection<OriLine> overlaps = new ConcurrentLinkedQueue<OriLine>();
				// for each line
				IntStream.range(0, byIntercept.size()).parallel().forEach(i -> {
					var line0 = byIntercept.get(i).getLine();
					// search another line of overlapping
					IntStream.range(i + 1, byIntercept.size()).parallel().forEach(j -> {
						var line1 = byIntercept.get(j).getLine();
						if (GeomUtil.isOverlap(line0, line1, pointEps)) {
							overlaps.add(line0);
							overlaps.add(line1);
						}
					});
				});
				if (!overlaps.isEmpty()) {
					overlapGroups.add(overlaps);
				}
			});
		});

		return new ArrayList<>(overlapGroups);
	}

	/**
	 * extracts all possible overlapping lines.
	 *
	 * @param lines
	 * @return all overlapping lines.
	 */
	public Collection<OriLine> extract(final Collection<OriLine> lines, final double pointEps) {
		var watch = new StopWatch(true);

		var overlappingLines = extractOverlapsGroupedBySupport(lines, pointEps).stream()
				.flatMap(Collection::stream)
				.toList();

		logger.debug("extract(): " + watch.getMilliSec() + "[ms]");

		return overlappingLines;
	}

	/**
	 *
	 * @param lines
	 * @param target
	 * @return overlapping lines of {@code target}.
	 */
	public Collection<OriLine> extract(final Collection<OriLine> lines, final OriLine target, final double pointEps) {
		return lines.parallelStream()
				.filter(l -> GeomUtil.isOverlap(l, target, pointEps))
				.toList();
	}
}
