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
package oripa.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * @author OUCHI Koji
 *
 */
class BitArrayTest {

	@Test
	void test32BitsGetSet() {
		var bits = new BitArray(32);

		bits.setOne(31);
		assertTrue(bits.get(31));

		bits.setOne(0);
		assertTrue(bits.get(0));
	}

	@Test
	void test33BitsGetSet() {
		var bits = new BitArray(33);

		bits.setOne(32);
		assertTrue(bits.get(32));

		bits.setZero(32);
		bits.setOne(31);
		assertTrue(bits.get(31));
	}

}
