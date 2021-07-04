package oripa.bind.state;

import java.awt.geom.AffineTransform;
import java.util.Optional;

import oripa.appstate.ApplicationState;
import oripa.appstate.StateManagerInterface;
import oripa.domain.cptool.Painter;
import oripa.domain.paint.EditMode;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.copypaste.CopyAndPasteAction;
import oripa.domain.paint.copypaste.SelectionOriginHolder;

public class CopyAndPasteActionWrapper extends CopyAndPasteAction {

	private final StateManagerInterface<EditMode> stateManager;
	private final boolean isCut;

	public CopyAndPasteActionWrapper(
			final StateManagerInterface<EditMode> stateManager,
			final boolean isCut, final SelectionOriginHolder originHolder) {

		super(originHolder);

		this.stateManager = stateManager;

		this.isCut = isCut;
		if (isCut) {
			super.setEditMode(EditMode.CUT);
		}
	}

	@Override
	protected void recoverImpl(final PaintContextInterface context) {
		super.recoverImpl(context);
		if (isCut) {
			context.creasePatternUndo().pushUndoInfo();
			Painter painter = context.getPainter();
			painter.removeSelectedLines();
		}
	}

	@Override
	public void onRightClick(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {

		Optional<ApplicationState<EditMode>> prevOpt = stateManager.pop();

		// a case having switched copy to cut.
		prevOpt.ifPresent(prev -> prev.performActions(null));
	}

}
