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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * @author OUCHI Koji
 *
 */
public class AtomicByteDenseMatrix implements ByteMatrix {
    private final AtomicInteger[][] values;

    public AtomicByteDenseMatrix(final int rowCount, final int columnCount) {
        values = new AtomicInteger[rowCount][columnCount];

        IntStream.range(0, rowCount).parallel().forEach(i -> {
            IntStream.range(0, columnCount).parallel().forEach(j -> {
                values[i][j] = new AtomicInteger();
            });
        });
    }

    @Override
    public void set(final int i, final int j, final byte value) {
        values[i][j].set(value);
    }

    @Override
    public byte get(final int i, final int j) {
        return values[i][j].byteValue();
    }

    @Override
    public ByteMatrix clone() {
        var matrix = new AtomicByteDenseMatrix(values.length, values[0].length);

        IntStream.range(0, matrix.rowCount()).parallel().forEach(i -> {
            IntStream.range(0, matrix.columnCount()).parallel().forEach(j -> {
                matrix.values[i][j].set(values[i][j].get());
            });
        });

        return matrix;
    }

    @Override
    public int rowCount() {
        return values.length;
    }

    @Override
    public int columnCount() {
        return values[0].length;
    }

}
