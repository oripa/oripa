package oripa.gui.bind.state;

import java.util.Optional;

import oripa.appstate.ApplicationState;
import oripa.appstate.StateManager;
import oripa.domain.paint.PaintContext;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.creasepattern.EditOutlineAction;
import oripa.gui.presenter.creasepattern.GraphicMouseAction;
import oripa.gui.presenter.creasepattern.MouseActionHolder;

public class EditOutlineActionWrapper extends EditOutlineAction {

	private final StateManager<EditMode> stateManager;
	private final MouseActionHolder actionHolder;

	public EditOutlineActionWrapper(final StateManager<EditMode> stateManager,
			final MouseActionHolder actionHolder) {
		this.stateManager = stateManager;
		this.actionHolder = actionHolder;
	}

	@Override
	public GraphicMouseAction onLeftClick(
			final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {
		int vertexCountBeforeAction = paintContext.getVertexCount();

		GraphicMouseAction next = super.onLeftClick(viewContext, paintContext,
				differentAction);

		int vertexCountAfterAction = paintContext.getVertexCount();

		// Action is performed and the selection is cleared.
		// It's the time to get back to previous graphic action.
		if (isActionPerformed(vertexCountBeforeAction, vertexCountAfterAction)) {
			popPreviousState();
			next = actionHolder.getMouseAction();
		}

		return next;
	}

	private boolean isActionPerformed(final int countBeforeAction, final int countAfterAction) {
		return countBeforeAction > 0 && countAfterAction == 0;
	}

	@Override
	public void onRightClick(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {

		popPreviousState();
	}

	private void popPreviousState() {
		Optional<ApplicationState<EditMode>> prevOpt = stateManager.pop();

		prevOpt.ifPresent(prev -> prev.performActions(null));
	}

}
