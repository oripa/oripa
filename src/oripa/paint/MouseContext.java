package oripa.paint;

import java.awt.Point;
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
    	pickedLines.clear();
    	pickedVertices.clear();
    	tmpOutline.clear();
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
    	    
    void pushLine(OriLine picked){
    	pickedLines.push(picked);
    }
    
    Vector2d popVertex(){
    	return pickedVertices.pop();
    }

    OriLine popLine(){
    	return pickedLines.pop();
    }
    
    Vector2d peekVertex(){
    	return pickedVertices.peek();
    }
    
    OriLine peekLine(){
    	return pickedLines.peek();
    }

    public int getLineCount(){
    	return pickedLines.size();
    }

    public int getVertexCount(){
    	return pickedVertices.size();
    }


}
