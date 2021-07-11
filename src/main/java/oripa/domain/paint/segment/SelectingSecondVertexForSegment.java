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

		if (context.getVertexCount() != 2) {
			throw new IllegalStateException("wrong state: impossible vertex selection.");
		}

//		OriLine line = new OriLine(context.getVertex(0),
//				context.getVertex(1), context.getLineTypeOfNewLines());
//
//		context.creasePatternUndo().pushUndoInfo();
//
//		Painter painter = context.getPainter();
//		painter.addLine(line);
//
//		context.clear(false);

		var command = new PickedVerticesConnectionLineAdderCommand(context);
		command.execute();
	}

	@Override
	protected void initialize() {
		setPreviousClass(SelectingFirstVertexForSegment.class);
		setNextClass(SelectingFirstVertexForSegment.class);
	}
}
