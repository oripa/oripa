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

import javax.vecmath.Vector2d;

import oripa.domain.fold.foldability.FoldabilityChecker;
import oripa.domain.fold.halfedge.OriEdge;
import oripa.domain.fold.halfedge.OriVertex;

/**
 * @author OUCHI Koji
 *
 */
public class FoldableLineAngleSuggester {

	public Collection<Double> suggest(final OriVertex vertex) {
		var type = new MaekawaTheoremSuggester().suggest(vertex);

		if (type == null) {
			return List.of();
		}

		var kawasakiAngles = new KawasakiTheoremSuggester().suggest(vertex);

		var foldableAngles = new ArrayList<Double>();

		var checker = new FoldabilityChecker();

		for (var angle : kawasakiAngles) {
			var sp = vertex.getPositionBeforeFolding();
			var sv = new OriVertex(sp);

			vertex.edgeStream().forEach(e -> sv.addEdge(new OriEdge(sv, e.oppositeVertex(vertex), e.getType())));

			var ep = new Vector2d(sp);
			ep.add(new Vector2d(Math.cos(angle), Math.sin(angle)));

			var ev = new OriVertex(ep);
			var edge = new OriEdge(sv, ev, type.toInt());
			sv.addEdge(edge);

			if (checker.testLocalFlatFoldability(sv)) {
				foldableAngles.add(angle);
			}
		}

		return foldableAngles;
	}
}
