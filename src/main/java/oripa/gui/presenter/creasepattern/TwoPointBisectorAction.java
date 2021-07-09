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
	public void onDraw(final ObjectGraphicDrawer drawer, final PaintContextInterface context) {
		super.onDraw(drawer, context);
		drawPickCandidateVertex(drawer, context);
	}
}
