package oripa.domain.paint.addvertex;

import javax.vecmath.Vector2d;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingVertex;
import oripa.value.OriLine;

public class AddingVertex extends PickingVertex {
	@Override
	protected void initialize() {

	}

	@Override
	protected boolean onAct(final PaintContext context, final Vector2d currentPoint,
			final boolean freeSelection) {

		boolean result = super.onAct(context, currentPoint, true);

		if (result == true) {
			OriLine line = context.getCandidateLineToPick();

			if (line != null) {
				context.pushLine(line);
			} else {
				result = false;
			}
		}

		return result;
	}

	@Override
	protected void onResult(final PaintContext context, final boolean doSpecial) {

		if (context.getVertexCount() == 0 || context.getLineCount() != 1) {
			throw new IllegalStateException("wrong state: impossible selection.");
		}

		var command = new VertexAdderCommand(context);
		command.execute();
	}

}
