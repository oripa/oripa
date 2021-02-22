/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2005-2009 Jun Mitani http://mitani.cs.tsukuba.ac.jp/

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
import java.util.Collections;
import java.util.List;

import oripa.geom.RectangleDomain;

/**
 * Entity for folding-estimation
 *
 * @author Koji
 *
 */
public class OrigamiModel {

	private List<OriFace> faces = new ArrayList<OriFace>();
	private List<OriVertex> vertices = new ArrayList<OriVertex>();
	private List<OriEdge> edges = new ArrayList<OriEdge>();

	private List<OriFace> sortedFaces = new ArrayList<OriFace>();

	private boolean folded = false;

	private boolean hasModel = false;

	private final boolean probablyFoldable = false;

	private double paperSize = -1;

	// =============================================================
	// Constructors
	// =============================================================

	@SuppressWarnings("unused")
	private OrigamiModel() {
	}

	public OrigamiModel(final double paperSize) {
		this.paperSize = paperSize;
	}

	// =============================================================
	// Getter/Setter
	// =============================================================

	public boolean isFolded() {
		return folded;
	}

	public void setFolded(final boolean folded) {
		this.folded = folded;
	}

	public List<OriFace> getFaces() {
		return faces;
	}

	public List<OriVertex> getVertices() {
		return vertices;
	}

	public List<OriEdge> getEdges() {
		return edges;
	}

	public double getPaperSize() {
		return paperSize;
	}

	public List<OriFace> getSortedFaces() {
		return sortedFaces;
	}

	public void setSortedFaces(final List<OriFace> sortedFaces) {
		this.sortedFaces = sortedFaces;
	}

	/**
	 * @param faces faces
	 */
	public void setFaces(final List<OriFace> faces) {
		this.faces = faces;
	}

	/**
	 * @param vertices vertices
	 */
	public void setVertices(final List<OriVertex> vertices) {
		this.vertices = vertices;
	}

	/**
	 * @param edges edges
	 */
	public void setEdges(final List<OriEdge> edges) {
		this.edges = edges;
	}

	/**
	 * @return hasModel
	 */
	public boolean hasModel() {
		return hasModel;
	}

	/**
	 * @param hasModel hasModel
	 */
	public void setHasModel(final boolean hasModel) {
		this.hasModel = hasModel;
	}

	/**
	 * Flips x coordinates and reverse the order of layers.
	 */
	public void flipXCoordinates() {
		var domain = new RectangleDomain();

		for (OriFace face : faces) {
			face.z_order = -face.z_order;
			for (OriHalfedge he : face.halfedges) {
				domain.enlarge(he.getPosition());
			}
		}

		double centerX = domain.getCenterX();

		faces.stream().flatMap(f -> f.halfedges.stream()).forEach(he -> {
			he.positionForDisplay.x = 2 * centerX - he.positionForDisplay.x;
		});

		faces.forEach(face -> {
			face.faceFront = !face.faceFront;
			face.setOutline();
		});

		Collections.sort(faces, new FaceOrderComparator());

		Collections.reverse(sortedFaces);
	}

}
