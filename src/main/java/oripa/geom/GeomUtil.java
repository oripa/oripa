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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import javax.vecmath.Vector2d;

import oripa.util.ClosedRange;
import oripa.util.MathUtil;
import oripa.value.CalculationResource;

public class GeomUtil {

	/**
	 * Discouraged to use in order to make eps configurable.
	 *
	 * @return eps for point equality measured by distance.
	 */
	public static double pointEps() {
		return CalculationResource.POINT_EPS;
	}

	public static boolean areEqual(final Vector2d p0, final Vector2d p1, final double eps) {
		return distance(p0, p1) < eps;
	}

	public static double distance(final Vector2d p0, final Vector2d p1) {
		return distance(p0.x, p0.y, p1.x, p1.y);
	}

	public static boolean isParallel(final Vector2d dir0, final Vector2d dir1) {
		double angle = dir0.angle(dir1);
		return angle < MathUtil.angleRadianEps() || angle > Math.PI - MathUtil.angleRadianEps();
	}

	/**
	 *
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
	public static int distinguishLineSegmentsOverlap(final Vector2d s0, final Vector2d e0,
			final Vector2d s1, final Vector2d e1, final double pointEps) {
		// Whether or not is parallel
		Vector2d dir0 = new Vector2d(e0);
		dir0.sub(s0);
		Vector2d dir1 = new Vector2d(e1);
		dir1.sub(s1);

		if (!isParallel(dir0, dir1)) {
			return 0;
		}

		int cnt = 0;
		if (distancePointToSegment(s0, s1, e1) < pointEps) {
			cnt++;
		}
		if (distancePointToSegment(e0, s1, e1) < pointEps) {
			cnt++;
		}
		if (distancePointToSegment(s1, s0, e0) < pointEps) {
			cnt++;
		}
		if (distancePointToSegment(e1, s0, e0) < pointEps) {
			cnt++;
		}
		return cnt;
	}

	public static int distinguishLineSegmentsOverlap(final Segment seg0, final Segment seg1, final double pointEps) {
		return distinguishLineSegmentsOverlap(seg0.getP0(), seg0.getP1(), seg1.getP0(), seg1.getP1(), pointEps);
	}

	/**
	 *
	 * @param s0
	 * @param e0
	 * @param s1
	 * @param e1
	 * @return {@code true} if end points of both lines are on the other line.
	 *         Note that this method returns {@code true} if the lines touch at
	 *         end points and does not share other part.
	 */
	public static boolean isRelaxedOverlap(final Vector2d s0, final Vector2d e0,
			final Vector2d s1, final Vector2d e1, final double pointEps) {
		var cnt = distinguishLineSegmentsOverlap(s0, e0, s1, e1, pointEps);
		return cnt >= 2;

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
		return isRelaxedOverlap(seg0.getP0(), seg0.getP1(), seg1.getP0(), seg1.getP1(), pointEps);
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
		var overlapCount = distinguishLineSegmentsOverlap(seg0, seg1, pointEps);
		if (overlapCount >= 3) {
			return true;
		}
		if (overlapCount == 2) {
			if (areEqual(seg0.getP0(), seg1.getP0(), pointEps)) {
				return false;
			} else if (areEqual(seg0.getP0(), seg1.getP1(), pointEps)) {
				return false;
			} else if (areEqual(seg0.getP1(), seg1.getP0(), pointEps)) {
				return false;
			} else if (areEqual(seg0.getP1(), seg1.getP1(), pointEps)) {
				return false;
			} else {
				return true;
			}
		}

		return false;
	}

	/**
	 * Both distances between the extremities of the lines should be less than
	 * the threshold The lines can be reversed, so the test has to be done both
	 * ways
	 *
	 * @param l0
	 *            First line to compare
	 * @param l1
	 *            Second line to compare
	 * @return true if both segments are (at least almost) equals
	 */
	public static boolean isSameLineSegment(final Segment l0, final Segment l1, final double pointEps) {
		if (areEqual(l0.getP0(), l1.getP0(), pointEps) && areEqual(l0.getP1(), l1.getP1(), pointEps)) {
			return true;
		}
		if (areEqual(l0.getP0(), l1.getP1(), pointEps) && areEqual(l0.getP1(), l1.getP0(), pointEps)) {
			return true;
		}

		return false;
	}

