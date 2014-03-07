package oripa.controller.paint.symmetric;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.vecmath.Vector2d;

import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.core.GraphicMouseAction;

public class SymmetricalLineAction extends GraphicMouseAction {


	public SymmetricalLineAction(){
		setActionState(new SelectingVertexForSymmetric());
	}



//	private OriLine closeLine = null;
//
//	@Override
//	public Vector2d onMove(MouseContext context, AffineTransform affine,
//			MouseEvent event) {
//		Vector2d result = super.onMove(context, affine, event);
//
//		if(context.getVertexCount() == 3){
//			if(closeLine != null){
//				closeLine.selected = false;
//			}
//			
//			closeLine = context.pickCandidateL;
//	
//			if(closeLine != null){
//				closeLine.selected = true;
//			}
//		}		
//		return result;
//	}



	@Override
	public Vector2d onMove(
			PaintContextInterface context, AffineTransform affine, boolean differentAction) {

		if (context.getVertexCount() < 2) {
			return super.onMove(context, affine, differentAction);
		}
		
		// enable auto-walk selection only
		return super.onMove(context, affine, false);
	}

	@Override
	public void onDrag(PaintContextInterface context, AffineTransform affine, boolean differentAction) {

	}

	@Override
	public void onRelease(PaintContextInterface context, AffineTransform affine,
			boolean differentAction) {

	}


	@Override
	public void onDraw(Graphics2D g2d, PaintContextInterface context) {

		super.onDraw(g2d, context);


		drawPickCandidateVertex(g2d, context);
	}



	@Override
	public void onPress(PaintContextInterface context, AffineTransform affine,
			boolean differentAction) {
		
	}

}
