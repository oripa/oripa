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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.value.OriLine;

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
		var outlineEdges = new ArrayList<OriEdge>();

		// Construct the faces
		for (OriVertex v : vertices) {

			for (OriEdge e : v.getEdges()) {

				if (e.getType() == OriLine.Type.CUT.toInt()) {
					outlineEdges.add(e);
					continue;
				}

				if (v == e.getStartVertex()) {
					if (e.getLeft() != null) {
						continue;
					}
				} else {
					if (e.getRight() != null) {
						continue;
					}
				}

				OriFace face = makeFace(v, e);
				if (face == null) {
					return false;
				}
				faces.add(face);
			}
		}
		if (faces.isEmpty()) { // happens when there is no crease
			OriEdge outlineEdge = outlineEdges.get(0);
			OriVertex v = outlineEdge.getStartVertex();

			OriFace face = makeFace(v, outlineEdge);
			if (face == null) {
				return false;
			}
			faces.add(face);
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
			OriHalfedge he = new OriHalfedge(walkV, face);
			face.halfedges.add(he);
			he.setTemporaryType(walkE.getType());
			if (walkE.getStartVertex() == walkV) {
				walkE.setLeft(he);
			} else {
				walkE.setRight(he);
			}
			walkV = walkE.oppositeVertex(walkV);
			walkE = walkV.getPrevEdge(walkE);
		} while (walkV != startingVertex);
		face.makeHalfedgeLoop();
		face.buildOutline();
		face.buildOutlineBeforeFolding();
		return face;
	}

}