	/**
	 *
	 * @param l
	 *            is assumed to be long enough.
	 * @param domain
	 *            defines clip area.
	 * @return Optional of clipped segment. Empty if failed.
	 */
	public static Optional<Segment> clipLine(final Segment l, final RectangleDomain domain, final double pointEps) {

		double left = domain.getLeft();
		double right = domain.getRight();

		double top = domain.getTop();
		double bottom = domain.getBottom();

		var leftSegment = new Segment(left, top, left, bottom);
		var rightSegment = new Segment(right, top, right, bottom);

		var topSegment = new Segment(left, top, right, top);
		var bottomSegment = new Segment(left, bottom, right, bottom);

		final List<Vector2d> crossPoints = new ArrayList<>();

		Consumer<Vector2d> addIfDistinct = cp -> {
			if (cp == null) {
				return;
			}
			if (crossPoints.stream().allMatch(v -> distance(v, cp) > pointEps)) {
				crossPoints.add(cp);
			}
		};

		addIfDistinct.accept(getCrossPoint(l, leftSegment));
		addIfDistinct.accept(getCrossPoint(l, rightSegment));
		addIfDistinct.accept(getCrossPoint(l, topSegment));
		addIfDistinct.accept(getCrossPoint(l, bottomSegment));

		if (crossPoints.size() == 2) {

			return Optional.of(new Segment(crossPoints.get(0), crossPoints.get(1)));
		}

		return Optional.empty();
	}

	public static Segment getVerticalLine(final Vector2d v, final Segment line) {
		double x0 = line.getP0().x;
		double y0 = line.getP0().y;
		double x1 = line.getP1().x;
		double y1 = line.getP1().y;
		double px = v.x;
		double py = v.y;
		Vector2d sub0, sub, sub0b;

		sub0 = new Vector2d(x0 - px, y0 - py);
		sub0b = new Vector2d(-sub0.x, -sub0.y);
		sub = new Vector2d(x1 - x0, y1 - y0);

		double t = ((sub.x * sub0b.x) + (sub.y * sub0b.y))
				/ ((sub.x * sub.x) + (sub.y * sub.y));

		return new Segment(x0 + t * sub.x, y0 + t * sub.y, px, py);
	}

	public static Vector2d getIncenter(final Vector2d v0, final Vector2d v1, final Vector2d v2) {
		double l0 = distance(v1, v2);
		double l1 = distance(v0, v2);
		double l2 = distance(v0, v1);

		Vector2d vc = new Vector2d();
		vc.x = (v0.x * l0 + v1.x * l1 + v2.x * l2) / (l0 + l1 + l2);
		vc.y = (v0.y * l0 + v1.y * l1 + v2.y * l2) / (l0 + l1 + l2);

		return vc;
	}

	public static Vector2d getBisectorVec(final Vector2d v0, final Vector2d v1, final Vector2d v2) {
		Vector2d v0_v1 = new Vector2d();
		v0_v1.sub(v0, v1);
		v0_v1.normalize();
		Vector2d v2_v1 = new Vector2d();
		v2_v1.sub(v2, v1);
		v2_v1.normalize();

		// a dot b = |a||b| cos(theta)
		double angle = Math.acos(v0_v1.dot(v2_v1));

		if (MathUtil.areEqual(angle, Math.PI, MathUtil.angleRadianEps())) {
			double bisectorAngle = MathUtil.angleOf(v0_v1) + Math.PI / 2;
			return new Vector2d(Math.cos(bisectorAngle), Math.sin(bisectorAngle));
		}

		return new Vector2d(v0_v1.x + v2_v1.x, v0_v1.y + v2_v1.y);
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
		Vector2d cp = getNearestPointToLine(p, sp, ep);
		return new Vector2d(2 * cp.x - p.x, 2 * cp.y - p.y);
	}

	// Returns the intersection of the semi straight line and the line segment.
	// Null if not intersect
	public static Vector2d getCrossPoint(final Ray ray, final Segment seg) {
		Vector2d p0 = new Vector2d(ray.p);
		Vector2d p1 = new Vector2d();
		p1.add(p0, ray.dir);

		Vector2d segP0 = seg.getP0();
		Vector2d segP1 = seg.getP1();

		var answerOpt = solveRayCrossPointVectorEquation(p0, p1, segP0, segP1);

		if (answerOpt.isEmpty()) {
			return null;
		}

		double t = answerOpt.get().get(1);
		Vector2d cp = new Vector2d();
		cp.x = (1.0 - t) * segP0.x + t * segP1.x;
		cp.y = (1.0 - t) * segP0.y + t * segP1.y;
		return cp;
	}

