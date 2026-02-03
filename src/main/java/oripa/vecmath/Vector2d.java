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

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import oripa.util.MathUtil;

/**
 * Immutable 2D vector with fluent interface.
 *
 * @author OUCHI Koji
 *
 */
public class Vector2d {
    // true values
    private final double x;
    private final double y;

    // for robust normalization which also extracts length.
    private final double length;
    private final double xNormal;
    private final double yNormal;

    public static Vector2d fromArray(final double[] xy) {
        return new Vector2d(xy[0], xy[1]);
    }

    public static Vector2d fromList(final List<Double> xy) {
        return new Vector2d(xy.get(0), xy.get(1));
    }

    public static Vector2d unitVector(final double angle) {
        var x = cos(angle);
        var y = sin(angle);
        return new Vector2d(x, y, 1, x, y);
    }

    public Vector2d(final double x, final double y) {
        this.x = x;
        this.y = y;

        var v = normalize(x, y);
        this.length = v[0];
        this.xNormal = v[1];
        this.yNormal = v[2];
    }

    private Vector2d(final double x, final double y, final double length, final double xNormal, final double yNormal) {
        this.x = x;
        this.y = y;

        this.length = length;
        this.xNormal = xNormal;
        this.yNormal = yNormal;

    }

    public <T extends Vector2d> Optional<T> findNearest(final Collection<T> vertices) {
        T nearest = null;
        double distance = Double.MAX_VALUE;
        for (var v : vertices) {
            if (this.equals(v)) {
                return Optional.of(v);
            }
            var d = v.distance(this);
            if (distance > d) {
                nearest = v;
                distance = d;
            }
        }

        return Optional.ofNullable(nearest);
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
        return yNormal / xNormal;
    }

    /**
     *
     * @param v
     * @return this + v
     */
    public Vector2d add(final Vector2d v) {
        double x_ = x + v.x;
        double y_ = y + v.y;

        return new Vector2d(x_, y_);
    }

    public Vector2d subtract(final Vector2d v) {
        return add(v.multiply(-1.0));
    }

    public Vector2d multiply(final double a) {
        double sign = Math.signum(a);
        return new Vector2d(a * x, a * y, length * Math.abs(a), sign * xNormal, sign * yNormal);
    }

    /**
     * Returns perpendicular vector (y, -x) directing right side of this vector
     * in mathematical coordinate system (not screen coordinate system).
     *
     * @return (y, -x)
     */
    public Vector2d getRightSidePerpendicular() {
        return new Vector2d(y, -x, length, yNormal, -xNormal);
    }

    public double length() {
        return length;
    }

    public double lengthSquared() {
        return length * length;
    }

    private double[] normalize(final double x, final double y) {
        // simple robust computation, algorithm 2 of
        // https://arxiv.org/abs/1606.06508
        var ax = Math.abs(x);
        var ay = Math.abs(y);

        if (ax > ay) {
            var y_ = y / x;
            var h = Math.sqrt(1 + y_ * y_);
            var x_normal = Math.signum(x) / h;
            var r = ax * h;
            return new double[] { r, x_normal, y_ * x_normal };
        } else {
            if (ay == 0) {
                return new double[] { 0, 0, 0 };
            }
            var x_ = x / y;
            var h = Math.sqrt(x_ * x_ + 1);
            var y_normal = Math.signum(y) / h;
            var r = ay * h;
            return new double[] { r, x_ * y_normal, y_normal };

        }
    }

    public Vector2d normalize() {
        return new Vector2d(xNormal, yNormal, 1, xNormal, yNormal);
    }

    public double dot(final Vector2d v) {

        return length * v.length * (xNormal * v.xNormal + yNormal * v.yNormal);
    }

    public double angle(final Vector2d v) {
        // normalized dot product
        var cos = xNormal * v.xNormal + yNormal * v.yNormal;

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

        double xNormal_ = cos * xNormal - sin * yNormal;
        double yNormal_ = sin * xNormal + cos * yNormal;

        return new Vector2d(
                length * xNormal_,
                length * yNormal_,
                length,
                xNormal_,
                yNormal_);
    }

    /**
     *
     * @return arc tangent of this vector between 0 and 2 * PI
     */
    public double ownAngle() {
        return MathUtil.normalizeAngle(atan2(yNormal, xNormal));
    }

    /**
     *
     * @param v
     * @return Euclidean distance between the given vector and this vector.
     */
    public double distance(final Vector2d v) {
        var dx = x - v.x;
        var dy = y - v.y;

        return normalize(dx, dy)[0];
    }

    public boolean isParallel(final Vector2d v) {
        double angle = angle(v);
        return angle < MathUtil.angleRadianEps() || angle > PI - MathUtil.angleRadianEps();
    }

    public double crossProductZ(final Vector2d v) {
        return length * v.length * (xNormal * v.yNormal - yNormal * v.xNormal);

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
