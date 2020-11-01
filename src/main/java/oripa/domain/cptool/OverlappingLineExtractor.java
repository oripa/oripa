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

import oripa.geom.GeomUtil;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class OverlappingLineExtractor {

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

	public Collection<OriLine> extract(final Collection<OriLine> lines) {
		var lineArray = new OriLine[lines.size()];
		lineArray = lines.toArray(lineArray);

		var overlappingLines = new ArrayList<OriLine>();

		for (int i = 0; i < lineArray.length; i++) {
			var line0 = lineArray[i];
			for (int j = i + 1; j < lineArray.length; j++) {
				var line1 = lineArray[j];
				if (isOverlap(line0, line1)) {
					overlappingLines.add(line0);
					overlappingLines.add(line1);
				}
			}
		}

		return overlappingLines;
	}
}
