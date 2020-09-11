package oripa.appstate;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import oripa.domain.paint.EditMode;

public class StatePopper implements ActionListener {
	private final StateManager stateManager;

	/**
	 * Constructor
	 */
	public StatePopper(final StateManager stateManager) {
		this.stateManager = stateManager;
	}

	public ApplicationState<EditMode> pop() {
		return stateManager.pop();
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
