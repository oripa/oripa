package oripa.gui.presenter.creasepattern;

import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.triangle.SelectingVertexForTriangleSplit;

public class TriangleSplitAction extends GraphicMouseAction {

	public TriangleSplitAction() {
		setActionState(new SelectingVertexForTriangleSplit());
	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContextInterface paintContext) {

		super.onDraw(drawer, viewContext, paintContext);

		drawPickCandidateVertex(drawer, viewContext, paintContext);
	}
}
