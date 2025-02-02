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
import java.util.TreeSet;

import oripa.util.collection.CollectionUtil;
import oripa.value.OriLine;
import oripa.value.OriPoint;

/**
 * @author OUCHI Koji
 *
 */
public class PointsMerger {

	/**
	 *
	 * @param lines
	 * @return new lines with merged vertices. very short lines are removed from
	 *         the result.
	 */
	public Collection<OriLine> mergeClosePoints(final Collection<OriLine> lines, final double pointEps) {
		var cleaned = lines.stream()
				.filter(line -> line.length() >= pointEps)
				.toList();

		var pointSet = new TreeSet<OriPoint>(cleaned.stream()
				.flatMap(OriLine::oriPointStream)
				.toList());

		final var merged = new ArrayList<OriLine>();

		for (var line : cleaned) {
			var p0 = find(pointSet, line.getOriPoint0(), pointEps);
			var p1 = find(pointSet, line.getOriPoint1(), pointEps);

			merged.add(new OriLine(p0, p1, line.getType()));
		}

		return merged;
	}

	private OriPoint find(final TreeSet<OriPoint> pointSet, final OriPoint p, final double pointEps) {
		var boundSet = CollectionUtil.rangeSetInclusive(
				pointSet,
				new OriPoint(p.getX() - pointEps, p.getY() - pointEps),
				new OriPoint(p.getX() + pointEps, p.getY() + pointEps));

		var neighbors = boundSet.stream()
				.filter(point -> point.equals(p, pointEps))
				.sorted()
				.toList();

		return neighbors.get(0);
	}
}
