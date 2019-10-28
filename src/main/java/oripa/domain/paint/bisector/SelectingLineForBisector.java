package oripa.domain.paint.bisector;

import oripa.domain.cptool.Painter;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.PickingLine;

public class SelectingLineForBisector extends PickingLine {

	@Override
	protected void initialize() {
		setPreviousClass(SelectingVertexForBisector.class);
		setNextClass(SelectingVertexForBisector.class);

	}

	@Override
	protected void undoAction(final PaintContextInterface context) {
		context.popVertex();

	}

	@Override
	protected void onResult(final PaintContextInterface context, final boolean doSpecial) {
		if (context.getLineCount() != 1 ||
				context.getVertexCount() != 3) {
			throw new IllegalStateException("wrong state: impossible line and vertex selection.");
		}

		context.creasePatternUndo().pushCachedUndoInfo();

		Painter painter = context.getPainter();

		painter.addBisectorLine(
				context.getVertex(0), context.getVertex(1), context.getVertex(2),
				context.getLine(0), context.getLineTypeToDraw());

		context.clear(false);
	}

}
