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
package oripa.domain.fold.halfedge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
public class OriFacesFactory {
	private static final Logger logger = LoggerFactory.getLogger(OriFacesFactory.class);

	/**
	 * Creates and sets new face objects to given {@code faces}. Since ORIPA
	 * tries to output failure model data, we can't make create() method.
	 *
	 * @param vertices
	 *            {@link OriEdge#left} and {@link OriEdge#right} of each edge in
	 *            {@link OriVertex#edges} will be updated.
	 *            {@link OriVertex#edges} is assumed to have been set.
	 * @param faces
	 *            new faces will be set.
	 * @return {@code true} if the face construction succeeds.
	 */
	public boolean buildFaces(final Collection<OriVertex> vertices,
			final Collection<OriFace> faces, final double eps) {

		boolean valid = true;

		var createdFaces = createFaces(vertices, eps);

		logger.trace("created faces: {}", createdFaces);

		if (createdFaces.stream().anyMatch(Objects::isNull)) {
			createdFaces = createdFaces.stream()
					.filter(Objects::nonNull)
					.toList();
			valid = false;
		}

		createdFaces = createdFaces.stream()
				.filter(face -> face.halfedgeCount() >= 3)
				.toList();

		var boundaryFaces = createBoundaryFaces(vertices, eps);

		// find boundary face with no internal vertex
		boundaryFaces.removeIf(
				face -> vertices.stream()
						.anyMatch(vertex -> face.includesExclusively(vertex.getPosition(), eps)));

		faces.addAll(createdFaces);
		faces.addAll(boundaryFaces);

		logger.debug("#face = {}", faces.size());

		return valid;
	}

	private Collection<OriFace> createFaces(final Collection<OriVertex> vertices, final double eps) {
		var faces = new ArrayList<OriFace>();

		// Construct the faces
		for (OriVertex v : vertices) {
			var createdFaces = v.edgeStream()
					.filter(e -> isTarget(v, e))
					.map(e -> makeFace(v, e, eps))
					.toList();

			faces.addAll(createdFaces);
		}

		return faces;
	}

	/**
	 *
	 * @param v
	 *            vertex on e
	 * @param e
	 *            edge
	 * @return true if e is an edge to traverse.
	 */
	private boolean isTarget(final OriVertex v, final OriEdge e) {
		if (e.isBoundary()) {
			return false;
		}

		if (v == e.getStartVertex()) {
			if (e.getLeft().isPresent()) {
				return false;
			}
		} else {
			if (e.getRight().isPresent()) {
				return false;
			}
		}
		return true;
	}

	List<OriFace> createBoundaryFaces(final Collection<OriVertex> vertices, final double eps) {

		var boundaryFaces = new ArrayList<OriFace>();

		for (OriVertex v : vertices) {

			var createdFaces = v.edgeStream()
					.filter(e -> isTargetBoundary(v, e))
					.map(e -> makeBoundaryFace(v, e, eps))
					.toList();

			boundaryFaces.addAll(createdFaces.stream()
					.filter(Objects::nonNull)
					.toList());
		}

		logger.debug("boundary faces: {}", boundaryFaces);

		return boundaryFaces;
	}

	private boolean isTargetBoundary(final OriVertex v, final OriEdge e) {
		if (!e.isBoundary()) {
			return false;
		}

		if (e.getLeft().isPresent() || e.getRight().isPresent()) {
			return false;
		}

		return true;
	}

	private OriFace makeFace(final OriVertex startingVertex, final OriEdge startingEdge, final double eps) {
		return makeFace(startingVertex, startingEdge, 500,
				eps,
				// to make a loop in counterclockwise in mathematical
				// coordinates.
				(walkV, walkE) -> walkV.getPrevEdge(walkE));
	}

	private OriFace makeBoundaryFace(final OriVertex startingVertex, final OriEdge startingEdge, final double eps) {
		return makeFace(startingVertex, startingEdge, 10000,
				eps,
				(walkV, walkE) -> {
					var nextEdge = walkE;
					do {
						nextEdge = walkV.getPrevEdge(nextEdge);
					} while (!nextEdge.isBoundary());
					return nextEdge;
				});
	}

	/**
	 * Can return null. Wrapping with Optional is not comfortable for caller
	 * since the caller uses this method to create elements of collection.
	 *
	 * @param startingVertex
	 * @param startingEdge
	 * @param maxCount
	 * @param getNextEdge
	 * @return
	 */
	private OriFace makeFace(final OriVertex startingVertex, final OriEdge startingEdge, final int maxCount,
			final double eps,
			final BiFunction<OriVertex, OriEdge, OriEdge> getNextEdge) {
		OriFace face = new OriFace();
		OriVertex walkV = startingVertex;
		OriEdge walkE = startingEdge;
		int debugCount = 0;
		do {
			if (debugCount++ > maxCount) {
				var badPositions = new ArrayList<Vector2d>();
				for (int i = 0; i < (10 > maxCount ? maxCount : 10); i++) {
					badPositions.add(face.getHalfedge(i).getPosition());
				}
				logger.error("invalid input for making faces. {}", badPositions);
//						throw new UnfoldableModelException("algorithmic error");
				return null;
			}

			setHalfedgeToFaceAndEdge(walkV, walkE, face);

			walkV = walkE.oppositeVertex(walkV);
			walkE = getNextEdge.apply(walkV, walkE);
		} while (walkV != startingVertex);
		face.makeHalfedgeLoop(eps);
		return face;
	}

	private void setHalfedgeToFaceAndEdge(final OriVertex walkV, final OriEdge walkE, final OriFace face) {
		OriHalfedge he = new OriHalfedge(walkV, face);
		face.addHalfedge(he);
		he.setTemporaryType(walkE.getType());
		if (walkE.getStartVertex() == walkV) {
			walkE.setLeft(he);
		} else {
			walkE.setRight(he);
		}
	}

}
