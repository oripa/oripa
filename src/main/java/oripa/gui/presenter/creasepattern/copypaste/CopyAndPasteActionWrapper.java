package oripa.gui.presenter.creasepattern.copypaste;

import oripa.appstate.StateManager;
import oripa.appstate.StatePopper;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.copypaste.SelectionOriginHolder;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.presenter.creasepattern.EditMode;

public class CopyAndPasteActionWrapper extends CopyAndPasteAction {

	private final StateManager<EditMode> stateManager;

	public CopyAndPasteActionWrapper(
			final StateManager<EditMode> stateManager,
			final SelectionOriginHolder originHolder) {

		super(originHolder, new PasteAction(originHolder));

		this.stateManager = stateManager;
	}

	@Override
	protected void recoverImpl(final PaintContext context) {
		super.recoverImpl(context);
	}

	@Override
	public void onRightClick(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {
		new StatePopper<>(stateManager).run();
	}

}
