package oripa.gui.presenter.creasepattern;

import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.addvertex.AddingVertex;

public class AddVertexAction extends GraphicMouseAction {

	public AddVertexAction() {
		setEditMode(EditMode.VERTEX);

		setActionState(new AddingVertex());

	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final PaintContextInterface context) {

		super.onDraw(drawer, context);

		drawPickCandidateVertex(drawer, context);
	}
}
