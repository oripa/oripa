package oripa.domain.paint.pbisec;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingVertex;
import oripa.util.Command;

public class SelectingSecondVertexForBisector extends PickingVertex {

	public SelectingSecondVertexForBisector() {
		super();
	}

	@Override
	protected void onResult(final PaintContext context, final boolean doSpecial) {

		if (context.getVertexCount() != 2) {
			throw new IllegalStateException();
		}

		Command command = new PerpendicularBisectorAdderCommand(context);
		command.execute();
	}

	@Override
	protected void initialize() {
		setPreviousClass(SelectingFirstVertexForBisector.class);
		setNextClass(SelectingFirstVertexForBisector.class);
	}

}
