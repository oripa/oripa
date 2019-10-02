package oripa.paint.core;

import java.awt.geom.Point2D;

public interface ActionState {
	ActionState doAction(PaintContext context, Point2D.Double currentPoint, boolean differentAction);

	ActionState undo(PaintContext context);
	

	void setNextState(ActionState state);
	void setPreviousState(ActionState state);

	ActionState getNextState();
	ActionState getPreviousState();

}
