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

import static java.lang.Math.abs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import javax.vecmath.Vector2d;

import oripa.value.CalculationResource;
import oripa.value.OriLine;

public class GeomUtil {
	public final static double EPS = CalculationResource.POINT_EPS;

	public static double distance(final Vector2d p0, final Vector2d p1) {
		return distance(p0.x, p0.y, p1.x, p1.y);
	}

	public static boolean isParallel(final Vector2d dir0, final Vector2d dir1) {
		// tolerance of 1 degree
		return dir0.angle(dir1) < Math.PI / 180 || dir0.angle(dir1) > Math.PI * 179.0 / 180;
	}

	/**
	 *
	 * @return Count of end points on other segment for each segment. If the
	 *         count is 0 then they are not overlapping. If the count is 1, they
	 *         are not overlapping. If the count is 2, then the two segments
	 *         partially overlaps. If the count is 3, then one segment overlaps
	 *         entirely and an end point is shared with the other segment. If
	 *         the count is 4, then the two segments are equal.
	 */
	public static int distinguishLineSegmentsOverlap(final Vector2d s0, final Vector2d e0,
			final Vector2d s1, final Vector2d e1) {
		// Whether or not is parallel
		Vector2d dir0 = new Vector2d(e0);
		dir0.sub(s0);
		Vector2d dir1 = new Vector2d(e1);
		dir1.sub(s1);

		if (!isParallel(dir0, dir1)) {
			return 0;
		}

		int cnt = 0;
		if (distancePointToSegment(s0, s1, e1) < EPS) {
			cnt++;
		}
		if (distancePointToSegment(e0, s1, e1) < EPS) {
			cnt++;
		}
		if (distancePointToSegment(s1, s0, e0) < EPS) {
			cnt++;
		}
		if (distancePointToSegment(e1, s0, e0) < EPS) {
			cnt++;
		}
		return cnt;
	}

