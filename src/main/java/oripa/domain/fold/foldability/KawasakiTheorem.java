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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.fold.halfedge.OriVertex;
import oripa.domain.fold.origeom.OriGeomUtil;
import oripa.util.MathUtil;
import oripa.util.rule.AbstractRule;

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
	 * @param vertex
	 * @return true if the {@code vertex} passes the theorem test.
	 */
	@Override
	public boolean holds(final OriVertex vertex) {

		if (!vertex.isInsideOfPaper()) {
			return true;
		}

		double oddSum = 0;

		for (int i = 0; i < vertex.edgeCount(); i++) {
			double angle = OriGeomUtil.getAngleDifference(vertex, i);

			if (i % 2 == 0) {
				oddSum += angle;
			}
		}

		final double eps = MathUtil.angleRadianEps();
		if (!MathUtil.areEqual(oddSum, Math.PI, eps)) {
			logger.trace("edge angle sum invalid");
			return false;
		}

		return true;
	}

}
