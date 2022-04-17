package oripa.gui.presenter.creasepattern;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.segment.SelectingFirstVertexForSegment;

public class TwoPointSegmentAction extends AbstractGraphicMouseAction {

	public TwoPointSegmentAction() {
		setActionState(new SelectingFirstVertexForSegment());
	}

	@Override
	public void destroy(final PaintContext context) {
		super.destroy(context);
	}

	@Override
	protected void recoverImpl(final PaintContext context) {
		context.clear(true);
		setActionState(new SelectingFirstVertexForSegment());
	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {

		drawPickCandidateVertex(drawer, viewContext, paintContext);

		super.onDraw(drawer, viewContext, paintContext);

		drawTemporaryLine(drawer, viewContext, paintContext);

	}
}
