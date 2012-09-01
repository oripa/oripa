package oripa.paint.segment;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import oripa.paint.GraphicMouseAction;
import oripa.paint.MouseContext;

public class TwoPointSegmentAction extends GraphicMouseAction {

	
	
	public TwoPointSegmentAction(){
		setActionState(new SelectingFirstVertexForSegment());
	}
	

	

	@Override
	public void destroy(MouseContext context) {
		super.destroy(context);
		setActionState(new SelectingFirstVertexForSegment());
	}




	@Override
	public void onDragged(MouseContext context, AffineTransform affine, MouseEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReleased(MouseContext context, AffineTransform affine,
			MouseEvent event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	public void onDraw(Graphics2D g2d, MouseContext context) {

		super.onDraw(g2d, context);
		

		drawTemporaryLine(g2d, context);
		drawPickCandidateVertex(g2d, context);


	}




	@Override
	public void onPressed(MouseContext context, AffineTransform affine,
			MouseEvent event) {
		// TODO Auto-generated method stub
		
	}

}
