package oripa.gui.presenter.creasepattern;

import java.util.Collection;

import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.selectline.SelectingLine;
import oripa.value.OriLine;

public class SelectLineAction extends RectangularSelectableAction {

	public SelectLineAction() {
		setEditMode(EditMode.SELECT);
		setNeedSelect(true);

		setActionState(new SelectingLine());
	}

	/**
	 * set old line-selected marks to current context.
	 */
	@Override
	public void undo(final PaintContextInterface context) {
		context.creasePatternUndo().undo();

		recover(context);
	}

	@Override
	public void redo(final PaintContextInterface context) {
		context.creasePatternUndo().redo();

		recover(context);
	}

	@Override
	protected void recoverImpl(final PaintContextInterface context) {
		context.clear(false);

		Collection<OriLine> creasePattern = context.getCreasePattern();
		if (creasePattern == null) {
			return;
		}

		creasePattern.stream()
				.filter(line -> line.selected)
				.forEach(line -> context.pushLine(line));
	}

	@Override
	protected void afterRectangularSelection(final Collection<OriLine> selectedLines,
			final CreasePatternViewContext viewContext, final PaintContextInterface paintContext) {

		if (selectedLines.isEmpty()) {
			return;
		}

		paintContext.creasePatternUndo().pushUndoInfo();

		for (OriLine line : selectedLines) {
			if (line.isBoundary()) {
				continue;
			}
			// Don't select if the line is hidden
			if (!viewContext.isMVLineVisible() && line.isMV()) {
				continue;
			}
			if (!viewContext.isAuxLineVisible() && line.isAux()) {
				continue;
			}

			if (paintContext.getPickedLines().contains(line) == false) {
				line.selected = true;
				paintContext.pushLine(line);
			}

		}

	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContextInterface paintContext) {
		super.onDraw(drawer, viewContext, paintContext);

		this.drawPickCandidateLine(drawer, viewContext, paintContext);
	}

}
