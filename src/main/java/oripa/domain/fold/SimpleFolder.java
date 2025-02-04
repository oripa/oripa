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
import oripa.geom.Line;
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
	void simpleFoldWithoutZorder(
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

			transformFaces(jobs, limitUsed ? 1000 : Integer.MAX_VALUE, eps);
		}

		for (OriEdge e : edges) {
			var sv = e.getStartVertex();
			var ev = e.getEndVertex();

			sv.setPosition(e.getLeft().orElseThrow().getPositionWhileFolding());

			var rightOpt = e.getRight();
			rightOpt.map(OriHalfedge::getPositionWhileFolding)
					.ifPresent(position -> ev.setPosition(position));
		}

		for (var face : faces) {
			face.refreshPositions();
		}

	}

	/**
	 * Breath first search to move faces to the position after folding with the
	 * narrow number of transformations for each face. This approach reduces the
	 * numerical error.
	 *
	 * @param faces
	 * @param callLimit
	 */
	private void transformFaces(final Queue<OriFace> faces, final int callLimit, final double eps) {
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

				pairFace.setMovedByFold(true);
				flipFace(pairFace, he);

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
	private Vector2d transformVertex(final Vector2d vertex, final Line preLine,
			final Vector2d afterOrigin, final Vector2d afterDir) {
		double param[] = new double[1];
		double d0 = GeomUtil.distance(vertex, preLine, param);
		// distance between reference point of preLine and foot cross point from
		// vertex to preLine
		double d1 = param[0];

		var originToFootDir = afterDir.multiply(d1);

		// compute a direction vector perpendicular to the crease.
		// the vector directs the right side of the crease halfedge
		// since all vertices of the face are on the right side of the crease
		// halfedge.
		var afterDirFromFoot = afterDir.getRightSidePerpendicular().multiply(d0);

		// compute moved vertex coordinates
		// = afterOrigin + originToFootDir + afterDirFromFoot
		// trying to reduce digit loss.
		// note: afterOrigin + originToFootDir is a foot cross point from vertex
		// to moved mirror axis line (or the crease)
		var terms = List.of(afterOrigin, originToFootDir, afterDirFromFoot);
		double x = MathUtil.preciseSum(terms.stream().map(Vector2d::getX).toList());
		double y = MathUtil.preciseSum(terms.stream().map(Vector2d::getY).toList());
		return new Vector2d(x, y);
	}

	private void flipFace(final OriFace face, final OriHalfedge baseHe) {
		var baseHePair = baseHe.getPair().orElseThrow();
		var baseHePairNext = baseHePair.getNext();

		// baseHe.pair keeps the position before folding.
		var preOrigin = baseHePairNext.getPositionWhileFolding();
		var afterOrigin = baseHe.getPositionWhileFolding();

		// Creates the base unit vector for before the rotation
		// (reversed direction)
		var baseDir = baseHePair.getPositionWhileFolding().subtract(baseHePairNext.getPositionWhileFolding());

		// Creates the base unit vector for after the rotation
		var baseHeNext = baseHe.getNext();
		var afterDir = baseHeNext.getPositionWhileFolding().subtract(baseHe.getPositionWhileFolding())
				.normalize();

		Line preLine = new Line(preOrigin, baseDir);

		// move the vertices of the face to keep the face connection
		// on baseHe
		face.halfedgeStream().forEach(he -> {
			he.setPositionWhileFolding(transformVertex(
					he.getPositionWhileFolding(), preLine, afterOrigin, afterDir));
		});

		face.setPrecreases(face.precreaseStream().map(precrease -> new OriLine(
				transformVertex(precrease.getP0(), preLine, afterOrigin, afterDir),
				transformVertex(precrease.getP1(), preLine, afterOrigin, afterDir),
				OriLine.Type.AUX))
				.toList());

		// add mirror effect if necessary
		if (face.isFaceFront() == baseHe.getFace().isFaceFront()) {
			var ep = baseHeNext.getPositionWhileFolding();
			var sp = baseHe.getPositionWhileFolding();

			face.halfedgeStream().forEach(he -> {
				he.setPositionWhileFolding(flipVertex(he.getPositionWhileFolding(), sp, ep));
			});
			face.setPrecreases(face.precreaseStream().map(precrease -> new OriLine(
					flipVertex(precrease.getP0(), sp, ep),
					flipVertex(precrease.getP1(), sp, ep),
					OriLine.Type.AUX))
					.toList());
			face.invertFaceFront();
		}
	}

	private Vector2d flipVertex(final Vector2d vertex, final Vector2d sp, final Vector2d ep) {
		return GeomUtil.getSymmetricPoint(vertex, sp, ep);
	}

}
