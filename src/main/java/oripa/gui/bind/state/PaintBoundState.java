package oripa.gui.bind.state;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Supplier;

import oripa.appstate.ApplicationState;
import oripa.appstate.StateManager;
import oripa.appstate.StatePusher;
import oripa.gui.bind.state.action.PaintActionSetter;
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
	private Runnable errorHandler;
	private Supplier<Boolean> errorDetecter;

	/**
	 * set paint action and hint updater without error handler.
	 *
	 * @param mouseAction
	 *            paint action
	 * @param changeHint
	 *            event handler for hint.
	 * @param actions
	 *            additional actions. These will be performed before state
	 *            changes.
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
	 * @param stateManager
	 *            state manager.
	 * @param errorDetecter
	 *            Detects error. returns true if an error occurs.
	 * @param errorHandler
	 *            a callback for error handling.
	 * @param changeHint
	 *            event handler for hint.
	 * @param actions
	 *            additional actions. These will be performed before state
	 *            changes.
	 */
	public PaintBoundState(
			final StateManager<EditMode> stateManager,
			final Supplier<Boolean> errorDetecter,
			final Runnable errorHandler,
			final EditMode editMode,
			final PaintActionSetter actionSetter,
			final ChangeViewSetting changeHint,
			final ActionListener[] actions) {

		super(editMode, actions);

		addBasicListeners(stateManager, actionSetter, changeHint);

		this.errorHandler = errorHandler;
		this.errorDetecter = errorDetecter;
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

	public void setErrorListeners(final Supplier<Boolean> detecter, final Runnable handler) {
		errorDetecter = detecter;
		errorHandler = handler;
	}

	/**
	 * This method first detects error by {@code errorDetecter}. Then
	 * {@code errorHandler.run()} is called if an error occurs. If no error
	 * occurs or {@code errorDetecter} is not given, it sets given paint action
	 * to a current paint mode.
	 */
	@Override
	public void performActions(final ActionEvent e) {
		if (errorDetecter != null) {
			if (errorDetecter.get()) {
				errorHandler.run();
				return;
			}
		}

		super.performActions(e);
	}
}
