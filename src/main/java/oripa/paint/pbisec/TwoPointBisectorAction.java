package oripa.paint.pbisec;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import oripa.paint.GraphicMouseAction;
import oripa.paint.PaintContext;

public class TwoPointBisectorAction extends GraphicMouseAction {


	public TwoPointBisectorAction(){
		setActionState(new SelectingFirstVertexForBisector());
	}
	

	

	@Override
	public void destroy(PaintContext context) {
		super.destroy(context);
		setActionState(new SelectingFirstVertexForBisector());
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
		
		drawPickCandidateVertex(g2d, context);

	}




	@Override
	public void onPress(PaintContext context, AffineTransform affine,
			boolean differentAction) {
		// TODO Auto-generated method stub
		
	}

}
