package oripa.gui.presenter.creasepattern;

import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.pbisec.SelectingFirstVertexForBisector;

public class TwoPointBisectorAction extends GraphicMouseAction {

	public TwoPointBisectorAction() {
		setActionState(new SelectingFirstVertexForBisector());
	}

	@Override
	public void destroy(final PaintContextInterface context) {
		super.destroy(context);
		setActionState(new SelectingFirstVertexForBisector());
	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContextInterface paintContext) {
		super.onDraw(drawer, viewContext, paintContext);
		drawPickCandidateVertex(drawer, viewContext, paintContext);
	}
}
