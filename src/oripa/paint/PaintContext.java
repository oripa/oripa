package oripa.paint;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Stack;

import javax.vecmath.Vector2d;

import oripa.ORIPA;
import oripa.geom.OriLine;

public class PaintContext {
    
    
    private static PaintContext instance = null;
    

    public static PaintContext getInstance(){
    	if(instance == null){
    		instance = new PaintContext();
    	}
    	
    	return instance;
    }
    
    private PaintContext(){}
    
    
//--------------------------------------------------
    
    private Stack<Vector2d> pickedVertices = new Stack<>();


	private Stack<OriLine> pickedLines = new Stack<>();
    private boolean isPasting = false;	
	
	public Vector2d pickCandidateV = new Vector2d();
    public OriLine pickCandidateL = new OriLine();

	public boolean dispGrid = true;
    public double scale;

    private ArrayList<Vector2d> gridPoints;
    
    private boolean missionCompleted = false;
    
    
    private Point2D.Double mousePoint;


    public Point2D.Double getLogicalMousePoint() {
		return mousePoint;
	}

    public boolean isPasting() {
		return isPasting;
	}

	public void startPasting() {
		this.isPasting = true;
	}
	
	public void finishPasting() {
		this.isPasting = false;
	}
	

	public void setLogicalMousePoint(Point2D.Double logicalPoint) {
		this.mousePoint = logicalPoint;
	}

	public void set(double scale, boolean dispGrid){
    	this.scale = scale;
    	this.dispGrid = dispGrid;
    }

	public Collection<Vector2d> updateGrids(int gridDivNum){
		gridPoints = new ArrayList<>();
		
        double step = ORIPA.doc.size / gridDivNum;
        for (int ix = 0; ix < Globals.gridDivNum + 1; ix++) {
            for (int iy = 0; iy < gridDivNum + 1; iy++) {
                double x = -ORIPA.doc.size / 2 + step * ix;
                double y = -ORIPA.doc.size / 2 + step * iy;
                
                gridPoints.add(new Vector2d(x, y));
            }
        }

        return gridPoints;
	}
	

	/**
	 * remove all lines and all vertices in this context.
	 * 
	 * @param unselect	true if the removed lines should be marked as unselected.
	 */
    public void clear(boolean unselect){
    	
    	
    	if(unselect && pickedLines.empty() == false){
	    	for(OriLine l : pickedLines){
	    		l.selected = false;
	    	}
    	}    	
    	
    	pickedLines.clear();
    	pickedVertices.clear();
    	
    	pickCandidateL = null;
    	pickCandidateV = null;
    	
    	
    	missionCompleted = false;
    }

   
    
    public boolean isMissionCompleted() {
		return missionCompleted;
	}

	public void setMissionCompleted(boolean missionCompleted) {
		this.missionCompleted = missionCompleted;
	}

	public Stack<Vector2d> getVertices() {
		return pickedVertices;
	}
    
    public Stack<OriLine> getLines() {
		return pickedLines;
	}

	public OriLine getLine(int index){
    	return pickedLines.get(index);
    }
    
    
    public Vector2d getVertex(int index){
    	return pickedVertices.get(index);
    }
    
    public void pushVertex(Vector2d picked){
    	pickedVertices.push(picked);
    }
    	    
    public void pushLine(OriLine picked){
    //	picked.selected = true;
    	pickedLines.push(picked);
    }
    
    public Vector2d popVertex(){
    	if(pickedVertices.empty()){
    		return null;
    	}

    	return pickedVertices.pop();
    }

    
    /**
     * pop the last pushed line and mark it unselected.
     * @return popped line. null if no line is pushed.
     */
    public OriLine popLine(){
    	if(pickedLines.empty()){
    		return null;
    	}
    	
    	OriLine line = pickedLines.pop();
    	line.selected = false;
    	return line;
    }
    
    /**
     * performs the same as {@code Vector.remove(Object o)}.
     * @param line
     * @return
     */
    public boolean removeLine(OriLine line){
    	
    	return pickedLines.remove(line);
    }
    
    public Vector2d peekVertex(){
    	return pickedVertices.peek();
    }
    
    public OriLine peekLine(){
    	return pickedLines.peek();
    }

    public int getLineCount(){
    	return pickedLines.size();
    }

    public int getVertexCount(){
    	return pickedVertices.size();
    }


}
