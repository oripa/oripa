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
	
	private ActionState state;
	protected void setActionState(ActionState state){
		this.state = state;
	}
	
	protected boolean currentStateIs(Class<? extends ActionState> s){
		return state.equals(s);
	}
		
    public void onLeftClick(MouseContext context, AffineTransform affine, MouseEvent event){
		Point2D.Double clickPoint = GeometricalOperation.getLogicalPoint(affine, event.getPoint());
		
		state = state.doAction(context, 
				clickPoint, buttonCTRLIsPressed(event));

    }

	public void onRightClick(MouseContext context, AffineTransform affine,
			MouseEvent event) {
		state = state.undo(context);
	}
	
	/**
	 * searches vertex and line close enough to the mouse cursor.
	 * The result is stored into context.pickCandidateL(and V).
	 * 
	 * @param context
	 * @param affine
	 * @param event
	 * @return close vertex
	 */
	public Vector2d onMove(MouseContext context, AffineTransform affine, MouseEvent event) {
		Point2D.Double current = GeometricalOperation.getLogicalPoint(affine, event.getPoint());

		Vector2d closeVertex = GeometricalOperation.pickVertex(
				context, current, buttonCTRLIsPressed(event));

		context.pickCandidateV = closeVertex;
		
		OriLine closeLine;
		closeLine = GeometricalOperation.pickLine(
				context, current);
		
		context.pickCandidateL = closeLine;

		return closeVertex;
	}

	public abstract void onDrag(MouseContext context, AffineTransform affine, MouseEvent event);

	public abstract void onRelease(MouseContext context, AffineTransform affine, MouseEvent event);
	
	
	/**
	 * draws selected lines and selected vertices as selected state.
	 * 
	 * @param g2d
	 * @param context
	 */
	public void onDraw(Graphics2D g2d, MouseContext context){
		drawPickedLines(g2d, context);
		drawPickedVertices(g2d, context);

	}
	
	private void drawPickedLines(Graphics2D g2d, MouseContext context){
		for(int i = 0; i < context.getLineCount(); i++){
			g2d.setColor(Config.LINE_COLOR_PICKED);
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
            drawVertex(g2d, context, vertex.x, vertex.y);
		}		
	}


    public void drawVertex(Graphics2D g2d, MouseContext context, 
    		double x, double y){
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

    /**
     * draws the line between the most recently selected vertex and 
     * the closest vertex sufficiently to the mouse cursor.
     * if every vertex is far from cursor, this method uses the cursor point
     * instead of close vertex.
     * @param g2d
     * @param context
     */
    public void drawCandidateLine(Graphics2D g2d, MouseContext context){
		ElementSelector selector = new ElementSelector();

		if(context.getVertexCount() > 0){
			Vector2d picked = context.peekVertex();

			Color color = selector.selectColorByLineType(Globals.inputLineType);
			g2d.setColor(color);
			drawLine(g2d, picked, GeometricalOperation.getCandidateVertex(context, true));
		}
    	
    }
    
    
	public boolean buttonCTRLIsPressed(MouseEvent event){
		return ((event.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) 
				== MouseEvent.CTRL_DOWN_MASK);		
	}

}
