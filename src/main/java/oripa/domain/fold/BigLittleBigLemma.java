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

import java.util.List;

import javax.vecmath.Vector2d;

import oripa.util.collection.AbstractRule;
import oripa.value.OriLine;

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

		Vector2d p = vertex.p;

		if (vertex.edges.size() < 4) {
			return true;
		}

		var lemmaHolds = true;

		for (int i = 0; i < vertex.edges.size(); i++) {
			var e1 = getEdge(vertex.edges, i);
			var e2 = getEdge(vertex.edges, i + 1);
			var e3 = getEdge(vertex.edges, i + 2);
			var e4 = getEdge(vertex.edges, i + 3);

			if (!isFoldLine(e2) || !isFoldLine(e3)) {
				continue;
			}

			var a1 = getAngle(e1.oppositeVertex(vertex), vertex, e2.oppositeVertex(vertex));
			var a2 = getAngle(e2.oppositeVertex(vertex), vertex, e3.oppositeVertex(vertex));
			var a3 = getAngle(e3.oppositeVertex(vertex), vertex, e4.oppositeVertex(vertex));

			if (a1 - a2 > EPS && a3 - a2 > EPS) {
				lemmaHolds &= e2.type != e3.type;
			}
		}

		return lemmaHolds;
	}

	private boolean isFoldLine(final OriEdge e) {
		return e.type == OriLine.Type.RIDGE.toInt() || e.type == OriLine.Type.VALLEY.toInt();
	}

	private OriEdge getEdge(final List<OriEdge> edges, final int index) {
		return edges.get((index + edges.size()) % edges.size());
	}

	private double getAngle(final OriVertex v1, final OriVertex v2, final OriVertex v3) {
		var preP = new Vector2d(v1.p);
		var p = new Vector2d(v2.p);
		var nxtP = new Vector2d(v3.p);

		nxtP.sub(p);
		preP.sub(p);

		return preP.angle(nxtP);
	}
}
