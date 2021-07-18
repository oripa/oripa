package oripa.domain.paint.byvalue;

import javax.vecmath.Vector2d;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingVertex;
import oripa.util.Command;

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

		if (context.getVertexCount() != 2 || context.getLineCount() != 0) {
			throw new IllegalStateException("Wrong state: impossible selection.");
		}

		Command command = new LengthMeasureCommand(context, valueSetting);
		command.execute();

		context.setMissionCompleted(true);
	}

}
