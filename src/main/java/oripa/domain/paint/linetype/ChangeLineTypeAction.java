package oripa.domain.paint.linetype;

import java.awt.Graphics2D;
import java.util.Collection;

import oripa.domain.paint.EditMode;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.RectangularSelectableAction;
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
			final PaintContextInterface context) {

		if (selectedLines.isEmpty()) {
			return;
		}
		context.creasePatternUndo().pushUndoInfo();

		var painter = context.getPainter();
		painter.alterLineTypes(selectedLines, setting.getTypeFrom(), setting.getTypeTo());
	}

	@Override
	public void onDraw(final Graphics2D g2d, final PaintContextInterface context) {

		super.onDraw(g2d, context);

		drawPickCandidateLine(g2d, context);
	}

}
