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
	public void onDraw(final ObjectGraphicDrawer drawer, final PaintContextInterface context) {

		super.onDraw(drawer, context);

		if (context.getVertexCount() < 3) {
			drawPickCandidateVertex(drawer, context);
		} else {
			drawPickCandidateLine(drawer, context);
		}
	}
}
