package oripa.domain.paint;

import oripa.vecmath.Vector2d;

/**
 * An interface for (modified) State pattern with undo. Implementation of this
 * interface should determine the state to be used after every action/undo.
 *
 * @author OUCHI Koji
 */
public interface ActionState {
	/**
	 * Performs the action of this state and returns the next state. Action can
	 * vary by context like first action selects a vertex and second action
	 * selects a line. This method should clear selection of vertices and lines
	 * if the final action is performed.
	 *
	 * @param context
	 * @param currentPoint
	 *            Deprecated. This will be deleted in the future release.
	 * @param differentAction
	 *            true if action should be changed.
	 * @return next state.
	 */
	public ActionState doAction(PaintContext context,
			Vector2d currentPoint, boolean differentAction);

	/**
	 * Performs undo of this state and returns the previous state.
	 *
	 * @return previous state.
	 */
	public ActionState undo(PaintContext context);

	public ActionState getNextState();

	public ActionState getPreviousState();

}
