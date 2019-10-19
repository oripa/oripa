package oripa.domain.paint.linetype;

import oripa.domain.cptool.Painter;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.PickingLine;
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
	protected void onResult(final PaintContextInterface context, final boolean doSpecial) {

		context.creasePatternUndo().pushUndoInfo();

		UIPanelSettingDB setting = UIPanelSettingDB.getInstance();
		Painter painter = context.getPainter();
		painter.alterLineType(
				context.peekLine(), setting.getTypeFrom(), setting.getTypeTo());

		context.clear(false);
	}

}
