package oripa.domain.paint.segment;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.GraphicMouseAction;

public class TwoPointSegmentAction extends GraphicMouseAction {

	public TwoPointSegmentAction() {
		setActionState(new SelectingFirstVertexForSegment());
	}

	@Override
	public void destroy(final PaintContextInterface context) {
		super.destroy(context);
	}

	@Override
	protected void recoverImpl(final PaintContextInterface context) {
		context.clear(true);
		setActionState(new SelectingFirstVertexForSegment());
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

		drawTemporaryLine(g2d, context);
		drawPickCandidateVertex(g2d, context);

	}

	@Override
	public void onPress(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {

	}

}
