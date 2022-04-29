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
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.geom.GeomUtil;

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
			final Collection<OriFace> faces) {

		boolean valid = true;

		var createdFaces = createFaces(vertices);

		logger.debug("created faces: {}", createdFaces);

		if (createdFaces.stream().anyMatch(Objects::isNull)) {
			createdFaces = createdFaces.stream()
					.filter(Objects::nonNull)
					.collect(Collectors.toList());
			valid = false;
		}

		var boundaryFaces = createBoundaryFaces(vertices);

		// find boundary face with no internal vertex
		boundaryFaces.removeIf(
				face -> vertices.stream().anyMatch(
						vertex -> face.isOnFaceExclusively(vertex.getPosition(), GeomUtil.EPS)));

		faces.addAll(createdFaces);
		faces.addAll(boundaryFaces);

		return valid;
	}

	private Collection<OriFace> createFaces(final Collection<OriVertex> vertices) {
		var faces = new ArrayList<OriFace>();

		// Construct the faces
		for (OriVertex v : vertices) {
			var createdFaces = v.edgeStream()
					.filter(e -> isTarget(v, e))
					.map(e -> makeFace(v, e))
					.collect(Collectors.toList());

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
			if (e.getLeft() != null) {
				return false;
			}
		} else {
			if (e.getRight() != null) {
				return false;
			}
		}
		return true;
	}

	List<OriFace> createBoundaryFaces(final Collection<OriVertex> vertices) {

		var boundaryFaces = new ArrayList<OriFace>();

		for (OriVertex v : vertices) {

			var createdFaces = v.edgeStream()
					.filter(e -> isTargetBoundary(v, e))
					.map(e -> makeBoundaryFace(v, e))
					.collect(Collectors.toList());

			boundaryFaces.addAll(createdFaces.stream()
					.filter(Objects::nonNull)
					.collect(Collectors.toList()));
		}

		logger.debug("boundary faces: {}", boundaryFaces);

		return boundaryFaces;
	}

	private boolean isTargetBoundary(final OriVertex v, final OriEdge e) {
		if (!e.isBoundary()) {
			return false;
		}

		if (e.getLeft() != null || e.getRight() != null) {
			return false;
		}

		return true;
	}

	private OriFace makeFace(final OriVertex startingVertex, final OriEdge startingEdge) {
		OriFace face = new OriFace();
		OriVertex walkV = startingVertex;
		OriEdge walkE = startingEdge;
		int debugCount = 0;
		do {
			if (debugCount++ > 100) {
				logger.error("invalid input for making faces.");
//						throw new UnfoldableModelException("algorithmic error");
				return null;
			}

			setHalfedgeToFaceAndEdge(walkV, walkE, face);

			walkV = walkE.oppositeVertex(walkV);
			walkE = walkV.getPrevEdge(walkE); // to make a loop in clockwise
		} while (walkV != startingVertex);
		face.makeHalfedgeLoop();
		return face;
	}

	private OriFace makeBoundaryFace(final OriVertex startingVertex, final OriEdge startingEdge) {
		OriFace face = new OriFace();
		OriVertex walkV = startingVertex;
		OriEdge walkE = startingEdge;
		int debugCount = 0;
		do {
			if (debugCount++ > 1000) {
				logger.error("invalid input for making faces.");
//						throw new UnfoldableModelException("algorithmic error");
				return null;
			}

			setHalfedgeToFaceAndEdge(walkV, walkE, face);

			walkV = walkE.oppositeVertex(walkV);
			do {
				walkE = walkV.getPrevEdge(walkE); // to make a loop in clockwise
			} while (!walkE.isBoundary());

		} while (walkV != startingVertex);
		face.makeHalfedgeLoop();
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
