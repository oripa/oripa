package oripa.domain.paint;

/**
 * An interface for (modified) State pattern with undo. Implementation of this
 * interface should determine the state to be used after every action/undo.
 *
 * @author OUCHI Koji
 */
public interface ActionState {
    /**
     * Performs the action of this state and returns the next state. This method
     * can return {@code this} object if the next action is the same as current
     * state.
     *
     * @param context
     *            storage for user interaction
     * @param differentAction
     *            true if action should be changed.
     * @return next state.
     */
    ActionState doAction(PaintContext context, boolean differentAction);

    /**
     * Performs undo of this state and returns the previous state.
     *
     * @return previous state.
     */
    ActionState undo(PaintContext context);
}
