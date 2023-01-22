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

import java.util.List;

import javax.vecmath.Vector2d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.fold.halfedge.OriEdge;
import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OriHalfedge;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.geom.GeomUtil;
import oripa.geom.Line;

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
			final OrigamiModel origamiModel) {
		List<OriFace> faces = origamiModel.getFaces();
		List<OriEdge> edges = origamiModel.getEdges();

		int id = 0;
		for (OriFace face : faces) {
//			face.faceFront = true;
//			face.movedByFold = false;
			face.setFaceID(id);
			id++;
		}

		walkFace(faces.get(0));

		for (OriEdge e : edges) {
			var sv = e.getStartVertex();
			var ev = e.getEndVertex();

			sv.getPosition().set(e.getLeft().getPositionWhileFolding());

			var right = e.getRight();
			if (right != null) {
				ev.getPosition().set(right.getPositionWhileFolding());
			}
		}
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
			final OrigamiModel model) {
		List<OriEdge> edges = model.getEdges();
		List<OriFace> faces = model.getFaces();

//			for (OriFace face : faces) {
//				face.faceFront = true;
//				face.movedByFold = false;
//			}

		walkFace(faces, faces.get(0), 0);

		for (OriEdge e : edges) {
			var sv = e.getStartVertex();
			sv.getPosition().set(e.getLeft().getPositionWhileFolding());
		}

	}

	/**
	 * Recursive method that flips the faces, making the folds
	 */
	private void walkFace(final OriFace face) {
		face.setMovedByFold(true);

		face.halfedgeStream().forEach(he -> {
			var pair = he.getPair();
			if (pair == null) {
				return;
			}
			var pairFace = pair.getFace();
			if (pairFace.isMovedByFold()) {
				return;
			}

			flipFace(pairFace, he);
			pairFace.setMovedByFold(true);
			walkFace(pairFace);
		});
	}

	/**
	 * Move the {@code vertex} keeping the face connection. The transformation
	 * is based on geometric interpretation without affine transformation.
	 *
	 * @param vertex
	 *            vertex to be moved. there will be a side effect.
	 * @param preLine
	 *            crease line before folding.
	 * @param afterOrigin
	 *            a reference point on the moved crease line.
	 * @param afterDir
	 *            clockwise direction vector of the moved crease line.
	 */
	private void transformVertex(final Vector2d vertex, final Line preLine,
			final Vector2d afterOrigin, final Vector2d afterDir) {
		double param[] = new double[1];
		double d0 = GeomUtil.distance(vertex, preLine, param);
		// distance between reference point of preLine and foot cross point from
		// vertex to preLine
		double d1 = param[0];

		// compute foot cross point from vertex to moved mirror axis line (or
		// the crease)
		Vector2d footV = new Vector2d(afterOrigin);
		footV.x += d1 * afterDir.x;
		footV.y += d1 * afterDir.y;

		// compute a direction vector perpendicular to the crease.
		// the vector directs the right side of the crease halfedge
		// since all vertices of the face are on the right side of the crease
		// halfedge.
		Vector2d afterDirFromFoot = new Vector2d();
		afterDirFromFoot.x = afterDir.y;
		afterDirFromFoot.y = -afterDir.x;

		// set moved vertex coordinates
		vertex.x = footV.x + d0 * afterDirFromFoot.x;
		vertex.y = footV.y + d0 * afterDirFromFoot.y;
	}

	private void flipFace(final OriFace face, final OriHalfedge baseHe) {
		var baseHePair = baseHe.getPair();
		var baseHePairNext = baseHePair.getNext();

		// baseHe.pair keeps the position before folding.
		Vector2d preOrigin = new Vector2d(baseHePairNext.getPositionWhileFolding());
		Vector2d afterOrigin = new Vector2d(baseHe.getPositionWhileFolding());

		// Creates the base unit vector for before the rotation
		// (reversed direction. the algorithm walks along the halfedges
		// clockwisely in mathematical coordinate system)
		Vector2d baseDir = new Vector2d();
		baseDir.sub(baseHePair.getPositionWhileFolding(), baseHePairNext.getPositionWhileFolding());

		// Creates the base unit vector for after the rotation
		var baseHeNext = baseHe.getNext();
		Vector2d afterDir = new Vector2d();
		afterDir.sub(baseHeNext.getPositionWhileFolding(), baseHe.getPositionWhileFolding());
		afterDir.normalize();

		Line preLine = new Line(preOrigin, baseDir);

		// move the vertices of the face to keep the face connection
		// on baseHe
		face.halfedgeStream().forEach(he -> {
			transformVertex(he.getPositionWhileFolding(), preLine, afterOrigin, afterDir);
		});

		face.precreaseStream().forEach(precrease -> {
			transformVertex(precrease.p0, preLine, afterOrigin, afterDir);
			transformVertex(precrease.p1, preLine, afterOrigin, afterDir);
		});

		// add mirror effect if necessary
		if (face.isFaceFront() == baseHe.getFace().isFaceFront()) {
			Vector2d ep = baseHeNext.getPositionWhileFolding();
			Vector2d sp = baseHe.getPositionWhileFolding();

			face.halfedgeStream().forEach(he -> {
				flipVertex(he.getPositionWhileFolding(), sp, ep);
			});
			face.precreaseStream().forEach(precrease -> {
				flipVertex(precrease.p0, sp, ep);
				flipVertex(precrease.p1, sp, ep);

			});
			face.invertFaceFront();
		}
	}

	/**
	 * Make the folds by flipping the faces
	 *
	 * @param faces
	 * @param face
	 * @param walkFaceCount
	 */
	private void walkFace(final List<OriFace> faces, final OriFace face, final int walkFaceCount) {
		face.setMovedByFold(true);
		if (walkFaceCount > 1000) {
			logger.error("walkFace too deep");
			return;
		}
		face.halfedgeStream().forEach(he -> {
			var pair = he.getPair();
			if (pair == null) {
				return;
			}
			var pairFace = pair.getFace();
			if (pairFace.isMovedByFold()) {
				return;
			}

			flipFace2(faces, pairFace, he);
			pairFace.setMovedByFold(true);
			walkFace(faces, pairFace, walkFaceCount + 1);
		});
	}

	private void flipFace2(final List<OriFace> faces, final OriFace face,
			final OriHalfedge baseHe) {
		flipFace(face, baseHe);
		faces.remove(face);
		faces.add(face);
	}

	private void flipVertex(final Vector2d vertex, final Vector2d sp, final Vector2d ep) {
		var v = GeomUtil.getSymmetricPoint(vertex, sp, ep);

		vertex.x = v.x;
		vertex.y = v.y;
	}

}
