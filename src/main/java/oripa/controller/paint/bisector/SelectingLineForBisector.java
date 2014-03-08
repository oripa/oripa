package oripa.controller.paint.bisector;

import java.util.List;

import javax.vecmath.Vector2d;

import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.core.PickingLine;
import oripa.domain.cptool.Painter;

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
	protected void onResult(final PaintContextInterface context) {
		if (context.getLineCount() != 1 ||
				context.getVertexCount() != 3) {
			throw new RuntimeException();
		}

		context.getUndoer().pushCachedUndoInfo();

		Painter painter = context.getPainter();

		List<Vector2d> vertices = context.getPickedVertices();
		painter.addBisectorLine(
				context.getVertex(0), context.getVertex(1), context.getVertex(2),
				context.getLine(0));

		context.clear(false);
	}

}
