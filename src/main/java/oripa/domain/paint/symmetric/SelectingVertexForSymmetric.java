package oripa.domain.paint.symmetric;

import java.awt.geom.Point2D.Double;

import javax.vecmath.Vector2d;

import oripa.domain.cptool.Painter;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.PickingVertex;

public class SelectingVertexForSymmetric extends PickingVertex {

	public SelectingVertexForSymmetric() {
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

		Vector2d first = context.getVertex(0);
		Vector2d second = context.getVertex(1);
		Vector2d third = context.getVertex(2);

		Painter painter = context.getPainter();

		if (doSpecial) {
			painter.addSymmetricLineAutoWalk(
					first, second, third, first, context.getLineTypeOfNewLines());
		} else {
			painter.addSymmetricLine(
					first, second, third, context.getLineTypeOfNewLines());
		}

		context.clear(false);
	}

}
