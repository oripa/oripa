package oripa.gui.presenter.creasepattern;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.deletevertex.DeletingVertex;

public class DeleteVertexAction extends GraphicMouseAction {

	public DeleteVertexAction() {
		setEditMode(EditMode.VERTEX);

		setActionState(new DeletingVertex());

	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {

		super.onDraw(drawer, viewContext, paintContext);

		drawPickCandidateVertex(drawer, viewContext, paintContext);
	}
}
