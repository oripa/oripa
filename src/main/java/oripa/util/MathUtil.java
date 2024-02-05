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
public class MathUtil {

	public static double normalizeAngle(final double angle) {
		final double TWO_PI = 2 * Math.PI;
		return (TWO_PI + angle) % TWO_PI;
	}

	public static double angleRadianEps() {
		return 1e-5;
	}

	/**
	 * Returns {@code true} if the difference is smaller than
	 * {@link MathUtil#angleRadianEps()}.
	 *
	 * @param a0
	 * @param a1
	 */
	public static boolean areRadianEqual(final double a0, final double a1) {
		return areEqual(a0, a1, angleRadianEps());
	}

	public static double angleDegreeEps() {
		return Math.toDegrees(angleRadianEps());
	}

	public static double normalizedValueEps() {
		return 1e-6;
	}

	/**
	 * Returns {@code true} if the given values are equal allowing error
	 * {@code eps}. The test is exclusive.
	 *
	 * @param v0
	 * @param v1
	 * @param eps
	 * @return {@code true} if the difference is between {@code -eps} and
	 *         {@code eps}.
	 */
	public static boolean areEqual(final double v0, final double v1, final double eps) {
		return Math.abs(v1 - v0) < eps;
	}

	/**
	 * Returns {@code true} if the given values are equal allowing error
	 * {@code eps}. The test is inclusive.
	 *
	 * @param v0
	 * @param v1
	 * @param eps
	 * @return {@code true} if the difference is between {@code -eps} and
	 *         {@code eps}.
	 */
	public static boolean areEqualInclusive(final double v0, final double v1, final double eps) {
		return Math.abs(v1 - v0) <= eps;
	}

}
