package oripa.appstate;

/**
 * Implementation of this interface should hold current state and (only one)
 * previous state to help getting back.
 *
 * @author koji
 *
 */
public interface StateManagerInterface<GroupEnum> {

	public ApplicationState<GroupEnum> getCurrent();

	/**
	 * Pushes {@code s} as a new state to be held. The current state will be
	 * dropped to previous state.
	 *
	 * @param s
	 *            new state
	 */
	public void push(ApplicationState<GroupEnum> s);

	/**
	 * Pops previous state. It will be set to current state.
	 *
	 * @return previous state. null if empty.
	 */
	public ApplicationState<GroupEnum> pop();

	/**
	 * Pops the last state of the given {@code group}. The current state will be
	 * dropped to previous state.
	 *
	 * @param group
	 *            ID.
	 * @return last state of the group. {@code null} if {@code group} does not
	 *         have such a state.
	 */
	public ApplicationState<GroupEnum> popLastOf(GroupEnum group);
}
