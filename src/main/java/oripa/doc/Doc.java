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
import java.util.Comparator;
import java.util.List;

import javax.vecmath.Vector2d;

import oripa.ORIPA;
import oripa.doc.command.ElementRemover;
import oripa.doc.command.LineAdder;
import oripa.doc.command.LinePaster;
import oripa.doc.core.CreasePattern;
import oripa.fold.FoldedModelInfo;
import oripa.fold.OriEdge;
import oripa.fold.OriFace;
import oripa.fold.OriHalfedge;
import oripa.fold.OriVertex;
import oripa.fold.OrigamiModel;
import oripa.geom.GeomUtil;
import oripa.geom.Line;
import oripa.geom.Ray;
import oripa.paint.core.PaintConfig;
import oripa.resource.Constants;
import oripa.value.OriLine;


public class Doc {
	class PointComparatorX implements Comparator<Vector2d> {

		@Override
		public int compare(Vector2d v1, Vector2d v2) {
			if(v1.x == v2.x){
				return 0;
			}
			return v1.x > v2.x ? 1 : -1;
		}
	}

	class PointComparatorY implements Comparator<Vector2d> {

		@Override
		public int compare(Vector2d v1, Vector2d v2) {
			if(v1.y == v2.y){
				return 0;
			}
			return ((Vector2d) v1).y > ((Vector2d) v2).y ? 1 : -1;
		}
	}

	class FaceOrderComparator implements Comparator<OriFace> {

		@Override
		public int compare(OriFace f1, OriFace f2) {
			return f1.z_order > f2.z_order ? 1 : -1;
		}
	}


	
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


