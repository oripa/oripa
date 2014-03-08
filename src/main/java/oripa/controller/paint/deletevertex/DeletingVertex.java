package oripa.controller.paint.deletevertex;

import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.core.PickingVertex;
import oripa.domain.cptool.Painter;

public class DeletingVertex extends PickingVertex {

	@Override
	protected void initialize() {

	}

	@Override
	protected void onResult(final PaintContextInterface context) {

		if (context.getVertexCount() > 0) {
			context.getUndoer().pushUndoInfo();

			Painter painter = context.getPainter();
			painter.removeVertex(context.popVertex());

		}

		context.clear(false);
	}

}
