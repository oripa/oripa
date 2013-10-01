package oripa.fold;

import java.util.ArrayList;

import oripa.geom.OriEdge;
import oripa.geom.OriFace;
import oripa.geom.OriVertex;
import oripa.value.OriLine;


/**
 * Entity for folding-estimation
 * @author Koji
 *
 */
public class OrigamiModel {
	
	private ArrayList<OriFace> faces = new ArrayList<OriFace>();
	private ArrayList<OriVertex> vertices = new ArrayList<OriVertex>();
	private ArrayList<OriEdge> edges = new ArrayList<OriEdge>();
	//    public ArrayList<OriLine> tmpSelectedLines = new ArrayList<OriLine>();

	private ArrayList<OriLine> crossLines = new ArrayList<OriLine>();

	private ArrayList<OriFace> sortedFaces = new ArrayList<OriFace>();

	private FoldedModelInfo foldedModelInfo = new FoldedModelInfo();
	
	// FIXME ambiguous name
	private boolean isValidPattern = false;

	private boolean folded = false;

	
	public boolean isFolded() {
		return folded;
	}

	public void setFolded(boolean folded) {
		this.folded = folded;
	}

	private BoundBox boundBox;
	
	private double paperSize = -1;
	
//	public void setFaces(ArrayList<OriFace> faces) {
//		this.faces = faces;
//	}
//	public void setVertices(ArrayList<OriVertex> vertices) {
//		this.vertices = vertices;
//	}
//	public void setEdges(ArrayList<OriEdge> edges) {
//		this.edges = edges;
//	}

	private OrigamiModel() {}
	
	public OrigamiModel(double paperSize) {
		setPaperSize(paperSize);
	}
	
	public void setValidPattern(boolean isValidPattern) {
		this.isValidPattern = isValidPattern;
	}

	public ArrayList<OriFace> getFaces() {
		return faces;
	}
	public ArrayList<OriVertex> getVertices() {
		return vertices;
	}
	public ArrayList<OriEdge> getEdges() {
		return edges;
	}
	public boolean isValidPattern() {
		return isValidPattern;
	}

	public BoundBox getBoundBox() {
		return boundBox;
	}

	public void setBoundBox(BoundBox boundBox) {
		this.boundBox = boundBox;
	}

	public ArrayList<OriLine> getCrossLines() {
		return crossLines;
	}

	public void setCrossLines(ArrayList<OriLine> crossLines) {
		this.crossLines = crossLines;
	}

	public double getPaperSize() {
		return paperSize;
	}

	public void setPaperSize(double paperSize) {
		this.paperSize = paperSize;
	}

	public ArrayList<OriFace> getSortedFaces() {
		return sortedFaces;
	}

	public void setSortedFaces(ArrayList<OriFace> sortedFaces) {
		this.sortedFaces = sortedFaces;
	}

	public FoldedModelInfo getFoldedModelInfo() {
		return foldedModelInfo;
	}

	public void setFoldedModelInfo(FoldedModelInfo foldedModelInfo) {
		this.foldedModelInfo = foldedModelInfo;
	}
	
	
	

	
	
	
}
