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

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * @author OUCHI Koji
 *
 */
class OriLineTest {

	@Test
	void testHashCode() {
		var line1 = new OriLine(0, 1, 2, 3, OriLine.Type.VALLEY);
		var line2 = new OriLine(0, 1, 2, 3, OriLine.Type.MOUNTAIN);

		assertTrue(line1.equals(line2));
		assertTrue(line1.hashCode() == line2.hashCode());

		// test reversed direction
		line2.p0.set(2, 3);
		line2.p1.set(0, 1);

		assertTrue(line1.equals(line2));
		assertTrue(line1.hashCode() == line2.hashCode());

	}

}
