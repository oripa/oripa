/*
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package oripa.geom;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;

import oripa.util.ClosedRange;
import oripa.util.MathUtil;
import oripa.value.CalculationResource;
import oripa.vecmath.Matrix2d;
import oripa.vecmath.Vector2d;

public class GeomUtil {

    /**
     * Discouraged to use in order to make eps configurable.
     *
     * @return eps for point equality measured by distance.
     */
    public static double pointEps() {
        return CalculationResource.POINT_EPS;
    }

    /**
     * this method returns the count of end points on other segment for each
     * segment.
     * <ul>
     * <li>If the count is 0 or 1, then they are not overlapping.</li>
     * <li>If the count is 2, then they partially overlap.</li>
     * <li>If the count is 3, then one segment overlaps entirely and an end
     * point is shared with the other segment.</li>
     * <li>If the count is 4, then the two segments are equal.</li>
     * </ul>
     */
    public static int distinguishSegmentsOverlap(final Segment seg0, final Segment seg1, final double pointEps) {
        if (!seg0.getLine().isParallel(seg1.getLine())) {
            return 0;
        }

        int cnt = 0;
        if (distancePointToSegment(seg0.getP0(), seg1) < pointEps) {
            cnt++;
        }
        if (distancePointToSegment(seg0.getP1(), seg1) < pointEps) {
            cnt++;
        }
        if (distancePointToSegment(seg1.getP0(), seg0) < pointEps) {
            cnt++;
        }
        if (distancePointToSegment(seg1.getP1(), seg0) < pointEps) {
            cnt++;
        }
        return cnt;
    }

    /**
     *
     * @param seg0
     * @param seg1
     * @return {@code true} if end points of both lines are on the other line.
     *         Note that this method returns {@code true} if the lines touch at
     *         end points and does not share other part.
     */
    public static boolean isRelaxedOverlap(final Segment seg0, final Segment seg1, final double pointEps) {
        var overlapCount = distinguishSegmentsOverlap(seg0, seg1, pointEps);
        return overlapCount >= 2;
    }

    /**
     *
     * @param ray
     * @param seg
     * @param pointEps
     * @return {@code true} if end points of the ray and segment are on the
     *         other. Note that this method returns {@code true} if the they
     *         touch at end points and does not share other part.
     */
    public static boolean isRelaxedOverlap(final Ray ray, final Segment seg, final double pointEps) {
        return ray.getDirection().isParallel(seg.getLine().getDirection())
                && seg.pointStream().anyMatch(p -> distancePointToRay(p, ray) < pointEps);
    }

    public static boolean isOverlap(final Line line, final Segment seg, final double pointEps) {
        return line.equals(seg.getLine(), pointEps);
    }

    /**
     *
     * @param seg0
     * @param seg1
     * @param pointEps
     * @return {@code true} if end points of both lines are on the other line.
     *         Note that this method returns {@code false} if the lines touch at
     *         end points and does not share other part.
     */
    public static boolean isOverlap(final Segment seg0, final Segment seg1, final double pointEps) {
        var overlapCount = distinguishSegmentsOverlap(seg0, seg1, pointEps);
        if (overlapCount >= 3) {
            return true;
        }
        if (overlapCount == 2) {
            // any end point should not touch other end point.
            return !seg0.sharesEndPoint(seg1, pointEps);
        }

        return false;
    }

    /**
     * Returns a segment vertical to the given segment, whose end points are the
     * cross point and given {@code v}.
     *
     * @param v
     * @param segment
     * @return a vertical segment whose end points are the cross point and
     *         {@code v}.
     */
    public static Segment getVerticalSegment(final Vector2d v, final Segment segment) {

        var sp = segment.getP0();
        var ep = segment.getP1();

        var ds = v.distance(sp);
        var de = v.distance(ep);
        var sp_ = ds > de ? sp : ep;
        var ep_ = ds > de ? ep : sp;

        var dir = ep_.subtract(sp_).normalize();

        double t = computeParameterForNearestPointToLine(v, sp_, dir);

        var cp = sp_.add(dir.multiply(t));

        return new Segment(cp, v);
    }

    public static Vector2d getIncenter(final Vector2d v0, final Vector2d v1, final Vector2d v2) {
        double l0 = v1.distance(v2);
        double l1 = v0.distance(v2);
        double l2 = v0.distance(v1);

        // c = (v0 * l0 + v1 * l1 + v2 * l2) / (l0 + l1 + l2);
        var c = v0.multiply(l0).add(v1.multiply(l1)).add(v2.multiply(l2)).multiply(1.0 / (l0 + l1 + l2));

        return c;
    }

    /**
     * Computes the angle bisector direction of v0 - v1 and v2 - v1.
     *
     * @param v0
     *            the end point of the direction vector.
     * @param v1
     *            the shared start point of the direction vectors.
     * @param v2
     *            the end point of another direction vector.
     * @return Direction vector. Not normalized.
     */
    private static Vector2d getBisectorVec(final Vector2d v0, final Vector2d v1, final Vector2d v2) {

        var v0_v1 = v0.subtract(v1).normalize();
        var v2_v1 = v2.subtract(v1).normalize();

        double angle = v0_v1.angle(v2_v1);

        if (MathUtil.areRadianEqual(angle, Math.PI)) {
            double bisectorAngle = v0_v1.ownAngle() + Math.PI / 2;
            return Vector2d.unitVector(bisectorAngle);
        }

        return v0_v1.add(v2_v1);
    }

    /**
     * Computes the angle bisector line of v0 - v1 and v2 - v1.
     *
     * @param v0
     *            the end point of the direction vector.
     * @param v1
     *            the shared start point of the direction vectors.
     * @param v2
     *            the end point of another direction vector.
     * @return angle bisector line on v1.
     */
    public static Line getBisectorLine(final Vector2d v0, final Vector2d v1, final Vector2d v2) {
        return new Line(v1, getBisectorVec(v0, v1, v2));
    }

    /**
     *
     * @param p
     *            point to be projected.
     * @param sp
     *            start point of symmetry base line vector.
     * @param ep
     *            end point of symmetry base line vector.
     * @return symmetric of point p through the (sp, ep) axis
     */
    public static Vector2d getSymmetricPoint(final Vector2d p, final Vector2d sp,
            final Vector2d ep) {
        var ds = p.distance(sp);
        var de = p.distance(ep);
        var sp_ = ds > de ? sp : ep;
        var ep_ = ds > de ? ep : sp;

        Vector2d cp = getNearestPointToLine(p, sp_, ep_);
        return cp.multiply(2).subtract(p);
    }

    /**
     * Returns the intersection of the semi-straight line and the line segment.
     * Empty if not intersect.
     *
     * @param ray
     * @param seg
     * @return
     */
    public static Optional<Vector2d> getCrossPoint(final Ray ray, final Segment seg) {
        var p0 = ray.getEndPoint();
        var p1 = p0.add(ray.getDirection());

        var segP0 = seg.getP0();
        var segP1 = seg.getP1();

        return solveRayAndSegmentCrossPointVectorEquation(p0, p1, segP0, segP1)
                .map(parameters -> computeDividingPoint(parameters.get(1), segP0, segP1));
    }

    private static Optional<List<Double>> solveRayAndSegmentCrossPointVectorEquation(
            final Vector2d p0, final Vector2d p1,
            final Vector2d segP0, final Vector2d segP1) {
        final double eps = MathUtil.normalizedValueEps();
        return solveCrossPointVectorEquation(p0, p1, segP0, segP1,
                (s, t) -> new ClosedRange(0, 1, eps).includes(t) && s >= -eps);
    }

    /**
     * Returns the intersection of a straight line and a segment. Empty if not
     * intersect.
     *
     * @param line
     * @param seg
     * @return
     */
    public static Optional<Vector2d> getCrossPoint(final Line line, final Segment seg) {
        var p0 = line.getPoint();
        var p1 = p0.add(line.getDirection());

        var segP0 = seg.getP0();
        var segP1 = seg.getP1();

        return solveLineAndSegmentCrossPointVectorEquation(p0, p1, segP0, segP1)
                .map(parameters -> computeDividingPoint(parameters.get(1), segP0, segP1));

    }

    /**
     *
     * @param p0
     *            point on the line
     * @param p1
     *            point on the line
     * @param segP0
     *            end point of the segment
     * @param segP1
     *            end point of the segment
     * @return
     */
    private static Optional<List<Double>> solveLineAndSegmentCrossPointVectorEquation(
            final Vector2d p0, final Vector2d p1,
            final Vector2d segP0, final Vector2d segP1) {
        final double eps = MathUtil.normalizedValueEps();
        return solveCrossPointVectorEquation(p0, p1, segP0, segP1,
                (s, t) -> new ClosedRange(0, 1, eps).includes(t));
    }

    /**
     * Compute the intersection of straight lines
     *
     * @param l0
     * @param l1
     * @return
     */
    public static Optional<Vector2d> getCrossPoint(final Line l0, final Line l1) {
        var p0 = l0.getPoint();
        var p1 = p0.add(l0.getDirection());

        var q0 = l1.getPoint();
        var q1 = q0.add(l1.getDirection());

        return solveLinesCrossPointVectorEquation(p0, p1, q0, q1)
                .map(parameters -> computeDividingPoint(parameters.get(1), q0, q1));
    }

    private static Optional<List<Double>> solveLinesCrossPointVectorEquation(final Vector2d p0, final Vector2d p1,
            final Vector2d q0, final Vector2d q1) {
        return solveCrossPointVectorEquation(p0, p1, q0, q1, (s, t) -> true);
    }

    public static Segment getSegmentByValue(final Vector2d sv, final double length,
            final double deg_angle) {
        double rad_angle = Math.toRadians(deg_angle);
        var dir = Vector2d.unitVector(rad_angle).multiply(length);
        var ev = sv.add(dir);

        return new Segment(sv, ev);
    }

    /**
     * p and sp should be far enough because they will be used in subtraction.
     *
     * @param p
     *            point
     * @param sp
     *            start point of line vector
     * @param unitLineDir
     *            unit line vector = (ep - sp).normalize()
     * @return t = |p - sp| * cos(\theta) where theta is the angle between p -
     *         sp and unitLineVector
     */
    private static double computeParameterForNearestPointToLine(
            final Vector2d p, final Vector2d sp, final Vector2d unitLineDir) {

        var sub0 = p.subtract(sp);

        // forcing to use normalize() is good becouse it does no arithmetics,
        // which should be robust.

        // direction of the line
        // t = |sub0| * cos(\theta) / |dir| = |sub0|*cos(\theta)
        return unitLineDir.dot(sub0);
    }

    private static Vector2d getNearestPointToLine(
            final Vector2d p, final Vector2d sp, final Vector2d ep) {

        var ds = p.distance(sp);
        var de = p.distance(ep);
        var sp_ = ds > de ? sp : ep;
        var ep_ = ds > de ? ep : sp;

        // direction of the line
        var dir = ep_.subtract(sp_).normalize();

        double t = computeParameterForNearestPointToLine(p, sp_, dir);

        return sp_.add(dir.multiply(t));
    }

    public static double distancePointToSegment(final Vector2d p, final Segment segment) {
        return p.distance(getNearestPointToSegment(p, segment));
    }

    public static double distancePointToSegment(final Vector2d p, final Vector2d sp,
            final Vector2d ep) {

        return p.distance(getNearestPointToSegment(p, new Segment(sp, ep)));
    }

    /**
     * Computes the nearest point.
     *
     * @param p
     * @param segment
     * @param nearestPoint
     *            stores returned value.
     * @return
     */
    public static Vector2d getNearestPointToSegment(final Vector2d p, final Segment segment) {

        var sp = segment.getP0();
        var ep = segment.getP1();
        var length = segment.length();

        var ds = p.distance(sp);
        var de = p.distance(ep);
        var sp_ = ds > de ? sp : ep;
        var ep_ = ds > de ? ep : sp;

        // direction of the line
        var dir = ep_.subtract(sp_).normalize();

        double t = computeParameterForNearestPointToLine(p, sp_, dir);

        if (t <= 0.0) {
            return sp_;
        } else if (t >= length) {
            return ep_;
        } else {
            return sp_.add(dir.multiply(t));
        }
    }

    public static double distancePointToLine(final Vector2d p, final Line line) {
        var sp = line.getPoint();
        var ep = sp.add(line.getDirection());

        var ds = p.distance(sp);
        var de = p.distance(ep);
        var sp_ = ds > de ? sp : ep;
        var ep_ = ds > de ? ep : sp;

        return p.distance(getNearestPointToLine(p, sp_, ep_));
    }

    public static double distancePointToRay(final Vector2d p, final Ray ray) {
        return p.distance(getNearestPointToRay(p, ray));
    }

    public static Vector2d getNearestPointToRay(final Vector2d p, final Ray ray) {
        var sp = ray.getEndPoint();
        var dir = ray.getDirection();

        double t = computeParameterForNearestPointToLine(p, sp, dir);

        if (t <= 0.0) {
            return sp;
        }

        return sp.add(dir.multiply(t));

    }

    /**
     * Computes cross point of segments p0-p1 and q0-q1.
     *
     * @return cross point. Empty if the segments don't cross.
     */
    public static Optional<Vector2d> getCrossPoint(final Vector2d p0, final Vector2d p1,
            final Vector2d q0, final Vector2d q1) {

        return solveSegmentsCrossPointVectorEquation(p0, p1, q0, q1)
                .map(parameters -> computeDividingPoint(parameters.get(1), q0, q1));
    }

    /**
     * solve: cross point = p0 + s * (p1 - p0) = q0 + t * (q1 - q0)
     *
     * @param p0
     * @param p1
     * @param q0
     * @param q1
     * @return list of answer values. value at 0 is s for p0 and p1 equation and
     *         one at 1 is t for q0 and q1 equation. Empty if answer doesn't
     *         exist.
     */
    public static Optional<List<Double>> solveSegmentsCrossPointVectorEquation(final Vector2d p0, final Vector2d p1,
            final Vector2d q0, final Vector2d q1) {

        final double eps = MathUtil.normalizedValueEps();
        var range = new ClosedRange(0, 1, eps);

        return solveCrossPointVectorEquation(p0, p1, q0, q1,
                (s, t) -> range.includes(s) && range.includes(t));
    }

    /**
     * Let p = p1 - p0, q = q1 - q0 and d = q0 - p0. Here we assume the cross
     * point is c = (a, b). We have two parameters s and t such that p0 + sp =
     * q0 + tq = c. Then we obtain tq - sp + d = 0. This equation can be
     * described with Ax = -d where
     *
     * <pre>
     * {@code
     * A = [-p_x q_x] and x = [s]
     *     [-p_y q_y]         [t]
     * }
     * </pre>
     *
     * This method solves the equation above and returns the s and t as a list
     * of Double.
     *
     * @param p0
     * @param p1
     * @param q0
     * @param q1
     * @param answerPredicate
     *            returns true if the s and t are acceptable, otherwise it
     *            should return false.
     * @return
     * @apiNote Usually returning Optional of List is a bad idea but this method
     *          does it because the number of returned values is fixed as two
     *          and it is useful to process further in caller method.
     *
     */
    private static Optional<List<Double>> solveCrossPointVectorEquation(final Vector2d p0, final Vector2d p1,
            final Vector2d q0, final Vector2d q1, final BiPredicate<Double, Double> answerPredicate) {

        var p = p1.subtract(p0);
        var q = q1.subtract(q0);
        var d = q0.subtract(p0);

        var matrix = new Matrix2d(
                -p.getX(), q.getX(),
                -p.getY(), q.getY());

        var inverseOpt = matrix.inverse();

        return inverseOpt
                .map(inv -> inv.product(d.multiply(-1)))
                .filter(x -> answerPredicate.test(x.getX(), x.getY()))
                .map(x -> List.of(x.getX(), x.getY()));
    }

    /**
     * Computes p = q0 + t * (q1 - p0). p is the point that divides the segment
     * q0 -> q1 into t : 1-t. If t is negative, the point is the result of
     * external division |t| : 1+|t|. If t is larger than 1, the point is the
     * result of external division t : t-1.
     *
     * @param t
     *            ratio parameter.
     * @param q0
     * @param q1
     * @return
     */
    public static Vector2d computeDividingPoint(final double t, final Vector2d q0, final Vector2d q1) {
        // cp = (1 - t) * q0 + t * q1
        return q0.multiply(1.0 - t).add(q1.multiply(t));
    }

    public static Optional<Vector2d> getCrossPoint(final Segment l0, final Segment l1) {
        return getCrossPoint(l0.getP0(), l0.getP1(), l1.getP0(), l1.getP1());
    }

    public static double distance(final Vector2d p, final Line line, final double[] param) {

        var sp = line.getPoint();

        param[0] = computeParameterForNearestPointToLine(p, sp, line.getDirection());
        return distancePointToLine(p, line);
    }

    /**
     * @return true if vector p0 -> q ends in left side of p0 -> p1 (q is at
     *         counterclockwise position) otherwise false.
     */
    public static boolean isStrictlyCCW(final Vector2d p0, final Vector2d p1, final Vector2d q) {
        return CCWcheck(p0, p1, q, 0) == 1;
    }

    /**
     * @return true if vector p0 -> q ends in left side of p0 -> p1 (q is at
     *         counterclockwise position) otherwise false.
     */
    public static boolean isStrictlyCCW(final Vector2d p0, final Vector2d p1, final Vector2d q, final double eps) {
        return CCWcheck(p0, p1, q, eps) == 1;
    }

    public static boolean isCCW(final Vector2d p0, final Vector2d p1, final Vector2d q) {
        return CCWcheck(p0, p1, q, 0) >= 0;
    }

    public static int CCWcheck(final Vector2d p0, final Vector2d p1, final Vector2d q) {
        return CCWcheck(p0, p1, q, MathUtil.normalizedValueEps());
    }

    /**
     * tests whether counterclockwise position or not by cross product of
     * normalized vectors.
     *
     * @return 1 if vector p0 -> q ends on the left side of p0 -> p1 (q is at
     *         counterclockwise position in right-handed coordinate system), 0
     *         if p0-p1 and p0-q is collinear, otherwise -1;
     */
    private static int CCWcheck(final Vector2d p0, final Vector2d p1, final Vector2d q, final double eps) {
        var value = computeCCW(p0, p1, q);
        if (value > eps) {
            return 1;
        }
        if (value < -eps) {
            return -1;
        }
        return 0;
    }

    private static double computeCCW(final Vector2d p0, final Vector2d p1, final Vector2d q) {

        var d1 = p1.subtract(p0).normalize();

        var d2 = q.subtract(p0).normalize();

        return d1.crossProductZ(d2);
    }

    public static Optional<Vector2d> computeCentroid(final Collection<Vector2d> points) {

        if (points.isEmpty()) {
            return Optional.empty();
        }

        var sumOpt = points.stream()
                .reduce((result, x) -> result.add(x));

        return sumOpt.map(sum -> sum.multiply(1.0 / points.size()));
    }
}
