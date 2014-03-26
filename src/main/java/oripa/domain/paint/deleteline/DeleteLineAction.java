package oripa.domain.paint.deleteline;

import java.awt.Graphics2D;
import java.util.Collection;

import oripa.domain.cptool.Painter;
import oripa.domain.paint.EditMode;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.GraphicMouseAction;
import oripa.domain.paint.core.PaintContext;
import oripa.domain.paint.core.RectangularSelectableAction;
import oripa.value.OriLine;

public class DeleteLineAction extends RectangularSelectableAction {

	public DeleteLineAction() {
		setEditMode(EditMode.OTHER);

		setActionState(new DeletingLine());

	}

	@Override
	public void onDraw(final Graphics2D g2d, final PaintContextInterface context) {

		super.onDraw(g2d, context);

		drawPickCandidateLine(g2d, context);

	}

	/**
	 * Reset selection mark to avoid undesired deletion.
	 * 
	 * @see GraphicMouseAction#recover(PaintContext)
	 * @param context
	 */
	@Override
	public void recover(final PaintContextInterface context) {
		context.clear(true);
	}

	@Override
	protected void afterRectangularSelection(final Collection<OriLine> selectedLines,
			final PaintContextInterface context) {

		if (selectedLines.isEmpty() == false) {
			context.creasePatternUndo().pushUndoInfo();
			Painter painter = context.getPainter();
			for (OriLine l : selectedLines) {
				painter.removeLine(l);
			}
		}
	}

}
