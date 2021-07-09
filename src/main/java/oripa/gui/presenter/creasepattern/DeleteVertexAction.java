package oripa.gui.presenter.creasepattern;

import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.deletevertex.DeletingVertex;

public class DeleteVertexAction extends GraphicMouseAction {

	public DeleteVertexAction() {
		setEditMode(EditMode.VERTEX);

		setActionState(new DeletingVertex());

	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final PaintContextInterface context) {

		super.onDraw(drawer, context);

		drawPickCandidateVertex(drawer, context);
	}
}
