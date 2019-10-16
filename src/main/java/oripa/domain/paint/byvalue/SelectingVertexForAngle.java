package oripa.domain.paint.byvalue;

import java.awt.geom.Point2D.Double;

import javax.vecmath.Vector2d;

import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.PickingVertex;

public class SelectingVertexForAngle extends PickingVertex {

	public SelectingVertexForAngle() {
		super();
	}

	@Override
	protected void initialize() {
	}

	@Override
	protected boolean onAct(final PaintContextInterface context, final Double currentPoint,
			final boolean doSpecial) {

		context.setMissionCompleted(false);

		boolean vertexIsSelected = super.onAct(context, currentPoint, doSpecial);

		if (!vertexIsSelected) {
			return false;
		}

		if (context.getVertexCount() < 3) {
			return false;
		}

		return true;
	}

	@Override
	public void onResult(final PaintContextInterface context) {

		Vector2d first = context.getVertex(0);
		Vector2d second = context.getVertex(1);
		Vector2d third = context.getVertex(2);

		Vector2d dir1 = new Vector2d(third);
		Vector2d dir2 = new Vector2d(first);
		dir1.sub(second);
		dir2.sub(second);

		double deg_angle = Math.toDegrees(dir1.angle(dir2));

		ValueDB valDB = ValueDB.getInstance();

		valDB.setAngle(deg_angle);
		valDB.notifyObservers();

		context.clear(false);

		context.setMissionCompleted(true);
	}

}
