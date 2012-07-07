package oripa.paint;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Stack;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import oripa.Config;
import oripa.Constants;
import oripa.Globals;
import oripa.ORIPA;
import oripa.Constants.EditMode;
import oripa.geom.GeomUtil;
import oripa.geom.OriLine;

public abstract class GraphicMouseAction {

	protected void log(double x, double y){
		System.out.println(x + "," + y);
		
	}
	
	protected void log(Vector2d p){
		if(p == null){
			return;
		}
		log(p.x, p.y);
	}	
	
	

	protected Point2D.Double getLogicalPoint(AffineTransform affine, Point p){		// Gets the logical coordinates of the click
		return GeometricalOperation.getLogicalPoint(affine, p);
	}
	  // returns the OriLine sufficiently closer to point p
    public OriLine pickLine(Point2D.Double p, double scale) {
    	return GeometricalOperation.pickLine(p, scale);
    }
    
    private boolean pickPointOnLine(Point2D.Double p, Object[] line_vertex, double scale) {
    	return GeometricalOperation.pickPointOnLine(p, line_vertex, scale);
    }

    public Vector2d pickVertex(Point2D.Double p, double scale, boolean dispGrid) {
    	return GeometricalOperation.pickVertex(p, scale, dispGrid);
    }

	public Vector2d pick(MouseContext context, Point2D.Double currentPoint,
			boolean freeSelection){
		return GeometricalOperation.pickVertexByContext(context, currentPoint, freeSelection);
	}

	public Vector2d getCandidateVertex(MouseContext context, boolean enableMousePoint){
		return GeometricalOperation.getCandidateVertex(context, enableMousePoint);
	}

	private ActionState state;
	protected void setActionState(ActionState state){
		this.state = state;
	}
		
    public void onLeftClick(MouseContext context, AffineTransform affine, MouseEvent event){
		Point2D.Double clickPoint = getLogicalPoint(affine, event.getPoint());
		
		state = state.doAction(context, 
				clickPoint, buttonCTRLIsPressed(event));

    }

	public void onRightClick(MouseContext context, AffineTransform affine,
			MouseEvent event) {
		state = state.undo(context);
	}
	
	public Vector2d onMove(MouseContext context, AffineTransform affine, MouseEvent event) {
		Point2D.Double current = getLogicalPoint(affine, event.getPoint());

		Vector2d picked = pick(context, current, buttonCTRLIsPressed(event));

		context.pickCandidateV = picked;
		
		return picked;
	}

	public abstract void onDrag(MouseContext context, AffineTransform affine, MouseEvent event);

	public abstract void onRelease(MouseContext context, AffineTransform affine, MouseEvent event);
	
	public void onDraw(Graphics2D g2d, MouseContext context){
		drawPickedLines(g2d, context);
		drawPickedVertices(g2d, context);

	}
	
	private void drawPickedLines(Graphics2D g2d, MouseContext context){
		for(int i = 0; i < context.getLineCount(); i++){
			g2d.setColor(Config.LINE_COLOR_CANDIDATE);
			g2d.setStroke(Config.STROKE_PICKED);
			
			OriLine line = context.getLine(i);
			
			drawLine(g2d, line);
		}
		
	}
	
	
	private void drawPickedVertices(Graphics2D g2d, MouseContext context){
		ElementSelector selector = new ElementSelector();

		for(int i = 0; i < context.getVertexCount(); i++){
			g2d.setColor(selector.selectColorByPickupOrder(i));
			
			Vector2d vertex = context.getVertex(i);
            drawPickedVertex(g2d, context, vertex.x, vertex.y);
		}		
	}


    public void drawPickedVertex(Graphics2D g2d, MouseContext context, double x, double y){
    	double scale = context.scale;
        g2d.fill(new Rectangle2D.Double(x - 5.0 / scale,
                y - 5.0 / scale, 10.0 / scale, 10.0 / scale));

    }
    
    public void drawLine(Graphics2D g2d, OriLine line){
		g2d.draw(new Line2D.Double(line.p0.x, line.p0.y, 
				line.p1.x, line.p1.y));
    	
    }
    
    public void drawLine(Graphics2D g2d, Vector2d p0, Vector2d p1){
		g2d.draw(new Line2D.Double(p0.x, p0.y, 
				p1.x, p1.y));
    	
    }
    
    public void drawCandidateLine(Graphics2D g2d, MouseContext context){
		ElementSelector selector = new ElementSelector();

		if(context.getVertexCount() > 0){
			Vector2d picked = context.peekVertex();

			Color color = selector.selectColorByLineType(Globals.inputLineType);
			g2d.setColor(color);
			drawLine(g2d, picked, getCandidateVertex(context, true));
		}
    	
    }
    
    
	public boolean buttonCTRLIsPressed(MouseEvent event){
		return ((event.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) 
				== MouseEvent.CTRL_DOWN_MASK);		
	}

}
