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
package oripa.domain.fold.rule;

import javax.vecmath.Vector2d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.fold.OriEdge;
import oripa.domain.fold.OriVertex;
import oripa.util.collection.AbstractRule;
import oripa.value.OriLine;

/**
 * Kawasaki Theorem
 *
 * Assume there is a vertex v on a paper. When lines l_i are given for i=1...2n
 * such that v is one of the end points for all l_i and the numbering is
 * clockwise order, foldable origami satisfies the following condition:
 *
 * a_1 + a_3 + ... = a_2 + a_4 + ... = PI
 *
 * where a_i is the angle in radian between l_i and l_{i+1}, especially a_n is
 * the angle between l_1 and l_n.
 *
 * @author Koji
 *
 */
public class KawasakiTheorem extends AbstractRule<OriVertex> {
	private static final Logger logger = LoggerFactory.getLogger(KawasakiTheorem.class);

	/**
	 *
	 * @param vertices
	 * @return true if all vertices are passes the theorem test.
	 */
	@Override
	public boolean holds(final OriVertex vertex) {

		Vector2d p = vertex.p;
		double oddSum = 0;

		for (int i = 0; i < vertex.edges.size(); i++) {
			OriEdge e = vertex.edges.get(i);

			// corner does not need test
			if (e.type == OriLine.Type.CUT.toInt()) {
				return true;
			}

			Vector2d preP = new Vector2d(vertex.edges.get(i).oppositeVertex(vertex).p);
			Vector2d nxtP = new Vector2d(
					vertex.edges.get((i + 1) % vertex.edges.size()).oppositeVertex(vertex).p);

			nxtP.sub(p);
			preP.sub(p);

			if (i % 2 == 0) {
				oddSum += preP.angle(nxtP);
			} else {
			}
		}

		final double oneDegreeInRad = Math.PI / 180;
		final double eps = oneDegreeInRad / 2;
		if (Math.abs(oddSum - Math.PI) > eps) {
			logger.info("edge angle sum invalid");
			return false;
		}

		return true;
	}

}
