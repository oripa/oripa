package oripa.paint.byvalue;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

import javax.vecmath.Vector2d;

import oripa.paint.ElementSelector;
import oripa.paint.Globals;
import oripa.paint.GraphicMouseAction;
import oripa.paint.PaintContext;

public class LineByValueAction extends GraphicMouseAction {

	
	public LineByValueAction(){
		setActionState(new SelectingVertexToDrawLine());
	}
	
	@Override
	public void onDrag(PaintContext context, AffineTransform affine,
			boolean differentAction) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRelease(PaintContext context, AffineTransform affine,
			boolean differentAction) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDraw(Graphics2D g2d, PaintContext context) {
		super.onDraw(g2d, context);
		
		drawPickCandidateVertex(g2d, context);
        Vector2d v = context.pickCandidateV;
		if(v != null){
	        try {
	            double length = ValueDB.getInstance().getLength();
	            double angle = ValueDB.getInstance().getAngle();
	
	            angle = Math.toRadians(angle);
	            
	            ElementSelector selector = new ElementSelector();
	            g2d.setColor(selector.selectColorByLineType(Globals.inputLineType));
	            g2d.setStroke(selector.selectStroke(Globals.inputLineType));
	            
	            Vector2d dir = new Vector2d(Math.cos(angle), -Math.sin(angle));
	            dir.scale(length);
	            g2d.draw(new Line2D.Double(v.x, v.y, v.x + dir.x, v.y + dir.y));
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		}
	}

	@Override
	public void onPress(PaintContext context, AffineTransform affine,
			boolean differentAction) {
		// TODO Auto-generated method stub
		
	}

	
}
