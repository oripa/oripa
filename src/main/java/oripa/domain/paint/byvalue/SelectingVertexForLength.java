package oripa.domain.paint.byvalue;

import javax.vecmath.Vector2d;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingVertex;
import oripa.geom.GeomUtil;

public class SelectingVertexForLength extends PickingVertex {

	private final ValueSetting valueSetting;

	public SelectingVertexForLength(final ValueSetting valueSetting) {
		super();
		this.valueSetting = valueSetting;
	}

	@Override
	protected void initialize() {
	}

	@Override
	protected boolean onAct(final PaintContext context, final Vector2d currentPoint,
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
	public void onResult(final PaintContext context, final boolean doSpecial) {

		double length = GeomUtil.distance(
				context.getVertex(0), context.getVertex(1));

		valueSetting.setLength(length);

		context.clear(false);

		context.setMissionCompleted(true);
	}

}
