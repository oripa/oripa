package oripa.gui.presenter.creasepattern;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.bisector.SelectingVertexForBisector;

public class AngleBisectorAction extends AbstractGraphicMouseAction {

	public AngleBisectorAction() {
		setActionState(new SelectingVertexForBisector());
	}

	@Override
	public void destroy(final PaintContext context) {
		super.destroy(context);
		setActionState(new SelectingVertexForBisector());
	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {

		if (paintContext.getVertexCount() < 3) {
			drawPickCandidateVertex(drawer, viewContext, paintContext);
		} else {
			drawPickCandidateLine(drawer, viewContext, paintContext);
		}

		super.onDraw(drawer, viewContext, paintContext);
	}
}
