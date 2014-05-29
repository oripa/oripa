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

package oripa.doc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.vecmath.Vector2d;

import oripa.ORIPA;
import oripa.fold.FoldedModelInfo;
import oripa.fold.OriFace;
import oripa.fold.OriHalfedge;
import oripa.fold.OrigamiModel;
import oripa.geom.GeomUtil;
import oripa.paint.core.PaintConfig;
import oripa.paint.creasepattern.CreasePattern;
import oripa.paint.creasepattern.tool.LineAdder;
import oripa.paint.creasepattern.tool.LinePaster;
import oripa.resource.Constants;
import oripa.value.OriLine;


public class Doc {
//	class PointComparatorX implements Comparator<Vector2d> {
//
//		@Override
//		public int compare(Vector2d v1, Vector2d v2) {
//			if(v1.x == v2.x){
//				return 0;
//			}
//			return v1.x > v2.x ? 1 : -1;
//		}
//	}
//
//	class PointComparatorY implements Comparator<Vector2d> {
//
//		@Override
//		public int compare(Vector2d v1, Vector2d v2) {
//			if(v1.y == v2.y){
//				return 0;
//			}
//			return ((Vector2d) v1).y > ((Vector2d) v2).y ? 1 : -1;
//		}
//	}
//
//	class FaceOrderComparator implements Comparator<OriFace> {
//
//		@Override
//		public int compare(OriFace f1, OriFace f2) {
//			return f1.z_order > f2.z_order ? 1 : -1;
//		}
//	}


	
	private double paperSize;

	// Crease Pattern

	private CreasePattern creasePattern = null;
	private ArrayList<OriLine> crossLines = new ArrayList<OriLine>();


	// Origami Model for Estimation
	private OrigamiModel origamiModel = null;

		
	// Folded Model Information (Result of Estimation)

	private FoldedModelInfo foldedModelInfo = null;
	

	final public static int NO_OVERLAP = 0;
	final public static int UPPER = 1;
	final public static int LOWER = 2;
	final public static int UNDEFINED = 9;

	// Project data

	private String dataFilePath = "";
	private String title;
	private String editorName;
	private String originalAuthorName;
	private String reference;
	public String memo;
	private UndoManager<UndoInfo> undoManager = new UndoManager<>(30);



	int debugCount = 0;


	public Doc(){
		initialize(Constants.DEFAULT_PAPER_SIZE);
	}   

	public Doc(double size) {
		initialize(size);
	}

	private void initialize(double size){

		this.paperSize = size;
		creasePattern = new CreasePattern(size);	

		
		OriLine l0 = new OriLine(-size / 2.0, -size / 2.0, size / 2.0, -size / 2.0, OriLine.TYPE_CUT);
		OriLine l1 = new OriLine(size / 2.0, -size / 2.0, size / 2.0, size / 2.0, OriLine.TYPE_CUT);
		OriLine l2 = new OriLine(size / 2.0, size / 2.0, -size / 2.0, size / 2.0, OriLine.TYPE_CUT);
		OriLine l3 = new OriLine(-size / 2.0, size / 2.0, -size / 2.0, -size / 2.0, OriLine.TYPE_CUT);
		creasePattern.add(l0);
		creasePattern.add(l1);
		creasePattern.add(l2);
		creasePattern.add(l3);

		
		origamiModel  = new OrigamiModel(size);
		foldedModelInfo = new FoldedModelInfo();
	}

	public void setDataFilePath(String path){
		this.dataFilePath = path;
	}

	public String getDataFilePath(){
		return dataFilePath;
	}

	public String getDataFileName(){
		File file = new File(ORIPA.doc.dataFilePath);
		String fileName = file.getName();

		return fileName;

	}



	public UndoInfo createUndoInfo(){
		UndoInfo undoInfo = new UndoInfo(creasePattern);
		return undoInfo;
	}

	public void cacheUndoInfo(){
		undoManager.setCache(createUndoInfo());
	}

	public void pushCachedUndoInfo(){
		undoManager.pushCachedInfo();
	}

	public void pushUndoInfo() {
		UndoInfo ui = new UndoInfo(creasePattern);
		undoManager.push(ui);
	}

	public void pushUndoInfo(UndoInfo uinfo){
		undoManager.push(uinfo);
	}

	public void loadUndoInfo() {
		UndoInfo info = undoManager.pop();

		if(info == null){
			return;
		}

		creasePattern.clear();
		creasePattern.addAll(info.getLines());
	}

