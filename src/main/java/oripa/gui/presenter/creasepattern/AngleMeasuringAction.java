package oripa.gui.presenter.creasepattern;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.byvalue.SelectingVertexForAngle;
import oripa.domain.paint.byvalue.ValueSetting;

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

		GraphicMouseAction action;
		action = super.onLeftClick(viewContext, paintContext, differentAction);

		if (paintContext.isMissionCompleted()) {
			action = new LineByValueAction(valueSetting);
		}

		return action;
	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {

		super.onDraw(drawer, viewContext, paintContext);

		drawPickCandidateVertex(drawer, viewContext, paintContext);

	}
}
