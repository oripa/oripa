package oripa.domain.paint.p2ll;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickedVerticesConnectionLineAdderCommand;
import oripa.domain.paint.core.PickingVertex;

public class SelectingSecondVertexForFoldLine extends PickingVertex {

	@Override
	protected void initialize() {
		setPreviousClass(SelectingFirstVertexForFoldLine.class);
		setNextClass(SelectingVertex.class);
	}

	@Override
	protected void onResult(final PaintContext context, final boolean doSpecial) {
		var p0 = context.getVertex(1);
		var p1 = context.getVertex(2);

		context.clear(false);

		context.pushVertex(p0);
		context.pushVertex(p1);

		var command = new PickedVerticesConnectionLineAdderCommand(context);

		command.execute();
	}
}
