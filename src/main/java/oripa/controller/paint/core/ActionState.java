package oripa.controller.paint.core;

import java.awt.geom.Point2D;

import oripa.controller.paint.PaintContextInterface;

public interface ActionState {
	public ActionState doAction(PaintContextInterface context, 
			Point2D.Double currentPoint, boolean differentAction);

	public ActionState undo(PaintContextInterface context);
	

	public void setNextState(ActionState state);	
	public void setPreviousState(ActionState state);

	public ActionState getNextState();	
	public ActionState getPreviousState();

}
