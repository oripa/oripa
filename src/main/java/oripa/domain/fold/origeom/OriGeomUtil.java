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
package oripa.domain.fold.origeom;

import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OriHalfedge;
import oripa.domain.fold.halfedge.OriVertex;
import oripa.geom.GeomUtil;
import oripa.geom.Segment;
import oripa.util.MathUtil;
import oripa.vecmath.Vector2d;

/**
 * Mathematical operations related to half-edge data structure elements.
 *
 * @author OUCHI Koji
 *
 */
public class OriGeomUtil {

//	public static double pointEps(final double paperSize) {
//		return paperSize * 1e-7;
//	}

	/**
	 * Whether {@code face0} and {@code face1} overlap partially or entirely
	 * after fold.
	 *
	 * @param face0
	 * @param face1
	 * @param eps
	 * @return {@code true} if {@code face0} and {@code face1} overlap partially
	 *         or entirely after fold.
	 */
	public static boolean isFaceOverlap(final OriFace face0, final OriFace face1,
			final double eps) {

		// If the vertices of face0 are on face1, true
		if (face0.halfedgeStream().anyMatch(he -> face1.isOnFaceExclusively(he.getPosition(), eps))) {
			return true;
		}

		// If the vertices of face1 are on face0, true
		if (face1.halfedgeStream().anyMatch(he -> face0.isOnFaceExclusively(he.getPosition(), eps))) {
			return true;
		}

		Vector2d center0 = face0.getCentroid();
		Vector2d center1 = face1.getCentroid();

		// If the gravity center of face0 is on face1, true
		if (face1.isOnFaceExclusively(center0, eps)) {
			return true;
		}

		// If the gravity center of face1 is on face0, true
		if (face0.isOnFaceExclusively(center1, eps)) {
			return true;
		}

		// If the outline of face0 intersects face1's, true
		if (face0.halfedgeStream().anyMatch(he0 -> isHalfedgeCrossFace(face1, he0, eps))) {
			return true;
		}

		// If the outline of face1 intersects face0's, true
		if (face1.halfedgeStream().anyMatch(he1 -> isHalfedgeCrossFace(face0, he1, eps))) {
			return true;
		}

		return false;
	}

	/**
	 * Whether {@code heg} crosses {@code face}. This method also returns
	 * {@code true} if {@code face} includes both end points of the {@code heg}.
	 * The inclusion test is exclusive.
	 *
	 * @param face
	 * @param heg
	 * @param eps
	 * @return {@code true} if {@code heg} crosses {@code face} after folding.
	 *         {@code false} if {@code heg} doesn't cross {@code face} or both
	 *         end points of {@code heg} are on an edge of {@code face}.
	 */
	public static boolean isHalfedgeCrossFace(final OriFace face, final OriHalfedge heg,
			final double eps) {
		// if halfedge is on a line on a face's edge, they are parallel and
		// never cross.
		if (isLineOnEdgeOfFace(face, heg, eps)) {
			return false;
		}

		if (isHalfedgeCrossTwoEdgesOfFace(face, heg, eps)) {
			return true;
		}

		if (isHalfedgeCrossEdgeOrIncluded(face, heg, eps)) {
			return true;
		}

		return false;
	}

	private static boolean isLineOnEdgeOfFace(final OriFace face, final OriHalfedge heg, final double eps) {
		Vector2d p1 = heg.getPosition();
		Vector2d p2 = heg.getNext().getPosition();
		var heLine = new Segment(p1, p2).getLine();

		return face.halfedgeStream().anyMatch(he -> GeomUtil.distancePointToLine(he.getPosition(), heLine) < eps
				&& GeomUtil.distancePointToLine(he.getNext().getPosition(), heLine) < eps);
	}

	private static boolean isHalfedgeCrossTwoEdgesOfFace(final OriFace face, final OriHalfedge heg, final double eps) {
		Vector2d preCrossPoint = null;
		for (OriHalfedge he : face.halfedgeIterable()) {
			// Checks if the line crosses any of the edges of the face
			Vector2d cp = GeomUtil.getCrossPoint(he.getPosition(),
					he.getNext().getPosition(), heg.getPosition(),
					heg.getNext().getPosition());
			if (cp == null) {
				continue;
			}

			if (preCrossPoint == null) {
				preCrossPoint = cp;
			} else {
				if (GeomUtil.distance(cp, preCrossPoint) > eps) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean isHalfedgeCrossEdgeOrIncluded(final OriFace face, final OriHalfedge heg, final double eps) {
		// If at least one of the endpoints is fully contained

		return face.isOnFaceExclusively(heg.getPosition(), eps)
				|| face.isOnFaceExclusively(heg.getNext().getPosition(), eps);
	}

	/**
	 * Whether {@code face} includes {@code line} entirely. The inclusion test
	 * is inclusive.
	 *
	 * @param face
	 * @param line
	 * @return {@code true} if {@code face} includes {@code line} entirely.
	 */
	public static boolean isSegmentIncludedInFace(final OriFace face, final Segment line, final double eps) {
		return face.isOnFaceInclusively(line.getP0(), eps)
				&& face.isOnFaceInclusively(line.getP1(), eps);
	}

	/**
	 * The angle between edges v1-v2 and v2-v3.
	 *
	 * @param v1
	 * @param v2
	 * @param v3
	 * @return 0 to 2 * pi between edges v1-v2 and v2-v3
	 */
	private static double getAngleDifference(
			final OriVertex v1, final OriVertex v2, final OriVertex v3) {
		var p = v2.getPositionBeforeFolding();
		var preP = v1.getPositionBeforeFolding().subtract(p);
		var nxtP = v3.getPositionBeforeFolding().subtract(p);

		var prePAngle = MathUtil.angleOf(preP);
		var nxtPAngle = MathUtil.angleOf(nxtP);

		if (prePAngle > nxtPAngle) {
			nxtPAngle += 2 * Math.PI;
		}

		return nxtPAngle - prePAngle;

//		return preP.angle(nxtP); // fails if a concave face exists.
	}

	/**
	 * The angle between i-th edge and (i+1)-th edge incident to {@code v}.
	 *
	 * @param v
	 * @param index
	 * @return 0 to 2 * pi between i-th edge and (i+1)-th edge
	 */
	public static double getAngleDifference(final OriVertex v, final int index) {
		return getAngleDifference(
				v.getOppositeVertex(index),
				v,
				v.getOppositeVertex(index + 1));
	}
}
