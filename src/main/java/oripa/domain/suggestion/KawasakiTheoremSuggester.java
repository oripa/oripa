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
package oripa.domain.suggestion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import oripa.domain.cptool.PseudoRayFactory;
import oripa.domain.fold.halfedge.OriVertex;
import oripa.domain.fold.origeom.OriGeomUtil;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class KawasakiTheoremSuggester {

	private final double paperSize;

	public KawasakiTheoremSuggester(final double paperSize) {
		this.paperSize = paperSize;
	}

	public Collection<OriLine> suggest(final OriVertex vertex) {

		int angleCount = vertex.edgeCount();

		if (angleCount % 2 == 0) {
			return List.of();
		}

		var suggestions = new ArrayList<OriLine>();
		var type = guessType(vertex);

		if (type == null) {
			return List.of();
		}

		double evenSum = 0;

		for (int i = 0; i < angleCount; i += 2) {
			evenSum += OriGeomUtil.getAngleDifference(vertex, i);
		}

		double oddSum = 2 * Math.PI - evenSum;

		evenSum -= OriGeomUtil.getAngleDifference(vertex, 0);

		// computes as if indices are re-assigned as current i-th is the new
		// 0th.
		for (var i = 0; i < angleCount; i++) {
			double theta = OriGeomUtil.getAngleDifference(vertex, i);
			double t = evenSum + theta - Math.PI;

			if (evenSum < Math.PI && oddSum < Math.PI) {
				var baseAngle = vertex.getEdge(i).getAngle(vertex);
				var factory = new PseudoRayFactory();
				var segment = factory.create(vertex.getPositionBeforeFolding(), baseAngle + t, paperSize);
				suggestions.add(new OriLine(segment, type));
			}

			var tmpEvenSum = evenSum;
			evenSum = oddSum + theta - OriGeomUtil.getAngleDifference(vertex, i + 1);
			oddSum = tmpEvenSum;
		}

		return suggestions;
	}

	private OriLine.Type guessType(final OriVertex vertex) {
		double edgeCount = vertex.edgeCount();
		int mountainCount = 0;
		int valleyCount = 0;

		for (int i = 0; i < edgeCount; i++) {
			if (vertex.getEdge(i).isMountain()) {
				mountainCount++;
			} else {
				valleyCount++;
			}
		}
		int diff = mountainCount - valleyCount;
		switch (diff) {
		case 3:
			return OriLine.Type.VALLEY;
		case -3:
			return OriLine.Type.MOUNTAIN;
		case 1:
			return OriLine.Type.MOUNTAIN;
		case -1:
			return OriLine.Type.VALLEY;
		default:
			return null;
		}
	}
}
