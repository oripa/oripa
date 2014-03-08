package oripa.controller.paint.bisector;

import java.awt.geom.Point2D.Double;

import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.core.PickingVertex;

public class SelectingVertexForBisector extends PickingVertex {

	public SelectingVertexForBisector() {
		super();
	}

	@Override
	protected void initialize() {
		setPreviousClass(this.getClass());
		setNextClass(SelectingLineForBisector.class);

//		System.out.println("SelectingFirstVertex.initialize() is called");
	}

	private boolean doingFirstAction = true;

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

		return result;
	}

	@Override
	public void onResult(final PaintContextInterface context) {

	}

}
