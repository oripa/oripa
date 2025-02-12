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

import java.util.List;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author OUCHI Koji
 *
 */
public class MathUtil {
	private static Logger logger = LoggerFactory.getLogger(MathUtil.class);

	/**
	 *
	 * @param angle
	 * @return converted angle whose range is between 0 and 2 * pi.
	 */
	public static double normalizeAngle(final double angle) {
		final double TWO_PI = 2 * Math.PI;
		return (TWO_PI + angle) % TWO_PI;
	}

	/**
	 * For error limit of a radian value between 0 and 2 * pi.
	 */
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

	/**
	 * For error limit of a value between 0 and 1.
	 */
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
	 * A shorthand for {@code areEqual(v, 0, eps)}.
	 *
	 * @param v
	 * @param eps
	 * @return
	 */
	public static boolean isZero(final double v, final double eps) {
		return areEqual(v, 0, eps);
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

	public static double newtonMethod(final Function<Double, Double> f, final double initialX, final double delta,
			final double eps) throws IllegalStateException {

		double x = initialX;
		for (int i = 0; i < 50; i++) {
			double f_x = f.apply(x);

			if (isZero(f_x, eps)) {
				logger.debug("answer found @ {}: f_x = {}, x = {}", i, f_x, x);
				return x;
			}

			x = x - f_x * delta / (f.apply(x + delta) - f_x);

			if (i % 10 == 0) {
				logger.trace("f_x = {}, x = {}", f_x, x);
			}
		}

		throw new IllegalStateException("approximation failed.");
	}

	/**
	 * Neumaier Sum from https://en.wikipedia.org/wiki/Kahan_summation_algorithm
	 *
	 * @param values
	 * @return
	 */
	public static double preciseSum(final List<Double> values) {
		double sum = 0.0;
		// A running compensation for lost low-order bits.
		double c = 0.0;

		for (double v : values) {
			double t = sum + v;
			if (Math.abs(sum) >= Math.abs(v)) {
				c += (sum - t) + v; // If sum is bigger, low-order digits of
									// input[i] are lost.
			} else {
				c += (v - t) + sum; // Else low-order digits of sum are lost.
			}
			sum = t;
		}

		return sum + c;
	}

	public static double preciseSum(final double a, final double b) {
		return preciseSum(List.of(a, b));
	}

	public static double preciseAddWithFactor(final double ra, final double a, final double rb, final double b) {
		// there may be some other trick to reduce errors but I couldn't figure
		// out it...

		return preciseSum(ra * a, rb * b);
	}

}
