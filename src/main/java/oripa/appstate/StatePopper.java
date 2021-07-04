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

	@Override
	public void actionPerformed(final ActionEvent e) {
		ApplicationState<EditMode> current = stateManager.pop();

		if (current == null) {
			return;
		}

		current.performActions(e);
	}
}
