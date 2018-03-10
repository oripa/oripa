package oripa.paint.byvalue;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

import javax.vecmath.Vector2d;

import oripa.paint.core.PaintConfig;
import oripa.paint.core.GraphicMouseAction;
import oripa.paint.core.PaintContext;

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
	            
	            g2d.setColor(PaintConfig.colors.getColorForLine(PaintConfig.inputLineType));
				g2d.setStroke(PaintConfig.colors.getStrokeForLine(PaintConfig.inputLineType, context.scale));


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
