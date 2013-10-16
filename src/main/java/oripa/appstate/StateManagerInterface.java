package oripa.appstate;

public interface StateManagerInterface<GroupEnum> {

	public ApplicationState<GroupEnum> getCurrent();
	public void push(ApplicationState<GroupEnum> s);
	public ApplicationState<GroupEnum> pop();
	public ApplicationState<GroupEnum> popLastOf(GroupEnum group);
}
