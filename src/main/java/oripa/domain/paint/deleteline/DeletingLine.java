package oripa.domain.paint.deleteline;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingLine;
import oripa.util.Command;

public class DeletingLine extends PickingLine {

	public DeletingLine() {
		super();
	}

	@Override
	protected void initialize() {
	}

	@Override
	protected void onResult(final PaintContext context, final boolean doSpecial) {
		if (context.getLineCount() == 0) {
			return;
		}

		Command command = new LineDeleterCommand(context);
		command.execute();
	}

}
