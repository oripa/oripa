package oripa.controller.paint.byvalue;

import java.awt.geom.Point2D.Double;

import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.core.PickingVertex;
import oripa.geom.GeomUtil;

public class SelectingVertexForLength extends PickingVertex {

	public SelectingVertexForLength() {
		super();
	}

	@Override
	protected void initialize() {
	}

	private boolean doingFirstAction = true;

	@Override
	protected boolean onAct(final PaintContextInterface context, final Double currentPoint,
			final boolean doSpecial) {

		context.setMissionCompleted(false);

		if (doingFirstAction) {
			context.getUndoer().cacheUndoInfo();
			doingFirstAction = false;
		}

		boolean result = super.onAct(context, currentPoint, doSpecial);

		if (result == true) {
			if (context.getVertexCount() < 2) {
				result = false;
			}
		}

		return result;
	}

	@Override
	public void onResult(final PaintContextInterface context) {

		double length = GeomUtil.Distance(
				context.getVertex(0), context.getVertex(1));

		ValueDB valDB = ValueDB.getInstance();
		valDB.setLength(length);
		valDB.notifyObservers();

//        Globals.subLineInputMode = Constants.SubLineInputMode.NONE;

		doingFirstAction = true;
		context.clear(false);

		context.setMissionCompleted(true);
	}

}
