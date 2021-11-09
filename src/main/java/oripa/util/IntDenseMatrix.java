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

/**
 * @author OUCHI Koji
 *
 */
public class IntDenseMatrix implements IntMatrix {
	private final int[][] values;

	public IntDenseMatrix(final int rowCount, final int columnCount) {
		values = new int[rowCount][columnCount];
	}

	private IntDenseMatrix(final int[][] values) {
		this.values = Matrices.clone(values);
	}

	@Override
	public void set(final int i, final int j, final int value) {
		values[i][j] = value;
	}

	@Override
	public int get(final int i, final int j) {
		return values[i][j];
	}

	@Override
	public IntMatrix clone() {
		return new IntDenseMatrix(values);
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
