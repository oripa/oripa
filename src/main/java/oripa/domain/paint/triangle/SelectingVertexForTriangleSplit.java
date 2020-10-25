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
		var first = context.getVertex(0);
		var second = context.getVertex(1);
		var third = context.getVertex(2);

		context.clear(false);

		context.creasePatternUndo().pushUndoInfo();

		Painter painter = context.getPainter();
		painter.addTriangleDivideLines(
				first, second, third, context.getLineTypeOfNewLines());
	}
}
