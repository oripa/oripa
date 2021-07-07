package oripa.domain.paint.symmetric;

import java.awt.geom.AffineTransform;

import javax.vecmath.Vector2d;

import oripa.domain.paint.ObjectGraphicDrawer;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.GraphicMouseAction;

public class SymmetricalLineAction extends GraphicMouseAction {

	public SymmetricalLineAction() {
		setActionState(new SelectingVertexForSymmetric());
	}

	@Override
	public Vector2d onMove(
			final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {

		if (context.getVertexCount() < 2) {
			return super.onMove(context, affine, differentAction);
		}

		// enable auto-walk selection only
		return super.onMove(context, affine, false);
	}

	@Override
	public void onDrag(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {

	}

	@Override
	public void onRelease(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {

	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final PaintContextInterface context) {

		super.onDraw(drawer, context);

		drawPickCandidateVertex(drawer, context);
	}

	@Override
	public void onPress(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {

	}

}
