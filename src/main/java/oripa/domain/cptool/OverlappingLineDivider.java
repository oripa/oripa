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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import oripa.geom.GeomUtil;
import oripa.util.collection.CollectionUtil;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
public class OverlappingLineDivider {

	private final SequentialLineFactory sequentialLineFactory = new SequentialLineFactory();

	private final PointSorter pointSorter = new PointSorter();

	/**
	 * Divides each element of {@code lines} at end point of
	 * {@code dividerLines}'s element if the lines overlap.
	 *
	 * @param dividerLines
	 *            lines dividing elements of {@code lines}
	 * @param lines
	 *            lines to be divided
	 * @return division result.
	 */
	public Collection<OriLine> divideIfOverlap(final Collection<OriLine> dividerLines, final Collection<OriLine> lines,
			final double pointEps) {
		var extractor = new OverlappingLineExtractor();

		var allLines = new HashSet<OriLine>(dividerLines);
		allLines.addAll(lines);

		var overlapGroups = extractor.extractOverlapsGroupedBySupport(allLines, pointEps);

		Set<OriLine> dividerLineSet = new HashSet<>(dividerLines);
		Set<OriLine> lineSet = CollectionUtil.newConcurrentHashSet(lines);

		overlapGroups.parallelStream().forEach(overlaps -> {
			var dividerOverlaps = overlaps.stream()
					.filter(ov -> dividerLineSet.contains(ov))
					.collect(Collectors.toCollection(HashSet::new));

			var lineOverlaps = overlaps.stream()
					.filter(ov -> !dividerOverlaps.contains(ov))
					.collect(Collectors.toCollection(HashSet::new));

			lineSet.removeAll(lineOverlaps);

			// Cannot be done in parallel since two or more dividers might
			// divides the same line.
			dividerOverlaps.forEach(divider -> divideLinesIfOverlap(divider, lineOverlaps, pointEps));

			lineSet.addAll(lineOverlaps);
		});

		return lineSet;
	}

	/**
	 *
	 * @param dividerLine
	 * @param lines
	 *            will be updated as each line is divided by the end point(s) of
	 *            {@code dividerLine}
	 * @param pointEps
	 */
	private void divideLinesIfOverlap(final OriLine dividerLine, final Set<OriLine> lines,
			final double pointEps) {

		Set<OriLine> targettedLines = CollectionUtil.newConcurrentHashSet();
		Set<OriLine> splitLines = CollectionUtil.newConcurrentHashSet();

		lines.parallelStream()
				.forEach(line -> {
					var splitPoints = new ArrayList<Vector2d>();

					int overlapCount = GeomUtil.distinguishLineSegmentsOverlap(dividerLine, line, pointEps);

					switch (overlapCount) {
					case 2, 3:
						splitPoints.addAll(createSplitPoints(line, dividerLine.getP0(), pointEps));
						splitPoints.addAll(createSplitPoints(line, dividerLine.getP1(), pointEps));
						break;
					default:
						return;
					}

					var sortedPoints = pointSorter.sortPointsOnLine(splitPoints, line);

					targettedLines.add(line);
					splitLines.addAll(
							sequentialLineFactory.createSequentialLines(sortedPoints, line.getType(), pointEps));
				});

		lines.removeAll(targettedLines);
		lines.addAll(splitLines);
	}

	private List<Vector2d> createSplitPoints(final OriLine line, final Vector2d p, final double pointEps) {
		var points = List.of(line.getP0(), line.getP1());

		// is close to segment?
		if (GeomUtil.distancePointToSegment(p, line) > pointEps) {
			return points;
		}

		if (points.stream().anyMatch(q -> GeomUtil.distance(p, q) >= pointEps)) {
			return Stream.concat(points.stream(), Stream.of(p)).toList();
		}

		return points;
	}

}
