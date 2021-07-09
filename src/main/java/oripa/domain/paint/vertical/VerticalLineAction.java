package oripa.domain.paint.vertical;

import oripa.domain.paint.ObjectGraphicDrawer;
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
	public void onDraw(final ObjectGraphicDrawer drawer, final PaintContextInterface context) {

		super.onDraw(drawer, context);

		if (context.getVertexCount() == 0) {
			drawPickCandidateVertex(drawer, context);
		} else if (context.getVertexCount() == 1) {
			drawPickCandidateLine(drawer, context);
		}
	}
}
