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
package oripa.domain.fold.foldability;

import oripa.domain.fold.halfedge.OriVertex;
import oripa.util.MathUtil;
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
	private static final double EPS = MathUtil.angleRadianEps();

	public BigLittleBigLemma() {
		super("Big-little-big");
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.util.collection.Rule#holds(java.lang.Object)
	 */
	@Override
	public boolean holds(final OriVertex vertex) {
		var edgeNum = vertex.edgeCount();
		if (edgeNum < 4) {
			return true;
		}

		var lemmaHolds = true;

		for (int i = 0; i < edgeNum; i++) {
			var e2 = vertex.getEdge(i + 1);
			var e3 = vertex.getEdge(i + 2);

			if (!e2.isAssigned() || !e3.isAssigned()) {
				continue;
			}

			var a1 = vertex.getAngleDifference(i);
			var a2 = vertex.getAngleDifference(i + 1);
			var a3 = vertex.getAngleDifference(i + 2);

			if (a1 - a2 > EPS && a3 - a2 > EPS) {
				lemmaHolds &= e2.getType() != e3.getType();
			}
		}

		return lemmaHolds;
	}
}
