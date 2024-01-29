package oripa.domain.paint.core;

import oripa.domain.paint.PaintContext;
import oripa.vecmath.Vector2d;

/**
 * abstract class specified for picking vertex.
 *
 * @author koji
 *
 */
public abstract class PickingVertex extends AbstractActionState {

	public PickingVertex() {
		super();
	}

	/**
	 * Picks the nearest vertex and push it into context.
	 *
	 */

	@Override
	protected boolean onAct(final PaintContext context, final Vector2d currentPoint,
			final boolean freeSelection) {
		var pickedOpt = context.getCandidateVertexToPick();

		pickedOpt.ifPresent(picked -> context.pushVertex(picked));

		return pickedOpt.isPresent();
	}

	/**
	 * delete from context the latest picked vertex.
	 *
	 * @return Previous state
	 */
	@Override
	protected void undoAction(final PaintContext context) {
		context.popVertex();
	}

}
