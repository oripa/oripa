package oripa.bind.state;

import java.util.Optional;

import oripa.appstate.ApplicationState;
import oripa.appstate.StateManager;
import oripa.domain.cptool.Painter;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.copypaste.SelectionOriginHolder;
import oripa.gui.presenter.creasepattern.CopyAndPasteAction;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.presenter.creasepattern.EditMode;

public class CopyAndPasteActionWrapper extends CopyAndPasteAction {

	private final StateManager<EditMode> stateManager;
	private final boolean isCut;

	public CopyAndPasteActionWrapper(
			final StateManager<EditMode> stateManager,
			final boolean isCut, final SelectionOriginHolder originHolder) {

		super(originHolder);

		this.stateManager = stateManager;

		this.isCut = isCut;
		if (isCut) {
			super.setEditMode(EditMode.CUT);
		}
	}

	@Override
	protected void recoverImpl(final PaintContext context) {
		super.recoverImpl(context);
		if (isCut) {
			context.creasePatternUndo().pushUndoInfo();
			Painter painter = context.getPainter();
			painter.removeSelectedLines();
		}
	}

	@Override
	public void onRightClick(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {
		Optional<ApplicationState<EditMode>> prevOpt = stateManager.pop();

		prevOpt.ifPresent(prev -> prev.performActions(null));
	}

}
