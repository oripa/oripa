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
		var snapPointFactory = new LineSnapPointFactory();

		Command command = new LineSnapPointsSetterCommand(context, snapPointFactory);
		command.execute();
	}

	@Override
	protected void initialize() {
		setPreviousClass(SelectingFirstVertexForLine.class);
		setNextClass(SelectingFirstEndPoint.class);
	}
}
