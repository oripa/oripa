package oripa.gui.presenter.creasepattern;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.vertical.SelectingVertexForVertical;

public class VerticalLineAction extends GraphicMouseAction {

	public VerticalLineAction() {
		setActionState(new SelectingVertexForVertical());
	}

	@Override
	public void destroy(final PaintContext context) {
		super.destroy(context);
		setActionState(new SelectingVertexForVertical());
	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {

		super.onDraw(drawer, viewContext, paintContext);

		if (paintContext.getVertexCount() == 0) {
			drawPickCandidateVertex(drawer, viewContext, paintContext);
		} else if (paintContext.getVertexCount() == 1) {
			drawPickCandidateLine(drawer, viewContext, paintContext);
		}
	}
}
