package oripa.paint.symmetric;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import oripa.paint.GraphicMouseAction;
import oripa.paint.PaintContext;

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


		drawPickCandidateVertex(g2d, context);
	}



	@Override
	public void onPress(PaintContext context, AffineTransform affine,
			boolean differentAction) {
		// TODO Auto-generated method stub
		
	}

}
