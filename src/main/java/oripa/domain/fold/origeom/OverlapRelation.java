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
package oripa.domain.fold.origeom;

/**
 * A wrapper of integer matrix for overlap relation operations.
 *
 * @author OUCHI Koji
 *
 */
public class OverlapRelation {
	private final int[][] overlapRelation;

	public OverlapRelation(final int faceCount) {
		overlapRelation = new int[faceCount][faceCount];
	}

	public int get(final int i, final int j) {
		return overlapRelation[i][j];
	}

	public int[][] get() {
		return overlapRelation;
	}

	/**
	 * Sets {@code value} to {@code overlapRelation[i][j]}. This method sets
	 * inversion of {@code value} to {@code overlapRelation[j][i]}.
	 *
	 * @param i
	 *            row index
	 * @param j
	 *            column index
	 * @param value
	 *            a value of {@link OverlapRelationValues}
	 * @throws IllegalArgumentException
	 *             when {@code value} is not of {@link OverlapRelationValues}.
	 */
	public void set(final int i, final int j, final int value) throws IllegalArgumentException {
		overlapRelation[i][j] = value;

		switch (value) {
		case OverlapRelationValues.LOWER:
			overlapRelation[j][i] = OverlapRelationValues.UPPER;
			break;
		case OverlapRelationValues.UPPER:
			overlapRelation[j][i] = OverlapRelationValues.LOWER;
			break;
		case OverlapRelationValues.UNDEFINED:
		case OverlapRelationValues.NO_OVERLAP:
			overlapRelation[j][i] = value;
			break;

		default:
			throw new IllegalArgumentException("value argument is wrong.");
		}
	}

	/**
	 * Sets {@code OverlapRelationValues.LOWER} to
	 * {@code overlapRelation[i][j]}. This method sets
	 * {@code OverlapRelationValues.UPPER} to {@code overlapRelation[j][i]}.
	 *
	 * @param i
	 *            row index
	 * @param j
	 *            column index
	 */
	public void setLower(final int i, final int j) {
		set(i, j, OverlapRelationValues.LOWER);
	}

	/**
	 * Sets {@code OverlapRelationValues.UPPER} to
	 * {@code overlapRelation[i][j]}. This method sets
	 * {@code OverlapRelationValues.LOWER} to {@code overlapRelation[j][i]}.
	 *
	 * @param i
	 *            row index
	 * @param j
	 *            column index
	 */
	public void setUpper(final int i, final int j) {
		set(i, j, OverlapRelationValues.UPPER);
	}

	/**
	 * Sets {@code OverlapRelationValues.UNDEFINED} to
	 * {@code overlapRelation[i][j]} and {@code overlapRelation[j][i]}.
	 *
	 * @param i
	 *            row index
	 * @param j
	 *            column index
	 */
	public void setUndefined(final int i, final int j) {
		set(i, j, OverlapRelationValues.UNDEFINED);
	}

	/**
	 * Sets {@code OverlapRelationValues.NO_OVERLAP} to
	 * {@code overlapRelation[i][j]} and {@code overlapRelation[j][i]}.
	 *
	 * @param i
	 *            row index
	 * @param j
	 *            column index
	 */
	public void setNoOverlap(final int i, final int j) {
		set(i, j, OverlapRelationValues.NO_OVERLAP);
	}

	/**
	 *
	 * @return true if LOWER and UPPER are set to [i][j] and [j][i]
	 *         respectively.
	 */
	public boolean setLowerIfUndefined(final int i, final int j) {
		if (!isUndefined(i, j)) {
			return false;
		}
		overlapRelation[i][j] = OverlapRelationValues.LOWER;
		overlapRelation[j][i] = OverlapRelationValues.UPPER;
		return true;
	}

	/**
	 *
	 * @param i
	 * @param j
	 * @return {@code true} if
	 *         {@code overlapRelation[i][j] == OverlapRelationValues.LOWER}.
	 */
	public boolean isLower(final int i, final int j) {
		return overlapRelation[i][j] == OverlapRelationValues.LOWER;
	}

	public boolean isUpper(final int i, final int j) {
		return overlapRelation[i][j] == OverlapRelationValues.UPPER;
	}

	public boolean isUndefined(final int i, final int j) {
		return overlapRelation[i][j] == OverlapRelationValues.UNDEFINED;
	}

	public boolean isNoOverlap(final int i, final int j) {
		return overlapRelation[i][j] == OverlapRelationValues.NO_OVERLAP;
	}
}
