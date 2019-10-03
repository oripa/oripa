package oripa.domain.paint.selectline;

import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.PickingLine;
import oripa.value.OriLine;

public class SelectingLine extends PickingLine {

	public SelectingLine() {
		super();
	}

	@Override
	protected void initialize() {
	}

	@Override
	protected void undoAction(final PaintContextInterface context) {
		// TODO Auto-generated method stub
		super.undoAction(context);
	}

	@Override
	protected void onResult(final PaintContextInterface context) {

		context.creasePatternUndo().pushUndoInfo();

		OriLine line = context.peekLine();

		// toggle selection
		if (line.selected) {
			line.selected = false;
			context.popLine();
			// line should be already stored.
			context.removeLine(line);
		}
		else {
			line.selected = true;
		}

	}

}