	public boolean canUndo(){
		return undoManager.canUndo();
	}

	public boolean isChanged(){
		return undoManager.isChanged();
	}

	public void clearChanged(){
		undoManager.clearChanged();
	}



//	public void CircleCopy(double cx, double cy, double angleDeg, int num) {
//		ArrayList<OriLine> copiedLines = new ArrayList<OriLine>();
//
//
//		oripa.geom.RectangleClipper clipper =
//				new oripa.geom.RectangleClipper(
//						-paperSize / 2, -paperSize / 2, paperSize / 2, paperSize / 2);
//
//		double angle = angleDeg * Math.PI / 180.0;
//
//		for (int i = 0; i < num; i++) {
//			double angleRad = angle * (i + 1);
//			for (OriLine l : creasePattern) {
//				if (!l.selected) {
//					continue;
//				}
//
//				OriLine cl = new OriLine(l);
//				double tx0 = l.p0.x - cx;
//				double ty0 = l.p0.y - cy;
//				double tx1 = l.p1.x - cx;
//				double ty1 = l.p1.y - cy;
//
//				double ttx0 = tx0 * Math.cos(angleRad) - ty0 * Math.sin(angleRad);
//				double tty0 = tx0 * Math.sin(angleRad) + ty0 * Math.cos(angleRad);
//
//				double ttx1 = tx1 * Math.cos(angleRad) - ty1 * Math.sin(angleRad);
//				double tty1 = tx1 * Math.sin(angleRad) + ty1 * Math.cos(angleRad);
//
//				cl.p0.x = ttx0 + cx;
//				cl.p0.y = tty0 + cy;
//				cl.p1.x = ttx1 + cx;
//				cl.p1.y = tty1 + cy;
//
//				if (clipper.clip(cl)) {
//					copiedLines.add(cl);
//				}
//			}
//		}
//		for (OriLine l : copiedLines) {
//			addLine(l);
//		}
//
//		Painter painter = new Painter();
//		painter.resetSelectedOriLines(creasePattern);
//	}







//	// v1-v2 is the symmetry line, v0-v1 is the sbject to be copied.
//	// automatically generates possible rebouncing of the fold (used when Ctrl is pressed)
//	public void addSymmetricLineAutoWalk(Vector2d v0, Vector2d v1, Vector2d v2, int stepCount, Vector2d startV) {
//		stepCount++;
//		if (stepCount > 36) {
//			return;
//		}
//		Vector2d v3 = GeomUtil.getSymmetricPoint(v0, v1, v2);
//		Ray ray = new Ray(v1, new Vector2d(v3.x - v1.x, v3.y - v1.y));
//
//		double minDist = Double.MAX_VALUE;
//		Vector2d bestPoint = null;
//		OriLine bestLine = null;
//		for (OriLine l : creasePattern) {
//			Vector2d crossPoint = GeomUtil.getCrossPoint(ray, l.getSegment());
//			if (crossPoint == null) {
//				continue;
//			}
//			double distance = GeomUtil.Distance(crossPoint, v1);
//			if (distance < CalculationResource.POINT_EPS) {
//				continue;
//			}
//
//			if (distance < minDist) {
//				minDist = distance;
//				bestPoint = crossPoint;
//				bestLine = l;
//			}
//		}
//
//		if (bestPoint == null) {
//			return;
//		}
//
//		addLine(new OriLine(v1, bestPoint, PaintConfig.inputLineType));
//
//		if (GeomUtil.Distance(bestPoint, startV) < CalculationResource.POINT_EPS) {
//			return;
//		}
//
//		addSymmetricLineAutoWalk(v1, bestPoint, bestLine.p0, stepCount, startV);
//
//	}


	public void addLine(OriLine inputLine) {
		pushUndoInfo();
		LineAdder lineAdder = new LineAdder();
		lineAdder.addLine(inputLine, creasePattern);		
	}



