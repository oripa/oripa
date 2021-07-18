package oripa.gui.presenter.creasepattern.byvalue;

import javax.vecmath.Vector2d;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.byvalue.SelectingVertexToDrawLine;
import oripa.domain.paint.byvalue.ValueSetting;
import oripa.gui.presenter.creasepattern.AbstractGraphicMouseAction;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.presenter.creasepattern.ObjectGraphicDrawer;

public class LineByValueAction extends AbstractGraphicMouseAction {

	private final ValueSetting valueSetting;

	public LineByValueAction(final ValueSetting valueSetting) {
		super();
		setActionState(new SelectingVertexToDrawLine(valueSetting));

		this.valueSetting = valueSetting;
	}

	@Override
	protected void recoverImpl(final PaintContext context) {
		context.clear(true);
	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {
		super.onDraw(drawer, viewContext, paintContext);

		drawPickCandidateVertex(drawer, viewContext, paintContext);
		Vector2d v = paintContext.getCandidateVertexToPick();
		if (v == null) {
			return;
		}
		try {
			var angle = valueSetting.getAngle();
			var length = valueSetting.getLength();

			var radianAngle = Math.toRadians(angle);

			drawer.selectColor(paintContext.getLineTypeOfNewLines());
			drawer.selectStroke(
					paintContext.getLineTypeOfNewLines(),
					paintContext.getScale(), viewContext.isZeroLineWidth());

			Vector2d dir = new Vector2d(Math.cos(radianAngle), -Math.sin(radianAngle));
			dir.scale(length);
			var w = new Vector2d(v);
			w.add(dir);
			drawer.drawLine(v, w);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