	private static Optional<List<Double>> solveRayCrossPointVectorEquation(final Vector2d p0, final Vector2d p1,
			final Vector2d segP0, final Vector2d segP1) {
		final double eps = MathUtil.normalizedValueEps();
		return solveCrossPointVectorEquation(p0, p1, segP0, segP1,
				(s, t) -> new ClosedRange(0, 1, eps).includes(t) && s >= -eps);
	}

	// Compute the intersection of straight lines
	public static Vector2d getCrossPoint(final Line l0, final Line l1) {
		var p0 = new Vector2d(l0.p);
		var p1 = new Vector2d();
		p1.add(p0, l0.dir);

		var q0 = new Vector2d(l1.p);
		var q1 = new Vector2d();
		q1.add(q0, l1.dir);

		var answerOpt = solveLinesCrossPointVectorEquation(p0, p1, q0, q1);

		if (answerOpt.isEmpty()) {
			return null;
		}

		var t = answerOpt.get().get(1);

		// cp = (1 - t) * q0 + t * q1
		Vector2d cp = new Vector2d();
		cp.x = (1.0 - t) * q0.x + t * q1.x;
		cp.y = (1.0 - t) * q0.y + t * q1.y;
		return cp;
	}

	private static Optional<List<Double>> solveLinesCrossPointVectorEquation(final Vector2d p0, final Vector2d p1,
			final Vector2d q0, final Vector2d q1) {
		return solveCrossPointVectorEquation(p0, p1, q0, q1, (s, t) -> true);
	}

	public static Segment getLineByValue(final Vector2d sv, final double length,
			final double deg_angle) {
		Vector2d ev = new Vector2d(sv);
		double rad_angle = Math.toRadians(deg_angle);
		Vector2d dir = new Vector2d(length * Math.cos(rad_angle), length * Math.sin(rad_angle));
		ev.add(dir);
		return new Segment(sv, ev);
	}

	/**
	 *
	 * @param p
	 *            point
	 * @param sp
	 *            start point of line vector
	 * @param ep
	 *            end point of line vector
	 * @return t = |p - sp| * cos(\theta) / |ep - sp| where theta is the angle
	 *         between p - sp and ep - sp
	 */
	private static double computeParameterForNearestPointToLine(
			final Vector2d p, final Vector2d sp, final Vector2d ep) {
		double x0 = sp.x;
		double y0 = sp.y;
		double x1 = ep.x;
		double y1 = ep.y;
		double px = p.x;
		double py = p.y;
		Vector2d dir, sub0;

		sub0 = new Vector2d(px - x0, py - y0);

		// direction of the line
		dir = new Vector2d(x1 - x0, y1 - y0);

		// t = |sub0| * cos(\theta) / |dir|
		return dir.dot(sub0) / dir.lengthSquared();
	}

	private static Vector2d getNearestPointToLine(
			final Vector2d p, final Vector2d sp, final Vector2d ep) {

		// direction of the line
		Vector2d dir = new Vector2d(ep);
		dir.sub(sp);

		double t = computeParameterForNearestPointToLine(p, sp, ep);

		return new Vector2d(sp.x + t * dir.x, sp.y + t * dir.y);
	}

	public static double distancePointToSegment(final Vector2d p, final Vector2d sp,
			final Vector2d ep) {

		double t = computeParameterForNearestPointToLine(p, sp, ep);

		if (t < 0.0) {
			return distance(p, sp);
		} else if (t > 1.0) {
			return distance(p, ep);
		} else {
			// direction of the line
			Vector2d dir = new Vector2d(ep);
			dir.sub(sp);
			return distance(sp.x + t * dir.x, sp.y + t * dir.y, p.x, p.y);
		}
	}

