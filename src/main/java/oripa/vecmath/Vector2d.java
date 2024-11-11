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

import static java.lang.Math.*;

import java.util.List;
import java.util.Objects;

import oripa.util.MathUtil;

/**
 * Immutable 2D vector with fluent interface.
 *
 * @author OUCHI Koji
 *
 */
public class Vector2d {

	private final double x;
	private final double y;

	public static Vector2d fromArray(final double[] xy) {
		return new Vector2d(xy[0], xy[1]);
	}

	public static Vector2d fromList(final List<Double> xy) {
		return new Vector2d(xy.get(0), xy.get(1));
	}

	public static Vector2d unitVector(final double angle) {
		return new Vector2d(cos(angle), sin(angle));
	}

	public Vector2d(final double x, final double y) {
		this.x = x;
		this.y = y;
	}

	public double[] toArray() {
		return new double[] { x, y };
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getSlope() {
		return y / x;
	}

	/**
	 *
	 * @param v
	 * @return this + v
	 */
	public Vector2d add(final Vector2d v) {
		return new Vector2d(x + v.x, y + v.y);
	}

	public Vector2d subtract(final Vector2d v) {
		return new Vector2d(x - v.x, y - v.y);
	}

	public Vector2d multiply(final double a) {
		return new Vector2d(a * x, a * y);
	}

	/**
	 * Returns perpendicular vector (y, -x) directing right side of this vector
	 * in mathematical coordinate system (not screen coordinate system).
	 *
	 * @return (y, -x)
	 */
	public Vector2d getRightSidePerpendicular() {
		return new Vector2d(y, -x);
	}

	public double length() {
		return sqrt(x * x + y * y);
	}

	public double lengthSquared() {
		return x * x + y * y;
	}

	public Vector2d normalize() {
		return multiply(1.0 / length());
	}

	public double dot(final Vector2d v) {
		return x * v.x + y * v.y;
	}

	public double angle(final Vector2d v) {
		var cos = dot(v) / (length() * v.length());

		if (cos < -1.0) {
			cos = -1.0;
		}
		if (cos > 1.0) {
			cos = 1.0;
		}

		return acos(cos);
	}

	public Vector2d rotate(final double theta) {

		double cos = cos(theta);
		double sin = sin(theta);

		return new Vector2d(cos * x - sin * y, sin * x + cos * y);
	}

	/**
	 *
	 * @return arc tangent of this vector between 0 and 2 * PI
	 */
	public double ownAngle() {
		return MathUtil.normalizeAngle(atan2(y, x));
	}

	/**
	 *
	 * @param v
	 * @return Euclidean distance between the given vector and this vector.
	 */
	public double distance(final Vector2d v) {
		return sqrt((x - v.x) * (x - v.x) + (y - v.y) * (y - v.y));
	}

	public boolean isParallel(final Vector2d v) {
		double angle = angle(v);
		return angle < MathUtil.angleRadianEps() || angle > PI - MathUtil.angleRadianEps();
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof Vector2d v) {
			return x == v.x && y == v.y;
		}
		return false;

	}

	/**
	 * Returns {@code true} if the Euclidean distance is less than {@code eps}.
	 *
	 * @param v
	 * @param eps
	 * @return true if the distance between this object and the given object is
	 *         close enough.
	 */
	public boolean equals(final Vector2d v, final double eps) {
		return distance(v) < eps;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
