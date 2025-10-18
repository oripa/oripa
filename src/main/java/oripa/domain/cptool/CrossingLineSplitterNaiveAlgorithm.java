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

import oripa.geom.GeomUtil;
import oripa.value.OriLine;

/**
 * Simple algorithm for splitting lines at cross points
 *
 * @author OUCHI Koji
 *
 */
public class CrossingLineSplitterNaiveAlgorithm implements CrossingLineSplitter {
	private final LineDivider lineDivider;

	public CrossingLineSplitterNaiveAlgorithm(final LineDivider lineDivider) {
		this.lineDivider = lineDivider;
	}

	@Override
	public Collection<OriLine> splitIgnoringType(final Collection<OriLine> inputLines, final double eps) {
		var splitLines = new HashSet<OriLine>();

		// every line can be split by other lines
		for (var line0 : inputLines) {
			var splits = new ArrayList<OriLine>();
			splits.add(line0);

			// test all pairs
			for (var line1 : inputLines) {
				var itr = splits.iterator();
				Collection<OriLine> result = List.of();

				// find and remove segment to split
				while (itr.hasNext()) {
					var s = itr.next();
					result = split(s, line1, eps);
					if (result.size() == 2) {
						itr.remove();
						break;
					}
				}
				// add the result of split
				splits.addAll(result);
			}

			splitLines.addAll(splits);
		}

		return splitLines;
	}

	private Collection<OriLine> split(final OriLine toSplit, final OriLine other, final double eps) {

		// close end points should remain as-is.
		if (toSplit.sharesEndPoint(other, eps)) {
			return List.of();
		}

		// split at cross point.
		var crossPointOpt = GeomUtil.getCrossPoint(toSplit, other);
		if (crossPointOpt.isPresent()) {
			var cp = crossPointOpt.get();
			if (toSplit.pointStream().anyMatch(p -> p.equals(cp, eps))) {
				return List.of();
			}
			return lineDivider.divideLine(toSplit, cp, eps);
		}

		// split at an end point of other.
		if (GeomUtil.distancePointToSegment(other.getP0(), toSplit) < eps) {
			return lineDivider.divideLine(toSplit, other.getP0(), eps);
		}
		if (GeomUtil.distancePointToSegment(other.getP1(), toSplit) < eps) {
			return lineDivider.divideLine(toSplit, other.getP1(), eps);
		}

		return List.of();
	}

}
