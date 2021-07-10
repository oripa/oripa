package oripa.gui.presenter.creasepattern;

import javax.vecmath.Vector2d;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.byvalue.SelectingVertexToDrawLine;
import oripa.domain.paint.byvalue.ValueSetting;

public class LineByValueAction extends GraphicMouseAction {

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
	public void onDraw(final ObjectGraphicDrawer g2d, final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {
		super.onDraw(g2d, viewContext, paintContext);

		drawPickCandidateVertex(g2d, viewContext, paintContext);
		Vector2d v = paintContext.getCandidateVertexToPick();
		if (v == null) {
			return;
		}
		try {
			var angle = valueSetting.getAngle();
			var length = valueSetting.getLength();

			var radianAngle = Math.toRadians(angle);

			g2d.selectColor(paintContext.getLineTypeOfNewLines());
			g2d.selectStroke(
					paintContext.getLineTypeOfNewLines(),
					paintContext.getScale(), viewContext.isZeroLineWidth());

			Vector2d dir = new Vector2d(Math.cos(radianAngle), -Math.sin(radianAngle));
			dir.scale(length);
			var w = new Vector2d(v);
			w.add(dir);
			g2d.drawLine(v, w);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
