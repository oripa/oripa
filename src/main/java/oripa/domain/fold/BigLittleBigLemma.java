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
package oripa.domain.fold;

import oripa.util.rule.AbstractRule;

/**
 * For consecutive edges e1, e2, e3, e4 incident to a vertex v, we have angles
 * a1, a2, a3 which are between the edges. if a1 > a2 and a3 > a2, then the
 * assignment of e2 and e3 should differ for flat foldability.
 *
 * @author OUCHI Koji
 *
 */
public class BigLittleBigLemma extends AbstractRule<OriVertex> {
	private static final double EPS = 1e-5;

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.util.collection.Rule#holds(java.lang.Object)
	 */
	@Override
	public boolean holds(final OriVertex vertex) {

		if (vertex.edges.size() < 4) {
			return true;
		}

		var lemmaHolds = true;

		for (int i = 0; i < vertex.edges.size(); i++) {
			var e1 = vertex.getEdge(i);
			var e2 = vertex.getEdge(i + 1);
			var e3 = vertex.getEdge(i + 2);
			var e4 = vertex.getEdge(i + 3);

			if (!e2.isFoldLine() || !e3.isFoldLine()) {
				continue;
			}

			var a1 = OriGeomUtil.getAngleDifference(
					e1.oppositeVertex(vertex), vertex, e2.oppositeVertex(vertex));
			var a2 = OriGeomUtil.getAngleDifference(
					e2.oppositeVertex(vertex), vertex, e3.oppositeVertex(vertex));
			var a3 = OriGeomUtil.getAngleDifference(
					e3.oppositeVertex(vertex), vertex, e4.oppositeVertex(vertex));

			if (a1 - a2 > EPS && a3 - a2 > EPS) {
				lemmaHolds &= e2.type != e3.type;
			}
		}

		return lemmaHolds;
	}
}
