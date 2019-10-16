package oripa.domain.paint.byvalue;

import java.awt.geom.Point2D.Double;

import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.PickingVertex;
import oripa.geom.GeomUtil;

public class SelectingVertexForLength extends PickingVertex {

	public SelectingVertexForLength() {
		super();
	}

	@Override
	protected void initialize() {
	}

	private final boolean doingFirstAction = true;

	@Override
	protected boolean onAct(final PaintContextInterface context, final Double currentPoint,
			final boolean doSpecial) {

		context.setMissionCompleted(false);

		boolean vertexIsSelected = super.onAct(context, currentPoint, doSpecial);

		if (!vertexIsSelected) {
			return false;
		}

		if (context.getVertexCount() < 2) {
			return false;
		}

		return true;
	}

	@Override
	public void onResult(final PaintContextInterface context) {

		double length = GeomUtil.Distance(
				context.getVertex(0), context.getVertex(1));

		ValueDB valDB = ValueDB.getInstance();
		valDB.setLength(length);
		valDB.notifyObservers();

		context.clear(false);

		context.setMissionCompleted(true);
	}

}
