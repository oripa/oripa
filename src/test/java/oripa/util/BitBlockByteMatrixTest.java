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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author OUCHI Koji
 *
 */
class BitBlockByteMatrixTest {
    static Logger logger = LoggerFactory.getLogger(BitBlockByteMatrixTest.class);

    BitBlockByteMatrix matrix;

    @BeforeEach
    void setUp() {
        matrix = new BitBlockByteMatrix(200, 100, 2);
    }

    @Test
    void testSetAndGet() {
        assertSetAndGet(0, 0, (byte) 0x01);
        assertSetAndGet(0, 63, (byte) 0x02);
        assertSetAndGet(1, 2, (byte) 0x03);
        assertSetAndGet(199, 99, (byte) 0x02);

    }

    void assertSetAndGet(final int i, final int j, final byte value) {
        matrix.set(i, j, value);
        logger.debug(matrix.toBinaryString());
        assertEquals(value, matrix.get(i, j));
    }
}
