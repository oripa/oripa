package oripa.gui.presenter.creasepattern;

import java.util.Collection;

import oripa.domain.cptool.Painter;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.deleteline.DeletingLine;
import oripa.value.OriLine;

public class DeleteLineAction extends RectangularSelectableAction {

	public DeleteLineAction() {
		setEditMode(EditMode.OTHER);

		setActionState(new DeletingLine());

	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {

		super.onDraw(drawer, viewContext, paintContext);

		drawPickCandidateLine(drawer, viewContext, paintContext);

	}

	/**
	 * Reset selection mark to avoid undesired deletion.
	 *
	 * @see GraphicMouseAction#recover(PaintContext)
	 * @param context
	 */
	@Override
	protected void recoverImpl(final PaintContext context) {
		context.clear(true);
	}

	@Override
	protected void afterRectangularSelection(final Collection<OriLine> selectedLines,
			final CreasePatternViewContext viewContext, final PaintContext paintContext) {

		if (selectedLines.isEmpty()) {
			return;
		}
		paintContext.creasePatternUndo().pushUndoInfo();
		Painter painter = paintContext.getPainter();
		painter.removeLines(selectedLines);
	}

}
