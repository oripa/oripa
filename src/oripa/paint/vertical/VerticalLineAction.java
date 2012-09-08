package oripa.paint.vertical;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.vecmath.Vector2d;

import oripa.geom.OriLine;
import oripa.paint.GraphicMouseAction;
import oripa.paint.PaintContext;

public class VerticalLineAction extends GraphicMouseAction {


	public VerticalLineAction(){
		setActionState(new SelectingVertexForVertical());
	}


	@Override
	public void destroy(PaintContext context) {
		super.destroy(context);
		setActionState(new SelectingVertexForVertical());
		
	}


	private OriLine closeLine = null;

	@Override
	public Vector2d onMove(PaintContext context, AffineTransform affine,
			boolean differentAction) {
		Vector2d result = super.onMove(context, affine, differentAction);

		if(context.getVertexCount() == 1){
			if(closeLine != null){
				closeLine.selected = false;
			}
			
			closeLine = context.pickCandidateL;
	
			if(closeLine != null){
				closeLine.selected = true;
			}
		}		
		return result;
	}







	@Override
	public void onDrag(PaintContext context, AffineTransform affine, boolean differentAction) {
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


		if(context.getVertexCount() == 0){

			drawPickCandidateVertex(g2d, context);
		}
		else if(context.getVertexCount() == 1){
			drawPickCandidateLine(g2d, context);
			
		}
	}




	@Override
	public void onPress(PaintContext context, AffineTransform affine,
			boolean differentAction) {
		// TODO Auto-generated method stub
		
	}

}
