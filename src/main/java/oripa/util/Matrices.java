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
import java.util.List;

/**
 * @author OUCHI Koji
 *
 */
public class Matrices {

	/**
	 * copies {@code from} matrix to {@code to} matrix.
	 *
	 * @param from
	 * @param to
	 */
	public static void copy(final int[][] from, final int[][] to) {
		for (int i = 0; i < from.length; i++) {
			System.arraycopy(from[i], 0, to[i], 0, from[i].length);
		}
	}

	/**
	 * copies {@code from} matrix to {@code to} matrix.
	 *
	 * @param from
	 * @param to
	 */
	public static void copy(final byte[][] from, final byte[][] to) {
		for (int i = 0; i < from.length; i++) {
			System.arraycopy(from[i], 0, to[i], 0, from[i].length);
		}
	}

	/**
	 * creates a new matrix and copy the values of {@code from} to the new one.
	 *
	 * @param from
	 *            is assume to be a m x n matrix.
	 * @return deep copy of {@code from}.
	 */
	public static int[][] clone(final int[][] from) {
		var to = new int[from.length][from[0].length];
		copy(from, to);
		return to;
	}

	/**
	 * creates a new matrix and copy the values of {@code from} to the new one.
	 *
	 * @param from
	 *            is assume to be a m x n matrix.
	 * @return deep copy of {@code from}.
	 */
	public static byte[][] clone(final byte[][] from) {
		var to = new byte[from.length][from[0].length];
		copy(from, to);
		return to;
	}

	public static String toString(final int[][] mat) {
		var strings = List.of(mat).stream()
				.map(a -> String.join(",", Arrays.toString(a)))
				.toList();

		return String.join(",", strings);
	}
}
