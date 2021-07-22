package oripa.domain.paint.pbisec;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingVertex;
import oripa.util.Command;

public class SelectingSecondVertexForBisector extends PickingVertex {

	@Override
	protected void initialize() {
		setPreviousClass(SelectingFirstVertexForBisector.class);
		setNextClass(SelectingFirstEndPoint.class);
	}

	@Override
	protected void onResult(final PaintContext context, final boolean doSpecial) {
		Command command = new PerpendicularBisectorSnapPointsSetterCommand(context);
		command.execute();
	}

}
