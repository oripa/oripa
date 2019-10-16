package oripa.domain.paint.mirror;

import java.awt.geom.Point2D;

import oripa.domain.cptool.Painter;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.PickingLine;
import oripa.value.OriLine;

//TODO separate into FirstState and LatterState to delete state variable

public class SelectingLineForMirror extends PickingLine {

	public SelectingLineForMirror() {
		super();
	}

	@Override
	protected void initialize() {
	}

	private OriLine axis;
	private boolean doingFirstAction = true;

	/**
	 * This class keeps selecting line while {@code doSpecial} is false. When
	 * {@value doSpecial} is true, it executes mirror copy where the axis of
	 * mirror copy is the selected line.
	 *
	 * @param doSpecial
	 *            true if copy should be done.
	 * @return true if copy is done.
	 */
	@Override
	protected boolean onAct(final PaintContextInterface context, final Point2D.Double currentPoint,
			final boolean doSpecial) {
		if (doingFirstAction) {
			doingFirstAction = false;
			context.creasePatternUndo().cacheUndoInfo();

		}

		boolean lineIsSelected = super.onAct(context, currentPoint, doSpecial);

		if (!lineIsSelected) {
			return false;
		}

		if (doSpecial) {
			axis = context.popLine();
			return true;
		}

		OriLine line = context.peekLine();

		// toggle selection
		if (line.selected) {
			context.popLine();
			context.removeLine(line);
		} else {
			line.selected = true;
		}

		// repeat this state until axis is selected.
		return false;
	}

	@Override
	protected void undoAction(final PaintContextInterface context) {
		context.popLine();
	}

	@Override
	protected void onResult(final PaintContextInterface context) {

		context.creasePatternUndo().pushCachedUndoInfo();

		Painter painter = context.getPainter();
		painter.mirrorCopyBy(axis, context.getPickedLines());

		doingFirstAction = true;
		context.clear(true);
	}

}
