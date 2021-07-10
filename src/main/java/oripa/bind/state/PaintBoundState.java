package oripa.bind.state;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import oripa.appstate.ApplicationState;
import oripa.appstate.StateManager;
import oripa.appstate.StatePusher;
import oripa.bind.state.action.PaintActionSetter;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.viewsetting.ChangeViewSetting;

/**
 * A state when user is painting. This class performs:
 * <ul>
 * <li>state management actions</li>
 * <li>switching view</li>
 * <li>switching painting action</li>
 * </ul>
 *
 * @author koji
 *
 */
public class PaintBoundState extends ApplicationState<EditMode> {
	private Component parent;
	private ErrorListener errorListener;

	/**
	 * set paint action and hint updater without error handler.
	 *
	 * @param mouseAction
	 *            paint action
	 * @param changeHint
	 *            event handler for hint.
	 * @param actions
	 *            additional actions.
	 */
	public PaintBoundState(
			final StateManager<EditMode> stateManager,
			final EditMode editMode,
			final PaintActionSetter actionSetter,
			final ChangeViewSetting changeHint,
			final ActionListener[] actions) {
		super(editMode, actions);

		addBasicListeners(stateManager, actionSetter, changeHint);
	}

	/**
	 * set paint action and hint updater.
	 *
	 * @param parent
	 *            a parent component
	 * @param el
	 *            for managing error on {@code performActions()}.
	 * @param mouseAction
	 *            paint action
	 * @param changeHint
	 *            event handler for hint.
	 * @param actions
	 *            additional actions.
	 */
	public PaintBoundState(
			final Component parent,
			final StateManager<EditMode> stateManager,
			final ErrorListener el,
			final EditMode editMode,
			final PaintActionSetter actionSetter,
			final ChangeViewSetting changeHint,
			final ActionListener[] actions) {

		super(editMode, actions);

		addBasicListeners(stateManager, actionSetter, changeHint);

		// set a listener to handle an error on performActions().
		this.parent = parent;
		setErrorListener(el);
	}

	private void addBasicListeners(
			final StateManager<EditMode> stateManager,
			final PaintActionSetter actionSetter,
			final ChangeViewSetting changeHint) {

		// add a listener to push this state to the history stack.
		addAction(new StatePusher<EditMode>(this, stateManager));

		// add a listener to change paint action.
		addAction(actionSetter);

		if (changeHint != null) {
			// add view updater
			addAction(e -> changeHint.changeViewSetting());
		}

	}

	public void setErrorListener(final ErrorListener el) {
		errorListener = el;
	}

	/**
	 * This method first detects error by {@code ErrorListener.isError()}. Then
	 * {@code ErrorListener.onError()} is called if an error occurs. If no error
	 * occurs or ErrorListener is not given, it sets given paint action to a
	 * current paint mode.
	 */
	@Override
	public void performActions(final ActionEvent e) {
		if (errorListener != null) {
			if (errorListener.isError(e)) {
				errorListener.onError(parent, e);
				return;
			}
		}

		super.performActions(e);
	}
}
