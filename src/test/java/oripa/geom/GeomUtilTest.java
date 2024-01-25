/*
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package oripa.geom;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import oripa.vecmath.Vector2d;

class GeomUtilTest {

	@Test
	void test_getCrossPoint_Line_Line() {
		var l0 = new Line(new Vector2d(1, 0), new Vector2d(-1, 1));
		var l1 = new Line(new Vector2d(0, 0), new Vector2d(1, 1));

		var cp = GeomUtil.getCrossPoint(l0, l1).get();

		assertEquals(0.5, cp.getX(), 1e-8);
		assertEquals(0.5, cp.getY(), 1e-8);

	}

	@Test
	void test_getBisectorVec() {
		var v0 = new Vector2d(1, 0);
		var v1 = new Vector2d(0, 0);
		var v2 = new Vector2d(0, 1);

		var bisector = GeomUtil.getBisectorVec(v0, v1, v2);

		assertEquals(1.0, bisector.getX(), 1e-8);
		assertEquals(1.0, bisector.getY(), 1e-8);
	}
}