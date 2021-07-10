package oripa.domain.paint.linetype;

import oripa.domain.cptool.Painter;
import oripa.domain.paint.PaintContext;
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
	protected void undoAction(final PaintContext context) {
		super.undoAction(context);
	}

	@Override
	protected void onResult(final PaintContext context, final boolean doSpecial) {

		context.creasePatternUndo().pushUndoInfo();

		Painter painter = context.getPainter();
		painter.alterLineType(
				context.peekLine(), setting.getTypeFrom(), setting.getTypeTo());

		context.clear(false);
	}

}
