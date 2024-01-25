package oripa.domain.paint.addvertex;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingVertex;
import oripa.vecmath.Vector2d;

public class AddingVertex extends PickingVertex {
	@Override
	protected void initialize() {

	}

	@Override
	protected boolean onAct(final PaintContext context, final Vector2d currentPoint,
			final boolean freeSelection) {

		boolean result = super.onAct(context, currentPoint, true);

		if (result == true) {
			var lineOpt = context.getCandidateLineToPick();

			lineOpt.ifPresent(line -> context.pushLine(line));

			return lineOpt.isPresent();
		}

		return result;
	}

	@Override
	protected void onResult(final PaintContext context, final boolean doSpecial) {
		var command = new VertexAdderCommand(context);
		command.execute();
	}

}
