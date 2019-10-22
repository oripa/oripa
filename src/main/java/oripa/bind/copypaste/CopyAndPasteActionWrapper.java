package oripa.bind.copypaste;

import java.awt.geom.AffineTransform;

import oripa.appstate.ApplicationState;
import oripa.appstate.StateManager;
import oripa.domain.cptool.Painter;
import oripa.domain.paint.EditMode;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.copypaste.CopyAndPasteAction;

public class CopyAndPasteActionWrapper extends CopyAndPasteAction {

	private final boolean isCut;

	public CopyAndPasteActionWrapper(final boolean isCut) {
		super();
		this.isCut = isCut;
		if (isCut) {
			super.setEditMode(EditMode.CUT);
		}
	}

	@Override
	protected void recoverImpl(final PaintContextInterface context) {
		super.recoverImpl(context);
		if (isCut) {
			Painter painter = context.getPainter();
			painter.removeSelectedLines();
		}
	}

	@Override
	public void onRightClick(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {

		StateManager stateManager = StateManager.getInstance();
		ApplicationState<EditMode> prev = stateManager.pop();

		if (prev == null) {
			return;
		}

		// a case having switched copy to cut.
		prev.performActions(null);
	}

}
