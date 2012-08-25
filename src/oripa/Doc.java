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

package oripa;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JOptionPane;
import javax.vecmath.Vector2d;

import oripa.geom.GeomUtil;
import oripa.geom.Line;
import oripa.geom.OriEdge;
import oripa.geom.OriFace;
import oripa.geom.OriHalfedge;
import oripa.geom.OriLine;
import oripa.geom.OriVertex;
import oripa.geom.Ray;
import oripa.paint.Globals;

class PointComparatorX implements Comparator<Vector2d> {

    @Override
    public int compare(Vector2d v1, Vector2d v2) {
        return v1.x > v2.x ? 1 : -1;
    }
}

class PointComparatorY implements Comparator<Vector2d> {

    @Override
    public int compare(Vector2d v1, Vector2d v2) {
        return ((Vector2d) v1).y > ((Vector2d) v2).y ? 1 : -1;
    }
}

class FaceOrderComparator implements Comparator<OriFace> {

    @Override
    public int compare(OriFace f1, OriFace f2) {
        return f1.z_order > f2.z_order ? 1 : -1;
    }
}


public class Doc {

    public ArrayList<OriLine> crossLines = new ArrayList<OriLine>();
    public ArrayList<OriLine> lines = new ArrayList<OriLine>();
    public ArrayList<OriFace> faces = new ArrayList<OriFace>();
    public ArrayList<OriVertex> vertices = new ArrayList<OriVertex>();
    public ArrayList<OriEdge> edges = new ArrayList<OriEdge>();
    public ArrayList<OriLine> tmpSelectedLines = new ArrayList<OriLine>();
    public boolean isValidPattern = false;
    public double size;
    public boolean hasModel = false;
    public boolean bFolded = false;
    public ArrayList<OriFace> sortedFaces = new ArrayList<OriFace>();
    public static final double POINT_EPS = 1.0;
    boolean bOutLog = true;
    public String dataFilePath = "";
    final public static int NO_OVERLAP = 0;
    final public static int UPPER = 1;
    final public static int LOWER = 2;
    final public static int UNDEFINED = 9;
    public int overlapRelation[][];
    public ArrayList<int[][]> overlapRelations = new ArrayList<int[][]>();
    public int currentORmatIndex;
    public String title;
    public String editorName;
    public String originalAuthorName;
    public String reference;
    public String memo;
    public Vector2d foldedBBoxLT;
    public Vector2d foldedBBoxRB;

    private UndoManager<UndoInfo> undoManager = new UndoManager<>();
    
    int debugCount = 0;

    public Doc(){
    	initialize(Constants.DEFAULT_PAPER_SIZE);
    }   
    
    public Doc(double size) {
    	initialize(size);
    }

