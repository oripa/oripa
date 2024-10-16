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
package oripa.vecmath;

import oripa.util.MathUtil;

import java.util.Optional;

/**
 * @author OUCHI Koji
 *
 */
public class Matrix2d {
	private final double[][] m = new double[2][2];

	public Matrix2d(final double v00, final double v01, final double v10, final double v11) {
		m[0][0] = v00;
		m[0][1] = v01;
		m[1][0] = v10;
		m[1][1] = v11;
	}

	public Matrix2d(final double[][] matrix) {
		this(matrix[0][0], matrix[0][1], matrix[1][0], matrix[1][1]);
	}

	public Vector2d product(final Vector2d v) {
		var p = rowVector(0);
		var q = rowVector(1);

		return new Vector2d(p.dot(v), q.dot(v));
	}

	Vector2d rowVector(final int i) {
		return new Vector2d(m[i][0], m[i][1]);
	}

	public double determinant() {
		return m[0][0] * m[1][1] - m[0][1] * m[1][0];
	}

	public boolean isRegular() {
		var p = rowVector(0).normalize();
		var q = rowVector(1).normalize();

		final double eps = MathUtil.normalizedValueEps();

		return Math.abs(p.getX() * q.getY() - p.getY() * q.getX()) > eps;
	}

	public Optional<Matrix2d> inverse() {
		if (!isRegular()) {
			return Optional.empty();
		}

		var inverse = new double[2][2];

		var det = determinant();

		inverse[0][0] = m[1][1] / det;
		inverse[0][1] = -m[0][1] / det;
		inverse[1][0] = -m[1][0] / det;
		inverse[1][1] = m[0][0] / det;

		return Optional.of(new Matrix2d(inverse));
	}

}
