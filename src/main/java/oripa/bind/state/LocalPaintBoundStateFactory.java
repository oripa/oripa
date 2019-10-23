package oripa.bind.state;

import java.awt.Component;
import java.awt.event.ActionListener;

import oripa.appstate.ApplicationState;
import oripa.domain.paint.EditMode;
import oripa.domain.paint.GraphicMouseActionInterface;
import oripa.domain.paint.PaintContextInterface;

/**
 * Helper class
 *
 * @author Koji
 *
 */
class LocalPaintBoundStateFactory {

	private final ActionListener[] basicActions;
	private Component parent = null;

	/**
	 *
	 * @param parent
	 *            A parent component. {@code null} indicates to avoid error on
	 *            performActions() of created state.
	 * @param basicActions
	 *            Actions for all created states.
	 */
	public LocalPaintBoundStateFactory(final Component parent,
			final ActionListener[] basicActions) {
		this.basicActions = basicActions;
		this.parent = parent;
	}

	/**
	 * Create a state with error handler.
	 *
	 * @param mouseAction
	 *            Action for painting
	 * @param errorListener
	 *            For managing error on {@code performActions()} of created
	 *            state.
	 * @param context
	 *            context of painting.
	 * @param textID
	 *            ID for hint of painting.
	 * @param actions
	 *            Additional actions.
	 * @return
	 */
	public ApplicationState<EditMode> create(
			final GraphicMouseActionInterface mouseAction,
			final ErrorListener errorListener,
			final PaintContextInterface context,
			final String textID,
			final ActionListener[] actions) {

		PaintBoundState state = new PaintBoundState(
				parent, errorListener, mouseAction, context, textID, basicActions);

		state.addActions(actions);
		state.setErrorListener(errorListener);

		return state;
	}

	/**
	 *
	 * Create a state.
	 *
	 * @param mouseAction
	 *            Action for painting
	 * @param context
	 *            Context of painting.
	 * @param textID
	 *            ID for hint of painting.
	 * @param actions
	 *            Additional actions.
	 * @return
	 */
	public ApplicationState<EditMode> create(
			final GraphicMouseActionInterface mouseAction,
			final PaintContextInterface context,
			final String textID,
			final ActionListener[] actions) {

		ApplicationState<EditMode> state = new PaintBoundState(
				mouseAction, context, textID, basicActions);

		state.addActions(actions);

		return state;

	}

}
