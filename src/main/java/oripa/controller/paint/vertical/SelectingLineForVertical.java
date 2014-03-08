package oripa.controller.paint.vertical;

import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.core.PaintConfig;
import oripa.controller.paint.core.PickingLine;
import oripa.domain.cptool.Painter;
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
	protected void onResult(final PaintContextInterface context) {
		if (context.getLineCount() != 1 ||
				context.getVertexCount() != 1) {
			throw new RuntimeException();
		}

		OriLine vl = GeomUtil.getVerticalLine(
				context.getVertex(0), context.getLine(0), PaintConfig.inputLineType);

		context.getUndoer().pushUndoInfo();

		Painter painter = context.getPainter();
		painter.addLine(vl);

		context.clear(false);
	}

}
