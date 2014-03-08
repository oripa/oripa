package oripa.controller.paint.linetype;

import java.awt.Graphics2D;
import java.util.Collection;

import oripa.controller.paint.EditMode;
import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.core.RectangularSelectableAction;
import oripa.domain.cptool.Painter;
import oripa.value.OriLine;
import oripa.viewsetting.main.uipanel.UIPanelSettingDB;

public class ChangeLineTypeAction extends RectangularSelectableAction {

	public ChangeLineTypeAction() {
		setEditMode(EditMode.CHANGE_TYPE);
		setActionState(new SelectingLineForLineType());
	}

	@Override
	protected void afterRectangularSelection(final Collection<OriLine> selectedLines,
			final PaintContextInterface context) {

		if (selectedLines.isEmpty() == false) {
			context.getUndoer().pushUndoInfo();

			UIPanelSettingDB setting = UIPanelSettingDB.getInstance();
			for (OriLine l : selectedLines) {
				Painter painter = context.getPainter();
				// Change line type
				painter.alterLineType(
						l, setting.getTypeFrom(), setting.getTypeTo());
				// ORIPA.doc.alterLineType(l, setting.getLineTypeFromIndex(),
				// setting.getLineTypeToIndex());
			}

		}
	}

	@Override
	public void onDraw(final Graphics2D g2d, final PaintContextInterface context) {

		super.onDraw(g2d, context);

		drawPickCandidateLine(g2d, context);
	}

}
