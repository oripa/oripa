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
			final PaintContextInterface context, final boolean differentAction) {

		if (context.getVertexCount() < 2) {
			return super.onMove(context, differentAction);
		}

		// enable auto-walk selection only
		return super.onMove(context, false);
	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final PaintContextInterface context) {

		super.onDraw(drawer, context);

		drawPickCandidateVertex(drawer, context);
	}
}
