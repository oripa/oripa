package oripa.domain.paint.vertical;

import oripa.domain.cptool.Painter;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.PickingLine;
import oripa.geom.GeomUtil;
import oripa.value.OriLine;

public class SelectingLineForVertical extends PickingLine {

	@Override
	protected void initialize() {
		setPreviousClass(SelectingVertexForVertical.class);
		setNextClass(SelectingVertexForVertical.class);
	}

	@Override
	protected void undoAction(final PaintContextInterface context) {
		context.clear(false);
	}

	@Override
	protected void onResult(final PaintContextInterface context, final boolean doSpecial) {
		if (context.getLineCount() != 1 ||
				context.getVertexCount() != 1) {
			throw new RuntimeException();
		}

		OriLine vl = GeomUtil.getVerticalLine(
				context.getVertex(0), context.getLine(0), context.getLineTypeOfNewLines());

		context.creasePatternUndo().pushUndoInfo();

		Painter painter = context.getPainter();
		painter.addLine(vl);

		context.clear(false);
	}

}
