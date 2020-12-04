package oripa.domain.paint.selectline;

import java.awt.Graphics2D;
import java.util.Collection;

import oripa.domain.paint.EditMode;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.RectangularSelectableAction;
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
			final PaintContextInterface context) {

		if (selectedLines.isEmpty()) {
			return;
		}

		context.creasePatternUndo().pushUndoInfo();

		for (OriLine line : selectedLines) {
			if (line.isBoundary()) {
				continue;
			}
			// Don't select if the line is hidden
			if (!context.isMVLineVisible() && line.isMV()) {
				continue;
			}
			if (!context.isAuxLineVisible() && line.isAux()) {
				continue;
			}

			if (context.getPickedLines().contains(line) == false) {
				line.selected = true;
				context.pushLine(line);
			}

		}

	}

	@Override
	public void onDraw(final Graphics2D g2d, final PaintContextInterface context) {
		super.onDraw(g2d, context);

		this.drawPickCandidateLine(g2d, context);
	}

}
