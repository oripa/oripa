package oripa.domain.paint.mirror;

import oripa.domain.cptool.Painter;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.selectline.SelectingLine;
import oripa.value.OriLine;

//TODO separate into FirstState and LatterState to delete state variable

public class SelectingLineForMirror extends SelectingLine {

	public SelectingLineForMirror() {
		super();
	}

	@Override
	protected void onResult(final PaintContextInterface context, final boolean doSpecial) {
		if (doSpecial) {
			context.creasePatternUndo().pushUndoInfo();

			OriLine axis;
			axis = context.popLine();

			Painter painter = context.getPainter();
			painter.mirrorCopyBy(axis, context.getPickedLines());

			context.clear(true);
		} else {
			super.onResult(context, false);
		}

	}

}
