package oripa.paint;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Stack;

import javax.vecmath.Vector2d;

import oripa.geom.OriLine;

public class MouseContext {
    public OriLine prePickLine = null;
    
    private Stack<Vector2d> pickedVertices = new Stack<>();


	private Stack<OriLine> pickedLines = new Stack<>();
    
    public Vector2d pickCandidateV = new Vector2d();
    public OriLine pickCandidateL = new OriLine();
    public ArrayList<Vector2d> tmpOutline = new ArrayList<>(); // Contour line when editing
    public boolean dispGrid = true;
    public double scale;

    
    private boolean missionCompleted = false;
    
    private static MouseContext instance = null;
    

    public static MouseContext getInstance(){
    	if(instance == null){
    		instance = new MouseContext();
    	}
    	
    	return instance;
    }
    
    private MouseContext(){}
    
    public Point2D.Double mousePoint;
    
    public void set(double scale, boolean dispGrid){
    	this.scale = scale;
    	this.dispGrid = dispGrid;
    }
    
    public void clear(){
    	
    	
    	if(pickedLines.empty() == false){
	    	for(OriLine l : pickedLines){
	    		l.selected = false;
	    	}
    	}    	
    	
    	pickedLines.clear();
    	pickedVertices.clear();
    	tmpOutline.clear();
    	
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
