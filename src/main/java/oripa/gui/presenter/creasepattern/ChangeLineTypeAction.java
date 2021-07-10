package oripa.gui.presenter.creasepattern;

import java.util.Collection;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.linetype.SelectingLineForLineType;
import oripa.domain.paint.linetype.TypeForChangeGettable;
import oripa.value.OriLine;

public class ChangeLineTypeAction extends RectangularSelectableAction {
	private final TypeForChangeGettable setting;

	public ChangeLineTypeAction(final TypeForChangeGettable setting) {
		this.setting = setting;
		setEditMode(EditMode.CHANGE_TYPE);
		setActionState(new SelectingLineForLineType(setting));
	}

	@Override
	protected void afterRectangularSelection(final Collection<OriLine> selectedLines,
			final CreasePatternViewContext viewContext, final PaintContext paintContext) {

		if (selectedLines.isEmpty()) {
			return;
		}
		paintContext.creasePatternUndo().pushUndoInfo();

		var painter = paintContext.getPainter();
		painter.alterLineTypes(selectedLines, setting.getTypeFrom(), setting.getTypeTo());
	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {

		super.onDraw(drawer, viewContext, paintContext);

		drawPickCandidateLine(drawer, viewContext, paintContext);
	}

}