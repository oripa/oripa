package oripa.domain.paint.deleteline;

import oripa.domain.cptool.Painter;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.PickingLine;

public class DeletingLine extends PickingLine {

	public DeletingLine() {
		super();
	}

	@Override
	protected void initialize() {
	}

	@Override
	protected void onResult(final PaintContextInterface context, final boolean doSpecial) {

		if (context.getLineCount() > 0) {
			context.creasePatternUndo().pushUndoInfo();

			Painter painter = context.getPainter();
			painter.removeLine(context.popLine());
		}

		context.clear(true);
	}

}
