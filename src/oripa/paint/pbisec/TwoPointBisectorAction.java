package oripa.paint.pbisec;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import oripa.paint.GraphicMouseAction;
import oripa.paint.MouseContext;

public class TwoPointBisectorAction extends GraphicMouseAction {


	public TwoPointBisectorAction(){
		setActionState(new SelectingFirstVertexForBisector());
	}
	

	

	@Override
	public void destroy(MouseContext context) {
		super.destroy(context);
		setActionState(new SelectingFirstVertexForBisector());
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
		
		drawPickCandidateVertex(g2d, context);

	}




	@Override
	public void onPressed(MouseContext context, AffineTransform affine,
			MouseEvent event) {
		// TODO Auto-generated method stub
		
	}

}
