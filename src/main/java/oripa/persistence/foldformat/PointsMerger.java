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
package oripa.persistence.foldformat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import oripa.geom.GeomUtil;
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
				.filter(line -> GeomUtil.distance(line.p0, line.p1) > pointEps)
				.collect(Collectors.toList());

		final var merged = new ArrayList<OriLine>();
		cleaned.stream()
				.forEach(line -> merged.add(new OriLine(line.p0, line.p1, line.getType())));

		for (int i = 0; i < merged.size(); i++) {
			var p00 = merged.get(i).p0;
			var p01 = merged.get(i).p1;
			for (int j = i + 1; j < merged.size(); j++) {
				var p10 = merged.get(j).p0;
				var p11 = merged.get(j).p1;

				substituteToP1IfClose(p00, p10, pointEps);
				substituteToP1IfClose(p00, p11, pointEps);

				substituteToP1IfClose(p01, p10, pointEps);
				substituteToP1IfClose(p01, p11, pointEps);
			}
		}

		return merged;
	}

	private void substituteToP1IfClose(final OriPoint p0, final OriPoint p1, final double pointEps) {
		if (GeomUtil.distance(p0, p1) <= pointEps) {
			p1.x = p0.x;
			p1.y = p0.y;
		}
	}
}
