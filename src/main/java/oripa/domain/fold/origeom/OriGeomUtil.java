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

import java.util.function.Predicate;

import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OriHalfedge;
import oripa.geom.GeomUtil;
import oripa.geom.Segment;
import oripa.vecmath.Vector2d;

/**
 * Mathematical operations related to half-edge data structure elements. This
 * class contains complex methods or methods whose responsibility among related
 * objects is ambiguous.
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

        return testFaceOverlap(face0, face1, eps) || testFaceOverlap(face1, face0, eps);
    }

    private static boolean testFaceOverlap(final OriFace face0, final OriFace face1, final double eps) {
        // If the vertices of face0 are on face1, true
        if (face0.halfedgeStream().anyMatch(he -> face1.includesExclusively(he.getPosition(), eps))) {
            return true;
        }

        // If a inner point of face0 is on face1, true
        var innerPoints0 = face0.getInnerPoints(eps);
        if (innerPoints0.stream().anyMatch(p -> face1.includesExclusively(p, eps))) {
            return true;
        }

        // If the outline of face0 intersects face1's, true
        if (face0.halfedgeStream().anyMatch(he0 -> isHalfedgeCrossFace(face1, he0, eps))) {
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

        Predicate<Vector2d> isOnLine = p -> GeomUtil.distancePointToLine(p, heLine) < eps;

        return face.halfedgeStream().anyMatch(he -> isOnLine.test(he.getPosition())
                && isOnLine.test(he.getNext().getPosition()));
    }

    private static boolean isHalfedgeCrossTwoEdgesOfFace(final OriFace face, final OriHalfedge heg, final double eps) {
        Vector2d preCrossPoint = null;
        for (OriHalfedge he : face.halfedgeIterable()) {
            // Checks if the line crosses any of the edges of the face
            var cpOpt = GeomUtil.getCrossPoint(he.getPosition(),
                    he.getNext().getPosition(), heg.getPosition(),
                    heg.getNext().getPosition());
            if (cpOpt.isEmpty()) {
                continue;
            }

            var cp = cpOpt.get();

            if (preCrossPoint == null) {
                preCrossPoint = cp;
            } else {
                if (!cp.equals(preCrossPoint, eps)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isHalfedgeCrossEdgeOrIncluded(final OriFace face, final OriHalfedge heg, final double eps) {
        // If at least one of the end points is fully contained

        return face.includesExclusively(heg.getPosition(), eps)
                || face.includesExclusively(heg.getNext().getPosition(), eps);
    }

}
