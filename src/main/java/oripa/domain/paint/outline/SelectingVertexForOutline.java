package oripa.domain.paint.outline;

import java.awt.geom.Point2D;
import java.util.Collection;

import javax.vecmath.Vector2d;

import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.PickingVertex;
import oripa.geom.GeomUtil;

public class SelectingVertexForOutline extends PickingVertex {

	@Override
	protected void initialize() {

	}

	@Override
	protected boolean onAct(final PaintContextInterface context, final Point2D.Double currentPoint,
			final boolean freeSelection) {
		context.setMissionCompleted(false);
		return super.onAct(context, currentPoint, freeSelection);
	}

	@Override
	protected void onResult(final PaintContextInterface context) {

		Vector2d v = context.popVertex();

		boolean bClose = false;
		for (Vector2d tv : context.getPickedVertices()) {
			if (GeomUtil.Distance(v, tv) < 1) {
				bClose = true;
				break;
			}
		}

		if (bClose) {
			if (context.getVertexCount() > 2) {
				// finish editing

				context.creasePatternUndo().pushUndoInfo();
				closeTmpOutline(context.getPickedVertices(), context.getCreasePattern());

				context.clear(false);
				context.setMissionCompleted(true);
			}
		} else {
			// continue selecting
			context.pushVertex(v);
		}

	}

	private void closeTmpOutline(final Collection<Vector2d> outlineVertices,
			final CreasePatternInterface creasePattern) {

		(new CloseTempOutline()).execute(creasePattern, outlineVertices);

	}

}
