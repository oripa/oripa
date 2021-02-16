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
package oripa.domain.fold;

import java.util.function.Function;
import java.util.stream.Collectors;

import javax.vecmath.Vector2d;

import oripa.geom.GeomUtil;
import oripa.geom.Line;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
class OriGeomUtil {

	static boolean isFaceOverlap(final OriFace face0, final OriFace face1,
			final double eps) {

		// If the vertices of face0 are on face1, true
		for (OriHalfedge he : face0.halfedges) {
			if (isOnFoldedFace(face1, he.positionAfterFolded, eps)) {
				return true;
			}
		}

		// If the vertices of face1 are on face0, true
		for (OriHalfedge he : face1.halfedges) {
			if (isOnFoldedFace(face0, he.positionAfterFolded, eps)) {
				return true;
			}
		}

		// TODO: Can these be replaced with OriFace.getCentroidAfterFolding()?
		Vector2d center0 = GeomUtil.computeCentroid(face0.halfedges.stream()
				.map(he -> he.positionAfterFolded).collect(Collectors.toList()));
		Vector2d center1 = GeomUtil.computeCentroid(face1.halfedges.stream()
				.map(he -> he.positionAfterFolded).collect(Collectors.toList()));

		// If the gravity center of face0 is on face1, true
		if (isOnFoldedFace(face1, center0, eps)) {
			return true;
		}

		// If the gravity center of face1 is on face0, true
		if (isOnFoldedFace(face0, center1, eps)) {
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

	/**
	 *
	 * @param face
	 * @param heg
	 * @param eps
	 * @return true if a line along {@code heg} crosses {@code face}.
	 */
	static boolean isLineCrossFace(final OriFace face, final OriHalfedge heg,
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
		if (isOnFoldedFace(face, heg.positionAfterFolded, eps)) {
			return true;
		}
		if (isOnFoldedFace(face, heg.next.positionAfterFolded, eps)) {
			return true;
		}
		return false;
	}

	static boolean isOriLineCrossFace(final OriFace face, final OriLine line) {
		return isOnFaceInclusive(line.p0, face)
				&& isOnFaceInclusive(line.p1, face);
	}

	static boolean isLineCrossFace4(final OriFace face, final OriHalfedge heg, final double size) {
		Vector2d p1 = heg.positionAfterFolded;
		Vector2d p2 = heg.next.positionAfterFolded;
		Vector2d dir = new Vector2d();
		dir.sub(p2, p1);
		Line heLine = new Line(p1, dir);

		final double eps = size * 0.001;

		for (OriHalfedge he : face.halfedges) {
			// About the relation of contours (?)

			// Check if the line is on the contour of the face
			if (GeomUtil.distancePointToLine(he.positionAfterFolded, heLine) < 1
					&& GeomUtil.distancePointToLine(he.next.positionAfterFolded, heLine) < 1) {
				return false;
			}
		}

		Vector2d preCrossPoint = null;
		for (OriHalfedge he : face.halfedges) {
			// Checks if the line crosses any of the edges of the face
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
					return true;
				}
			}
		}

		// Checks if the line is in the interior of the face
		if (isOnFoldedFace(face, heg.positionAfterFolded, eps)) {
			return true;
		}
		if (isOnFoldedFace(face, heg.next.positionAfterFolded, eps)) {
			return true;
		}

		return false;
	}

	private static boolean isOnFaceInclusive(final Vector2d v, final OriFace face) {
		// If its on the faces edge, return true
		if (isOnEdgeOfFace(face, v, GeomUtil.EPS, he -> he.vertex.p)) {
			return true;
		}

		return isInsideFace(face, v, GeomUtil.EPS, he -> he.vertex.p);
	}

	/**
	 * Whether v is inside of face or is on edge of face where the positions are
	 * ones after folding.
	 *
	 * @param face
	 * @param v
	 * @param eps
	 * @return
	 */
	static boolean isOnFoldedFace(final OriFace face, final Vector2d v,
			final double eps) {
		// If its on the faces edge, return false
		if (isOnEdgeOfFace(face, v, eps, he -> he.positionAfterFolded)) {
			return false;
		}

		return isInsideFace(face, v, eps, he -> he.positionAfterFolded);
	}

	/**
	 * Whether v is on edge of face.
	 *
	 * @param face
	 * @param v
	 * @param eps
	 * @param getPosition
	 * @return
	 */
	private static boolean isOnEdgeOfFace(final OriFace face, final Vector2d v, final double eps,
			final Function<OriHalfedge, Vector2d> getPosition) {
		int heNum = face.halfedges.size();

		// If its on the face's edge, return true
		for (int i = 0; i < heNum; i++) {
			OriHalfedge he = face.halfedges.get(i);
			if (GeomUtil.distancePointToSegment(v, getPosition.apply(he),
					getPosition.apply(he.next)) < eps) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Whether v is inside of face. Don't care whether v is on edge of face.
	 * Don't care whether v is on edges of face.
	 *
	 * @param face
	 * @param v
	 * @param eps
	 * @param getPosition
	 * @return
	 */
	private static boolean isInsideFace(final OriFace face, final Vector2d v, final double eps,
			final Function<OriHalfedge, Vector2d> getPosition) {
		int heNum = face.halfedges.size();

		OriHalfedge baseHe = face.halfedges.get(0);
		boolean baseFlg = GeomUtil.CCWcheck(getPosition.apply(baseHe),
				getPosition.apply(baseHe.next), v);

		for (int i = 1; i < heNum; i++) {
			OriHalfedge he = face.halfedges.get(i);
			if (GeomUtil.CCWcheck(getPosition.apply(he), getPosition.apply(he.next),
					v) != baseFlg) {
				return false;
			}
		}

		return true;
	}

	/**
	 *
	 * @param v1
	 * @param v2
	 * @param v3
	 * @return 0 to pi between edges v1-v2 and v2-v3
	 */
	public static double getAngleDifference(
			final OriVertex v1, final OriVertex v2, final OriVertex v3) {
		var preP = new Vector2d(v1.p);
		var p = new Vector2d(v2.p);
		var nxtP = new Vector2d(v3.p);

		nxtP.sub(p);
		preP.sub(p);

		return preP.angle(nxtP);
	}
}
