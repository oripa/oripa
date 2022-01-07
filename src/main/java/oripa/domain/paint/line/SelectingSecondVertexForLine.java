package oripa.domain.paint.line;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingVertex;
import oripa.util.Command;

public class SelectingSecondVertexForLine extends PickingVertex {

	public SelectingSecondVertexForLine() {
		super();
	}

	@Override
	protected void onResult(final PaintContext context, final boolean doSpecial) {
		Command command = new LineAdderCommand(context);
		command.execute();
	}

	@Override
	protected void initialize() {
		setPreviousClass(SelectingFirstVertexForLine.class);
		setNextClass(SelectingFirstVertexForLine.class);

		// System.out.println("SelectingSecondVertex.initialize() is called");
	}
}
