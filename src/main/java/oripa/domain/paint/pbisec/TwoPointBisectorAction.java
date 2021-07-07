package oripa.domain.paint.pbisec;

import java.awt.geom.AffineTransform;

import oripa.domain.paint.ObjectGraphicDrawer;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.GraphicMouseAction;

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
