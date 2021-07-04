package oripa.appstate;

import java.util.HashMap;
import java.util.Map;

import oripa.domain.paint.EditMode;

/**
 * A simple implementation of {@link StateManagerInterface}. This object ignores
 * {@link EditMode#COPY} and {@link EditMode#CUT} to handle line selection and
 * pasting correctly.
 *
 * @author OUCHI Koji
 *
 */
public class StateManager implements StateManagerInterface<EditMode> {

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
	public ApplicationState<EditMode> pop() {
		if (current == previous) {
			return null;
		}

		current = previous;
		return current;
	}

	@Override
	public ApplicationState<EditMode> popLastOf(final EditMode group) {
		if (!lastCommands.containsKey(group)) {
			return null;
		}

		var lastCommand = lastCommands.get(group);
		if (current == lastCommand) {
			return null;
		}

		previous = current;
		current = lastCommand;

		return current;
	}
}
