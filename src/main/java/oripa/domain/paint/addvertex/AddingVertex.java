package oripa.domain.paint.addvertex;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingVertex;

public class AddingVertex extends PickingVertex {
	@Override
	protected void initialize() {

	}

	@Override
	protected boolean onAct(final PaintContext context, final boolean freeSelection) {

		boolean result = super.onAct(context, true);

		if (result == true) {
			var lineOpt = context.getCandidateLineToPick();

			lineOpt.ifPresent(context::pushLine);

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
