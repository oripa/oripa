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

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.fold.halfedge.OriEdge;
import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OriHalfedge;
import oripa.domain.fold.halfedge.OriVertex;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.geom.GeomUtil;
import oripa.util.MathUtil;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
class SimpleFolder {
    private static final Logger logger = LoggerFactory.getLogger(SimpleFolder.class);

    /**
     * Does a simple fold as a preparation of layer order enumeration.
     *
     * @param origamiModel
     *            half-edge based data structure. It will be affected by this
     *            method.
     */
    void simpleFoldWithoutZOrder(
            final OrigamiModel origamiModel, final double eps) {
        foldImpl(origamiModel, eps, false);
    }

    /**
     * Computes position of each face after folding given model which contains
     * some error. The number of faces to be moved is limited to avoid troubles.
     *
     * @param model
     *            half-edge based data structure. It will be affected by this
     *            method.
     */
    public void foldWithoutLineType(
            final OrigamiModel model, final double eps) {
        foldImpl(model, eps, true);
    }

    private void foldImpl(final OrigamiModel origamiModel, final double eps, final boolean limitUsed) {
        List<OriEdge> edges = origamiModel.getEdges();
        List<OriFace> faces = origamiModel.getFaces();

        int id = 0;
        for (OriFace face : faces) {
            face.setFaceID(id);
            id++;
        }

        if (faces.size() > 0) {
            var jobs = new LinkedList<OriFace>();
            var paperCenterOpt = GeomUtil.computeCentroid(
                    origamiModel.getVertices().stream()
                            .map(OriVertex::getPosition)
                            .toList());
            jobs.add(faces.stream()
                    .filter(face -> face.includesInclusively(paperCenterOpt.get(), eps))
                    .findFirst()
                    .orElse(faces.get(0)));
            jobs.get(0).setMovedByFold(true);

            moveAndFlipFaces(jobs, limitUsed ? 1000 : Integer.MAX_VALUE, eps);
        }

        for (OriEdge e : edges) {
            var v0 = e.getV0();
            var v1 = e.getV1();

            v0.setPosition(e.getHalfedgeV0V1().orElseThrow().getPositionWhileFolding());

            var v1V0Opt = e.getHalfedgeV1V0();
            v1V0Opt.map(OriHalfedge::getPositionWhileFolding)
                    .ifPresent(position -> v1.setPosition(position));
        }

        for (var face : faces) {
            face.refreshPositions(eps);
        }

    }

    /**
     * Breadth first search to move faces to the position after folding with the
     * narrow number of transformations for each face. This approach reduces the
     * numerical error.
     *
     * @param faces
     * @param callLimit
     */
    private void moveAndFlipFaces(final Queue<OriFace> faces, final int callLimit, final double eps) {
        int callCount = 0;
        while (!faces.isEmpty() && callCount < callLimit) {
            callCount++;

            var face = faces.remove();

            face.halfedgeStream().forEach(he -> {
                var pairOpt = he.getPair();
                if (pairOpt.isEmpty()) {
                    return;
                }

                var pairFace = pairOpt.get().getFace();
                if (pairFace.isMovedByFold()) {
                    return;
                }

                moveAndFlipFace(pairFace, he);
                pairFace.setMovedByFold(true);

                faces.add(pairFace);
            });
        }
    }

