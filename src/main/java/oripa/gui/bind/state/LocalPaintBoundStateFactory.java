package oripa.gui.bind.state;

import java.awt.event.ActionListener;
import java.util.function.Supplier;

import oripa.appstate.ApplicationState;
import oripa.appstate.StateManager;
import oripa.gui.bind.state.action.PaintActionSetter;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.viewsetting.ChangeViewSetting;

/**
 * Helper class
 *
 * @author Koji
 *
 */
class LocalPaintBoundStateFactory {

	private final StateManager<EditMode> stateManager;
	private final ActionListener[] basicActions;

	/**
	 *
	 * @param parent
	 *            A parent component. This object can be used as the parent of
	 *            an error dialog by {@link ErrorListener}.
	 * @param stateManager
	 * @param basicActions
	 *            Actions for all created states.
	 */
	public LocalPaintBoundStateFactory(
			final StateManager<EditMode> stateManager,
			final ActionListener[] basicActions) {
		this.stateManager = stateManager;
		this.basicActions = basicActions;
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
	 * @param changeHint
	 *            event handler to change a hint of painting.
	 * @param actions
	 *            Additional actions.
	 * @return
	 */
	public ApplicationState<EditMode> create(
			final EditMode editMode,
			final PaintActionSetter actionSetter,
			final Supplier<Boolean> errorDetecter,
			final Runnable errorHandler,
			final ChangeViewSetting changeHint,
			final ActionListener[] actions) {

		PaintBoundState state = new PaintBoundState(
				stateManager, errorDetecter, errorHandler,
				editMode, actionSetter, changeHint, basicActions);

		state.addActions(actions);
		// state.setErrorListeners(errorDetecter, errorHandler);

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
	 * @param changeHint
	 *            event handler to change a hint of painting.
	 * @param actions
	 *            Additional actions.
	 * @return
	 */
	public ApplicationState<EditMode> create(
			final EditMode editMode,
			final PaintActionSetter actionSetter,
			final ChangeViewSetting changeHint,
			final ActionListener[] actions) {

		ApplicationState<EditMode> state = new PaintBoundState(
				stateManager, editMode, actionSetter, changeHint,
				basicActions);

		state.addActions(actions);

		return state;

	}

}
