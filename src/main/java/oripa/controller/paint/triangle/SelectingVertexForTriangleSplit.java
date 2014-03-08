package oripa.controller.paint.triangle;

import java.awt.geom.Point2D.Double;

import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.core.PickingVertex;
import oripa.domain.cptool.Painter;

public class SelectingVertexForTriangleSplit extends PickingVertex {

	public SelectingVertexForTriangleSplit() {
		super();
	}

	@Override
	protected void initialize() {
	}

	private boolean doingFirstAction = true;

	@Override
	protected boolean onAct(final PaintContextInterface context, final Double currentPoint,
			final boolean doSpecial) {

		if (doingFirstAction) {
			context.getUndoer().cacheUndoInfo();
			doingFirstAction = false;
		}

		boolean result = super.onAct(context, currentPoint, doSpecial);

		if (result == true) {
			if (context.getVertexCount() < 3) {
				result = false;
			}
		}

		return result;
	}

	@Override
	public void onResult(final PaintContextInterface context) {

		context.getUndoer().pushCachedUndoInfo();

		Painter painter = context.getPainter();
		painter.addTriangleDivideLines(
				context.getVertex(0), context.getVertex(1), context.getVertex(2));

		doingFirstAction = true;
		context.clear(false);
	}

}
