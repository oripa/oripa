package oripa.appstate;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An action listener which pushes given state into a state manager.
 *
 * @author OUCHI Koji
 *
 */
public class StatePusher<GroupEnum> implements ActionListener {
	private static final Logger logger = LoggerFactory.getLogger(StatePusher.class);

	private final StateManager<GroupEnum> stateManager;
	private final ApplicationState<GroupEnum> state;

	public StatePusher(final ApplicationState<GroupEnum> s, final StateManager<GroupEnum> stateManager) {
		state = s;
		this.stateManager = stateManager;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		logger.debug("push paint bound state: " + state);
		stateManager.push(state);
	}
}