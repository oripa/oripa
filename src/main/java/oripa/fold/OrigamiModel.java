package oripa.fold;

import java.util.ArrayList;
import java.util.List;

import oripa.value.OriLine;


/**
 * Entity for folding-estimation
 * @author Koji
 *
 */
public class OrigamiModel {
	
	private List<OriFace> faces = new ArrayList<>();
	private List<OriVertex> vertices = new ArrayList<>();
	private List<OriEdge> edges = new ArrayList<>();

	private List<OriLine> crossLines = new ArrayList<>();

	private List<OriFace> sortedFaces = new ArrayList<>();

	//private FoldedModelInfo foldedModelInfo = new FoldedModelInfo();
	
	private boolean folded = false;

	private boolean hasModel = false;

	private boolean probablyFoldable = false;
	
	private double paperSize = -1;
	

	//=============================================================
	// Constructors
	//=============================================================
			
	@SuppressWarnings("unused")
	private OrigamiModel() {}

	public OrigamiModel(double paperSize) {
		setPaperSize(paperSize);
	}
	
	//=============================================================
	// Getter/Setter
	//=============================================================

	public boolean isFolded() {
		return folded;
	}

	public void setFolded(boolean folded) {
		this.folded = folded;
	}

	
	
	/**
	 * @return probablyFoldable
	 */
	public boolean isProbablyFoldable() {
		return probablyFoldable;
	}

	/**
	 * @param probablyFoldable probablyFoldableを登録する
	 */
	public void setProbablyFoldable(boolean probablyFoldable) {
		this.probablyFoldable = probablyFoldable;
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


	public List<OriLine> getCrossLines() {
		return crossLines;
	}

	public void setCrossLines(List<OriLine> crossLines) {
		this.crossLines = crossLines;
	}

	public double getPaperSize() {
		return paperSize;
	}

	public void setPaperSize(double paperSize) {
		this.paperSize = paperSize;
	}

	public List<OriFace> getSortedFaces() {
		return sortedFaces;
	}

	public void setSortedFaces(List<OriFace> sortedFaces) {
		this.sortedFaces = sortedFaces;
	}

//	public FoldedModelInfo getFoldedModelInfo() {
//		return foldedModelInfo;
//	}
//
//	public void setFoldedModelInfo(FoldedModelInfo foldedModelInfo) {
//		this.foldedModelInfo = foldedModelInfo;
//	}

	/**
	 * @param faces facesを登録する
	 */
	public void setFaces(List<OriFace> faces) {
		this.faces = faces;
	}

	/**
	 * @param vertices verticesを登録する
	 */
	public void setVertices(List<OriVertex> vertices) {
		this.vertices = vertices;
	}

	/**
	 * @param edges edgesを登録する
	 */
	public void setEdges(List<OriEdge> edges) {
		this.edges = edges;
	}

	/**
	 * @return hasModel
	 */
	public boolean hasModel() {
		return hasModel;
	}

	/**
	 * @param hasModel hasModelを登録する
	 */
	public void setHasModel(boolean hasModel) {
		this.hasModel = hasModel;
	}
	
	
	
	
	
	
	
}
