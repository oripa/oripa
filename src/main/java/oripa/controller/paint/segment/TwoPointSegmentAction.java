package oripa.controller.paint.segment;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.core.GraphicMouseAction;

public class TwoPointSegmentAction extends GraphicMouseAction {

	
	
	public TwoPointSegmentAction(){
		setActionState(new SelectingFirstVertexForSegment());
	}
	

	

	@Override
	public void destroy(PaintContextInterface context) {
		super.destroy(context);
	}

	@Override
	public void recover(PaintContextInterface context) {
		// TODO Auto-generated method stub
		setActionState(new SelectingFirstVertexForSegment());
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
		

		drawTemporaryLine(g2d, context);
		drawPickCandidateVertex(g2d, context);


	}




	@Override
	public void onPress(PaintContextInterface context, AffineTransform affine,
			boolean differentAction) {
		// TODO Auto-generated method stub
		
	}

}
