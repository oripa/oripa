package oripa.appstate;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.paint.EditMode;

public class InputCommandStatePopper implements ActionListener {
	private static final Logger logger = LoggerFactory.getLogger(InputCommandStatePopper.class);

	private final StateManagerInterface<EditMode> stateManager;

	/**
	 * Constructor
	 */
	public InputCommandStatePopper(final StateManagerInterface<EditMode> stateManager) {
		this.stateManager = stateManager;
	}

	public ApplicationState<EditMode> pop() {
		return stateManager.popLastOf(EditMode.INPUT);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		ApplicationState<EditMode> current = pop();

		logger.debug("pop input command state: " + current);

		if (current == null) {
			return;
		}

		current.performActions(e);
	}
}
