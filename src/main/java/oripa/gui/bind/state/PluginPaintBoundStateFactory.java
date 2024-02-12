package oripa.gui.bind.state;

import oripa.appstate.ApplicationState;
import oripa.appstate.StateManager;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.creasepattern.MouseActionSetterFactory;
import oripa.gui.presenter.plugin.GraphicMouseActionPlugin;

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
			final GraphicMouseActionPlugin plugin) {
		var mouseAction = plugin.getGraphicMouseAction();

		ApplicationState<EditMode> state = new PaintBoundState(
				stateManager, mouseAction.getEditMode(), setterFactory.create(mouseAction), plugin.getChangeHint(),
				new Runnable[] { plugin.getChangeOnSelected()::changeViewSetting });

		return state;

	}

}
