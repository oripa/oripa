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

import java.util.HashMap;

/**
 * Implementation of "dictionary of keys".
 *
 * @author OUCHI Koji
 *
 */
public class ByteSparseMatrix implements ByteMatrix {
    private final HashMap<IntPair, Byte> values;

    public final int rowCount;
    public final int columnCount;

    public ByteSparseMatrix(final int rowCount, final int columnCount) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        values = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    private ByteSparseMatrix(final ByteSparseMatrix m) {
        this.rowCount = m.rowCount;
        this.columnCount = m.columnCount;
        this.values = (HashMap<IntPair, Byte>) m.values.clone();
    }

    @Override
    public ByteMatrix clone() {
        return new ByteSparseMatrix(this);
    }

    @Override
    public void set(final int i, final int j, final byte value) {
        if (i < 0 || i >= rowCount || j < 0 || j >= columnCount) {
            throw new IllegalArgumentException();
        }
        if (value == 0) {
            values.remove(new IntPair(i, j));
            return;
        }
        values.put(new IntPair(i, j), value);
    }

    @Override
    public byte get(final int i, final int j) {
        if (i < 0 || i >= rowCount || j < 0 || j >= columnCount) {
            throw new IllegalArgumentException();
        }
        var value = values.get(new IntPair(i, j));
        return value == null ? 0 : value;
    }

    @Override
    public int rowCount() {
        return rowCount;
    }

    @Override
    public int columnCount() {
        return columnCount;
    }
}
