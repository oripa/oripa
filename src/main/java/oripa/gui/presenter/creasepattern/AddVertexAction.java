package oripa.gui.presenter.creasepattern;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.addvertex.AddingVertex;
import oripa.gui.view.creasepattern.ObjectGraphicDrawer;
import oripa.vecmath.Vector2d;

public class AddVertexAction extends AbstractGraphicMouseAction {

	public AddVertexAction() {
		setEditMode(EditMode.VERTEX);

		setActionState(new AddingVertex());
	}

	@Override
	public Vector2d onMove(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {
		return super.onMove(viewContext, paintContext, true);
	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {

		super.onDraw(drawer, viewContext, paintContext);

		drawPickCandidateVertex(drawer, viewContext, paintContext);
	}
}