    /**
     * Move the {@code vertex} keeping the face connection. The transformation
     * is based on geometric interpretation without affine transformation.
     *
     * @param vertex
     *            vertex to be moved.
     * @param preLine
     *            crease line before folding.
     * @param afterOrigin
     *            a reference point on the moved crease line.
     * @param afterDir
     *            clockwise direction vector of the moved crease line.
     */
    private Vector2d moveVertexKeepingFaceConnection(final Vector2d vertex,
            final Vector2d origin, final Vector2d dir,
            final Vector2d afterOrigin, final Vector2d afterDir) {
        var distanceAndProjection = GeomUtil.distanceToLineAndProjection(vertex, origin, dir);
        double distanceLineToVertex = distanceAndProjection.v1();
        // distance between origin and foot cross point from
        // vertex to line
        var afterOriginToFootDir = afterDir.multiply(distanceAndProjection.v2());

        // compute a direction vector perpendicular to the crease.
        // the vector directs the right side of the crease halfedge
        // since all vertices of the face should be on the right side of the
        // crease halfedge after the move.
        var afterDirFromFoot = afterDir.getRightSidePerpendicular().multiply(distanceLineToVertex);

        // compute moved vertex coordinates
        // = afterOrigin + afterOriginToFootDir + afterDirFromFoot
        // trying to reduce digit loss.
        // note: afterOrigin + originToFootDir is a foot cross point from vertex
        // to moved mirror axis line (or the crease)
        var terms = List.of(afterOrigin, afterOriginToFootDir, afterDirFromFoot);
        double x = MathUtil.preciseSum(terms.stream().map(Vector2d::getX).toList());
        double y = MathUtil.preciseSum(terms.stream().map(Vector2d::getY).toList());
        return new Vector2d(x, y);
    }

    /**
     * Moves the given face and flips it if neccesary. the given halfedge has to
     * be paired with a halfedge of the face and should have the position after
     * move.
     *
     * @param face
     *            to be moved
     * @param movedBaseHe
     *            a halfedge that is paired with a halfedge of {@code face}.
     *            {@code movedBaseHe} should have been moved.
     */
    private void moveAndFlipFace(final OriFace face, final OriHalfedge movedBaseHe) {
        // pair of moved base halfedge is not moved yet and is of the given
        // face.
        var baseHe = movedBaseHe.getPair().orElseThrow();
        var baseHeNext = baseHe.getNext();

        // baseHe.pair keeps the position before folding.
        var preOrigin = baseHeNext.getPositionWhileFolding();
        var afterOrigin = movedBaseHe.getPositionWhileFolding();

        // Creates the base unit vector for the line before move.
        // note that the direction of baseHe is reverse of movedBaseHe.
        var baseDir = baseHe.getPositionWhileFolding().subtract(baseHeNext.getPositionWhileFolding()).normalize();

        // Creates the base unit vector for the line after move
        var movedBaseHeNext = movedBaseHe.getNext();
        var afterDir = movedBaseHeNext.getPositionWhileFolding().subtract(movedBaseHe.getPositionWhileFolding())
                .normalize();

        // move the vertices of the face to keep the face connection
        // on baseHe
        face.halfedgeStream().forEach(he -> {
            he.setPositionWhileFolding(moveVertexKeepingFaceConnection(
                    he.getPositionWhileFolding(), preOrigin, baseDir, afterOrigin, afterDir));
        });

        face.setPrecreases(face.precreaseStream().map(precrease -> new OriLine(
                moveVertexKeepingFaceConnection(precrease.getP0(), preOrigin, baseDir, afterOrigin, afterDir),
                moveVertexKeepingFaceConnection(precrease.getP1(), preOrigin, baseDir, afterOrigin, afterDir),
                OriLine.Type.AUX))
                .toList());

        // add mirror effect if necessary
        if (face.isFaceFront() == movedBaseHe.getFace().isFaceFront()) {
            var v1 = movedBaseHeNext.getPositionWhileFolding();
            var v0 = movedBaseHe.getPositionWhileFolding();

            face.halfedgeStream().forEach(he -> {
                he.setPositionWhileFolding(flipVertex(he.getPositionWhileFolding(), v0, v1));
            });
            face.setPrecreases(face.precreaseStream().map(precrease -> new OriLine(
                    flipVertex(precrease.getP0(), v0, v1),
                    flipVertex(precrease.getP1(), v0, v1),
                    OriLine.Type.AUX))
                    .toList());
            face.invertFaceFront();
        }
    }

    private Vector2d flipVertex(final Vector2d vertex, final Vector2d v0, final Vector2d v1) {
        return GeomUtil.getSymmetricPoint(vertex, v0, v1);
    }

}
