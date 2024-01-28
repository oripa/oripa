package oripa.gui.presenter.creasepattern;

import java.util.Collection;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.deleteline.DeletingLine;
import oripa.domain.paint.deleteline.LineDeleterCommand;
import oripa.gui.view.creasepattern.ObjectGraphicDrawer;
import oripa.util.Command;
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
	 * Resets selection marks to avoid undesired deletion.
	 *
	 * @see AbstractGraphicMouseAction#recover(PaintContext)
	 * @param context
	 */
	@Override
	protected void recoverImpl(final PaintContext context) {
		context.clear(true);
	}

	@Override
	protected void afterRectangularSelection(final Collection<OriLine> selectedLines,
			final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {

		if (selectedLines.isEmpty()) {
			return;
		}

		selectedLines.forEach(paintContext::pushLine);

		Command command = new LineDeleterCommand(paintContext);
		command.execute();
	}

}
