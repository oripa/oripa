package oripa.domain.paint.core;

import java.awt.geom.Point2D.Double;

import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.geometry.NearestItemFinder;
import oripa.value.OriLine;

/**
 * abstract class specified for picking vertex.
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
	protected boolean onAct(final PaintContextInterface context, final Double currentPoint,
			final boolean doSpecial) {

		OriLine picked = NearestItemFinder.pickLine(
				context);

//		OriLine picked = NearestItemFinder.pickLine(
//				context.getCreasePattern(),
//				currentPoint, context.getScale());

		if (picked == null) {
			System.out.println("onAct() failed");
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
	protected void undoAction(final PaintContextInterface context) {
		context.popLine();
	}

}
