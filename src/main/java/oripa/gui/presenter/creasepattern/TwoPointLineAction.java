package oripa.gui.presenter.creasepattern;

import java.util.Optional;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.line.SelectingFirstVertexForLine;
import oripa.gui.presenter.creasepattern.geometry.NearestItemFinder;
import oripa.gui.view.creasepattern.ObjectGraphicDrawer;
import oripa.vecmath.Vector2d;

public class TwoPointLineAction extends AbstractGraphicMouseAction {

	public TwoPointLineAction() {
		setActionState(new SelectingFirstVertexForLine());
	}

	@Override
	public void destroy(final PaintContext context) {
		super.destroy(context);
	}

	@Override
	protected void recoverImpl(final PaintContext context) {
		context.clear(true);
		setActionState(new SelectingFirstVertexForLine());

	}

	@Override
	public Optional<Vector2d> onMove(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {
		if (paintContext.getVertexCount() < 2) {
			return super.onMove(viewContext, paintContext, differentAction);
		}

		var snapPointOpt = NearestItemFinder.getNearestInSnapPoints(viewContext, paintContext);

		snapPointOpt.ifPresent(snapPoint -> paintContext.setCandidateVertexToPick(snapPoint));

		return snapPointOpt;
	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {
		if (paintContext.getVertexCount() >= 2) {
			drawSnapPoints(drawer, viewContext, paintContext);
		}

		if (paintContext.getVertexCount() == 3) {
			drawTemporaryLine(drawer, viewContext, paintContext);
		}

		drawPickCandidateVertex(drawer, viewContext, paintContext);

		super.onDraw(drawer, viewContext, paintContext);
	}

}
