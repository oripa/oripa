package oripa.appstate;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

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
		Optional<ApplicationState<EditMode>> currentOpt = stateManager.pop();

		currentOpt.ifPresent(current -> current.performActions(e));
	}
}
