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

import java.util.Optional;

import oripa.util.MathUtil;

/**
 * @author OUCHI Koji
 *
 */
public class Matrix2d {
	private final double v00;
	private final double v01;
	private final double v10;
	private final double v11;

	public Matrix2d(final double v00, final double v01, final double v10, final double v11) {
		this.v00 = v00;
		this.v01 = v01;
		this.v10 = v10;
		this.v11 = v11;
	}

	public Vector2d product(final Vector2d v) {
		var p = rowVector(0);
		var q = rowVector(1);

		return new Vector2d(p.dot(v), q.dot(v));
	}

	Vector2d rowVector(final int i) {
		if (i == 0) {
			return new Vector2d(v00, v01);
		} else if (i == 1) {
			return new Vector2d(v10, v11);
		} else {
			throw new IllegalArgumentException("This matrix only has 2 rows");
		}
	}

	public double determinant() {
		return v00 * v11 - v01 * v10;
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

		var det = determinant();

		return Optional.of(new Matrix2d(
				v11 / det,
				-v01 / det,
				-v10 / det,
				v00 / det));
	}

}
