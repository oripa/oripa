package oripa.domain.paint.byvalue;

import javax.vecmath.Vector2d;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingVertex;
import oripa.util.Command;

public class SelectingVertexForAngle extends PickingVertex {

	private final ByValueContext valueSetting;

	public SelectingVertexForAngle(final ByValueContext valueSetting) {
		super();
		this.valueSetting = valueSetting;
	}

	@Override
	protected void initialize() {
	}

	@Override
	protected boolean onAct(final PaintContext context, final Vector2d currentPoint,
			final boolean doSpecial) {

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
	public void onResult(final PaintContext context, final boolean doSpecial) {
		Command command = new AngleMeasureCommand(context, valueSetting);
		command.execute();
	}

}
