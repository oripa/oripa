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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

	@ParameterizedTest
	@CsvSource({
			"1, 1,   1, -1,  0",
			"1, 0,   0, 1,   0",
			"1, 2,   2, 1,   4"
	})
	void testDotProduct(double x1, double y1, double x2, double y2, double expected) {
		var v0 = new Vector2d(x1, y1);
		var v1 = new Vector2d(x2, y2);

		assertEquals(expected, v0.dot(v1), 1e-8);
	}

	@ParameterizedTest
	@CsvSource({
			"1, 0,   0,   1, 0",
			"1, 0,   45,  0.70710678, 0.70710678",
			"0.70710678, 0.70710678,   45,  0, 1",
			"0, 1,   45, -0.70710678, 0.70710678",
	})
	void testRotate(double x, double y, double angleDegrees, double expectedX, double expectedY) {
		var v = new Vector2d(x, y).rotate(Math.toRadians(angleDegrees));

		assertEquals(expectedX, v.getX(), 1e-8);
		assertEquals(expectedY, v.getY(), 1e-8);
	}

	@ParameterizedTest
	@CsvSource({
			"1, 0,   1, 0,   0",
			"1, 0,   5, 0,   0",
			"1, 0,   0, 1,   90",
			"1, 0,   0.70710678, 0.70710678,   45",
			"0.8660254038, 0.5,   0.5, 0.8660254038,   30",
	})
	void testAngle(double x1, double y1, double x2, double y2, double expectedAngleDegrees) {
		var angle1 = new Vector2d(x1, y1).angle(new Vector2d(x2, y2));
		var angle2 = new Vector2d(x2, y2).angle(new Vector2d(x1, y1));

		assertEquals(angle1, angle2, 1e-8);
		assertEquals(expectedAngleDegrees, Math.toDegrees(angle1), 1e-8);
	}

	@ParameterizedTest
	@CsvSource({
			"1, 0,   0",
			"0, 5,   90",
			"0.70710678, 0.70710678,   45",
			"0.8660254038, 0.5,   30",
	})
	void testOwnAngle(double x, double y, double expectedAngleDegrees) {
		var angle = new Vector2d(x, y).ownAngle();

		assertEquals(expectedAngleDegrees, Math.toDegrees(angle), 1e-8);
	}

}
