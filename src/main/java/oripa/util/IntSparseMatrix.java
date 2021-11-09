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
public class IntSparseMatrix implements IntMatrix {
	private final HashMap<IntPair, Integer> values;

	public final int rowCount;
	public final int columnCount;

	public IntSparseMatrix(final int rowCount, final int columnCount) {
		this.rowCount = rowCount;
		this.columnCount = columnCount;
		values = new HashMap<>();
	}

	@SuppressWarnings("unchecked")
	private IntSparseMatrix(final IntSparseMatrix m) {
		this.rowCount = m.rowCount;
		this.columnCount = m.columnCount;
		this.values = (HashMap<IntPair, Integer>) m.values.clone();
	}

	@Override
	public IntMatrix clone() {
		return new IntSparseMatrix(this);
	}

	@Override
	public void set(final int i, final int j, final int value) {
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
	public int get(final int i, final int j) {
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
