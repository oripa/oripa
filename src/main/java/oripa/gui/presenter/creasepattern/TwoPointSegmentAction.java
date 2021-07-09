package oripa.gui.presenter.creasepattern;

import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.segment.SelectingFirstVertexForSegment;

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
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContextInterface paintContext) {

		super.onDraw(drawer, viewContext, paintContext);

		drawTemporaryLine(drawer, viewContext, paintContext);
		drawPickCandidateVertex(drawer, viewContext, paintContext);

	}
}
