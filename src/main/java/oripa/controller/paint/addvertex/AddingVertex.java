package oripa.controller.paint.addvertex;

import java.awt.geom.Point2D;

import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.core.PickingVertex;
import oripa.controller.paint.geometry.NearestItemFinder;
import oripa.domain.cptool.Painter;
import oripa.value.OriLine;

public class AddingVertex extends PickingVertex {

	@Override
	protected void initialize() {

	}

	@Override
	protected boolean onAct(final PaintContextInterface context, final Point2D.Double currentPoint,
			final boolean freeSelection) {

		boolean result = super.onAct(context, currentPoint, true);

		if (result == true) {
			OriLine line = NearestItemFinder.pickLine(
					context);

			if (line != null) {
				context.pushLine(line);
			}
			else {
				result = false;
			}
		}

		return result;
	}

	@Override
	protected void onResult(final PaintContextInterface context) {

		if (context.getVertexCount() > 0) {

			context.creasePatternUndo().pushUndoInfo();

			Painter painter = context.getPainter();

			if (!painter.addVertexOnLine(
					context.popLine(), context.popVertex())) {
				context.creasePatternUndo().loadUndoInfo();
			}

		}

		context.clear(false);
	}

}
