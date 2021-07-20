package oripa.gui.presenter.creasepattern.byvalue;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.byvalue.SelectingVertexForAngle;
import oripa.domain.paint.byvalue.ValueSetting;
import oripa.gui.presenter.creasepattern.AbstractGraphicMouseAction;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.presenter.creasepattern.GraphicMouseAction;
import oripa.gui.presenter.creasepattern.ObjectGraphicDrawer;

public class AngleMeasuringAction extends AbstractGraphicMouseAction {

	private final ValueSetting valueSetting;

	public AngleMeasuringAction(final ValueSetting valueSetting) {
		setActionState(new SelectingVertexForAngle(valueSetting));
		this.valueSetting = valueSetting;
	}

	@Override
	public GraphicMouseAction onLeftClick(final CreasePatternViewContext viewContext,
			final PaintContext paintContext,
			final boolean differentAction) {
		int vertexCountBeforeAction = paintContext.getVertexCount();

		GraphicMouseAction action;
		action = super.onLeftClick(viewContext, paintContext, differentAction);

		int vertexCountAfterAction = paintContext.getVertexCount();

		if (isActionPerformed(vertexCountBeforeAction, vertexCountAfterAction)) {
			action = new LineByValueAction(valueSetting);
		}

		return action;
	}

	private boolean isActionPerformed(final int countBeforeAction, final int countAfterAction) {
		return countBeforeAction > 0 && countAfterAction == 0;
	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {

		super.onDraw(drawer, viewContext, paintContext);

		drawPickCandidateVertex(drawer, viewContext, paintContext);

	}
}
