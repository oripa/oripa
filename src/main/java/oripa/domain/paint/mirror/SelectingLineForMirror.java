package oripa.domain.paint.mirror;

import oripa.domain.cptool.Painter;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.selectline.SelectingLine;
import oripa.value.OriLine;

public class SelectingLineForMirror extends SelectingLine {

	public SelectingLineForMirror() {
		super();
	}

	@Override
	protected void onResult(final PaintContext context, final boolean doSpecial) {
		if (doSpecial) {
			context.creasePatternUndo().pushUndoInfo();

			final OriLine axis = context.popLine();

			Painter painter = context.getPainter();
			painter.mirrorCopyBy(axis, context.getPickedLines());

			context.clear(true);
		} else {
			super.onResult(context, false);
		}

	}

}
