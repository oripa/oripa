package oripa.domain.paint.linetype;

import oripa.domain.cptool.Painter;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.PickingLine;

public class SelectingLineForLineType extends PickingLine {

	private final TypeForChangeGettable setting;

	public SelectingLineForLineType(final TypeForChangeGettable setting) {
		super();
		this.setting = setting;
	}

	@Override
	protected void initialize() {
	}

	@Override
	protected void undoAction(final PaintContextInterface context) {
		super.undoAction(context);
	}

	@Override
	protected void onResult(final PaintContextInterface context, final boolean doSpecial) {

		context.creasePatternUndo().pushUndoInfo();

		Painter painter = context.getPainter();
		painter.alterLineType(
				context.peekLine(), setting.getTypeFrom(), setting.getTypeTo());

		context.clear(false);
	}

}
