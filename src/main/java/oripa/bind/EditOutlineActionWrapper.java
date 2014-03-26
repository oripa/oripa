package oripa.bind;

import java.awt.geom.AffineTransform;

import oripa.appstate.ApplicationState;
import oripa.appstate.StateManager;
import oripa.domain.paint.EditMode;
import oripa.domain.paint.GraphicMouseActionInterface;
import oripa.domain.paint.MouseActionHolder;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.outline.EditOutlineAction;
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
