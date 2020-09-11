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

	@Override
	protected boolean onAct(final PaintContextInterface context, final Double currentPoint,
			final boolean doSpecial) {

		if (context.getVertexCount() == 0) {
			context.creasePatternUndo().cacheUndoInfo();
		}

		boolean vertexIsSelected = super.onAct(context, currentPoint, doSpecial);

		if (!vertexIsSelected) {
			return false;
		}

		if (context.getVertexCount() < 3) {
			return false;
		}

		return true;
	}

	@Override
	public void onResult(final PaintContextInterface context, final boolean doSpecial) {

		context.creasePatternUndo().pushCachedUndoInfo();

		Painter painter = context.getPainter();
		painter.addTriangleDivideLines(
				context.getVertex(0), context.getVertex(1), context.getVertex(2),
				context.getLineTypeOfNewLines());

		context.clear(false);
	}
}
