package oripa.controller.paint.symmetric;

import java.awt.geom.Point2D.Double;

import javax.vecmath.Vector2d;

import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.core.PickingVertex;
import oripa.domain.cptool.Painter;

public class SelectingVertexForSymmetric extends PickingVertex {

	public SelectingVertexForSymmetric() {
		super();
	}

	@Override
	protected void initialize() {
	}

	private boolean doingFirstAction = true;

	private boolean doSpecial = false;

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

		this.doSpecial = doSpecial;

		return result;
	}

	@Override
	public void onResult(final PaintContextInterface context) {

		context.getUndoer().pushCachedUndoInfo();

		Vector2d first = context.getVertex(0);
		Vector2d second = context.getVertex(1);
		Vector2d third = context.getVertex(2);

		Painter painter = context.getPainter();

		if (doSpecial) {
			painter.addSymmetricLineAutoWalk(
					first, second, third, first);
		} else {
			painter.addSymmetricLine(
					first, second, third);
		}

		doingFirstAction = true;
		context.clear(false);
	}

}
