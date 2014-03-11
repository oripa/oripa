package oripa.controller.paint.linetype;

import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.core.PickingLine;
import oripa.domain.cptool.Painter;
import oripa.viewsetting.main.uipanel.UIPanelSettingDB;

public class SelectingLineForLineType extends PickingLine {

	public SelectingLineForLineType() {
		super();
	}

	@Override
	protected void initialize() {
	}

	@Override
	protected void undoAction(final PaintContextInterface context) {
		super.undoAction(context);
	}

	@Override
	protected void onResult(final PaintContextInterface context) {

		context.creasePatternUndo().pushUndoInfo();

		UIPanelSettingDB setting = UIPanelSettingDB.getInstance();
		Painter painter = context.getPainter();
		painter.alterLineType(
				context.peekLine(), setting.getTypeFrom(), setting.getTypeTo());

		context.clear(false);
	}

}