	public static double distancePointToSegment(final Vector2d p, final Vector2d sp,
			final Vector2d ep, final Vector2d nearestPoint) {

		double t = computeParameterForNearestPointToLine(p, sp, ep);

		if (t < 0.0) {
			nearestPoint.set(sp);
			return distance(p, sp);
		} else if (t > 1.0) {
			nearestPoint.set(ep);
			return distance(p, ep);
		} else {
			// direction of the line
			Vector2d dir = new Vector2d(ep);
			dir.sub(sp);
			nearestPoint.set(sp.x + t * dir.x, sp.y + t * dir.y);
			return distance(sp.x + t * dir.x, sp.y + t * dir.y, p.x, p.y);
		}
	}

	public static double distancePointToLine(final Vector2d p, final Line line) {
		Vector2d sp = line.p;
		Vector2d ep = new Vector2d(sp);
		ep.add(line.dir);

		return distance(getNearestPointToLine(p, sp, ep), p);
	}

	/**
	 * Computes cross point of segments p0-p1 and q0-q1.
	 *
	 * @return cross point. null if the segments don't cross.
	 */
	public static Vector2d getCrossPoint(final Vector2d p0, final Vector2d p1,
			final Vector2d q0, final Vector2d q1) {

		var parametersOpt = solveSegmentsCrossPointVectorEquation(p0, p1, q0, q1);

		if (parametersOpt.isEmpty()) {
			return null;
		}

		var t = parametersOpt.get().get(1);

		// cp = (1 - t) * q0 + t * q1
		Vector2d cp = new Vector2d();
		cp.x = (1.0 - t) * q0.x + t * q1.x;
		cp.y = (1.0 - t) * q0.y + t * q1.y;
		return cp;
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

	private static Optional<List<Double>> solveCrossPointVectorEquation(final Vector2d p0, final Vector2d p1,
			final Vector2d q0, final Vector2d q1, final BiPredicate<Double, Double> answerPredicate) {

		var answer = new ArrayList<Double>();

		Vector2d d0 = new Vector2d(p1.x - p0.x, p1.y - p0.y);
		Vector2d d1 = new Vector2d(q1.x - q0.x, q1.y - q0.y);
		Vector2d diff = new Vector2d(q0.x - p0.x, q0.y - p0.y);
		double det = d1.x * d0.y - d1.y * d0.x;

		final double eps = MathUtil.normalizedValueEps();

		if (det * det <= eps * d0.lengthSquared() * d1.lengthSquared()) {
			return Optional.empty();
		}

		// Lines intersect in a single point.
		double s = (d1.x * diff.y - d1.y * diff.x) / det;
		double t = (d0.x * diff.y - d0.y * diff.x) / det;

		if (!answerPredicate.test(s, t)) {
			return Optional.empty();
		}

		answer.add(s);
		answer.add(t);

		return Optional.of(answer);
	}

	public static Vector2d getCrossPoint(final Segment l0, final Segment l1) {
		return getCrossPoint(l0.getP0(), l0.getP1(), l1.getP0(), l1.getP1());
	}

	public static double distance(final Vector2d p, final Line line, final double[] param) {

		Vector2d sp = line.p;
		Vector2d ep = new Vector2d(sp);
		ep.add(line.dir);
		param[0] = computeParameterForNearestPointToLine(p, sp, ep);
		return distancePointToLine(p, line);
	}

	/**
	 * @return true if vector p0 -> q ends in left side of p0 -> p1 (q is at
	 *         counterclockwise position) otherwise false.
	 */
	public static boolean isCCW(final Vector2d p0, final Vector2d p1, final Vector2d q) {
		return CCWcheck(p0, p1, q, 0) == 1;
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
	public static int CCWcheck(final Vector2d p0, final Vector2d p1, final Vector2d q, final double eps) {
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
		double dx1, dx2, dy1, dy2;

		var d1 = new Vector2d(p1);
		d1.sub(p0);
		d1.normalize();

		var d2 = new Vector2d(q);
		d2.sub(p0);
		d2.normalize();

		dx1 = d1.getX();
		dy1 = d1.getY();
		dx2 = d2.getX();
		dy2 = d2.getY();

		return dx1 * dy2 - dy1 * dx2;
	}

	private static double distance(final double x0, final double y0, final double x1, final double y1) {
		return Math.sqrt((x0 - x1) * (x0 - x1) + (y0 - y1) * (y0 - y1));
	}

	public static Vector2d computeCentroid(final Collection<Vector2d> points) {
		var centroid = new Vector2d();
		points.forEach(centroid::add);
		centroid.scale(1.0 / points.size());

		return centroid;
	}

}
