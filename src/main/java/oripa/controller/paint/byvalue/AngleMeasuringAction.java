package oripa.controller.paint.byvalue;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import oripa.controller.paint.GraphicMouseActionInterface;
import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.core.GraphicMouseAction;

public class AngleMeasuringAction extends GraphicMouseAction {

	public AngleMeasuringAction(){
		setActionState(new SelectingVertexForAngle());
	}
	
	
	
	@Override
	public GraphicMouseActionInterface onLeftClick(PaintContextInterface context,
			AffineTransform affine, boolean differentAction) {

		GraphicMouseActionInterface action;
		action = super.onLeftClick(context, affine, differentAction);
		
		if(context.isMissionCompleted()){
			action = new LineByValueAction();
		}
		
		return action;
	}



	@Override
	public void onDrag(PaintContextInterface context, AffineTransform affine,
			boolean differentAction) {
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
