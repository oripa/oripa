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
package oripa.vecmath;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * @author OUCHI Koji
 *
 */
class Vector2dTest {

	/**
	 * Test method for {@link oripa.vecmath.Vector2d#normalize()}.
	 */
	@Test
	void testNormalization() {
		var v = new Vector2d(1, 1).normalize();

		assertEquals(Math.sqrt(2) / 2, v.getX(), 1e-8);
		assertEquals(Math.sqrt(2) / 2, v.getY(), 1e-8);
	}

}
