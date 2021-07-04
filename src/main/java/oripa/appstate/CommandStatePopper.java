package oripa.appstate;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.paint.EditMode;

public class CommandStatePopper implements ActionListener {
	private static final Logger logger = LoggerFactory.getLogger(CommandStatePopper.class);

	private final StateManagerInterface<EditMode> stateManager;
	private final EditMode editMode;

	/**
	 * Constructor
	 */
	public CommandStatePopper(final StateManagerInterface<EditMode> stateManager, final EditMode editMode) {
		this.stateManager = stateManager;
		this.editMode = editMode;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		Optional<ApplicationState<EditMode>> currentOpt = stateManager.popLastOf(editMode);

		currentOpt.ifPresent(current -> {
			logger.debug("pop {} command state: {}", editMode, current);
			current.performActions(e);
		});
	}
}
