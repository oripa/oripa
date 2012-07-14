package oripa.paint;

import java.awt.geom.Point2D;

public interface ActionState {
	public ActionState doAction(MouseContext context, 
			Point2D.Double currentPoint, boolean differentAction);

	public ActionState undo(MouseContext context);
	
//	public void onResult(MouseContext context);
	
	// syntax error. why?
//	public void setNextState(Class<? ActionState> state);	

	public void setNextState(ActionState state);	
	public void setPreviousState(ActionState state);

	public ActionState getNextState();	
	public ActionState getPreviousState();

}
