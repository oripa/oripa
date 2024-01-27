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
package oripa.domain.cptool;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import oripa.value.OriLine;
import oripa.value.OriPoint;

/**
 * @author OUCHI Koji
 *
 */
class LineDividerTest {

	static final double EPS = 1e-4;

	/**
	 * Test method for
	 * {@link oripa.domain.cptool.LineDivider#divideLine(oripa.value.OriLine, javax.vecmath.Vector2d, double)}.
	 */
	@Test
	void testDivideLine_shouldBeDivided() {
		var line = new OriLine(0, 0, 100, 100, OriLine.Type.MOUNTAIN);
		var dividingPoint = new OriPoint(50, 50);

		var divider = new LineDivider();

		var divided = divider.divideLine(line, dividingPoint, EPS);

		assertFalse(divided.isEmpty());
		assertEquals(2, divided.size());
	}

	@Test
	void testDivideLine_shouldNotBeDivided_closeToEndPoint() {
		var line = new OriLine(0, 0, 100, 100, OriLine.Type.MOUNTAIN);
		var dividingPoint = new OriPoint(1e-5, 1e-5);

		var divider = new LineDivider();

		var divided = divider.divideLine(line, dividingPoint, EPS);

		assertTrue(divided.isEmpty());
	}

	@Test
	void testDivideLine_shouldNotBeDivided_farFromLine() {
		var line = new OriLine(0, 0, 100, 100, OriLine.Type.MOUNTAIN);
		var dividingPoint = new OriPoint(30, 40);

		var divider = new LineDivider();

		var divided = divider.divideLine(line, dividingPoint, EPS);

		assertTrue(divided.isEmpty());
	}

}
