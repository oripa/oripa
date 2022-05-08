package oripa.appstate;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * A template for grouped state.
 *
 * @author koji
 *
 * @param <GroupEnum>
 *            Enum of group identifier
 */
// TODO Use Runnable instead of ActionListener.
public class ApplicationState<GroupEnum> implements GroupMember<GroupEnum> {
	private final GroupEnum group;

	private final ArrayList<ActionListener> actions = new ArrayList<ActionListener>();

	/**
	 * A constructor which binds a group and actions to this state.
	 *
	 * @param group
	 *            group identifier
	 * @param actions
	 *            actions to be performed on this state.
	 */
	public ApplicationState(final GroupEnum group, final ActionListener... actions) {
		this.group = group;
		addActions(actions);
	}

	public void addAction(final ActionListener action) {
		this.actions.add(action);
	}

	public void addActions(final ActionListener[] actions) {
		if (actions == null) {
			return;
		}

		List.of(actions).forEach(this::addAction);
	}

	/**
	 * performs actions of this state.
	 *
	 * @param e
	 */
	// TODO make this parameterless.
	public void performActions(final ActionEvent e) {
		if (actions == null) {
			return;
		}

		actions.forEach(action -> action.actionPerformed(e));
	}

	@Override
	public GroupEnum getGroup() {
		return group;
	}

}
