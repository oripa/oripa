package oripa.gui.bind.state;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import oripa.appstate.ApplicationState;
import oripa.appstate.StateManager;
import oripa.gui.presenter.creasepattern.EditMode;

/**
 * A simple implementation of {@link StateManager}. This object ignores
 * {@link EditMode#COPY} and {@link EditMode#CUT} to handle line selection and
 * pasting correctly.
 *
 * @author OUCHI Koji
 *
 */
public class EditModeStateManager implements StateManager<EditMode> {

	private ApplicationState<EditMode> current, previous;
	private final Map<EditMode, ApplicationState<EditMode>> lastCommands = new HashMap<>();

	@Override
	public ApplicationState<EditMode> getCurrent() {
		return current;
	}

	@Override
	public void push(final ApplicationState<EditMode> s) {
		lastCommands.put(s.getGroup(), s);

		if (current != null) {
			// pushing copy or cut causes empty pasting
			if (current.getGroup() != EditMode.COPY &&
					current.getGroup() != EditMode.CUT) {
				previous = current;
			}
		}
		current = s;
	}

	@Override
	public Optional<ApplicationState<EditMode>> pop() {
		if (current == previous) {
			return Optional.empty();
		}

		current = previous;
		return Optional.of(current);
	}

	@Override
	public Optional<ApplicationState<EditMode>> popLastOf(final EditMode group) {
		if (!lastCommands.containsKey(group)) {
			return Optional.empty();
		}

		var lastCommand = lastCommands.get(group);
		if (current == lastCommand) {
			return Optional.empty();
		}

		previous = current;
		current = lastCommand;

		return Optional.of(current);
	}
}
