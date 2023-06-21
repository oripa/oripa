package oripa.domain.paint.linetoline;

import java.util.List;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickedVerticesConnectionLineAdderCommand;
import oripa.domain.paint.core.PickingVertex;

public class SelectingSecondVertexForFoldLine extends PickingVertex {

	@Override
	protected void initialize() {
		setPreviousClass(SelectingFirstVertexForFoldLine.class);
		setNextClass(SelectingFirstLine.class);
	}

	@Override
	protected void onResult(final PaintContext context, final boolean doSpecial) {
		var vertices = List.of(context.getVertex(0), context.getVertex(1));

		context.clear(false);

		context.pushVertex(vertices.get(0));
		context.pushVertex(vertices.get(1));

		var command = new PickedVerticesConnectionLineAdderCommand(context);

		command.execute();
	}

}
