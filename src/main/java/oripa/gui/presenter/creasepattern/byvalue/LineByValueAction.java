package oripa.gui.presenter.creasepattern.byvalue;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.byvalue.ByValueContext;
import oripa.domain.paint.byvalue.SelectingVertexToDrawLine;
import oripa.gui.presenter.creasepattern.AbstractGraphicMouseAction;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.view.creasepattern.ObjectGraphicDrawer;
import oripa.vecmath.Vector2d;

public class LineByValueAction extends AbstractGraphicMouseAction {

	private final ByValueContext byValueContext;

	public LineByValueAction(final ByValueContext byValueContext) {
		super();
		setActionState(new SelectingVertexToDrawLine(byValueContext));

		this.byValueContext = byValueContext;
	}

	@Override
	protected void recoverImpl(final PaintContext context) {
		context.clear(true);
	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {

		drawPickCandidateVertex(drawer, viewContext, paintContext);

		super.onDraw(drawer, viewContext, paintContext);

		Vector2d v = paintContext.getCandidateVertexToPick();
		if (v == null) {
			return;
		}
		try {
			var angle = byValueContext.getAngle();
			var length = byValueContext.getLength();

			var radianAngle = Math.toRadians(angle);

			drawer.selectColor(paintContext.getLineTypeOfNewLines());
			drawer.selectStroke(
					paintContext.getLineTypeOfNewLines(),
					viewContext.getScale(), viewContext.isZeroLineWidth());

			Vector2d dir = new Vector2d(Math.cos(radianAngle), -Math.sin(radianAngle)).multiply(length);
			var w = v.add(dir);
			drawer.drawLine(v, w);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
