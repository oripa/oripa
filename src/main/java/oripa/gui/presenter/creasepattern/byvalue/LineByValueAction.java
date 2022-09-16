package oripa.gui.presenter.creasepattern.byvalue;

import javax.vecmath.Vector2d;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.byvalue.SelectingVertexToDrawLine;
import oripa.domain.paint.byvalue.ByValueContext;
import oripa.gui.presenter.creasepattern.AbstractGraphicMouseAction;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.view.creasepattern.ObjectGraphicDrawer;

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
