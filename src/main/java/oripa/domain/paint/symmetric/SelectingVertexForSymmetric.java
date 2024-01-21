package oripa.domain.paint.symmetric;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingVertex;
import oripa.util.Command;
import oripa.vecmath.Vector2d;

public class SelectingVertexForSymmetric extends PickingVertex {

	public SelectingVertexForSymmetric() {
		super();
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
	protected void onResult(final PaintContext context, final boolean doSpecial) {
		Command command = new SymmetricLineAdderCommand(context, doSpecial);
		command.execute();
	}

}
