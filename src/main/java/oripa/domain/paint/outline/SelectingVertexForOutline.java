package oripa.domain.paint.outline;

import java.awt.geom.Point2D;
import java.util.Collection;

import javax.vecmath.Vector2d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.PickingVertex;
import oripa.geom.GeomUtil;

public class SelectingVertexForOutline extends PickingVertex {
	private static final Logger LOGGER = LoggerFactory.getLogger(SelectingVertexForOutline.class);

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
	protected void onResult(final PaintContextInterface context, final boolean doSpecial) {

		LOGGER.debug("# of picked vertices (before): " + context.getPickedVertices().size());

		Vector2d v = context.popVertex();

		if (context.getPickedVertices().stream()
				.anyMatch(tv -> GeomUtil.distance(v, tv) < 1)) {
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

		LOGGER.debug("# of picked vertices (after): " + context.getPickedVertices().size());
	}

	private void closeTmpOutline(final Collection<Vector2d> outlineVertices,
			final CreasePatternInterface creasePattern) {

		(new CloseTempOutline()).execute(creasePattern, outlineVertices);

	}

}
