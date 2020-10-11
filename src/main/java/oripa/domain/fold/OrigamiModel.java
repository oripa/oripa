package oripa.domain.fold;

import java.util.ArrayList;
import java.util.List;

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
	 * @return probablyFoldable
	 */
	public boolean isProbablyFoldable() {
		FoldabilityChecker checker = new FoldabilityChecker();

		return checker.modelIsProbablyFoldable(vertices, faces);
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
}
