package oripa.bind.state;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import oripa.appstate.ApplicationState;
import oripa.appstate.StatePusher;
import oripa.bind.state.action.PaintActionSetter;
import oripa.controller.paint.EditMode;
import oripa.controller.paint.GraphicMouseActionInterface;
import oripa.controller.paint.MouseActionHolder;
import oripa.viewsetting.ViewChangeListener;
import oripa.viewsetting.main.ChangeHint;

/**
 * A state when user is painting. This class performs: - state management
 * actions - switching view - switching painting action
 * 
 * @author koji
 * 
 */
public class PaintBoundState extends ApplicationState<EditMode> {
	private Component parent;
	private ErrorListener errorListener;
	private final MouseActionHolder actionHolder = MouseActionHolder
			.getInstance();

	/**
	 * set paint action and hint updater without error handler.
	 * 
	 * @param mouseAction
	 *            paint action
	 * @param textID
	 *            ID for hint.
	 * @param actions
	 *            additional actions.
	 */
	public PaintBoundState(GraphicMouseActionInterface mouseAction,
			String textID,
			ActionListener[] actions) {
		super(mouseAction.getEditMode(), actions);

		addBasicListeners(mouseAction, textID);
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
	 * @param textID
	 *            ID for hint.
	 * @param actions
	 *            additional actions.
	 */
	public PaintBoundState(Component parent,
			ErrorListener el,
			GraphicMouseActionInterface mouseAction, String textID,
			ActionListener[] actions) {

		super(mouseAction.getEditMode(), actions);

		addBasicListeners(mouseAction, textID);

		// set a listener to handle an error on performActions().
		this.parent = parent;
		setErrorListener(el);
	}

	private void addBasicListeners(GraphicMouseActionInterface mouseAction,
			String textID) {

		// add a listener to push this state to the history stack.
		addAction(new StatePusher(this));

		// add a listener to change paint action.
		addAction(new PaintActionSetter(actionHolder, mouseAction));

		if (textID != null) {
			// add view updater
			addAction(new ViewChangeListener(new ChangeHint(textID)));
		}

	}

	public void setErrorListener(ErrorListener el) {
		errorListener = el;
	}

	/**
	 * This method first detects error by {@code ErrorListener.isError()}. Then
	 * {@code ErrorListener.onError()} is called if an error occurs. If no error
	 * occurs or ErrorListener is not given, it sets given paint action to a
	 * current paint mode.
	 */
	@Override
	public void performActions(ActionEvent e) {
		if (errorListener != null) {
			if (errorListener.isError(e)) {
				errorListener.onError(parent, e);
				return;
			}
		}

		super.performActions(e);

	}
}