	public int countSelectedLineNum(Collection<OriLine> creasePattern) {
		int count = 0;
		for (OriLine l : creasePattern) {
			if (l.selected) {
				count++;
			}
		}
		return count;

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

	private OriVertex addAndGetVertexFromVVec(Vector2d p) {
		List<OriVertex> vertices = origamiModel.getVertices();

		OriVertex vtx = null;
		for (OriVertex v : vertices) {
			if (GeomUtil.Distance(v.p, p) < CalculationResource.POINT_EPS) {
				vtx = v;
			}
		}

		if (vtx == null) {
			vtx = new OriVertex(p);
			vertices.add(vtx);
		}

		return vtx;
	}



	public boolean cleanDuplicatedLines() {
		debugCount = 0;
		System.out.println("pre cleanDuplicatedLines " + creasePattern.size());
		ArrayList<OriLine> tmpLines = new ArrayList<OriLine>();
		for (OriLine l : creasePattern) {
			OriLine ll = l;

			boolean bSame = false;
			// Test if the line is already in tmpLines to prevent duplicity
			for (OriLine line : tmpLines) {
				if (GeomUtil.isSameLineSegment(line, ll)) {
					bSame = true;
					break;
				}
			}
			if (bSame) {
				continue;
			}
			tmpLines.add(ll);
		}

		if (creasePattern.size() == tmpLines.size()) {
			return false;
		}

		creasePattern.clear();
		creasePattern.addAll(tmpLines);
		System.out.println("after cleanDuplicatedLines " + creasePattern.size());

		return true;
	}

	// Unselect all lines
	public void resetSelectedOriLines() {
		for (OriLine line : creasePattern) {
			line.selected = false;
		}
	}

	public void selectAllOriLines() {
		for (OriLine l : creasePattern) {
			if (l.typeVal != OriLine.TYPE_CUT) {
				l.selected = true;
			}
		}
	}

	private OriLine getMirrorCopiedLine(OriLine line, OriLine baseOriLine) {
		Line baseLine = baseOriLine.getLine();
		double dist0 = GeomUtil.Distance(line.p0, baseLine);
		Vector2d dir0 = new Vector2d();
		if (GeomUtil.isRightSide(line.p0, baseLine)) {
			dir0.set(-baseLine.dir.y, baseLine.dir.x);
		} else {
			dir0.set(baseLine.dir.y, -baseLine.dir.x);
		}
		dir0.normalize();
		Vector2d q0 = new Vector2d(
				line.p0.x + dir0.x * dist0 * 2,
				line.p0.y + dir0.y * dist0 * 2);

		double dist1 = GeomUtil.Distance(line.p1, baseLine);
		Vector2d dir1 = new Vector2d();
		if (GeomUtil.isRightSide(line.p1, baseLine)) {
			dir1.set(-baseLine.dir.y, baseLine.dir.x);
		} else {
			dir1.set(baseLine.dir.y, -baseLine.dir.x);
		}
		dir1.normalize();
		Vector2d q1 = new Vector2d(
				line.p1.x + dir1.x * dist1 * 2,
				line.p1.y + dir1.y * dist1 * 2);

		OriLine oriLine = new OriLine(q0, q1, line.typeVal);

		return oriLine;
	}

	public void mirrorCopyBy(OriLine l) {
		mirrorCopyBy(l, creasePattern);
	}


	public void mirrorCopyBy(OriLine l, 
			Collection<OriLine> lines) {

		ArrayList<OriLine> copiedLines = new ArrayList<OriLine>();
		for (OriLine line : lines) {
			if (!line.selected) {
				continue;
			}
			if (line == l) {
				continue;
			}

			copiedLines.add(getMirrorCopiedLine(line, l));
		}

		for (OriLine line : copiedLines) {
			addLine(line);
		}

	}    

	ElementRemover remover = new ElementRemover();
	public void removeLine(OriLine l) {
		remover.removeLine(l, creasePattern);
	}

	public void removeVertex(Vector2d v) {
		remover.removeVertex(v, creasePattern);
	}

	public void deleteSelectedLines() {
		ArrayList<OriLine> selectedLines = new ArrayList<OriLine>();
		for (OriLine line : creasePattern) {
			if (line.selected) {
				selectedLines.add(line);
			}
		}

		for (OriLine line : selectedLines) {
			creasePattern.remove(line);
		}
	}


	public void CircleCopy(double cx, double cy, double angleDeg, int num) {
		ArrayList<OriLine> copiedLines = new ArrayList<OriLine>();

		pushUndoInfo();

		oripa.geom.RectangleClipper clipper = new oripa.geom.RectangleClipper(-paperSize / 2, -paperSize / 2, paperSize / 2, paperSize / 2);
		double angle = angleDeg * Math.PI / 180.0;

		for (int i = 0; i < num; i++) {
			double angleRad = angle * (i + 1);
			for (OriLine l : creasePattern) {
				if (!l.selected) {
					continue;
				}

				OriLine cl = new OriLine(l);
				double tx0 = l.p0.x - cx;
				double ty0 = l.p0.y - cy;
				double tx1 = l.p1.x - cx;
				double ty1 = l.p1.y - cy;

				double ttx0 = tx0 * Math.cos(angleRad) - ty0 * Math.sin(angleRad);
				double tty0 = tx0 * Math.sin(angleRad) + ty0 * Math.cos(angleRad);

				double ttx1 = tx1 * Math.cos(angleRad) - ty1 * Math.sin(angleRad);
				double tty1 = tx1 * Math.sin(angleRad) + ty1 * Math.cos(angleRad);

				cl.p0.x = ttx0 + cx;
				cl.p0.y = tty0 + cy;
				cl.p1.x = ttx1 + cx;
				cl.p1.y = tty1 + cy;

				if (clipper.clip(cl)) {
					copiedLines.add(cl);
				}
			}
		}
		for (OriLine l : copiedLines) {
			addLine(l);
		}

		resetSelectedOriLines();
	}

	public void ArrayCopy(int row, int col, double interX, double interY, boolean bFillSheet) {
		int startRow = bFillSheet ? (int) (-paperSize / interY) : 0;
		int startCol = bFillSheet ? (int) (-paperSize / interX) : 0;
		int endRow = bFillSheet ? (int) (paperSize / interY + 0.5) : row;
		int endCol = bFillSheet ? (int) (paperSize / interX + 0.5) : col;

		pushUndoInfo();

		System.out.println("startRow=" + startRow + " startCol=" + startCol + " endRow=" + endRow + " endCol=" + endCol);

		ArrayList<OriLine> copiedLines = new ArrayList<OriLine>();

		oripa.geom.RectangleClipper clipper = new oripa.geom.RectangleClipper(-paperSize / 2, -paperSize / 2, paperSize / 2, paperSize / 2);
		for (int x = startCol; x < endCol; x++) {
			for (int y = startRow; y < endRow; y++) {
				if (x == 0 && y == 0) {
					continue;
				}

				// copies the selected lines
				for (OriLine l : creasePattern) {
					if (!l.selected) {
						continue;
					}

					OriLine cl = new OriLine(l);
					cl.p0.x += interX * x;
					cl.p0.y += interY * y;
					cl.p1.x += interX * x;
					cl.p1.y += interY * y;

					if (clipper.clip(cl)) {
						copiedLines.add(cl);
					}
				}
			}
		}

		for (OriLine l : copiedLines) {
			addLine(l);
		}
		resetSelectedOriLines();
	}

//	public boolean buildOrigami(boolean needCleanUp) {
//		List<OriFace> faces = origamiModel.getFaces();
//		List<OriEdge> edges = origamiModel.getEdges();
//		List<OriVertex> vertices = origamiModel.getVertices();
//
//		edges.clear();
//		vertices.clear();
//		faces.clear();
//
//
//		for (OriLine l : creasePattern) {
//			if (l.typeVal == OriLine.TYPE_NONE) {
//				continue;
//			}
//
//			OriVertex sv = addAndGetVertexFromVVec(l.p0);
//			OriVertex ev = addAndGetVertexFromVVec(l.p1);
//			OriEdge eg = new OriEdge(sv, ev, l.typeVal);
//			edges.add(eg);
//			sv.addEdge(eg);
//			ev.addEdge(eg);
//		}
//
//		for (OriVertex v : vertices) {
//
//			for (OriEdge e : v.edges) {
//
//				if (e.type == OriLine.TYPE_CUT) {
//					continue;
//				}
//
//				if (v == e.sv) {
//					if (e.left != null) {
//						continue;
//					}
//				} else {
//					if (e.right != null) {
//						continue;
//					}
//				}
//
//				OriFace face = new OriFace();
//				faces.add(face);
//				OriVertex walkV = v;
//				OriEdge walkE = e;
//				debugCount = 0;
//				while (true) {
//					if (debugCount++ > 200) {
//						System.out.println("ERROR");
//						return false;
//					}
//					OriHalfedge he = new OriHalfedge(walkV, face);
//					face.halfedges.add(he);
//					he.tmpInt = walkE.type;
//					if (walkE.sv == walkV) {
//						walkE.left = he;
//					} else {
//						walkE.right = he;
//					}
//					walkV = walkE.oppositeVertex(walkV);
//					walkE = walkV.getPrevEdge(walkE);
//
//					if (walkV == v) {
//						break;
//					}
//				}
//				face.makeHalfedgeLoop();
//				face.setOutline();
//				face.setPreOutline();
//			}
//		}
//
//		this.makeEdges();
//		for (OriEdge e : edges) {
//			e.type = e.left.tmpInt;
//		}
//
//		
//		origamiModel.setHasModel(true);
//
//		return true;
//
//	}


	boolean sortFinished = false;


	public boolean addVertexOnLine(OriLine line, Vector2d v) {
		// Normally you dont want to add a vertex too close to the end of the line
		if (GeomUtil.Distance(line.p0, v) < this.paperSize * 0.001
				|| GeomUtil.Distance(line.p1, v) < this.paperSize * 0.001) {
			return false;
		}
		OriLine l0 = new OriLine(line.p0, v, line.typeVal);
		OriLine l1 = new OriLine(v, line.p1, line.typeVal);
		creasePattern.remove(line);
		creasePattern.add(l0);
		creasePattern.add(l1);

		return true;
	}


	// Adds a rabbit-ear molecule given a triangle
	public void addTriangleDivideLines(Vector2d v0, Vector2d v1, Vector2d v2) {
		Vector2d c = GeomUtil.getIncenter(v0, v1, v2);
		if (c == null) {
			System.out.print("Failed to calculate incenter of the triangle");
		}
		addLine(new OriLine(c, v0, PaintConfig.inputLineType));
		addLine(new OriLine(c, v1, PaintConfig.inputLineType));
		addLine(new OriLine(c, v2, PaintConfig.inputLineType));
	}

	// Adds perpendicular bisector
	public void addPBisector(Vector2d v0, Vector2d v1) {
		Vector2d cp = new Vector2d(v0);
		cp.add(v1);
		cp.scale(0.5);
		Vector2d dir = new Vector2d();
		dir.sub(v0, v1);
		double tmp = dir.y;
		dir.y = -dir.x;
		dir.x = tmp;
		dir.scale(Constants.DEFAULT_PAPER_SIZE * 8);

		OriLine l = new OriLine(cp.x - dir.x, cp.y - dir.y, cp.x + dir.x, cp.y + dir.y, PaintConfig.inputLineType);
		GeomUtil.clipLine(l, paperSize / 2);
		addLine(l);
	}

	// v1-v2 is the symmetry line, v0-v1 is the sbject to be copied. 
	public void addSymmetricLine(Vector2d v0, Vector2d v1, Vector2d v2) {
		Vector2d v3 = GeomUtil.getSymmetricPoint(v0, v1, v2);
		Ray ray = new Ray(v1, new Vector2d(v3.x - v1.x, v3.y - v1.y));

		double minDist = Double.MAX_VALUE;
		Vector2d bestPoint = null;
		for (OriLine l : creasePattern) {
			Vector2d crossPoint = GeomUtil.getCrossPoint(ray, l.getSegment());
			if (crossPoint == null) {
				continue;
			}
			double distance = GeomUtil.Distance(crossPoint, v1);
			if (distance < CalculationResource.POINT_EPS) {
				continue;
			}

			if (distance < minDist) {
				minDist = distance;
				bestPoint = crossPoint;
			}
		}

		if (bestPoint == null) {
			return;
		}

		addLine(new OriLine(v1, bestPoint, PaintConfig.inputLineType));
	}

	// v1-v2 is the symmetry line, v0-v1 is the sbject to be copied.
	// automatically generates possible rebouncing of the fold (used when Ctrl is pressed)
	public void addSymmetricLineAutoWalk(Vector2d v0, Vector2d v1, Vector2d v2, int stepCount, Vector2d startV) {
		stepCount++;
		if (stepCount > 36) {
			return;
		}
		Vector2d v3 = GeomUtil.getSymmetricPoint(v0, v1, v2);
		Ray ray = new Ray(v1, new Vector2d(v3.x - v1.x, v3.y - v1.y));

		double minDist = Double.MAX_VALUE;
		Vector2d bestPoint = null;
		OriLine bestLine = null;
		for (OriLine l : creasePattern) {
			Vector2d crossPoint = GeomUtil.getCrossPoint(ray, l.getSegment());
			if (crossPoint == null) {
				continue;
			}
			double distance = GeomUtil.Distance(crossPoint, v1);
			if (distance < CalculationResource.POINT_EPS) {
				continue;
			}

			if (distance < minDist) {
				minDist = distance;
				bestPoint = crossPoint;
				bestLine = l;
			}
		}

		if (bestPoint == null) {
			return;
		}

		addLine(new OriLine(v1, bestPoint, PaintConfig.inputLineType));

		if (GeomUtil.Distance(bestPoint, startV) < CalculationResource.POINT_EPS) {
			return;
		}

		addSymmetricLineAutoWalk(v1, bestPoint, bestLine.p0, stepCount, startV);

	}

	public void addBisectorLine(Vector2d v0, Vector2d v1, Vector2d v2, OriLine l) {
		Vector2d dir = GeomUtil.getBisectorVec(v0, v1, v2);
		Vector2d cp = GeomUtil.getCrossPoint(new Line(l.p0, new Vector2d(l.p1.x - l.p0.x, l.p1.y - l.p0.y)), new Line(v1, dir));

		OriLine nl = new OriLine(v1, cp, PaintConfig.inputLineType);
		addLine(nl);

	}


	// Adds a new OriLine, also searching for intersections with others 
	// that would cause their mutual division
	public void addLine(OriLine inputLine) {
		LineAdder lineAdder = new LineAdder();
		
		lineAdder.addLine(inputLine, creasePattern);		
	}

	public void pasteLines(Collection<OriLine> lines){
		LinePaster paster = new LinePaster();
		
		paster.paste(lines, creasePattern);
	}
	
	
	private void makeEdges() {
		List<OriEdge> edges = origamiModel.getEdges();
		edges.clear();

		List<OriFace> faces = origamiModel.getFaces();

		ArrayList<OriHalfedge> tmpHalfedges = new ArrayList<OriHalfedge>();

		// Clear all the Halfedges
		for (OriFace face : faces) {
			for (OriHalfedge he : face.halfedges) {
				he.pair = null;
				he.edge = null;
				tmpHalfedges.add(he);
			}
		}

		// Search the halfedge pair
		int heNum = tmpHalfedges.size();
		for (int i = 0; i < heNum; i++) {
			OriHalfedge he0 = tmpHalfedges.get(i);
			if (he0.pair != null) {
				continue;
			}

			for (int j = i + 1; j < heNum; j++) {
				OriHalfedge he1 = tmpHalfedges.get(j);
				if (he0.vertex == he1.next.vertex && he0.next.vertex == he1.vertex) {
					OriEdge edge = new OriEdge();
					he0.pair = he1;
					he1.pair = he0;
					he0.edge = edge;
					he1.edge = edge;
					edge.sv = he0.vertex;
					edge.ev = he1.vertex;
					edge.left = he0;
					edge.right = he1;
					edges.add(edge);
					edge.type = OriLine.TYPE_NONE;//OriEdge.TYPE_NONE;
				}
			}
		}

		// If the pair wasnt found it should be an edge
		for (OriHalfedge he : tmpHalfedges) {
			if (he.pair == null) {
				OriEdge edge = new OriEdge();
				he.edge = edge;
				edge.sv = he.vertex;
				edge.ev = he.next.vertex;
				edge.left = he;
				edges.add(edge);
				edge.type = OriLine.TYPE_CUT;
			}
		}
	}



	public void setCrossLine(OriLine line) {
		crossLines.clear();

		List<OriFace> sortedFaces = origamiModel.getSortedFaces();

		for (OriFace face : sortedFaces) {
			ArrayList<Vector2d> vv = new ArrayList<Vector2d>();
			int crossCount = 0;
			for (OriHalfedge he : face.halfedges) {
				OriLine l = new OriLine(he.positionForDisplay.x, he.positionForDisplay.y,
						he.next.positionForDisplay.x, he.next.positionForDisplay.y, PaintConfig.inputLineType);

				double params[] = new double[2];
				boolean res = GeomUtil.getCrossPointParam(line.p0, line.p1, l.p0, l.p1, params);
				if (res == true && params[0] > -0.001 && params[1] > -0.001 && params[0] < 1.001 && params[1] < 1.001) {
					double param = params[1];
					crossCount++;

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

	public void alterLineType(OriLine l, TypeForChange from,  TypeForChange to) {
		LineTypeChanger changer = new LineTypeChanger();
		changer.alterLineType(l, creasePattern, from, to);
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
	 * @param origamiModel origamiModelを登録する
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
	 * @param foldedModelInfo foldedModelInfoを登録する
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
	 * @param crossLines crossLinesを登録する
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
//	 * @param faces facesを登録する
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
//	 * @param vertices verticesを登録する
//	 */
//	public void setVertices(List<OriVertex> vertices) {
//		origamiModel.setVertices(vertices);;
//	}
//
//	/**
//	 * @param edges edgesを登録する
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
//	 * @param isValidPattern isValidPatternを登録する
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
////	 * @param hasModel hasModelを登録する
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
//	 * @param sortedFaces sortedFacesを登録する
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
//	 * @param folded foldedを登録する
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
//	 * @param currentORmatIndex currentORmatIndexを登録する
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
//	 * @param overlapRelation overlapRelationを登録する
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
//	 * @param foldableOverlapRelations foldableOverlapRelationsを登録する
//	 */
//	public void setFoldableOverlapRelations(
//			List<int[][]> foldableOverlapRelations) {
//
//		foldedModelInfo.setFoldableOverlapRelations(foldableOverlapRelations);
//	}

	/**
	 * @param size sizeを登録する
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
	 * @param title titleを登録する
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
	 * @param editorName editorNameを登録する
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
	 * @param originalAuthorName originalAuthorNameを登録する
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
	 * @param memo memoを登録する
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
	 * @param reference referenceを登録する
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}
	
	
	
	
}
