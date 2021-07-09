package oripa.gui.presenter.creasepattern;

import javax.vecmath.Vector2d;

import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.symmetric.SelectingVertexForSymmetric;

public class SymmetricalLineAction extends GraphicMouseAction {

	public SymmetricalLineAction() {
		setActionState(new SelectingVertexForSymmetric());
	}

	@Override
	public Vector2d onMove(
			final CreasePatternViewContext viewContext, final PaintContextInterface paintContext,
			final boolean differentAction) {

		if (paintContext.getVertexCount() < 2) {
			return super.onMove(viewContext, paintContext, differentAction);
		}

		// enable auto-walk selection only
		return super.onMove(viewContext, paintContext, false);
	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContextInterface paintContext) {

		super.onDraw(drawer, viewContext, paintContext);

		drawPickCandidateVertex(drawer, viewContext, paintContext);
	}
}
