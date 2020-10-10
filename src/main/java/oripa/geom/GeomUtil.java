/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2005-2009 Jun Mitani http://mitani.cs.tsukuba.ac.jp/

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

package oripa.geom;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.vecmath.Vector2d;

import oripa.domain.fold.OriFace;
import oripa.domain.fold.OriHalfedge;
import oripa.value.OriLine;

public class GeomUtil {
	// private static final Logger logger =
	// LoggerFactory.getLogger(GeomUtil.class);

	public final static double EPS = 1.0e-6;

	public static double distance(final Vector2d p0, final Vector2d p1) {
		return distance(p0.x, p0.y, p1.x, p1.y);
	}

	public static double distanceSquared(final Vector2d p0, final Vector2d p1) {
		return distanceSquared(p0.x, p0.y, p1.x, p1.y);
	}

	public static double distanceSquared(final double x0, final double y0, final double x1,
			final double y1) {
		return (x0 - x1) * (x0 - x1) + (y0 - y1) * (y0 - y1);
	}

	// Determine if p3 is in p1~p2 range
	public static boolean isRange(final double p1, final double p2, final double p3) {
		return Math.min(p1, p2) <= p3 && p3 <= Math.max(p1, p2);
	}

	public static boolean isRange(final Vector2d p1, final Vector2d p2, final Vector2d p3) {
		return isRange(p1.x, p2.x, p3.x) && isRange(p1.y, p2.y, p3.y);
	}

	public static boolean isParallel(final Vector2d dir0, final Vector2d dir1) {
		// tolerance of 1 degree
		return dir0.angle(dir1) < Math.PI / 180 || dir0.angle(dir1) > Math.PI * 179.0 / 180;
	}

//	public static boolean isSegmentsCross(final Vector2d p0, final Vector2d p1, final Vector2d q0,
//			final Vector2d q1) {
//
//		// Rough check
//		// Check by coordinates x
//		if (p0.x >= p1.x) {
//			if ((p0.x < q0.x && p0.x < q1.x) || (p1.x > q0.x && p1.x > q1.x)) {
//				return false;
//			}
//		} else {
//			if ((p1.x < q0.x && p1.x < q1.x) || (p0.x > q0.x && p0.x > q1.x)) {
//				return false;
//			}
//		}
//
//		// checked by the coordinate y
//		if (p0.y >= p1.y) {
//			if ((p0.y < q0.y && p0.y < q1.y) || (p1.y > q0.y && p1.y > q1.y)) {
//				return false;
//			}
//		} else {
//			if ((p1.y < q0.y && p1.y < q1.y) || (p0.y > q0.y && p0.y > q1.y)) {
//				return false;
//			}
//		}
//
//		// >= 0.0 means that when p0 == q0, for example, returns false
//		if (((p0.x - p1.x) * (q0.y - p0.y) + (p0.y - p1.y) * (p0.x - q0.x))
//				* ((p0.x - p1.x) * (q1.y - p0.y) + (p0.y - p1.y) * (p0.x - q1.x)) >= 0.0) {
//			return false;
//		}
//
//		if (((q0.x - q1.x) * (p0.y - q0.y) + (q0.y - q1.y) * (q0.x - p0.x))
//				* ((q0.x - q1.x) * (p1.y - q0.y) + (q0.y - q1.y) * (q0.x - p1.x)) >= 0.0) {
//			return false;
//		}
//
//		return true;
//	}

