package oripa.appstate;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An action listener which pops the last command's state of given edit mode.
 *
 * @author OUCHI Koji
 *
 */
public class CommandStatePopper<GroupEnum> implements ActionListener {
	private static final Logger logger = LoggerFactory.getLogger(CommandStatePopper.class);

	private final StateManagerInterface<GroupEnum> stateManager;
	private final GroupEnum editMode;

	/**
	 * Constructor
	 */
	public CommandStatePopper(final StateManagerInterface<GroupEnum> stateManager, final GroupEnum editMode) {
		this.stateManager = stateManager;
		this.editMode = editMode;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		Optional<ApplicationState<GroupEnum>> currentOpt = stateManager.popLastOf(editMode);

		currentOpt.ifPresent(current -> {
			logger.debug("pop {} command state: {}", editMode, current);
			current.performActions(e);
		});
	}
}
