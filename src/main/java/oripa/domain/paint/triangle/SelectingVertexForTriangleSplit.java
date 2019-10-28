package oripa.domain.paint.triangle;

import java.awt.geom.Point2D.Double;

import oripa.domain.cptool.Painter;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.PickingVertex;

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
			context.creasePatternUndo().cacheUndoInfo();
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
	public void onResult(final PaintContextInterface context, final boolean doSpecial) {

		context.creasePatternUndo().pushCachedUndoInfo();

		Painter painter = context.getPainter();
		painter.addTriangleDivideLines(
				context.getVertex(0), context.getVertex(1), context.getVertex(2),
				context.getLineTypeToDraw());

		doingFirstAction = true;
		context.clear(false);
	}

}
