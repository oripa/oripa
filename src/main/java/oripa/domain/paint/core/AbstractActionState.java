package oripa.domain.paint.core;

import java.awt.geom.Point2D;

import oripa.domain.paint.PaintContextInterface;

/**
 * a frame work of State pattern with undo, which can get back to previous
 * state.
 *
 * Call doAction() to perform the action of the state. The flow of processing
 * is: doAction(): onAct() -> quit proccessing if onAct() returns false ->
 * onResult() -> finish!; unDo(): undoAction() -> finish! both method returns
 * ActionState to be used next time.
 *
 * @author koji
 *
 */
public abstract class AbstractActionState implements ActionState {
	private Class<? extends ActionState> next, prev;

	public AbstractActionState() {
		initialize();
	}

	/**
	 * Set next state class and previous state class here. If you do not
	 * anything, {@link #getNextState()} and {@link #getPreviousState()} will
	 * return {@code this} object.
	 */
	protected abstract void initialize();

	protected void setNextClass(final Class<? extends ActionState> next) {
		this.next = next;
	}

	protected void setPreviousClass(final Class<? extends ActionState> prev) {
		this.prev = prev;
	}

	/**
	 * first this method calls onAct(), then calls onResult() if onAct() returns
	 * true.
	 *
	 * @return A new instance of next state. if class of next state is not set
	 *         (or is null), returns {@value this}.
	 */
	@Override
	public final ActionState doAction(final PaintContextInterface context,
			final Point2D.Double currentPoint, final boolean freeSelection) {

		boolean success = onAct(context, currentPoint, freeSelection);

		if (!success) {
			return this;
		}

		onResult(context);

		ActionState nextState = getNextState();

		return nextState;
	}

	/**
	 * defines what to do after onAct() succeeded.
	 *
	 * @param context
	 */
	protected abstract void onResult(PaintContextInterface context);

	/**
	 * defines the job of this class.
	 *
	 * @param context
	 *            information relating mouse action.
	 * @param currentPoint
	 *            current point of mouse cursor.
	 * @param doSpecial
	 *            true if you want switch the action.
	 * @return true if the action succeeded, otherwise false.
	 */
	protected abstract boolean onAct(PaintContextInterface context,
			Point2D.Double currentPoint, boolean doSpecial);

	/**
	 * cancel the current actions and returns previous state.
	 *
	 * @return Previous state
	 */
	@Override
	public final ActionState undo(final PaintContextInterface context) {

		undoAction(context);

		ActionState prevState = getPreviousState();

		return prevState;
	}

	/**
	 * implement undo action. clean up the garbages! (and change previous state
	 * class if you need.)
	 *
	 * @param context
	 */
	protected abstract void undoAction(PaintContextInterface context);

	@Override
	public void setNextState(final ActionState state) {
		next = state.getClass();
	}

	@Override
	public void setPreviousState(final ActionState state) {
		prev = state.getClass();
	}

	@Override
	public ActionState getNextState() {
		return createInstance(next);
	}

	@Override
	public ActionState getPreviousState() {
		return createInstance(prev);
	}

	private ActionState createInstance(final Class<? extends ActionState> c) {
		ActionState state = null;

		if (c == null) {
			return this;
		}

		try {
			state = c.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return state;

	}

}
