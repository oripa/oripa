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
package oripa.persistence.foldformat;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * @author OUCHI Koji
 *
 */
class GeometryTest {

	@Test
	void testSortByAngle() {
		var coords = List.of(
				List.of(0.0, 0.0),
				List.of(1.0, 0.0),
				List.of(1.0, 1.0),
				List.of(0.0, 1.0),
				List.of(2.0, 1.0));

		var v = 2;
		var verticesAround = List.of(0, 1, 3, 4); // around v

		var sorted = Geometry.sortByAngle(v, verticesAround, coords);

		assertEquals(0, sorted.get(0));
		assertEquals(1, sorted.get(1));
		assertEquals(4, sorted.get(2));
		assertEquals(3, sorted.get(3));
	}

}
