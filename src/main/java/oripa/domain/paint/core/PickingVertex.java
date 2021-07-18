package oripa.domain.paint.core;

import javax.vecmath.Vector2d;

import oripa.domain.paint.PaintContext;

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
	 * @return true if the action succeed, false otherwise.
	 */

	@Override
	protected boolean onAct(final PaintContext context, final Vector2d currentPoint,
			final boolean freeSelection) {

//		Vector2d picked = NearestItemFinder.pickVertex(
//				context, freeSelection);

		var picked = context.getCandidateVertexToPick();

		if (picked == null) {
			return false;
		}

		context.pushVertex(picked);

		return true;
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
