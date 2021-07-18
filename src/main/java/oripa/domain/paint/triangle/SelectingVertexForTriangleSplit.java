package oripa.domain.paint.triangle;

import javax.vecmath.Vector2d;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingVertex;
import oripa.util.Command;

public class SelectingVertexForTriangleSplit extends PickingVertex {

	public SelectingVertexForTriangleSplit() {
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
		if (context.getVertexCount() != 3) {
			throw new IllegalStateException("Wrong state: impossible selection.");
		}

		Command command = new TriangleSplitAdderCommand(context);
		command.execute();
	}
}
