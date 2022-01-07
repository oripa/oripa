package oripa.domain.paint.segment;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickedVerticesConnectionLineAdderCommand;
import oripa.domain.paint.core.PickingVertex;

public class SelectingSecondVertexForSegment extends PickingVertex {

	public SelectingSecondVertexForSegment() {
		super();
	}

	@Override
	protected void onResult(final PaintContext context, final boolean doSpecial) {
		var command = new PickedVerticesConnectionLineAdderCommand(context);
		command.execute();
	}

	@Override
	protected void initialize() {
		setPreviousClass(SelectingFirstVertexForSegment.class);
		setNextClass(SelectingFirstVertexForSegment.class);
	}
}