	public void setCrossLine(OriLine line) {
		crossLines.clear();

		List<OriFace> sortedFaces = origamiModel.getSortedFaces();

		for (OriFace face : sortedFaces) {
			ArrayList<Vector2d> vv = new ArrayList<Vector2d>();
			//int crossCount = 0;
			for (OriHalfedge he : face.halfedges) {
				OriLine l = new OriLine(he.positionForDisplay.x, he.positionForDisplay.y,
						he.next.positionForDisplay.x, he.next.positionForDisplay.y, PaintConfig.inputLineType);

				double params[] = new double[2];
				boolean res = GeomUtil.getCrossPointParam(line.p0, line.p1, l.p0, l.p1, params);
				if (res == true && params[0] > -0.001 && params[1] > -0.001 && params[0] < 1.001 && params[1] < 1.001) {
					double param = params[1];
					//crossCount++;

					Vector2d crossV = new Vector2d();
					crossV.x = (1.0 - param) * he.vertex.preP.x + param * he.next.vertex.preP.x;
					crossV.y = (1.0 - param) * he.vertex.preP.y + param * he.next.vertex.preP.y;

					boolean isNewPoint = true;
					for (Vector2d v2d : vv) {
						if (GeomUtil.Distance(v2d, crossV) < 1) {
							isNewPoint = false;
							break;
						}
					}
					if (isNewPoint) {
						vv.add(crossV);
					}
				}
			}

			if (vv.size() >= 2) {
				crossLines.add(new OriLine(vv.get(0), vv.get(1), PaintConfig.inputLineType));
			}
		}

	}


	public void addCrossLineToPattern() {
				LinePaster paster = new LinePaster();
				paster.paste(crossLines, creasePattern);
	}

		
	public Collection<Vector2d> getVerticesAround(Vector2d v){
		return creasePattern.getVerticesAround(v);
	}
	
	public Collection<Collection<Vector2d>> getVerticesArea(
			double x, double y, double distance){
		
		return creasePattern.getVerticesArea(x, y, distance);
	}
	
	public CreasePattern getCreasePattern(){
		return creasePattern;
	}

	/**
	 * @return origamiModel
	 */
	public OrigamiModel getOrigamiModel() {
		return origamiModel;
	}
	
	/**
	 * @param origamiModel origamiModel is set to this instance.
	 */
	public void setOrigamiModel(OrigamiModel origamiModel) {
		this.origamiModel = origamiModel;
	}
	
	

	/**
	 * @return foldedModelInfo
	 */
	public FoldedModelInfo getFoldedModelInfo() {
		return foldedModelInfo;
	}

	/**
	 * @param foldedModelInfo foldedModelInfo is set to this instance.
	 */
	public void setFoldedModelInfo(FoldedModelInfo foldedModelInfo) {
		this.foldedModelInfo = foldedModelInfo;
	}

	//======================================================================
	// Getter/Setter eventually unnecessary
	

	/**
	 * @return crossLines
	 */
	public ArrayList<OriLine> getCrossLines() {
		return crossLines;
	}

	/**
	 * @param crossLines crossLines is set to this instance.
	 */
	public void setCrossLines(ArrayList<OriLine> crossLines) {
		this.crossLines = crossLines;
	}

	
	

	//-------------------------------------------------------------
	// moved to OrigamiModel
	
//	/**
//	 * @return faces
//	 */
//	public List<OriFace> getFaces() {
//		List<OriFace> faces = origamiModel.getFaces();
//		return faces;
//	}
//
//	/**
//	 * @param faces faces is set to this instance.
//	 */
//	public void setFaces(List<OriFace> faces) {
//		origamiModel.setFaces(faces);
//	}
//
//	/**
//	 * @return edges
//	 */
//	public List<OriEdge> getEdges() {
//		List<OriEdge> edges = origamiModel.getEdges();
//
//		return edges;
//	}
//
//	
//	/**
//	 * @return vertices
//	 */
//	public List<OriVertex> getVertices() {
//		List<OriVertex> vertices = origamiModel.getVertices();
//
//		return vertices;
//	}
//
//	/**
//	 * @param vertices vertices is set to this instance.
//	 */
//	public void setVertices(List<OriVertex> vertices) {
//		origamiModel.setVertices(vertices);;
//	}
//
//	/**
//	 * @param edges edges is set to this instance.
//	 */
//	public void setEdges(ArrayList<OriEdge> edges) {
//		origamiModel.setEdges(edges);
//	}
//
//	/**
//	 * @return isValidPattern
//	 */
//	public boolean isValidPattern() {
//		
//		return origamiModel.isValidPattern();
//	}
//
//	/**
//	 * @param isValidPattern isValidPattern is set to this instance.
//	 */
//	public void setValidPattern(boolean isValidPattern) {
//			origamiModel.setValidPattern(isValidPattern);
//	}
//
//	/**
//	 * @return hasModel
//	 */
//	public boolean hasModel() {
//		return origamiModel.hasModel();
//	}
//
////	/**
////	 * @param hasModel hasModel is set to this instance.
////	 */
////	public void setHasModel(boolean hasModel) {
////		this.hasModel = hasModel;
////	}
//
//	/**
//	 * @return sortedFaces
//	 */
//	public List<OriFace> getSortedFaces() {
//		List<OriFace> sortedFaces = origamiModel.getSortedFaces();
//		return sortedFaces;
//	}
//
//	/**
//	 * @param sortedFaces sortedFaces is set to this instance.
//	 */
//	public void setSortedFaces(List<OriFace> sortedFaces) {
//		origamiModel.setSortedFaces(sortedFaces);
//	}
//
//	/**
//	 * @return folded
//	 */
//	public boolean isFolded() {
//		return origamiModel.isFolded();
//	}
//
//	/**
//	 * @param folded folded is set to this instance.
//	 */
//	public void setFolded(boolean folded) {
//		origamiModel.setFolded(folded);
//	}

