package oripa.paint.segment;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import oripa.paint.GraphicMouseAction;
import oripa.paint.PaintContext;

public class TwoPointSegmentAction extends GraphicMouseAction {

	
	
	public TwoPointSegmentAction(){
		setActionState(new SelectingFirstVertexForSegment());
	}
	

	

	@Override
	public void destroy(PaintContext context) {
		super.destroy(context);
	}

	@Override
	public void recover(PaintContext context) {
		// TODO Auto-generated method stub
		setActionState(new SelectingFirstVertexForSegment());
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
		

		drawTemporaryLine(g2d, context);
		drawPickCandidateVertex(g2d, context);


	}




	@Override
	public void onPress(PaintContext context, AffineTransform affine,
			boolean differentAction) {
		// TODO Auto-generated method stub
		
	}

}
