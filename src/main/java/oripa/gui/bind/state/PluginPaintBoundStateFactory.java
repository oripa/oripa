package oripa.gui.bind.state;

import oripa.appstate.ApplicationState;
import oripa.appstate.StateManager;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.creasepattern.GraphicMouseAction;
import oripa.gui.presenter.creasepattern.MouseActionSetterFactory;
import oripa.gui.viewchange.ChangeViewSetting;

public class PluginPaintBoundStateFactory {

	private final StateManager<EditMode> stateManager;
	private final MouseActionSetterFactory setterFactory;

	public PluginPaintBoundStateFactory(
			final StateManager<EditMode> stateManager,
			final MouseActionSetterFactory setterFactory) {
		this.stateManager = stateManager;
		this.setterFactory = setterFactory;
	}

	public ApplicationState<EditMode> create(
			final GraphicMouseAction mouseAction,
			final ChangeViewSetting changeHint,
			final ChangeViewSetting changeOnSelected) {

		ApplicationState<EditMode> state = new PaintBoundState(
				stateManager, mouseAction.getEditMode(), setterFactory.create(mouseAction), changeHint,
				null);

		state.addActions(new Runnable[] { changeOnSelected::changeViewSetting });

		return state;

	}

}