	//-------------------------------------------------------------

//	/**
//	 * @return currentORmatIndex
//	 */
//	public int getCurrentORmatIndex() {
//		int currentORmatIndex = foldedModelInfo.getCurrentORmatIndex();
//
//		return currentORmatIndex;
//	}
//
//	/**
//	 * @param currentORmatIndex currentORmatIndex is set to this instance.
//	 */
//	public void setCurrentORmatIndex(int currentORmatIndex) {
//		foldedModelInfo.setCurrentORmatIndex(currentORmatIndex);
//	}
//
//	/**
//	 * @return foldedBBoxLT
//	 */
//	public Vector2d getFoldedBBoxLT() {
//		return foldedModelInfo.getBoundBox().getLeftAndTop();
//	}
//
//
//	/**
//	 * @return foldedBBoxRB
//	 */
//	public Vector2d getFoldedBBoxRB() {
//		return foldedModelInfo.getBoundBox().getRightAndBottom();
//	}
//
//
//
//
//	/**
//	 * @return overlapRelation
//	 */
//	public int[][] getOverlapRelation() {
//		int[][] overlapRelation = foldedModelInfo.getOverlapRelation();
//		return overlapRelation;
//	}
//
//	/**
//	 * @param overlapRelation overlapRelation is set to this instance.
//	 */
//	public void setOverlapRelation(int[][] overlapRelation) {
//		foldedModelInfo.setOverlapRelation(overlapRelation);
//	}
//
//	/**
//	 * @return foldableOverlapRelations
//	 */
//	public List<int[][]> getFoldableOverlapRelations() {
//		List<int[][]> foldableOverlapRelations = foldedModelInfo.getFoldableOverlapRelations();
//
//		return foldableOverlapRelations;
//	}
//
//	/**
//	 * @param foldableOverlapRelations foldableOverlapRelations is set to this instance.
//	 */
//	public void setFoldableOverlapRelations(
//			List<int[][]> foldableOverlapRelations) {
//
//		foldedModelInfo.setFoldableOverlapRelations(foldableOverlapRelations);
//	}

	/**
	 * @param size size is set to this instance.
	 */
	public void setPaperSize(double size) {
		this.paperSize = size;
		origamiModel.setPaperSize(size);
		creasePattern.changePaperSize(size);
	}
	/**
	 * @return size
	 */
	public double getPaperSize() {
		return paperSize;
	}

	/**
	 * @return title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title title is set to this instance.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return editorName
	 */
	public String getEditorName() {
		return editorName;
	}

	/**
	 * @param editorName editorName is set to this instance.
	 */
	public void setEditorName(String editorName) {
		this.editorName = editorName;
	}

	/**
	 * @return originalAuthorName
	 */
	public String getOriginalAuthorName() {
		return originalAuthorName;
	}

	/**
	 * @param originalAuthorName originalAuthorName is set to this instance.
	 */
	public void setOriginalAuthorName(String originalAuthorName) {
		this.originalAuthorName = originalAuthorName;
	}

	/**
	 * @return memo
	 */
	public String getMemo() {
		return memo;
	}

	/**
	 * @param memo memo is set to this instance.
	 */
	public void setMemo(String memo) {
		this.memo = memo;
	}

	/**
	 * @return reference
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * @param reference reference is set to this instance.
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}
	
	
	
	
}
