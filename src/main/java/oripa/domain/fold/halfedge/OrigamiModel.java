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
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import oripa.geom.RectangleDomain;
import oripa.vecmath.Vector2d;

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

	private boolean folded = false;

	private boolean locallyFlatFoldable = false;

	private boolean hasModel = false;

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

	/**
	 * Returns local flat foldability given by
	 * {@link #setLocallyFlatFoldable(boolean)}. This method does not compute
	 * the value to save the computation cost.
	 *
	 * @return
	 */
	public boolean isLocallyFlatFoldable() {
		return locallyFlatFoldable;
	}

	public void setLocallyFlatFoldable(final boolean foldable) {
		locallyFlatFoldable = foldable;
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

	/**
	 * @param faces
	 *            faces
	 */
	public void setFaces(final List<OriFace> faces) {
		this.faces = faces;
	}

	/**
	 * @param vertices
	 *            vertices
	 */
	public void setVertices(final List<OriVertex> vertices) {
		this.vertices = vertices;
	}

	/**
	 * @param edges
	 *            edges
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
	 * @param hasModel
	 *            hasModel
	 */
	public void setHasModel(final boolean hasModel) {
		this.hasModel = hasModel;
	}

	// =============================================================
	// Utility
	// =============================================================

	/**
	 * Flips x coordinates of the positions for display.
	 */
	public void flipXCoordinates() {
		var domain = new RectangleDomain();

		faces.stream().flatMap(f -> f.halfedgeStream()).forEach(he -> {
			domain.enlarge(he.getPosition());
		});

		double centerX = domain.getCenterX();

		faces.stream().flatMap(f -> f.halfedgeStream()).forEach(he -> {
			var x = 2 * centerX - he.getPositionForDisplay().getX();
			var y = he.getPositionForDisplay().getY();
			he.getPositionForDisplay().set(x, y);
		});
	}

	public RectangleDomain createDomainOfFoldedModel() {
		return createDomain(OriHalfedge::getPosition);
	}

	public RectangleDomain createPaperDomain() {
		return createDomain(OriHalfedge::getPositionBeforeFolding);
	}

	private RectangleDomain createDomain(final Function<OriHalfedge, Vector2d> positionExtractor) {
		var paperDomain = new RectangleDomain();
		paperDomain.enlarge(faces.stream()
				.flatMap(OriFace::halfedgeStream)
				.map(positionExtractor)
				.collect(Collectors.toList()));

		return paperDomain;
	}

	public boolean isUnassigned() {
		return edges.stream().anyMatch(edge -> edge.isUnassigned());
	}

	public ModelType getModelType() {
		if (!isLocallyFlatFoldable()) {
			return ModelType.ERROR_CONTAINING;
		} else if (isUnassigned()) {
			return ModelType.UNASSIGNED;
		} else {
			return ModelType.ASSIGNED;
		}
	}

	@Override
	public String toString() {
		return "[" + faces.toString() + edges.toString() + vertices.toString() + "]";
	}
}
