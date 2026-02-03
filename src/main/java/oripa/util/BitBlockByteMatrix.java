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

import java.util.Arrays;

/**
 * @author OUCHI Koji
 *
 */
public class BitBlockByteMatrix implements ByteMatrix {

    private final long[][] array;
    private final int rowCount, columnCount;
    private final int blockLength;

    private static final boolean[] BLOCK_LENGTH_AVAILABLE = new boolean[] {
            false, // 0
            true, // 1
            true, // 2
            false, // 3
            true, // 4
            false, // 5
            false, // 6
            false, // 7
            true, // 8
    };

    private final long mask;

    public BitBlockByteMatrix(final int rowCount, final int columnCount, final int blockLength) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        if (blockLength > 8) {
            throw new IllegalArgumentException("block length should be less than or equal to 8.");
        }
        if (!BLOCK_LENGTH_AVAILABLE[blockLength]) {
            throw new IllegalArgumentException("block length should be 1,2,4 or 8.");
        }

        this.blockLength = blockLength;

        int necessaryBits = columnCount * blockLength;
        array = new long[rowCount][necessaryBits / 64 + (necessaryBits % 64 == 0 ? 0 : 1)];

        mask = 0xFF >>> (8 - blockLength);
    }

    @Override
    public ByteMatrix clone() {
        var c = new BitBlockByteMatrix(rowCount, columnCount, blockLength);
        Matrices.copy(array, c.array);

        return c;
    }

    @Override
    public int rowCount() {
        return rowCount;
    }

    @Override
    public int columnCount() {
        return columnCount;
    }

    private int getBitLength(final int j) {
        return (j + 1) * blockLength;
    }

    private int getArrayIndex(final int j) {
        int bitLength = getBitLength(j);
        return bitLength / 64 + (bitLength % 64 == 0 ? 0 : 1) - 1;
    }

    private int getLastBitLength(final int j) {
        return (getBitLength(j) - blockLength) % 64;
    }

    @Override
    public byte get(final int i, final int j) {
        int arrayIndex = getArrayIndex(j);
        int lastBitLength = getLastBitLength(j);

        long arrayValue = array[i][arrayIndex];
        long extracted = (arrayValue & (mask << lastBitLength)) >>> lastBitLength;

        return (byte) extracted;
    }

    @Override
    public void set(final int i, final int j, final byte value) {
        int arrayIndex = getArrayIndex(j);
        int lastBitLength = getLastBitLength(j);

        long arrayValue = array[i][arrayIndex];

        // clear
        arrayValue &= ~(mask << lastBitLength);

        // set
        arrayValue += (value & mask) << lastBitLength;

        array[i][arrayIndex] = arrayValue;
    }

    public String toBinaryString() {
        var strings = Arrays.stream(array)
                .flatMapToLong(Arrays::stream)
                .mapToObj(Long::toBinaryString)
                .toList();

        return String.join(" ", strings);
    }
}
