package oripa.appstate;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

/**
 * An action listener which pops the last state from given state manager.
 *
 * @author OUCHI Koji
 *
 */
public class StatePopper<GroupEnum> implements ActionListener {
	private final StateManager<GroupEnum> stateManager;

	/**
	 * Constructor
	 */
	public StatePopper(final StateManager<GroupEnum> stateManager) {
		this.stateManager = stateManager;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		Optional<ApplicationState<GroupEnum>> currentOpt = stateManager.pop();

		currentOpt.ifPresent(current -> current.performActions());
	}
}
