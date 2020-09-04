package oripa.appstate;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import oripa.domain.paint.EditMode;

public class InputCommandStatePopper implements ActionListener {
	private final StateManager stateManager;

	/**
	 * Constructor
	 */
	public InputCommandStatePopper(final StateManager stateManager) {
		this.stateManager = stateManager;
	}

	public ApplicationState<EditMode> pop() {
		return stateManager.popLastInputCommand();
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		ApplicationState<EditMode> current = pop();

		if (current == null) {
			return;
		}

		current.performActions(e);
	}
}
