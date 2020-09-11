package oripa.bind.state;

import java.awt.geom.AffineTransform;

import oripa.appstate.ApplicationState;
import oripa.appstate.StateManager;
import oripa.domain.paint.EditMode;
import oripa.domain.paint.GraphicMouseActionInterface;
import oripa.domain.paint.MouseActionHolder;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.outline.EditOutlineAction;

public class EditOutlineActionWrapper extends EditOutlineAction {

	private final StateManager stateManager;
	private final MouseActionHolder actionHolder;

	public EditOutlineActionWrapper(final StateManager stateManager,
			final MouseActionHolder actionHolder) {
		this.stateManager = stateManager;
		this.actionHolder = actionHolder;
	}

	@Override
	public GraphicMouseActionInterface onLeftClick(
			final PaintContextInterface context,
			final boolean differentAction) {
		GraphicMouseActionInterface next = super.onLeftClick(context,
				differentAction);

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
		ApplicationState<EditMode> prev = stateManager.pop();

		prev.performActions(null);

	}

}
