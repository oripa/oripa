package oripa.appstate;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

import oripa.gui.presenter.creasepattern.EditMode;

/**
 * An action listener which pops the last state from given state manager.
 *
 * @author OUCHI Koji
 *
 */
public class StatePopper implements ActionListener {
	private final EditModeStateManager stateManager;

	/**
	 * Constructor
	 */
	public StatePopper(final EditModeStateManager stateManager) {
		this.stateManager = stateManager;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		Optional<ApplicationState<EditMode>> currentOpt = stateManager.pop();

		currentOpt.ifPresent(current -> current.performActions(e));
	}
}
