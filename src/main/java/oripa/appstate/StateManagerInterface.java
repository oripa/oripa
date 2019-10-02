package oripa.appstate;

public interface StateManagerInterface<GroupEnum> {

	ApplicationState<GroupEnum> getCurrent();
	void push(ApplicationState<GroupEnum> s);
	ApplicationState<GroupEnum> pop();
	ApplicationState<GroupEnum> popLastOf(GroupEnum group);
}
