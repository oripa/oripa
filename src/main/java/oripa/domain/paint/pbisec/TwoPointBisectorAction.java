package oripa.domain.paint.pbisec;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.GraphicMouseAction;

public class TwoPointBisectorAction extends GraphicMouseAction {


	public TwoPointBisectorAction(){
		setActionState(new SelectingFirstVertexForBisector());
	}
	

	

	@Override
	public void destroy(PaintContextInterface context) {
		super.destroy(context);
		setActionState(new SelectingFirstVertexForBisector());
	}




	@Override
	public void onDrag(PaintContextInterface context, AffineTransform affine, boolean differentAction) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRelease(PaintContextInterface context, AffineTransform affine,
			boolean differentAction) {
		// TODO Auto-generated method stub

	}
	

	
	@Override
	public void onDraw(Graphics2D g2d, PaintContextInterface context) {

		super.onDraw(g2d, context);
		
		drawPickCandidateVertex(g2d, context);

	}




	@Override
	public void onPress(PaintContextInterface context, AffineTransform affine,
			boolean differentAction) {
		// TODO Auto-generated method stub
		
	}

}
