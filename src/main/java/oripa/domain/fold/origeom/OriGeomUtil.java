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

import java.util.stream.Collectors;

import javax.vecmath.Vector2d;

import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OriHalfedge;
import oripa.domain.fold.halfedge.OriVertex;
import oripa.geom.GeomUtil;
import oripa.geom.Line;
import oripa.value.OriLine;

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
		for (OriHalfedge he : face0.halfedges) {
			if (face1.isOnFoldedFaceExclusively(he.positionAfterFolded, eps)) {
				return true;
			}
		}

		// If the vertices of face1 are on face0, true
		for (OriHalfedge he : face1.halfedges) {
			if (face0.isOnFoldedFaceExclusively(he.positionAfterFolded, eps)) {
				return true;
			}
		}

		// TODO: Can these be replaced with OriFace.getCentroid()?
		Vector2d center0 = GeomUtil.computeCentroid(face0.halfedges.stream()
				.map(he -> he.positionAfterFolded).collect(Collectors.toList()));
		Vector2d center1 = GeomUtil.computeCentroid(face1.halfedges.stream()
				.map(he -> he.positionAfterFolded).collect(Collectors.toList()));

		// If the gravity center of face0 is on face1, true
		if (face1.isOnFoldedFaceExclusively(center0, eps)) {
			return true;
		}

		// If the gravity center of face1 is on face0, true
		if (face0.isOnFoldedFaceExclusively(center1, eps)) {
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
	 * Whether {@code heg} crosses {@code face} after folding. The inclusion
	 * test is exclusive.
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
		Vector2d p1 = heg.positionAfterFolded;
		Vector2d p2 = heg.getNext().positionAfterFolded;
		Vector2d dir = new Vector2d();
		dir.sub(p2, p1);
		Line heLine = new Line(p1, dir);

		var hegNext = heg.getNext();

		for (OriHalfedge he : face.halfedges) {
			// About the relationship of each outline`s segment

			if (GeomUtil.distancePointToLine(he.positionAfterFolded, heLine) < eps
					&& GeomUtil.distancePointToLine(he.getNext().positionAfterFolded, heLine) < eps) {
				return false;
			}
		}
		Vector2d preCrossPoint = null;
		for (OriHalfedge he : face.halfedges) {
			Vector2d cp = GeomUtil.getCrossPoint(he.positionAfterFolded,
					he.getNext().positionAfterFolded, heg.positionAfterFolded,
					hegNext.positionAfterFolded);
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
		if (face.isOnFoldedFaceExclusively(heg.positionAfterFolded, eps)) {
			return true;
		}
		if (face.isOnFoldedFaceExclusively(hegNext.positionAfterFolded, eps)) {
			return true;
		}
		return false;
	}

	/**
	 * Whether {@code face} includes {@code line} entirely. The inclusion test
	 * is inclusive.
	 *
	 * @param face
	 * @param line
	 * @return {@code true} if {@code face} includes {@code line} entirely.
	 */
	public static boolean isOriLineIncludedInFace(final OriFace face, final OriLine line) {
		return face.isOnFaceInclusively(line.p0)
				&& face.isOnFaceInclusively(line.p1);
	}

	/**
	 * Whether {@code heg} crosses {@code face} after folding. The inclusion
	 * test is exclusive.
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
		Vector2d p1 = heg.positionAfterFolded;
		Vector2d p2 = heg.getNext().positionAfterFolded;
		Vector2d dir = new Vector2d();
		dir.sub(p2, p1);
		Line heLine = new Line(p1, dir);

		final double eps = size * 0.001;

		var hegNext = heg.getNext();

		for (OriHalfedge he : face.halfedges) {
			// About the relation of contours (?)

			// Check if the line is on the contour of the face
			if (GeomUtil.distancePointToLine(he.positionAfterFolded, heLine) < 1
					&& GeomUtil.distancePointToLine(he.getNext().positionAfterFolded, heLine) < 1) {
				return false;
			}
		}

		Vector2d preCrossPoint = null;
		for (OriHalfedge he : face.halfedges) {
			// Checks if the line crosses any of the edges of the face
			Vector2d cp = GeomUtil.getCrossPoint(he.positionAfterFolded,
					he.getNext().positionAfterFolded, heg.positionAfterFolded,
					hegNext.positionAfterFolded);
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
		if (face.isOnFoldedFaceExclusively(heg.positionAfterFolded, eps)) {
			return true;
		}
		if (face.isOnFoldedFaceExclusively(hegNext.positionAfterFolded, eps)) {
			return true;
		}

		return false;
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
