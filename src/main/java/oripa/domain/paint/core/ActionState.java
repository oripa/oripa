package oripa.domain.paint.core;

import javax.vecmath.Vector2d;

import oripa.domain.paint.PaintContextInterface;

public interface ActionState {
	public ActionState doAction(PaintContextInterface context,
			Vector2d currentPoint, boolean differentAction);

	public ActionState undo(PaintContextInterface context);

	public void setNextState(ActionState state);

	public void setPreviousState(ActionState state);

	public ActionState getNextState();

	public ActionState getPreviousState();

}
