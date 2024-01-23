package oripa.domain.paint.core;

import oripa.domain.paint.PaintContext;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

/**
 * abstract class specified for picking line.
 *
 * @author koji
 *
 */
public abstract class PickingLine extends AbstractActionState {

	public PickingLine() {
		super();
	}

	/**
	 * Picks the nearest line and push it into context.
	 *
	 * @return true if the action succeed, false otherwise.
	 */

	@Override
	protected boolean onAct(final PaintContext context, final Vector2d currentPoint,
			final boolean doSpecial) {
		OriLine picked = context.getCandidateLineToPick();

		if (picked == null) {
			return false;
		}

		context.pushLine(picked);

		return true;
	}

	/**
	 * delete from context the latest picked line.
	 *
	 * @return Previous state
	 */
	@Override
	protected void undoAction(final PaintContext context) {
		context.popLine();
	}

}
