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
package oripa.value;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import oripa.geom.Segment;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
class OriLineTest {

	@Test
	void testHashCode() {
		var line1 = new OriLine(0, 1, 2, 3, OriLine.Type.VALLEY);
		var line2 = new OriLine(0, 1, 2, 3, OriLine.Type.MOUNTAIN);

		assertNotEquals(line1, line2);
		assertNotEquals(line1.hashCode(), line2.hashCode());

		// test reversed direction
		var line2Reversed = new OriLine(2, 3, 0, 1, OriLine.Type.VALLEY);

		assertEquals(line1, line2Reversed);
		assertEquals(line1.hashCode(), line2Reversed.hashCode());

	}

	@Test
	void testAffineValues() {
		Segment segment = new OriLine(new Vector2d(0, 2), new Vector2d(1, 0), OriLine.Type.MOUNTAIN);

		assertEquals(1, segment.getAffineYValueAt(0.5), 1e-8);
		assertEquals(0.5, segment.getAffineXValueAt(1), 1e-8);
	}
}
