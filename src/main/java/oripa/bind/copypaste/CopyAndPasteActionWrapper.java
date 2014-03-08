package oripa.bind.copypaste;

import java.awt.geom.AffineTransform;

import oripa.appstate.ApplicationState;
import oripa.appstate.StateManager;
import oripa.controller.paint.EditMode;
import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.copypaste.CopyAndPasteAction;
import oripa.domain.cptool.Painter;

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
	public void recover(final PaintContextInterface context) {
		super.recover(context);
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