    private void initialize(double size){
    	
        this.size = size;

        OriLine l0 = new OriLine(-size / 2.0, -size / 2.0, size / 2.0, -size / 2.0, OriLine.TYPE_CUT);
        OriLine l1 = new OriLine(size / 2.0, -size / 2.0, size / 2.0, size / 2.0, OriLine.TYPE_CUT);
        OriLine l2 = new OriLine(size / 2.0, size / 2.0, -size / 2.0, size / 2.0, OriLine.TYPE_CUT);
        OriLine l3 = new OriLine(-size / 2.0, size / 2.0, -size / 2.0, -size / 2.0, OriLine.TYPE_CUT);
        lines.add(l0);
        lines.add(l1);
        lines.add(l2);
        lines.add(l3);

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
    
    public void setNextORMat() {
        if (currentORmatIndex < overlapRelations.size() - 1) {
            currentORmatIndex++;
            Folder.matrixCopy(overlapRelations.get(currentORmatIndex), overlapRelation);
        }
    }

    public void setPrevORMat() {
        if (currentORmatIndex > 0) {
            currentORmatIndex--;
            Folder.matrixCopy(overlapRelations.get(currentORmatIndex), overlapRelation);
        }

    }

    public int getSelectedLineNum() {
        int count = 0;
        for (OriLine l : lines) {
            if (l.selected) {
                count++;
            }
        }
        return count;

    }

    public UndoInfo createUndoInfo(){
        UndoInfo undoInfo = new UndoInfo(lines);
        return undoInfo;
    }
    
    public void cacheUndoInfo(){
    	undoManager.setCache(createUndoInfo());
    }

    public void pushCachedUndoInfo(){
    	undoManager.pushCachedInfo();
    }
    
    public void pushUndoInfo() {
        UndoInfo ui = new UndoInfo(lines);
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
        
        lines.clear();
        lines.addAll(info.getLines());
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
        OriVertex vtx = null;
        for (OriVertex v : vertices) {
            if (GeomUtil.Distance(v.p, p) < POINT_EPS) {
                vtx = v;
            }
        }

        if (vtx == null) {
            vtx = new OriVertex(p);
            vertices.add(vtx);
        }

        return vtx;
    }

    public void prepareForCopyAndPaste() {
        tmpSelectedLines.clear();
        for (OriLine l : lines) {
            if (l.selected) {
                tmpSelectedLines.add(l);
            }
        }
    }

    // Turn the model over
    public void filpAll() {
        Vector2d maxV = new Vector2d(-Double.MAX_VALUE, -Double.MAX_VALUE);
        Vector2d minV = new Vector2d(Double.MAX_VALUE, Double.MAX_VALUE);
        for (OriFace face : ORIPA.doc.faces) {
            face.z_order = -face.z_order;
            for (OriHalfedge he : face.halfedges) {
                maxV.x = Math.max(maxV.x, he.vertex.p.x);
                maxV.y = Math.max(maxV.y, he.vertex.p.y);
                minV.x = Math.min(minV.x, he.vertex.p.x);
                minV.y = Math.min(minV.y, he.vertex.p.y);
            }
        }

        double centerX = (maxV.x + minV.x) / 2;

        for (OriFace face : ORIPA.doc.faces) {
            for (OriHalfedge he : face.halfedges) {
                he.positionForDisplay.x = 2 * centerX - he.positionForDisplay.x;
            }
        }

        for (OriFace face : ORIPA.doc.faces) {
            face.faceFront = !face.faceFront;
            face.setOutline();
        }

        Collections.sort(faces, new FaceOrderComparator());

        Collections.reverse(sortedFaces);


    }

    // 
    public void setFacesOutline(boolean isSlide) {
        int minDepth = Integer.MAX_VALUE;
        int maxDepth = -Integer.MAX_VALUE;

        for (OriFace f : faces) {
            minDepth = Math.min(minDepth, f.z_order);
            maxDepth = Math.max(minDepth, f.z_order);
            for (OriHalfedge he : f.halfedges) {
                he.positionForDisplay.set(he.vertex.p);
            }
            f.setOutline();
        }


        if (isSlide) {
            double slideUnit = 10.0 / (maxDepth - minDepth);
            for (OriVertex v : vertices) {
                v.tmpFlg = false;
                v.tmpVec.set(v.p);
            }

            for (OriFace f : faces) {
                Vector2d faceCenter = new Vector2d();
                for (OriHalfedge he : f.halfedges) {
                    faceCenter.add(he.vertex.p);
                }
                faceCenter.scale(1.0 / f.halfedges.size());

                for (OriHalfedge he : f.halfedges) {
                    if (he.vertex.tmpFlg) {
                        continue;
                    }
                    he.vertex.tmpFlg = true;

                    he.vertex.tmpVec.x += slideUnit * f.z_order;
                    he.vertex.tmpVec.y += slideUnit * f.z_order;

                    Vector2d dirToCenter = new Vector2d(faceCenter);
                    dirToCenter.sub(he.vertex.tmpVec);
                    dirToCenter.normalize();
                    dirToCenter.scale(6.0);
                    he.vertex.tmpVec.add(dirToCenter);
                }
            }

            for (OriFace f : faces) {
                for (OriHalfedge he : f.halfedges) {
                    he.positionForDisplay.set(he.vertex.tmpVec);
                }
                f.setOutline();
            }
        }
    }

    public boolean cleanDuplicatedLines() {
        debugCount = 0;
        System.out.println("pre cleanDuplicatedLines " + lines.size());
        ArrayList<OriLine> tmpLines = new ArrayList<OriLine>();
        for (OriLine l : lines) {
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

        if (lines.size() == tmpLines.size()) {
            return false;
        }

        lines.clear();
        lines.addAll(tmpLines);
        System.out.println("after cleanDuplicatedLines " + lines.size());

        return true;
    }

    // Unselect all lines
    public void resetSelectedOriLines() {
        for (OriLine line : lines) {
            line.selected = false;
        }
    }

    public void selectAllOriLines() {
        for (OriLine l : lines) {
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
    	mirrorCopyBy(l, lines);
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

    public void removeLine(OriLine l) {
        lines.remove(l);
        // merge the lines if possible, to prevent unnecessary vertexes
        merge2LinesAt(l.p0);
        merge2LinesAt(l.p1);
    }

    public void removeVertex(Vector2d v) {
        merge2LinesAt(v);
    }

    public void deleteSelectedLines() {
        ArrayList<OriLine> selectedLines = new ArrayList<OriLine>();
        for (OriLine line : lines) {
            if (line.selected) {
                selectedLines.add(line);
            }
        }

        for (OriLine line : selectedLines) {
            lines.remove(line);
        }
    }

    private void merge2LinesAt(Vector2d p) {
        ArrayList<OriLine> sharedLines = new ArrayList<OriLine>();
        for (OriLine line : lines) {
            if (GeomUtil.Distance(line.p0, p) < 0.001 || GeomUtil.Distance(line.p1, p) < 0.001) {
                sharedLines.add(line);
            }
        }

        if (sharedLines.size() != 2) {
            return;
        }

        OriLine l0 = sharedLines.get(0);
        OriLine l1 = sharedLines.get(1);

        if (l0.typeVal != l1.typeVal) {
            return;
        }

        // Check if the lines have the same angle
        Vector2d dir0 = new Vector2d(l0.p1.x - l0.p0.x, l0.p1.y - l0.p0.y);
        Vector2d dir1 = new Vector2d(l1.p1.x - l1.p0.x, l1.p1.y - l1.p0.y);

        dir0.normalize();
        dir1.normalize();

        if (!GeomUtil.isParallel(dir0, dir1)) {
            return;
        }

        // Merge possibility found
        Vector2d p0 = new Vector2d();
        Vector2d p1 = new Vector2d();

        if (GeomUtil.Distance(l0.p0, p) < 0.001) {
            p0.set(l0.p1);
        } else {
            p0.set(l0.p0);
        }
        if (GeomUtil.Distance(l1.p0, p) < 0.001) {
            p1.set(l1.p1);
        } else {
            p1.set(l1.p0);
        }

        lines.remove(l0);
        lines.remove(l1);
        OriLine li = new OriLine(p0, p1, l0.typeVal);
        lines.add(li);
    }

    public void CircleCopy(double cx, double cy, double angleDeg, int num) {
        ArrayList<OriLine> copiedLines = new ArrayList<OriLine>();

        pushUndoInfo();

        oripa.geom.RectangleClipper clipper = new oripa.geom.RectangleClipper(-size / 2, -size / 2, size / 2, size / 2);
        double angle = angleDeg * Math.PI / 180.0;

        for (int i = 0; i < num; i++) {
            double angleRad = angle * (i + 1);
            for (OriLine l : lines) {
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
        int startRow = bFillSheet ? (int) (-size / interY) : 0;
        int startCol = bFillSheet ? (int) (-size / interX) : 0;
        int endRow = bFillSheet ? (int) (size / interY + 0.5) : row;
        int endCol = bFillSheet ? (int) (size / interX + 0.5) : col;

        pushUndoInfo();

        System.out.println("startRow=" + startRow + " startCol=" + startCol + " endRow=" + endRow + " endCol=" + endCol);
        int lineNum = lines.size();

        ArrayList<OriLine> copiedLines = new ArrayList<OriLine>();

        oripa.geom.RectangleClipper clipper = new oripa.geom.RectangleClipper(-size / 2, -size / 2, size / 2, size / 2);
        for (int x = startCol; x < endCol; x++) {
            for (int y = startRow; y < endRow; y++) {
                if (x == 0 && y == 0) {
                    continue;
                }

                // copies the selected lines
                for (int i = 0; i < lineNum; i++) {
                    OriLine l = lines.get(i);
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

    public boolean buildOrigami(boolean needCleanUp) {
        edges.clear();
        vertices.clear();
        faces.clear();

        int lineNum = lines.size();

        for (int i = 0; i < lineNum; i++) {
            OriLine l = lines.get(i);
            if (l.typeVal == OriLine.TYPE_NONE) {
                continue;
            }

            OriVertex sv = addAndGetVertexFromVVec(l.p0);
            OriVertex ev = addAndGetVertexFromVVec(l.p1);
            OriEdge eg = new OriEdge(sv, ev, l.typeVal);
            edges.add(eg);
            sv.addEdge(eg);
            ev.addEdge(eg);
        }

        for (OriVertex v : vertices) {

            for (OriEdge e : v.edges) {

                if (e.type == OriLine.TYPE_CUT) {
                    continue;
                }

                if (v == e.sv) {
                    if (e.left != null) {
                        continue;
                    }
                } else {
                    if (e.right != null) {
                        continue;
                    }
                }

                OriFace face = new OriFace();
                faces.add(face);
                OriVertex walkV = v;
                OriEdge walkE = e;
                debugCount = 0;
                while (true) {
                    if (debugCount++ > 200) {
                        System.out.println("ERROR");
                        return false;
                    }
                    OriHalfedge he = new OriHalfedge(walkV, face);
                    face.halfedges.add(he);
                    he.tmpInt = walkE.type;
                    if (walkE.sv == walkV) {
                        walkE.left = he;
                    } else {
                        walkE.right = he;
                    }
                    walkV = walkE.oppositeVertex(walkV);
                    walkE = walkV.getPrevEdge(walkE);

                    if (walkV == v) {
                        break;
                    }
                }
                face.makeHalfedgeLoop();
                face.setOutline();
                face.setPreOutline();
            }
        }

        this.makeEdges();
        for (OriEdge e : edges) {
            e.type = e.left.tmpInt;
        }

        hasModel = true;

        return true;

    }

    public boolean buildOrigami3(boolean needCleanUp) {
        edges.clear();
        vertices.clear();
        faces.clear();

        // Remove lines with the same position
        debugCount = 0;
        if (needCleanUp) {
            if (cleanDuplicatedLines()) {
                JOptionPane.showMessageDialog(
                        ORIPA.mainFrame, "Removing multiples edges with the same position ",
                        "Simplifying CP", JOptionPane.INFORMATION_MESSAGE);
            }

        }

        int lineNum = lines.size();
        // Create the edges from the vertexes
        for (int i = 0; i < lineNum; i++) {
            OriLine l = lines.get(i);
            if (l.typeVal == OriLine.TYPE_NONE) {
                continue;
            }

            OriVertex sv = addAndGetVertexFromVVec(l.p0);
            OriVertex ev = addAndGetVertexFromVVec(l.p1);
            OriEdge eg = new OriEdge(sv, ev, l.typeVal);
            edges.add(eg);
            sv.addEdge(eg);
            ev.addEdge(eg);
        }


        // Check if there are vertexes with just 2 collinear edges with same type
        // merge the edges and delete the vertex for efficiency 
        ArrayList<OriEdge> eds = new ArrayList<OriEdge>();
        ArrayList<OriVertex> tmpVVec = new ArrayList<OriVertex>();
        tmpVVec.addAll(vertices);
        for (OriVertex v : tmpVVec) {
            eds.clear();
            for (OriEdge e : edges) {
                if (e.sv == v || e.ev == v) {
                    eds.add(e);
                }
            }

            if (eds.size() != 2) {
                continue;
            }

            // If the types of the edges are different, do nothing
            if (eds.get(0).type != eds.get(1).type) {
                continue;
            }

            OriEdge e0 = eds.get(0);
            OriEdge e1 = eds.get(1);

            // Check if they are collinear
            Vector2d dir0 = new Vector2d(e0.ev.p.x - e0.sv.p.x, e0.ev.p.y - e0.sv.p.y);
            Vector2d dir1 = new Vector2d(e1.ev.p.x - e1.sv.p.x, e1.ev.p.y - e1.sv.p.y);

            dir0.normalize();
            dir1.normalize();

            if (GeomUtil.Distance(dir0, dir1) > 0.001
                    && Math.abs(GeomUtil.Distance(dir0, dir1) - 2.0) > 0.001) {
                continue;
            }

            // found mergeable edge 
            edges.remove(e0);
            edges.remove(e1);
            vertices.remove(v);
            e0.sv.edges.remove(e0);
            e0.ev.edges.remove(e0);
            e1.sv.edges.remove(e1);
            e1.ev.edges.remove(e1);
            if (e0.sv == v && e1.sv == v) {
                OriEdge ne = new OriEdge(e0.ev, e1.ev, e0.type);
                edges.add(ne);
                ne.sv.addEdge(ne);
                ne.ev.addEdge(ne);
            } else if (e0.sv == v && e1.ev == v) {
                OriEdge ne = new OriEdge(e0.ev, e1.sv, e0.type);
                edges.add(ne);
                ne.sv.addEdge(ne);
                ne.ev.addEdge(ne);
            } else if (e0.ev == v && e1.sv == v) {
                OriEdge ne = new OriEdge(e0.sv, e1.ev, e0.type);
                edges.add(ne);
                ne.sv.addEdge(ne);
                ne.ev.addEdge(ne);
            } else {
                OriEdge ne = new OriEdge(e0.sv, e1.sv, e0.type);
                edges.add(ne);
                ne.sv.addEdge(ne);
                ne.ev.addEdge(ne);
            }
        }

       // System.out.println("vnum=" + vertices.size());
       // System.out.println("enum=" + edges.size());


        // Construct the faces
        for (OriVertex v : vertices) {

            for (OriEdge e : v.edges) {

                if (e.type == OriLine.TYPE_CUT) {
                    continue;
                }

                if (v == e.sv) {
                    if (e.left != null) {
                        continue;
                    }
                } else {
                    if (e.right != null) {
                        continue;
                    }
                }

                OriFace face = new OriFace();
                faces.add(face);
                OriVertex walkV = v;
                OriEdge walkE = e;
                debugCount = 0;
                while (true) {
                    if (debugCount++ > 100) {
                        System.out.println("ERROR");
                        return false;
                    }
                    OriHalfedge he = new OriHalfedge(walkV, face);
                    face.halfedges.add(he);
                    he.tmpInt = walkE.type;
                    if (walkE.sv == walkV) {
                        walkE.left = he;
                    } else {
                        walkE.right = he;
                    }
                    walkV = walkE.oppositeVertex(walkV);
                    walkE = walkV.getPrevEdge(walkE);
                    if (walkV == v) {
                        break;
                    }
                }
                face.makeHalfedgeLoop();
                face.setOutline();
                face.setPreOutline();
            }
        }

        this.makeEdges();
        for (OriEdge e : edges) {
            e.type = e.left.tmpInt;
        }

        hasModel = true;

        return checkPatternValidity();
    }

    public boolean checkPatternValidity() {
        boolean isOK = true;

        // Check if the faces are convex
        for (OriFace face : faces) {
            if (face.halfedges.size() == 3) {
                continue;
            }

            OriHalfedge baseHe = face.halfedges.get(0);
            boolean baseFlg = GeomUtil.CCWcheck(baseHe.prev.vertex.p, 
                    baseHe.vertex.p, baseHe.next.vertex.p);

            for (int i = 1; i < face.halfedges.size(); i++) {
                OriHalfedge he = face.halfedges.get(i);
                if (GeomUtil.CCWcheck(he.prev.vertex.p, he.vertex.p, he.next.vertex.p) != baseFlg) {
                    isOK = false;
                    face.hasProblem = true;
                    break;
                }

            }
        }

        // Check Maekawa's theorem for all vertexes
        for (OriVertex v : vertices) {
            int ridgeCount = 0;
            int valleyCount = 0;
            boolean isCorner = false;
            for (OriEdge e : v.edges) {
                if (e.type == OriLine.TYPE_RIDGE) {
                    ridgeCount++;
                } else if (e.type == OriLine.TYPE_VALLEY) {
                    valleyCount++;
                } else if (e.type == OriLine.TYPE_CUT) {
                    isCorner = true;
                    break;
                }
            }

            if (isCorner) {
                continue;
            }

            if (Math.abs(ridgeCount - valleyCount) != 2) {
                System.out.println("edge type count invalid: "+ v+" "+Math.abs(ridgeCount - valleyCount));
                v.hasProblem = true;
                isOK = false;
            }
        }

        // Check Kawasaki's theorem for every vertex

        for (OriVertex v : vertices) {
            if (v.hasProblem) {
                continue;
            }
            Vector2d p = v.p;
            double oddSum = 0;
            double evenSum = 0;
            boolean isCorner = false;
            for (int i = 0; i < v.edges.size(); i++) {
                OriEdge e = v.edges.get(i);
                if (e.type == OriLine.TYPE_CUT) {
                    isCorner = true;
                    break;
                }

                Vector2d preP = new Vector2d(v.edges.get(i).oppositeVertex(v).p);
                Vector2d nxtP = new Vector2d(v.edges.get((i + 1) % v.edges.size()).oppositeVertex(v).p);

                nxtP.sub(p);
                preP.sub(p);

                if (i % 2 == 0) {
                    oddSum += preP.angle(nxtP);
                } else {
                    evenSum += preP.angle(nxtP);
                }
            }

            if (isCorner) {
                continue;
            }

            //System.out.println("oddSum = " + oddSum + "/ evenSum = " + evenSum);
            if (Math.abs(oddSum - Math.PI) > Math.PI / 180 / 2) {
                System.out.println("edge angle sum invalid");
                v.hasProblem = true;
                isOK = false;
            }
        }

        isValidPattern = isOK;
        calcFoldedBoundingBox();
        return isOK;
    }
    boolean sortFinished = false;

    public boolean isLineCrossFace4(OriFace face, OriHalfedge heg) {
        Vector2d p1 = heg.positionAfterFolded;
        Vector2d p2 = heg.next.positionAfterFolded;
        Vector2d dir = new Vector2d();
        dir.sub(p2, p1);
        Line heLine = new Line(p1, dir);

        for (OriHalfedge he : face.halfedges) {
            // About the relation of contours (?)

            // Check if the line is on the countour of the face
            if (GeomUtil.DistancePointToLine(he.positionAfterFolded, heLine) < 1
                    && GeomUtil.DistancePointToLine(he.next.positionAfterFolded, heLine) < 1) {
                return false;
            }
        }

        Vector2d preCrossPoint = null;
        for (OriHalfedge he : face.halfedges) {
            // Checks if the line crosses any of the edges of the face
            Vector2d cp = GeomUtil.getCrossPoint(he.positionAfterFolded, he.next.positionAfterFolded, heg.positionAfterFolded, heg.next.positionAfterFolded);
            if (cp == null) {
                continue;
            }

            if (preCrossPoint == null) {
                preCrossPoint = cp;
            } else {
                if (GeomUtil.Distance(cp, preCrossPoint) > size * 0.001) {
                    return true;
                }
            }
        }

        // Checkes if the line is in the interior of the face
        if (isOnFace(face, heg.positionAfterFolded)) {
            return true;
        }
        if (isOnFace(face, heg.next.positionAfterFolded)) {
            return true;
        }

        return false;
    }

    public boolean isOnFace(OriFace face, Vector2d v) {

        int heNum = face.halfedges.size();

        // Return false if the vector is on the contour of the face
        for (int i = 0; i < heNum; i++) {
            OriHalfedge he = face.halfedges.get(i);
            if (GeomUtil.DistancePointToSegment(v, he.positionAfterFolded, he.next.positionAfterFolded) < size * 0.001) {
                return false;
            }
        }

        OriHalfedge baseHe = face.halfedges.get(0);
        boolean baseFlg = GeomUtil.CCWcheck(baseHe.positionAfterFolded, baseHe.next.positionAfterFolded, v);

        for (int i = 1; i < heNum; i++) {
            OriHalfedge he = face.halfedges.get(i);
            if (GeomUtil.CCWcheck(he.positionAfterFolded, he.next.positionAfterFolded, v) != baseFlg) {
                return false;
            }
        }

        return true;
    }

    public boolean addVertexOnLine(OriLine line, Vector2d v) {
        // Normally you dont want to add a vertex too close to the end of the line
        if (GeomUtil.Distance(line.p0, v) < this.size * 0.001
                || GeomUtil.Distance(line.p1, v) < this.size * 0.001) {
            return false;
        }
        OriLine l0 = new OriLine(line.p0, v, line.typeVal);
        OriLine l1 = new OriLine(v, line.p1, line.typeVal);
        lines.remove(line);
        lines.add(l0);
        lines.add(l1);

        return true;
    }

    public boolean foldWithoutLineType() {
        for (OriFace face : faces) {
            face.faceFront = true;
        }

        faces.get(0).z_order = 0;
        debugCount = 0;

        walkFace(faces.get(0));

        Collections.sort(faces, new FaceOrderComparator());
        sortedFaces.clear();
        sortedFaces.addAll(faces);


        for (OriEdge e : edges) {
            e.sv.p.set(e.left.tmpVec);
            e.sv.tmpFlg = false;
        }

        setFacesOutline(false);
        calcFoldedBoundingBox();
        return true;
    }

    public void calcFoldedBoundingBox() {
        foldedBBoxLT = new Vector2d(Double.MAX_VALUE, Double.MAX_VALUE);
        foldedBBoxRB = new Vector2d(-Double.MAX_VALUE, -Double.MAX_VALUE);
        for (OriFace face : faces) {
            for (OriHalfedge he : face.halfedges) {
                foldedBBoxLT.x = Math.min(foldedBBoxLT.x, he.tmpVec.x);
                foldedBBoxLT.y = Math.min(foldedBBoxLT.y, he.tmpVec.y);
                foldedBBoxRB.x = Math.max(foldedBBoxRB.x, he.tmpVec.x);
                foldedBBoxRB.y = Math.max(foldedBBoxRB.y, he.tmpVec.y);
            }
        }
    }


    // Make the folds by flipping the faces 
    private void walkFace(OriFace face) {
        face.tmpFlg = true;
        if (debugCount++ > 1000) {
            System.out.println("walkFace too deap");
            return;
        }
        for (OriHalfedge he : face.halfedges) {
            if (he.pair == null) {
                continue;
            }
            if (he.pair.face.tmpFlg) {
                continue;
            }

            flipFace2(he.pair.face, he);
            he.pair.face.tmpFlg = true;
            walkFace(he.pair.face);
        }
    }


    // Method that doesnt use sin con 
    private void flipFace2(OriFace face, OriHalfedge baseHe) {
        
        Vector2d preOrigin = new Vector2d(baseHe.pair.next.tmpVec);
        Vector2d afterOrigin = new Vector2d(baseHe.tmpVec);

        // Creates the base unit vector for before the rotation
        Vector2d baseDir = new Vector2d();
        baseDir.sub(baseHe.pair.tmpVec, baseHe.pair.next.tmpVec);

        // Creates the base unit vector for after the rotation
        Vector2d afterDir = new Vector2d();
        afterDir.sub(baseHe.next.tmpVec, baseHe.tmpVec);
        afterDir.normalize();

        Line preLine = new Line(preOrigin, baseDir);

        for (OriHalfedge he : face.halfedges) {
            double param[] = new double[1];
            double d0 = GeomUtil.Distance(he.tmpVec, preLine, param);
            double d1 = param[0];

            Vector2d footV = new Vector2d(afterOrigin);
            footV.x += d1 * afterDir.x;
            footV.y += d1 * afterDir.y;

            Vector2d afterDirFromFoot = new Vector2d();
            afterDirFromFoot.x = afterDir.y;
            afterDirFromFoot.y = -afterDir.x;

            he.tmpVec.x = footV.x + d0 * afterDirFromFoot.x;
            he.tmpVec.y = footV.y + d0 * afterDirFromFoot.y;

        }
        
        // Ivertion
        if (face.faceFront == baseHe.face.faceFront) {
            Vector2d ep = baseHe.next.tmpVec;
            Vector2d sp = baseHe.tmpVec;

            Vector2d b = new Vector2d();
            b.sub(ep, sp);
            for (OriHalfedge he : face.halfedges) {

                if (GeomUtil.Distance(he.tmpVec, new Line(sp, b)) < GeomUtil.EPS) {
                    continue;
                }
                if (Math.abs(b.y) < GeomUtil.EPS) {
                    Vector2d a = new Vector2d();
                    a.sub(he.tmpVec, sp);
                    a.y = -a.y;
                    he.tmpVec.y = a.y + sp.y;
                } else {
                    Vector2d a = new Vector2d();
                    a.sub(he.tmpVec, sp);
                    he.tmpVec.y = ((b.y * b.y - b.x * b.x) * a.y + 2 * b.x * b.y * a.x) / b.lengthSquared();
                    he.tmpVec.x = b.x / b.y * a.y - a.x + b.x / b.y * he.tmpVec.y;
                    he.tmpVec.x += sp.x;
                    he.tmpVec.y += sp.y;
                }
            }
            face.faceFront = !face.faceFront;
        }

        faces.remove(face);
        faces.add(face);
    }

    
    // Adds a rabbit-ear molecule given a triangle
    public void addTriangleDivideLines(Vector2d v0, Vector2d v1, Vector2d v2) {
        Vector2d c = GeomUtil.getIncenter(v0, v1, v2);
        if (c == null) {
            System.out.print("Failed to calculate incenter of the triangle");
        }
        addLine(new OriLine(c, v0, Globals.inputLineType));
        addLine(new OriLine(c, v1, Globals.inputLineType));
        addLine(new OriLine(c, v2, Globals.inputLineType));
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

        OriLine l = new OriLine(cp.x - dir.x, cp.y - dir.y, cp.x + dir.x, cp.y + dir.y, Globals.inputLineType);
        GeomUtil.clipLine(l, size / 2);
        addLine(l);
    }

    // v1-v2 is the symmetry line, v0-v1 is the sbject to be copied. 
    public void addSymmetricLine(Vector2d v0, Vector2d v1, Vector2d v2) {
        Vector2d v3 = GeomUtil.getSymmetricPoint(v0, v1, v2);
        Ray ray = new Ray(v1, new Vector2d(v3.x - v1.x, v3.y - v1.y));

        double minDist = Double.MAX_VALUE;
        Vector2d bestPoint = null;
        for (OriLine l : lines) {
            Vector2d crossPoint = GeomUtil.getCrossPoint(ray, l.getSegment());
            if (crossPoint == null) {
                continue;
            }
            double distance = GeomUtil.Distance(crossPoint, v1);
            if (distance < POINT_EPS) {
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

        addLine(new OriLine(v1, bestPoint, Globals.inputLineType));
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
        for (OriLine l : lines) {
            Vector2d crossPoint = GeomUtil.getCrossPoint(ray, l.getSegment());
            if (crossPoint == null) {
                continue;
            }
            double distance = GeomUtil.Distance(crossPoint, v1);
            if (distance < POINT_EPS) {
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

        addLine(new OriLine(v1, bestPoint, Globals.inputLineType));

        if (GeomUtil.Distance(bestPoint, startV) < POINT_EPS) {
            return;
        }

        addSymmetricLineAutoWalk(v1, bestPoint, bestLine.p0, stepCount, startV);

    }

    public void addBisectorLine(Vector2d v0, Vector2d v1, Vector2d v2, OriLine l) {
        Vector2d dir = GeomUtil.getBisectorVec(v0, v1, v2);
        Vector2d cp = GeomUtil.getCrossPoint(new Line(l.p0, new Vector2d(l.p1.x - l.p0.x, l.p1.y - l.p0.y)), new Line(v1, dir));

        OriLine nl = new OriLine(v1, cp, Globals.inputLineType);
        addLine(nl);

    }
 
    // Adds a new OriLine, also searching for intersections with others 
    // that would cause their mutual division
    public void addLine(OriLine inputLine) {
        ArrayList<OriLine> crossingLines = new ArrayList<OriLine>();
        ArrayList<OriLine> tmpLines = new ArrayList<OriLine>();
        tmpLines.addAll(lines);

        // If it already exists, do nothing
        for (OriLine line : tmpLines) {
            if (GeomUtil.isSameLineSegment(line, inputLine)) {
                return;
            }
        }

        // If it intersects other line, devide them
        for (OriLine line : tmpLines) {
            // Inputted line does not intersect
            if (inputLine.typeVal == OriLine.TYPE_NONE && line.typeVal != OriLine.TYPE_NONE) {
                continue;
            }
            Vector2d crossPoint = GeomUtil.getCrossPoint(inputLine, line);
            if (crossPoint == null) {
                continue;
            }

            crossingLines.add(line);
            lines.remove(line);

            if (GeomUtil.Distance(line.p0, crossPoint) > POINT_EPS) {
                lines.add(new OriLine(line.p0, crossPoint, line.typeVal));
            }

            if (GeomUtil.Distance(line.p1, crossPoint) > POINT_EPS) {
                lines.add(new OriLine(line.p1, crossPoint, line.typeVal));
            }
        }

        ArrayList<Vector2d> points = new ArrayList<Vector2d>();
        points.add(inputLine.p0);
        points.add(inputLine.p1);

        for (OriLine line : lines) {

            // Dont devide if the type of line is aux is Aux
            if (inputLine.typeVal != OriLine.TYPE_NONE && line.typeVal == OriLine.TYPE_NONE) {
                continue;
            }

            // If the intersection is on the end of the line, skip
            if (GeomUtil.Distance(inputLine.p0, line.p0) < POINT_EPS) {
                continue;
            }
            if (GeomUtil.Distance(inputLine.p0, line.p1) < POINT_EPS) {
                continue;
            }
            if (GeomUtil.Distance(inputLine.p1, line.p0) < POINT_EPS) {
                continue;
            }
            if (GeomUtil.Distance(inputLine.p1, line.p1) < POINT_EPS) {
                continue;
            }
            if (GeomUtil.DistancePointToSegment(line.p0, inputLine.p0, inputLine.p1) < POINT_EPS) {
                points.add(line.p0);
            }
            if (GeomUtil.DistancePointToSegment(line.p1, inputLine.p0, inputLine.p1) < POINT_EPS) {
                points.add(line.p1);
            }

            // Calculates the intersection
            Vector2d crossPoint = GeomUtil.getCrossPoint(inputLine, line);
            if (crossPoint != null) {
                points.add(crossPoint);
            }

        }
        boolean sortByX = Math.abs(inputLine.p0.x - inputLine.p1.x) > Math.abs(inputLine.p0.y - inputLine.p1.y);
        if (sortByX) {
            Collections.sort(points, new PointComparatorX());
        } else {
            Collections.sort(points, new PointComparatorY());
        }
        
        Vector2d prePoint = points.get(0);
        
        for (int i = 1; i < points.size(); i++) {
            Vector2d p = points.get(i);
            if (GeomUtil.Distance(prePoint, p) < POINT_EPS) {
                continue;
            }

            lines.add(new OriLine(prePoint, p, inputLine.typeVal));
            prePoint = p;
        }
    }

    public void makeEdges() {
        edges.clear();

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
        for (OriFace face : sortedFaces) {
            ArrayList<Vector2d> vv = new ArrayList<Vector2d>();
            int crossCount = 0;
            for (OriHalfedge he : face.halfedges) {
                OriLine l = new OriLine(he.positionForDisplay.x, he.positionForDisplay.y,
                        he.next.positionForDisplay.x, he.next.positionForDisplay.y, Globals.inputLineType);

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
                crossLines.add(new OriLine(vv.get(0), vv.get(1), Globals.inputLineType));
            }
        }

    }

    public void alterLineType(OriLine l, int lineTypeFromIndex,  int lineTypeToIndex) {
        if (lineTypeFromIndex == 1 /*M*/ && l.typeVal != OriLine.TYPE_RIDGE) {
            return;
        }
        if (lineTypeFromIndex == 2 /*V*/ && l.typeVal != OriLine.TYPE_VALLEY) {
            return;
        }

        switch (lineTypeToIndex) {
            case 0:
                l.typeVal = OriLine.TYPE_RIDGE;
                break;
            case 1:
                l.typeVal = OriLine.TYPE_VALLEY;
                break;
            case 2:
                l.typeVal = OriLine.TYPE_NONE;
                break;
            case 3:
                l.typeVal = OriLine.TYPE_CUT;
                break;
            case 4:
                removeLine(l);
                break;
            case 5: {
                if (l.typeVal == OriLine.TYPE_RIDGE) {
                    l.typeVal = OriLine.TYPE_VALLEY;
                } else if (l.typeVal == OriLine.TYPE_VALLEY) {
                    l.typeVal = OriLine.TYPE_RIDGE;
                }

            }
        }
    }
}
