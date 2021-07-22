package oripa.gui.presenter.creasepattern;

import javax.vecmath.Vector2d;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.pbisec.SelectingFirstVertexForBisector;
import oripa.gui.presenter.creasepattern.geometry.NearestItemFinder;

public class TwoPointBisectorAction extends AbstractGraphicMouseAction {

	public TwoPointBisectorAction() {
		setActionState(new SelectingFirstVertexForBisector());
	}

	@Override
	public void destroy(final PaintContext context) {
		super.destroy(context);
		setActionState(new SelectingFirstVertexForBisector());
	}

	@Override
	public Vector2d onMove(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {
		if (paintContext.getVertexCount() < 2) {
			return super.onMove(viewContext, paintContext, differentAction);
		}

		var snapPoint = NearestItemFinder.getNearestInSnapPoints(viewContext, paintContext);
		paintContext.setCandidateVertexToPick(snapPoint);
		return snapPoint;
	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {
		if (paintContext.getVertexCount() >= 2) {
			drawSnapPoints(drawer, viewContext, paintContext);
		}

		super.onDraw(drawer, viewContext, paintContext);
		drawPickCandidateVertex(drawer, viewContext, paintContext);
	}

	private void drawSnapPoints(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {
		drawer.selectAssistLineColor();

		paintContext.getSnapPoints()
				.forEach(p -> drawVertex(drawer, viewContext, paintContext, p));
	}
}
