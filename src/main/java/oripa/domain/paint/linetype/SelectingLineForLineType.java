package oripa.domain.paint.linetype;

import oripa.domain.cptool.Painter;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.PickingLine;
import oripa.viewsetting.main.uipanel.UIPanelSetting;

public class SelectingLineForLineType extends PickingLine {

	private final UIPanelSetting setting;

	public SelectingLineForLineType(final UIPanelSetting setting) {
		super();
		this.setting = setting;
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

		Painter painter = context.getPainter();
		painter.alterLineType(
				context.peekLine(), setting.getTypeFrom(), setting.getTypeTo());

		context.clear(false);
	}

}
