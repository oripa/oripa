package oripa.domain.paint.segment;

import oripa.domain.cptool.Painter;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.PickingVertex;
import oripa.value.OriLine;

public class SelectingSecondVertexForSegment extends PickingVertex {

	public SelectingSecondVertexForSegment() {
		super();
	}

	@Override
	protected void onResult(final PaintContextInterface context, final boolean doSpecial) {

		if (context.getVertexCount() != 2) {
			throw new IllegalStateException("wrong state: impossible vertex selection.");
		}

		OriLine line = new OriLine(context.getVertex(0),
				context.getVertex(1), context.getLineTypeToDraw());

		context.creasePatternUndo().pushUndoInfo();

		Painter painter = context.getPainter();
		painter.addLine(line);

		context.clear(false);
	}

	@Override
	protected void initialize() {
		setPreviousClass(SelectingFirstVertexForSegment.class);
		setNextClass(SelectingFirstVertexForSegment.class);

//		System.out.println("SelectingSecondVertex.initialize() is called");
	}
}
