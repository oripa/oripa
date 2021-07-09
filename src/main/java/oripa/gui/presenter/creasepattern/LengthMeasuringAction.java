package oripa.gui.presenter.creasepattern;

import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.byvalue.SelectingVertexForLength;
import oripa.domain.paint.byvalue.ValueSetting;

public class LengthMeasuringAction extends GraphicMouseAction {

	private final ValueSetting valueSetting;

	public LengthMeasuringAction(final ValueSetting valueSetting) {
		super();
		setActionState(new SelectingVertexForLength(valueSetting));
		this.valueSetting = valueSetting;
	}

	@Override
	public GraphicMouseActionInterface onLeftClick(final CreasePatternViewContext viewContext,
			final PaintContextInterface paintContext,
			final boolean differentAction) {

		GraphicMouseActionInterface action;
		action = super.onLeftClick(viewContext, paintContext, differentAction);

		if (paintContext.isMissionCompleted()) {
			action = new LineByValueAction(valueSetting);
		}

		return action;
	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContextInterface paintContext) {

		super.onDraw(drawer, viewContext, paintContext);

		drawPickCandidateVertex(drawer, viewContext, paintContext);

	}
}
