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
	public void onDrag(MouseContext context, AffineTransform affine, MouseEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRelease(MouseContext context, AffineTransform affine,
			MouseEvent event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	public void onDraw(Graphics2D g2d, MouseContext context) {

		super.onDraw(g2d, context);
		
		drawPickCandidateVertex(g2d, context);

	}

}
