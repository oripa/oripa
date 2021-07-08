package oripa.domain.paint.segment;

import java.awt.geom.AffineTransform;

import oripa.domain.paint.ObjectGraphicDrawer;
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
	public void onDrag(final PaintContextInterface context, final boolean differentAction) {

	}

	@Override
	public void onRelease(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {

	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final PaintContextInterface context) {

		super.onDraw(drawer, context);

		drawTemporaryLine(drawer, context);
		drawPickCandidateVertex(drawer, context);

	}

	@Override
	public void onPress(final PaintContextInterface context, final boolean differentAction) {

	}

}
