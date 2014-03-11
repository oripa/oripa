package oripa.bind;

import java.awt.geom.AffineTransform;

import oripa.appstate.ApplicationState;
import oripa.appstate.StateManager;
import oripa.controller.paint.EditMode;
import oripa.controller.paint.GraphicMouseActionInterface;
import oripa.controller.paint.MouseActionHolder;
import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.outline.EditOutlineAction;
import oripa.viewsetting.main.ScreenUpdater;

public class EditOutlineActionWrapper extends EditOutlineAction {

	private final MouseActionHolder actionHolder = MouseActionHolder
			.getInstance();

	@Override
	public GraphicMouseActionInterface onLeftClick(
			final PaintContextInterface context,
			final boolean differentAction,
			final ScreenUpdater screenUpdater) {
		GraphicMouseActionInterface next = super.onLeftClick(context,
				differentAction, screenUpdater);

		if (context.isMissionCompleted()) {
			popPreviousState();
			next = actionHolder.getMouseAction();
		}

		return next;
	}

	@Override
	public void onRightClick(final PaintContextInterface context,
			final AffineTransform affine,
			final boolean differentAction) {

		popPreviousState();
	}

	private void popPreviousState() {
		StateManager stateManager = StateManager.getInstance();
		ApplicationState<EditMode> prev = stateManager.pop();

		prev.performActions(null);

	}

}
