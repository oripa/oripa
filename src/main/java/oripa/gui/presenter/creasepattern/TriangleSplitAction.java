package oripa.gui.presenter.creasepattern;

import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.triangle.SelectingVertexForTriangleSplit;

public class TriangleSplitAction extends GraphicMouseAction {

	public TriangleSplitAction() {
		setActionState(new SelectingVertexForTriangleSplit());
	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final PaintContextInterface context) {

		super.onDraw(drawer, context);

		drawPickCandidateVertex(drawer, context);
	}
}
