package oripa.domain.paint.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.paint.ActionState;
import oripa.domain.paint.PaintContext;
import oripa.vecmath.Vector2d;

/**
 * a framework of (modified) State pattern with undo, which can get back to
 * previous state.
 *
 * Call doAction() to perform the action of the state. The flow of processing
 * is:
 * <ul>
 * <li>doAction(): onAct() -> quit processing if onAct() returns false ->
 * onResult() -> finish!;</li>
 * <li>undo(): undoAction() -> finish!</li>
 * </ul>
 * both methods return ActionState to be used next time.
 *
 * @author OUCHI Koji
 *
 */
public abstract class AbstractActionState implements ActionState {
	private static Logger logger = LoggerFactory.getLogger(AbstractActionState.class);

	private Class<? extends ActionState> next, prev;

	public AbstractActionState() {
		initialize();
	}

	/**
	 * Set next state class and previous state class here. If you do nothing,
	 * {@link #getNextState()} and {@link #getPreviousState()} will return
	 * {@code this} object.
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
	 *         (or is null), returns {@code this}.
	 */
	@Override
	public final ActionState doAction(final PaintContext context,
			final Vector2d currentPoint, final boolean doSpecial) {

		boolean success = onAct(context, currentPoint, doSpecial);

		if (!success) {
			return this;
		}

		onResult(context, doSpecial);

		return getNextState();
	}

	/**
	 * defines what to do after onAct() succeeded.
	 *
	 * @param context
	 *            storage for user interaction
	 * @param doSpecial
	 *            true if action should be changed.
	 */
	protected abstract void onResult(PaintContext context, final boolean doSpecial);

	/**
	 * defines the job of this class.
	 *
	 * @param context
	 *            storage for user interaction
	 * @param currentPoint
	 *            Deprecated. This will be deleted in the future release.
	 * @param doSpecial
	 *            true if action should be changed.
	 * @return true if the action succeeded and should return the next state,
	 *         otherwise false.
	 */
	protected abstract boolean onAct(PaintContext context,
			Vector2d currentPoint, boolean doSpecial);

	/**
	 * cancel the current actions and returns previous state.
	 *
	 * @param context
	 *            storage for user interaction
	 * @return Previous state
	 */
	@Override
	public final ActionState undo(final PaintContext context) {

		undoAction(context);

		return getPreviousState();
	}

	/**
	 * implement undo action. clean up the garbages!
	 *
	 * @param context
	 *            storage for user interaction
	 */
	protected abstract void undoAction(PaintContext context);

	private ActionState getNextState() {
		return createInstance(next);
	}

	private ActionState getPreviousState() {
		return createInstance(prev);
	}

	private ActionState createInstance(final Class<? extends ActionState> c) {
		ActionState state = null;

		if (c == null) {
			return this;
		}

		try {
			state = c.getConstructor().newInstance();
		} catch (Exception e) {
			logger.error("failed to create next/previous state", e);
			throw new RuntimeException(e);
		}

		return state;
	}

}
