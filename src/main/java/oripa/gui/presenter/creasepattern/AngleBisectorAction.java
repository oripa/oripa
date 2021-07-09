package oripa.gui.presenter.creasepattern;

import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.bisector.SelectingVertexForBisector;

public class AngleBisectorAction extends GraphicMouseAction {

	public AngleBisectorAction() {
		setActionState(new SelectingVertexForBisector());
	}

	@Override
	public void destroy(final PaintContextInterface context) {
		super.destroy(context);
		setActionState(new SelectingVertexForBisector());
	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext, final PaintContextInterface paintContext) {

		super.onDraw(drawer, viewContext, paintContext);

		if (paintContext.getVertexCount() < 3) {
			drawPickCandidateVertex(drawer, viewContext, paintContext);
		} else {
			drawPickCandidateLine(drawer, viewContext, paintContext);
		}
	}
}
