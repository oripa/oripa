package oripa.gui.presenter.creasepattern;

import java.util.Collection;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.selectline.SelectingLine;
import oripa.gui.view.creasepattern.ObjectGraphicDrawer;
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
	public void undo(final PaintContext context) {
		context.creasePatternUndo().undo();
		context.refreshCreasePattern();

		recover(context);
	}

	@Override
	public void redo(final PaintContext context) {
		context.creasePatternUndo().redo();
		context.refreshCreasePattern();

		recover(context);
	}

	@Override
	protected void recoverImpl(final PaintContext context) {
		context.clear(false);

		Collection<OriLine> creasePattern = context.getCreasePattern();
		if (creasePattern == null) {
			return;
		}

		creasePattern.stream()
				.filter(OriLine::isSelected)
				.forEach(context::pushLine);
	}

	@Override
	protected void afterRectangularSelection(final Collection<OriLine> selectedLines,
			final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {

		if (selectedLines.isEmpty()) {
			return;
		}

		paintContext.creasePatternUndo().pushUndoInfo();

		for (OriLine line : selectedLines) {
			if (!differentAction && line.isBoundary()) {
				continue;
			}
			// Don't select if the line is hidden
			if (!viewContext.isMVLineVisible() && line.isFoldLine()) {
				continue;
			}
			if (!viewContext.isAuxLineVisible() && line.isAux()) {
				continue;
			}

			if (paintContext.getPickedLines().contains(line) == false) {
				line.setSelected(true);
				paintContext.pushLine(line);
			}

		}

	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {
		super.onDraw(drawer, viewContext, paintContext);

		this.drawPickCandidateLine(drawer, viewContext, paintContext);
	}

}
