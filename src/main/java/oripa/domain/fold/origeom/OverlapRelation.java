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

import oripa.util.IntDenseMatrix;
import oripa.util.IntMatrix;
import oripa.util.IntSparseMatrix;

/**
 * A wrapper of integer matrix for overlap relation operations.
 *
 * @author OUCHI Koji
 *
 */
public class OverlapRelation {
	private IntMatrix overlapRelation;

	/**
	 * Internally creates a n x n matrix where n is the given {@code faceCount}.
	 *
	 * @param faceCount
	 *            the number of faces of the model.
	 */
	public OverlapRelation(final int faceCount) {
		overlapRelation = new IntDenseMatrix(faceCount, faceCount);
	}

	private OverlapRelation(final IntMatrix mat) {
		overlapRelation = mat.clone();
	}

	/**
	 * @return deep copy of this instance.
	 */
	@Override
	public OverlapRelation clone() {
		return new OverlapRelation(overlapRelation);
	}

	public void switchToSparseMatrix() {
		var sparse = new IntSparseMatrix(overlapRelation.rowCount(), overlapRelation.columnCount());
		for (int i = 0; i < overlapRelation.rowCount(); i++) {
			for (int j = 0; j < overlapRelation.columnCount(); j++) {
				sparse.set(i, j, overlapRelation.get(i, j));
			}
		}
		overlapRelation = sparse;
	}

	/**
	 *
	 * @param i
	 *            row index
	 * @param j
	 *            column index
	 * @return [i][j] value.
	 */
	public int get(final int i, final int j) {
		return overlapRelation.get(i, j);
	}

	/**
	 * @return the n of n x n matrix.
	 */
	public int getSize() {
		return overlapRelation.rowCount();
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
		overlapRelation.set(i, j, value);

		switch (value) {
		case OverlapRelationValues.LOWER:
			overlapRelation.set(j, i, OverlapRelationValues.UPPER);
			break;
		case OverlapRelationValues.UPPER:
			overlapRelation.set(j, i, OverlapRelationValues.LOWER);
			break;
		case OverlapRelationValues.UNDEFINED:
		case OverlapRelationValues.NO_OVERLAP:
			overlapRelation.set(j, i, value);
			break;

		default:
			throw new IllegalArgumentException("value argument is wrong.");
		}
	}

	/**
	 * Sets {@link OverlapRelationValues#LOWER} to
	 * {@code overlapRelation[i][j]}. This method sets
	 * {@link OverlapRelationValues#UPPER} to {@code overlapRelation[j][i]}.
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
	 * Sets {@link OverlapRelationValues#UPPER} to
	 * {@code overlapRelation[i][j]}. This method sets
	 * {@link OverlapRelationValues#LOWER} to {@code overlapRelation[j][i]}.
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
	 * Sets {@link OverlapRelationValues#UNDEFINED} to
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
	 * Sets {@link OverlapRelationValues#NO_OVERLAP} to
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
		overlapRelation.set(i, j, OverlapRelationValues.LOWER);
		overlapRelation.set(j, i, OverlapRelationValues.UPPER);
		return true;
	}

	/**
	 *
	 * @return {@code true} if {@code overlapRelation[i][j]} is equal to
	 *         {@link OverlapRelationValues#LOWER}.
	 */
	public boolean isLower(final int i, final int j) {
		return overlapRelation.get(i, j) == OverlapRelationValues.LOWER;
	}

	/**
	 *
	 * @return {@code true} if {@code overlapRelation[i][j]} is equal to
	 *         {@link OverlapRelationValues#UPPER}.
	 */
	public boolean isUpper(final int i, final int j) {
		return overlapRelation.get(i, j) == OverlapRelationValues.UPPER;
	}

	/**
	 *
	 * @return {@code true} if {@code overlapRelation[i][j]} is equal to
	 *         {@link OverlapRelationValues#UNDEFINED}.
	 */
	public boolean isUndefined(final int i, final int j) {
		return overlapRelation.get(i, j) == OverlapRelationValues.UNDEFINED;
	}

	/**
	 *
	 * @return {@code true} if {@code overlapRelation[i][j]} is equal to
	 *         {@link OverlapRelationValues#NO_OVERLAP}.
	 */
	public boolean isNoOverlap(final int i, final int j) {
		return overlapRelation.get(i, j) == OverlapRelationValues.NO_OVERLAP;
	}
}
