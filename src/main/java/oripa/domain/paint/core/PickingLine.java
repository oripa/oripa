package oripa.domain.paint.core;

import oripa.domain.paint.PaintContext;
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
		var pickedOpt = context.getCandidateLineToPick();

		pickedOpt.ifPresent(picked -> context.pushLine(picked));

		return pickedOpt.isPresent();
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