	public static boolean isLineSegmentsOverlap(final Vector2d s0, final Vector2d e0,
			final Vector2d s1, final Vector2d e1) {
		var cnt = distinguishLineSegmentsOverlap(s0, e0, s1, e1);
		return cnt >= 2;

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
	public static boolean isSameLineSegment(final Segment l0, final Segment l1) {
		return (distance(l0.p0, l1.p0) < EPS && distance(l0.p1, l1.p1) < EPS) ||
				(distance(l0.p0, l1.p1) < EPS && distance(l0.p1, l1.p0) < EPS);
	}

	/**
	 *
	 * @param l
	 *            is assumed to be long enough.
	 * @param domain
	 *            defines clip area.
	 * @return true if clip was done.
	 */
	public static boolean clipLine(final OriLine l, final RectangleDomain domain) {

		double left = domain.getLeft();
		double right = domain.getRight();

		double top = domain.getTop();
		double bottom = domain.getBottom();

		var leftSegment = new OriLine(left, top, left, bottom, OriLine.Type.AUX);
		var rightSegment = new OriLine(right, top, right, bottom, OriLine.Type.AUX);

		var topSegment = new OriLine(left, top, right, top, OriLine.Type.AUX);
		var bottomSegment = new OriLine(left, bottom, right, bottom, OriLine.Type.AUX);

		final List<Vector2d> crossPoints = new ArrayList<>();

		Consumer<Vector2d> addIfDistinct = cp -> {
			if (cp == null) {
				return;
			}
			if (crossPoints.stream().allMatch(v -> distance(v, cp) > EPS)) {
				crossPoints.add(cp);
			}
		};

		addIfDistinct.accept(getCrossPoint(l, leftSegment));
		addIfDistinct.accept(getCrossPoint(l, rightSegment));
		addIfDistinct.accept(getCrossPoint(l, topSegment));
		addIfDistinct.accept(getCrossPoint(l, bottomSegment));

		if (crossPoints.size() == 2) {
			l.p0.set(crossPoints.get(0));
			l.p1.set(crossPoints.get(1));

			return true;
		}

		return false;
	}

	public static OriLine getVerticalLine(final Vector2d v, final OriLine line,
			final OriLine.Type type) {
		double x0 = line.p0.x;
		double y0 = line.p0.y;
		double x1 = line.p1.x;
		double y1 = line.p1.y;
		double px = v.x;
		double py = v.y;
		Vector2d sub0, sub, sub0b;

		sub0 = new Vector2d(x0 - px, y0 - py);
		sub0b = new Vector2d(-sub0.x, -sub0.y);
		sub = new Vector2d(x1 - x0, y1 - y0);

		double t = ((sub.x * sub0b.x) + (sub.y * sub0b.y))
				/ ((sub.x * sub.x) + (sub.y * sub.y));

		return new OriLine(x0 + t * sub.x, y0 + t * sub.y, px, py, type);
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

		Vector2d d0 = new Vector2d(ray.dir);
		Vector2d d1 = new Vector2d(seg.p1.x - seg.p0.x, seg.p1.y - seg.p0.y);
		Vector2d diff = new Vector2d(seg.p0.x - p0.x, seg.p0.y - p0.y);
		double det = d1.x * d0.y - d1.y * d0.x;

		double epsilon = 1.0e-6;
		if (det * det <= epsilon * d0.lengthSquared() * d1.lengthSquared()) {
			return null;
		}

		// Lines intersect in a single point. Return both s and t values for
		// use by calling functions.
		double invDet = 1.0 / det;
		double s = (d1.x * diff.y - d1.y * diff.x) * invDet;
		double t = (d0.x * diff.y - d0.y * diff.x) * invDet;

		if (t < 0.0 - epsilon || t > 1.0 + epsilon) {
			return null;
		} else if (s < 0.0 - epsilon) {
			return null;
		}
		Vector2d cp = new Vector2d();
		cp.x = (1.0 - t) * seg.p0.x + t * seg.p1.x;
		cp.y = (1.0 - t) * seg.p0.y + t * seg.p1.y;
		return cp;
	}

	// Compute the intersection of straight lines
	public static Vector2d getCrossPoint(final Line l0, final Line l1) {
		Vector2d p0 = new Vector2d(l0.p);
		Vector2d p1 = new Vector2d(l0.p);
		p1.add(l0.dir);

		Vector2d d0 = new Vector2d(p1.x - p0.x, p1.y - p0.y);
		Vector2d d1 = new Vector2d(l1.dir);
		Vector2d diff = new Vector2d(l1.p.x - p0.x, l1.p.y - p0.y);
		double det = d1.x * d0.y - d1.y * d0.x;

		if (det * det <= EPS * d0.lengthSquared() * d1.lengthSquared()) {
			return null;
		}

		// Lines intersect in a single point.
		double invDet = 1.0 / det;
		double t = (d0.x * diff.y - d0.y * diff.x) * invDet;

		Vector2d cp = new Vector2d();
		cp.x = (1.0 - t) * l1.p.x + t * (l1.p.x + l1.dir.x);
		cp.y = (1.0 - t) * l1.p.y + t * (l1.p.y + l1.dir.y);
		return cp;
	}

	public static OriLine getLineByValue(final Vector2d sv, final double length,
			final double deg_angle, final OriLine.Type type) {
		Vector2d ev = new Vector2d(sv);
		double rad_angle = Math.toRadians(deg_angle);
		Vector2d dir = new Vector2d(length * Math.cos(rad_angle), length * Math.sin(rad_angle));
		ev.add(dir);
		return new OriLine(sv, ev, type);
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
	 * solve: cross point = p0 + s * d0 = q0 + t * d1
	 *
	 * @return cross point
	 */
	public static Vector2d getCrossPoint(final Vector2d p0, final Vector2d p1,
			final Vector2d q0, final Vector2d q1) {
		Vector2d d0 = new Vector2d(p1.x - p0.x, p1.y - p0.y);
		Vector2d d1 = new Vector2d(q1.x - q0.x, q1.y - q0.y);
		Vector2d diff = new Vector2d(q0.x - p0.x, q0.y - p0.y);
		double det = d1.x * d0.y - d1.y * d0.x;

		if (det * det <= EPS * d0.lengthSquared() * d1.lengthSquared()) {
			return null;
		}

		// Lines intersect in a single point.
		double invDet = 1.0 / det;
		double s = (d1.x * diff.y - d1.y * diff.x) * invDet;
		double t = (d0.x * diff.y - d0.y * diff.x) * invDet;

		if (t < 0.0 - EPS || t > 1.0 + EPS) {
			return null;
		} else if (s < 0.0 - EPS || s > 1.0 + EPS) {
			return null;
		}

		// cp = (1 - t) * q0 + t * q1
		Vector2d cp = new Vector2d();
		cp.x = (1.0 - t) * q0.x + t * q1.x;
		cp.y = (1.0 - t) * q0.y + t * q1.y;
		return cp;
	}

	public static Vector2d getCrossPoint(final OriLine l0, final OriLine l1) {
		return getCrossPoint(l0.p0, l0.p1, l1.p0, l1.p1);
	}

	public static double distance(final Vector2d p, final Line line, final double[] param) {

		Vector2d sp = line.p;
		Vector2d ep = new Vector2d(sp);
		ep.add(line.dir);
		param[0] = computeParameterForNearestPointToLine(p, sp, ep);
		return distancePointToLine(p, line);
	}

	/**
	 * @return true if vector p0 -> q ends in left side of p1 -> p0 (q is at
	 *         counterclockwise position) otherwise false.
	 */
	public static boolean CCWcheck(final Vector2d p0, final Vector2d p1, final Vector2d q) {
		return CCWcheck(p0, p1, q, 0) == 1;
	}

	/**
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

		dx1 = p1.x - p0.x;
		dy1 = p1.y - p0.y;
		dx2 = q.x - p0.x;
		dy2 = q.y - p0.y;

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

	public static boolean detectOverlap(final Segment existingLine, final Segment newLine) {
		return areOnSameSupportLine(existingLine, newLine) && !areDisjointSegments(existingLine, newLine);
	}

	public static boolean areDisjointSegments(final Segment l1, final Segment l2) {
		if (segmentIsVertical(l1)) {
			return (l1.p0.y <= l2.p0.y && l1.p0.y <= l2.p1.y && l1.p1.y <= l2.p0.y && l1.p1.y <= l2.p1.y) ||
					(l2.p0.y <= l1.p0.y && l2.p0.y <= l1.p1.y && l2.p1.y <= l1.p0.y && l2.p1.y <= l1.p1.y);
		} else {
			return (l1.p0.x <= l2.p0.x && l1.p0.x <= l2.p1.x && l1.p1.x <= l2.p0.x && l1.p1.x <= l2.p1.x) ||
					(l2.p0.x <= l1.p0.x && l2.p0.x <= l1.p1.x && l2.p1.x <= l1.p0.x && l2.p1.x <= l1.p1.x);
		}
	}

	/**
	 * l1 and l2 share the same support line if they test the same at 2 distinct
	 * points (more or less epsilon)
	 */
	public static boolean areOnSameSupportLine(final Segment l1, final Segment l2) {
		if (segmentIsVertical(l1)) {
			return abs(getAffineXValueAt(l1, l2.p0.y) - l2.p0.x) < EPS
					&& abs(getAffineXValueAt(l1, l2.p1.y) - l2.p1.x) < EPS;
		}
		return abs(getAffineXValueAt(l1, l2.p0.x) - l2.p0.y) < EPS
				&& abs(getAffineXValueAt(l1, l2.p1.x) - l2.p1.y) < EPS;
	}

	private static boolean segmentIsVertical(final Segment l) {
		return l.p0.y - l.p1.y < EPS;
	}

	/**
	 * Calculates the affine value on the line, at the {@code yTested}
	 * coordinate using the x = ay + b expression
	 */
	private static double getAffineXValueAt(final Segment l, final double yTested) {
		return (l.p1.x - l.p0.x) * (yTested - l.p0.y) / (l.p1.y - l.p0.y) + l.p0.x;
	}

}
