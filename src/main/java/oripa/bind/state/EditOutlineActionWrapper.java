package oripa.bind.state;

import java.util.Optional;

import oripa.appstate.ApplicationState;
import oripa.appstate.StateManagerInterface;
import oripa.domain.paint.PaintContextInterface;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.creasepattern.EditOutlineAction;
import oripa.gui.presenter.creasepattern.GraphicMouseActionInterface;
import oripa.gui.presenter.creasepattern.MouseActionHolder;

public class EditOutlineActionWrapper extends EditOutlineAction {

	private final StateManagerInterface<EditMode> stateManager;
	private final MouseActionHolder actionHolder;

	public EditOutlineActionWrapper(final StateManagerInterface<EditMode> stateManager,
			final MouseActionHolder actionHolder) {
		this.stateManager = stateManager;
		this.actionHolder = actionHolder;
	}

	@Override
	public GraphicMouseActionInterface onLeftClick(
			final CreasePatternViewContext viewContext, final PaintContextInterface paintContext,
			final boolean differentAction) {

		GraphicMouseActionInterface next = super.onLeftClick(viewContext, paintContext,
				differentAction);

		if (paintContext.isMissionCompleted()) {
			popPreviousState();
			next = actionHolder.getMouseAction();
		}

		return next;
	}

	@Override
	public void onRightClick(final CreasePatternViewContext viewContext, final PaintContextInterface paintContext,
			final boolean differentAction) {

		popPreviousState();
	}

	private void popPreviousState() {
		Optional<ApplicationState<EditMode>> prevOpt = stateManager.pop();

		prevOpt.ifPresent(prev -> prev.performActions(null));
	}

}
