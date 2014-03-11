package oripa.controller.paint.segment;

import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.core.PaintConfig;
import oripa.controller.paint.core.PickingVertex;
import oripa.domain.cptool.Painter;
import oripa.value.OriLine;

public class SelectingSecondVertexForSegment extends PickingVertex {

	public SelectingSecondVertexForSegment() {
		super();
	}

	@Override
	protected void onResult(final PaintContextInterface context) {

		if (context.getVertexCount() != 2) {
			throw new RuntimeException();
		}

		OriLine line = new OriLine(context.getVertex(0),
				context.getVertex(1), PaintConfig.inputLineType);

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
