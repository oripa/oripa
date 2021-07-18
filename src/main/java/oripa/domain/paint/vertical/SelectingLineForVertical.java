package oripa.domain.paint.vertical;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingLine;
import oripa.util.Command;

public class SelectingLineForVertical extends PickingLine {

	@Override
	protected void initialize() {
		setPreviousClass(SelectingVertexForVertical.class);
		setNextClass(SelectingVertexForVertical.class);
	}

	@Override
	protected void undoAction(final PaintContext context) {
		context.clear(false);
	}

	@Override
	protected void onResult(final PaintContext context, final boolean doSpecial) {
		if (context.getLineCount() != 1 ||
				context.getVertexCount() != 1) {
			throw new IllegalStateException("Wrong state: impossible selection.");
		}

		Command command = new VerticalLineAdderCommand(context);
		command.execute();
	}

}
