package oripa.domain.paint.vertical;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.GraphicMouseAction;

public class VerticalLineAction extends GraphicMouseAction {

	public VerticalLineAction() {
		setActionState(new SelectingVertexForVertical());
	}

	@Override
	public void destroy(final PaintContextInterface context) {
		super.destroy(context);
		setActionState(new SelectingVertexForVertical());
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
	public void onDraw(final Graphics2D g2d, final PaintContextInterface context) {

		super.onDraw(g2d, context);

		if (context.getVertexCount() == 0) {
			drawPickCandidateVertex(g2d, context);
		} else if (context.getVertexCount() == 1) {
			drawPickCandidateLine(g2d, context);
		}
	}

	@Override
	public void onPress(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {

	}

}
