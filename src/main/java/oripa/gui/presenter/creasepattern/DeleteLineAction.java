package oripa.gui.presenter.creasepattern;

import java.util.Collection;

import oripa.domain.cptool.Painter;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.deleteline.DeletingLine;
import oripa.value.OriLine;

public class DeleteLineAction extends RectangularSelectableAction {

	public DeleteLineAction() {
		setEditMode(EditMode.OTHER);

		setActionState(new DeletingLine());

	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final PaintContextInterface context) {

		super.onDraw(drawer, context);

		drawPickCandidateLine(drawer, context);

	}

	/**
	 * Reset selection mark to avoid undesired deletion.
	 *
	 * @see GraphicMouseAction#recover(PaintContextInterface)
	 * @param context
	 */
	@Override
	protected void recoverImpl(final PaintContextInterface context) {
		context.clear(true);
	}

	@Override
	protected void afterRectangularSelection(final Collection<OriLine> selectedLines,
			final PaintContextInterface context) {

		if (selectedLines.isEmpty()) {
			return;
		}
		context.creasePatternUndo().pushUndoInfo();
		Painter painter = context.getPainter();
		painter.removeLines(selectedLines);
	}

}
