package oripa.domain.paint.bisector;

import oripa.domain.cptool.Painter;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingLine;

public class SelectingLineForBisector extends PickingLine {

	@Override
	protected void initialize() {
		setPreviousClass(SelectingVertexForBisector.class);
		setNextClass(SelectingVertexForBisector.class);

	}

	@Override
	protected void undoAction(final PaintContext context) {
		context.popVertex();

	}

	@Override
	protected void onResult(final PaintContext context, final boolean doSpecial) {
		if (context.getLineCount() != 1 ||
				context.getVertexCount() != 3) {
			throw new IllegalStateException("wrong state: impossible line and vertex selection.");
		}

		var first = context.getVertex(0);
		var second = context.getVertex(1);
		var third = context.getVertex(2);

		var line = context.getLine(0);

		context.clear(false);

		context.creasePatternUndo().pushUndoInfo();

		Painter painter = context.getPainter();

		painter.addBisectorLine(
				first, second, third,
				line, context.getLineTypeOfNewLines());

	}

}
