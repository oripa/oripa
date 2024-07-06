package oripa.domain.paint.p2ll;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingLine;

public class SelectingSecondLine extends PickingLine {

	@Override
	protected void initialize() {
		setPreviousClass(SelectingFirstLine.class);
		setNextClass(SelectingFirstVertexForFoldLine.class);
	}

	@Override
	protected void onResult(final PaintContext context, final boolean doSpecial) {
		var command = new PointToLineLinePerpendicularSetterCommand(context);

		command.execute();
	}

}
