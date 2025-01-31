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
package oripa.geom;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
class TwoEarTriangulationTest {

	@Test
	void testNonConvex() {
		var polygon = new Polygon(List.of(
				new Vector2d(0, 0),
				new Vector2d(2, 0),
				new Vector2d(2, 2),
				new Vector2d(1, 2),
				new Vector2d(1, 1),
				new Vector2d(0, 1)));

		var triangles = new TwoEarTriangulation().triangulate(polygon, 1e-6);

		assertEquals(4, triangles.size());
		for (var t : triangles) {
			assertEquals(3, t.verticesCount());
		}
	}

	@Test
	void testPointsOnOneEdge() {
		var polygon = new Polygon(List.of(
				new Vector2d(0, 0),
				new Vector2d(0, 1),
				new Vector2d(0, 2),
				new Vector2d(0, 3),
				new Vector2d(0, 4),
				new Vector2d(0, 5),
				new Vector2d(-1, 5),
				new Vector2d(-1, 4)));

		var triangles = new TwoEarTriangulation().triangulate(polygon, 1e-6);

		assertEquals(6, triangles.size());
		for (var t : triangles) {
			assertEquals(3, t.verticesCount());
		}

	}

}
