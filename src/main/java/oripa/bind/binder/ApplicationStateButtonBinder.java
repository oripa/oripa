package oripa.bind.binder;

import java.awt.event.KeyListener;

import javax.swing.AbstractButton;

import oripa.appstate.ApplicationState;
import oripa.domain.paint.EditMode;
import oripa.resource.StringID;

/**
 * A class to bind a state which holds paint action.
 *
 * @author koji
 *
 */
public class ApplicationStateButtonBinder
		extends AbstractButtonBinder<ApplicationState<EditMode>> {

	/**
	 * This method binds an ActionListener to perform the actions of the state.
	 *
	 * @param buttonClass
	 * @param state
	 * @param textID
	 *            A member of {@link StringID} for label
	 * @param keyListener
	 *            For catching key actions which requires immediate
	 *            drawing(e.g., for catching Ctrl pressed) It's weird to
	 *            handling by button object but needed because the focus stays
	 *            on the selected button.
	 * @return
	 */
	@Override
	public AbstractButton createButton(
			final Class<? extends AbstractButton> buttonClass,
			final ApplicationState<EditMode> state, final String textID,
			final KeyListener keyListener) {

		AbstractButton button = createEmptyButton(buttonClass, textID);

		if (keyListener != null) {
			button.addKeyListener(keyListener);
		}

		/*
		 * add listeners
		 */
		button.addActionListener(state::performActions);

		return button;
	}

}
