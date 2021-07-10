package oripa.appstate;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.bind.state.PaintBoundState;
import oripa.gui.presenter.creasepattern.EditMode;

/**
 * An action listener which pushes given state into a state manager.
 *
 * @author OUCHI Koji
 *
 */
public class StatePusher implements ActionListener {
	private static final Logger logger = LoggerFactory.getLogger(StatePusher.class);

	private final StateManager<EditMode> stateManager;
	private final PaintBoundState state;

	public StatePusher(final PaintBoundState s, final StateManager<EditMode> stateManager) {
		state = s;
		this.stateManager = stateManager;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		logger.debug("push paint bound state: " + state);
		stateManager.push(state);
	}
}