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

import javax.vecmath.Vector2d;

import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OriHalfedge;
import oripa.domain.fold.halfedge.OriVertex;
import oripa.geom.GeomUtil;
import oripa.geom.Line;
import oripa.geom.Segment;

/**
 * Mathematical operations related to half-edge data structure elements.
 *
 * @author OUCHI Koji
 *
 */
public class OriGeomUtil {

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
		if (face0.halfedgeStream().anyMatch(he0 -> isLineCrossFace(face1, he0, eps))) {
			return true;
		}

		// If the outline of face1 intersects face0's, true
		if (face1.halfedgeStream().anyMatch(he1 -> isLineCrossFace(face0, he1, eps))) {
			return true;
		}

		return false;
	}

	/**
	 * Whether {@code heg} crosses {@code face}. The inclusion test is
	 * exclusive.
	 *
	 * @param face
	 * @param heg
	 * @param size
	 * @return {@code true} if {@code heg} crosses {@code face} after folding.
	 *         {@code false} if {@code heg} doesn't cross {@code face} or both
	 *         end points of {@code heg} are on an edge of {@code face}.
	 */
	public static boolean isLineCrossFace4(final OriFace face, final OriHalfedge heg,
			final double size) {
		final double eps = size * 0.001;

		return isLineCrossFace(face, heg, eps);
//		if (isLineOnEdgeOfFace(face, heg, 1)) {
//			return false;
//		}
//
//		if (isHalfedgeCrossTwoEdgesOfFace(face, heg, eps)) {
//			return true;
//		}
//
//		if (isHalfedgeCrossEdgeOfFace(face, heg, eps)) {
//			return true;
//		}
//
//		return false;
	}

	/**
	 * Whether {@code heg} crosses {@code face}. The inclusion test is
	 * exclusive.
	 *
	 * @param face
	 * @param heg
	 * @param eps
	 * @return {@code true} if {@code heg} crosses {@code face} after folding.
	 *         {@code false} if {@code heg} doesn't cross {@code face} or both
	 *         end points of {@code heg} are on an edge of {@code face}.
	 */
	public static boolean isLineCrossFace(final OriFace face, final OriHalfedge heg,
			final double eps) {
		if (isLineOnEdgeOfFace(face, heg, eps)) {
			return false;
		}

		if (isHalfedgeCrossTwoEdgesOfFace(face, heg, eps)) {
			return true;
		}

		if (isHalfedgeCrossEdgeOfFace(face, heg, eps)) {
			return true;
		}

		return false;
	}

	private static boolean isLineOnEdgeOfFace(final OriFace face, final OriHalfedge heg, final double eps) {
		Vector2d p1 = heg.getPosition();
		Vector2d p2 = heg.getNext().getPosition();
		Vector2d dir = new Vector2d();
		dir.sub(p2, p1);
		Line heLine = new Line(p1, dir);

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

	private static boolean isHalfedgeCrossEdgeOfFace(final OriFace face, final OriHalfedge heg, final double eps) {
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
	public static boolean isOriLineIncludedInFace(final OriFace face, final Segment line) {
		return face.isOnFaceInclusively(line.getP0())
				&& face.isOnFaceInclusively(line.getP1());
	}

	/**
	 * The angle between edges v1-v2 and v2-v3.
	 *
	 * @param v1
	 * @param v2
	 * @param v3
	 * @return 0 to pi between edges v1-v2 and v2-v3
	 */
	public static double getAngleDifference(
			final OriVertex v1, final OriVertex v2, final OriVertex v3) {
		var preP = new Vector2d(v1.getPosition());
		var p = new Vector2d(v2.getPosition());
		var nxtP = new Vector2d(v3.getPosition());

		nxtP.sub(p);
		preP.sub(p);

		return preP.angle(nxtP);
	}
}
