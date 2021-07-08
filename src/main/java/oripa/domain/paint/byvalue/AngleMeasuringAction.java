package oripa.domain.paint.byvalue;

import java.awt.geom.AffineTransform;

import oripa.domain.paint.GraphicMouseActionInterface;
import oripa.domain.paint.ObjectGraphicDrawer;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.GraphicMouseAction;

public class AngleMeasuringAction extends GraphicMouseAction {

	private final ValueSetting valueSetting;

	public AngleMeasuringAction(final ValueSetting valueSetting) {
		setActionState(new SelectingVertexForAngle(valueSetting));
		this.valueSetting = valueSetting;
	}

	@Override
	public GraphicMouseActionInterface onLeftClick(final PaintContextInterface context,
			final boolean differentAction) {

		GraphicMouseActionInterface action;
		action = super.onLeftClick(context, differentAction);

		if (context.isMissionCompleted()) {
			action = new LineByValueAction(valueSetting);
		}

		return action;
	}

	@Override
	public void onDrag(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {

	}

	@Override
	public void onRelease(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {

	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final PaintContextInterface context) {

		super.onDraw(drawer, context);

		drawPickCandidateVertex(drawer, context);

	}

	@Override
	public void onPress(final PaintContextInterface context, final boolean differentAction) {

	}
}