	/**
	 *
	 * @param s0
	 * @param e0
	 * @param s1
	 * @param e1
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

//	// Calculate the intersection of p1-p2 and p3-p4.
//	// The return value is: 0 when there is no intersection,
//	// 1 when intersect (ap1 is the intersection),
//	// 2 when they are parallel and ap1-ap2 is the intersection
//	public static int getCrossPoint(final Vector2d ap1, final Vector2d ap2,
//			final Vector2d p1, final Vector2d p2, final Vector2d p3, final Vector2d p4) {
//
//		if (distance(p1, p3) < EPS && distance(p2, p4) < EPS) {
//			ap1.set(p1);
//			ap2.set(p2);
//			return 2;
//		}
//
//		if (distance(p1, p4) < EPS && distance(p2, p3) < EPS) {
//			ap1.set(p1);
//			ap2.set(p2);
//			return 2;
//		}
//
//		double a1, a2, b1, b2;
//
//		a1 = (p1.y - p2.y);
//		a2 = (p3.y - p4.y);
//		b1 = (p2.x - p1.x);
//		b2 = (p4.x - p3.x);
//
//		if (Math.abs(a1 * b2 - a2 * b1) < EPS) {
//			if (Math.max(p1.x, p2.x) < Math.min(p3.x, p4.x)
//					|| Math.max(p1.y, p2.y) < Math.min(p3.y, p4.y)
//					|| Math.max(p3.x, p4.x) < Math.min(p1.x, p2.x)
//					|| Math.max(p3.y, p4.y) < Math.min(p1.y, p2.y)) {
//				return 0;
//			}
//
//			if (isRange(p3, p4, p1)) {
//				ap1.set(p1);
//			} else if (isRange(p1, p4, p3)) {
//				ap1.set(p3);
//			} else if (isRange(p1, p3, p4)) {
//				ap1.set(p4);
//			} else {
//				return 0;
//			}
//
//			if (isRange(p3, p4, p2)) {
//				ap2.set(p2);
//			} else if (isRange(p2, p4, p3)) {
//				ap2.set(p3);
//			} else if (isRange(p2, p3, p4)) {
//				ap2.set(p4);
//			} else {
//				return 0;
//			}
//			return 2;
//		}
//
//		double c1, c2;
//
//		c1 = p1.x * p2.y - p2.x * p1.y;
//		c2 = p3.x * p4.y - p4.x * p3.y;
//
//		ap1.x = (b1 * c2 - b2 * c1) / (a1 * b2 - a2 * b1);
//		ap1.y = (a1 * c2 - a2 * c1) / (a2 * b1 - a1 * b2);
//
//		if (isRange(p1, p2, ap1) && isRange(p3, p4, ap1)) {
//			return 1;
//		} else {
//			return 0;
//		}
//	}

	public static boolean isSameLineSegment(final OriLine l0, final OriLine l1) {
		if (distance(l0.p0, l1.p0) < EPS && distance(l0.p1, l1.p1) < EPS) {
			return true;
		}
		if (distance(l0.p0, l1.p1) < EPS && distance(l0.p1, l1.p0) < EPS) {
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
	 * @return true if clip was done.
	 */
	public static boolean clipLine(final OriLine l, final RectangleDomain domain) {

		double left = domain.getLeft();
		double right = domain.getRight();

		double top = domain.getTop();
		double bottom = domain.getBottom();

		var leftSegment = new OriLine(left, top, left, bottom, OriLine.Type.NONE);
		var rightSegment = new OriLine(right, top, right, bottom, OriLine.Type.NONE);

		var topSegment = new OriLine(left, top, right, top, OriLine.Type.NONE);
		var bottomSegment = new OriLine(left, bottom, right, bottom, OriLine.Type.NONE);

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
		if (det * det > epsilon * d0.lengthSquared() * d1.lengthSquared()) {
			// Lines intersect in a single point. Return both s and t values for
			// use by calling functions.
			double invDet = 1.0 / det;
			double s = (d1.x * diff.y - d1.y * diff.x) * invDet;
			double t = (d0.x * diff.y - d0.y * diff.x) * invDet;

			if (t < 0.0 - epsilon || t > 1.0 + epsilon) {
				return null;
			} else if (s < 0.0 - epsilon) {
				return null;
			} else {
				Vector2d cp = new Vector2d();
				cp.x = (1.0 - t) * seg.p0.x + t * seg.p1.x;
				cp.y = (1.0 - t) * seg.p0.y + t * seg.p1.y;
				return cp;
			}
		}
		return null;
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

		double epsilon = 1.0e-6;
		if (det * det > epsilon * d0.lengthSquared() * d1.lengthSquared()) {
			// Lines intersect in a single point.
			double invDet = 1.0 / det;
			double t = (d0.x * diff.y - d0.y * diff.x) * invDet;

			Vector2d cp = new Vector2d();
			cp.x = (1.0 - t) * l1.p.x + t * (l1.p.x + l1.dir.x);
			cp.y = (1.0 - t) * l1.p.y + t * (l1.p.y + l1.dir.y);
			return cp;
		}
		return null;
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

	// (Including endpoints) intersection between two line segments
	public static Vector2d getCrossPoint(final Vector2d p0, final Vector2d p1,
			final Vector2d q0, final Vector2d q1) {
		return getCrossPoint(p0, p1, q0, q1, EPS);
	}

	/**
	 * solve: cross point = p0 + s * d0 = q0 + t * d1
	 *
	 * @param p0
	 * @param p1
	 * @param q0
	 * @param q1
	 * @param epsilon
	 * @return cross point
	 */
	private static Vector2d getCrossPoint(final Vector2d p0, final Vector2d p1,
			final Vector2d q0, final Vector2d q1, final double epsilon) {
		Vector2d d0 = new Vector2d(p1.x - p0.x, p1.y - p0.y);
		Vector2d d1 = new Vector2d(q1.x - q0.x, q1.y - q0.y);
		Vector2d diff = new Vector2d(q0.x - p0.x, q0.y - p0.y);
		double det = d1.x * d0.y - d1.y * d0.x;

		if (det * det > epsilon * d0.lengthSquared() * d1.lengthSquared()) {
			// Lines intersect in a single point.
			double invDet = 1.0 / det;
			double s = (d1.x * diff.y - d1.y * diff.x) * invDet;
			double t = (d0.x * diff.y - d0.y * diff.x) * invDet;

			if (t < 0.0 - epsilon || t > 1.0 + epsilon) {
				return null;
			} else if (s < 0.0 - epsilon || s > 1.0 + epsilon) {
				return null;
			} else {
				// cp = (1 - t) * q0 + t * q0
				Vector2d cp = new Vector2d();
				cp.x = (1.0 - t) * q0.x + t * q1.x;
				cp.y = (1.0 - t) * q0.y + t * q1.y;
				return cp;
			}

		}
		return null;
	}

	public static Vector2d getCrossPoint(final OriLine l0, final OriLine l1) {
		return getCrossPoint(l0.p0, l0.p1, l1.p0, l1.p1, EPS);
	}

//	public static boolean isRightSide(final Vector2d p, final Line line) {
//		Vector3d lineDir = new Vector3d(line.dir.x, line.dir.y, 0);
//		Vector3d pointDir = new Vector3d(p.x - line.p.x, p.y - line.p.y, 0);
//		Vector3d crossVec = new Vector3d();
//		crossVec.cross(pointDir, lineDir);
//		return crossVec.z > 0;
//	}

	public static double distance(final Vector2d p, final Line line, final double[] param) {

		Vector2d sp = line.p;
		Vector2d ep = new Vector2d(sp);
		ep.add(line.dir);
		param[0] = computeParameterForNearestPointToLine(p, sp, ep);
		return distancePointToLine(p, line);
	}

//  Investigate the relationship between the point q with the segment p0, p1
	public static boolean CCWcheck(final Vector2d p0, final Vector2d p1, final Vector2d q) {
		double dx1, dx2, dy1, dy2;

		dx1 = p1.x - p0.x;
		dy1 = p1.y - p0.y;
		dx2 = q.x - p0.x;
		dy2 = q.y - p0.y;

		if (dx1 * dy2 > dy1 * dx2) {
			return true;
		}
		return false;
	}

	private static double distance(final double x0, final double y0, final double x1,
			final double y1) {
		return Math.sqrt((x0 - x1) * (x0 - x1) + (y0 - y1) * (y0 - y1));
	}

	// FIXME: Circular package dependency
	public static boolean isContainsPointFoldedFace(final OriFace face, final Vector2d v,
			final double eps) {

		int heNum = face.halfedges.size();

		// If its on the faces edge, return false
		for (int i = 0; i < heNum; i++) {
			OriHalfedge he = face.halfedges.get(i);
			if (GeomUtil.distancePointToSegment(v, he.positionAfterFolded,
					he.next.positionAfterFolded) < eps) {
				return false;
			}
		}

		OriHalfedge baseHe = face.halfedges.get(0);
		boolean baseFlg = GeomUtil.CCWcheck(baseHe.positionAfterFolded,
				baseHe.next.positionAfterFolded, v);

		for (int i = 1; i < heNum; i++) {
			OriHalfedge he = face.halfedges.get(i);
			if (GeomUtil.CCWcheck(he.positionAfterFolded, he.next.positionAfterFolded,
					v) != baseFlg) {
				return false;
			}
		}

		return true;
	}

//    private static int whichSide(Triangle tri, Vector2d P, Vector2d D) {
//
//        // Vertices are projected to the form P+t*D.  Return value is +1 if all
//        // t > 0, -1 if all t < 0, 0 otherwise, in which case the line splits the
//        // triangle.
//
//        int iPositive = 0, iNegative = 0, iZero = 0;
//
//        for (int i = 0; i < 3; i++) {
//            Vector2d vi_p = new Vector2d();
//            vi_p.set(tri.p[i].x - P.x, tri.p[i].y - P.y);
//            double fT = D.dot(vi_p);
//
//            if (fT > 0.0f) {
//                iPositive++;
//            } else if (fT < 0.0f) {
//                iNegative++;
//            } else {
//                iZero++;
//            }
//
//            if (iPositive > 0 && iNegative > 0) {
//                return 0;
//            }
//        }
//
//        return (iZero == 0 ? (iPositive > 0 ? 1 : -1) : 0);
//    }

	// FIXME: Circular package dependency
	public static boolean isFaceOverlap(final OriFace face0, final OriFace face1,
			final double eps) {
		Vector2d center0 = new Vector2d();
		Vector2d center1 = new Vector2d();

		// If the vertices of face0 are on face1, true
		for (OriHalfedge he : face0.halfedges) {
			if (isContainsPointFoldedFace(face1, he.positionAfterFolded, eps)) {
				return true;
			}
			center0.add(he.positionAfterFolded);
		}

		// If the vertices of face1 are on face0, true
		for (OriHalfedge he : face1.halfedges) {
			if (isContainsPointFoldedFace(face0, he.positionAfterFolded, eps)) {
				return true;
			}
			center1.add(he.positionAfterFolded);
		}

		center0.scale(1.0 / face0.halfedges.size());
		// If the gravity center of face0 is on face1, true
		if (isContainsPointFoldedFace(face1, center0, eps)) {
			return true;
		}

		center1.scale(1.0 / face1.halfedges.size());
		// If the gravity center of face1 is on face0, true
		if (isContainsPointFoldedFace(face0, center1, eps)) {
			return true;
		}

		// If the outline of face0 intersects face1`s, true
		for (OriHalfedge he0 : face0.halfedges) {
			if (isLineCrossFace(face1, he0, eps)) {
				return true;
			}
		}

		for (OriHalfedge he1 : face1.halfedges) {
			if (isLineCrossFace(face0, he1, eps)) {
				return true;
			}
		}
		return false;
	}

	// FIXME: Circular package dependency
	public static boolean isOriLineCrossFace(final OriFace face, final OriLine line) {
		return isContainsPointFace(line.p0, face) && isContainsPointFace(line.p1, face);
	}

	// FIXME: Circular package dependency
	private static boolean isContainsPointFace(final Vector2d v, final OriFace face) {
		int heNum = face.halfedges.size();

		// If its on the faces edge, return true
		for (int i = 0; i < heNum; i++) {
			OriHalfedge he = face.halfedges.get(i);
			if (GeomUtil.distancePointToSegment(v, he.vertex.p,
					he.next.vertex.p) < EPS) {
				return true;
			}
		}

		OriHalfedge baseHe = face.halfedges.get(0);
		boolean baseFlg = GeomUtil.CCWcheck(baseHe.vertex.p,
				baseHe.next.vertex.p, v);

		for (int i = 1; i < heNum; i++) {
			OriHalfedge he = face.halfedges.get(i);
			if (GeomUtil.CCWcheck(he.vertex.p,
					he.next.vertex.p, v) != baseFlg) {
				return false;
			}
		}

		return true;
	}

	// FIXME: Circular package dependency
	public static boolean isLineCrossFace(final OriFace face, final OriHalfedge heg,
			final double eps) {
		Vector2d p1 = heg.positionAfterFolded;
		Vector2d p2 = heg.next.positionAfterFolded;
		Vector2d dir = new Vector2d();
		dir.sub(p2, p1);
		Line heLine = new Line(p1, dir);

		for (OriHalfedge he : face.halfedges) {
			// About the relationship of each outline`s segment

			if (GeomUtil.distancePointToLine(he.positionAfterFolded, heLine) < eps
					&& GeomUtil.distancePointToLine(he.next.positionAfterFolded, heLine) < eps) {
				return false;
			}
		}
		Vector2d preCrossPoint = null;
		for (OriHalfedge he : face.halfedges) {
			Vector2d cp = GeomUtil.getCrossPoint(he.positionAfterFolded,
					he.next.positionAfterFolded, heg.positionAfterFolded,
					heg.next.positionAfterFolded);
			if (cp == null) {
				continue;
			}

			if (preCrossPoint == null) {
				preCrossPoint = cp;
			} else {
				if (GeomUtil.distance(cp, preCrossPoint) > eps) {
					// Intersects at least in two places
					return true;
				}
			}
		}
		// If at least one of the endpoints is fully contained
		if (GeomUtil.isContainsPointFoldedFace(face, heg.positionAfterFolded, eps)) {
			return true;
		}
		if (GeomUtil.isContainsPointFoldedFace(face, heg.next.positionAfterFolded, eps)) {
			return true;
		}
		return false;
	}
}
