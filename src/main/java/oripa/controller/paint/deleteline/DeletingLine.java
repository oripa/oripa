package oripa.controller.paint.deleteline;

import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.core.PickingLine;
import oripa.domain.cptool.Painter;

public class DeletingLine extends PickingLine {

	public DeletingLine() {
		super();
	}

	@Override
	protected void initialize() {
	}

	@Override
	protected void onResult(final PaintContextInterface context) {

		if (context.getLineCount() > 0) {
			context.creasePatternUndo().pushUndoInfo();

			Painter painter = context.getPainter();
			painter.removeLine(context.popLine());
		}

		context.clear(false);
	}

}
