package oripa.appstate;

import oripa.domain.paint.EditMode;

/**
 * Lazy implementation. This class partially supports the responsibility of
 * {@link #popLastOf(EditMode)} method.
 *
 * @author OUCHI Koji
 *
 */
public class StateManager implements StateManagerInterface<EditMode> {

	private ApplicationState<EditMode> current, lastInputCommand, previous;

	@Override
	public ApplicationState<EditMode> getCurrent() {
		return current;
	}

	@Override
	public void push(final ApplicationState<EditMode> s) {

		if (s.getGroup() == EditMode.INPUT) {
			// keep for popLastInputCommand()
			lastInputCommand = s;
		}

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

	/**
	 * Currently this method accepts INPUT only. the current state will be
	 * dropped to previous state.
	 *
	 * @param group
	 *            ID.
	 * @return last state of the group. {@code null} if {@code group} is not
	 *         {@code oripa.domain.paint.EditMode.INPUT}.
	 */
	@Override
	public ApplicationState<EditMode> popLastOf(final EditMode group) {
		if (group != EditMode.INPUT) {
			return null;
		}

		return popLastInputCommand();
	}

	/**
	 * for the action of "input" radio button. the current state will be dropped
	 * to previous state.
	 *
	 * @return state of the last input command
	 */
	ApplicationState<EditMode> popLastInputCommand() {
		if (current == lastInputCommand) {
			return null;
		}
		previous = current;
		current = lastInputCommand;

		return current;
	}

}
