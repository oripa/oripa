package oripa.gui.presenter.creasepattern;

import java.util.Collection;

import javax.vecmath.Vector2d;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.copypaste.SelectionOriginHolder;
import oripa.domain.paint.geometry.NearestItemFinder;
import oripa.value.OriLine;

public class ChangeOriginAction extends AbstractGraphicMouseAction {

	private final SelectionOriginHolder holder;

	/**
	 * Constructor
	 */
	public ChangeOriginAction(final SelectionOriginHolder holder) {
		this.holder = holder;
	}

	@Override
	public GraphicMouseAction onLeftClick(final CreasePatternViewContext viewContext,
			final PaintContext paintContext,
			final boolean keepDoing) {

		return this;
	}

	@Override
	public void doAction(final PaintContext context, final Vector2d point,
			final boolean differntAction) {

	}

	@Override
	public void undo(final PaintContext context) {
	}

	@Override
	public Vector2d onMove(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {
		Vector2d closeVertex = NearestItemFinder.pickVertexFromPickedLines(paintContext);
		paintContext.setCandidateVertexToPick(closeVertex);

		if (closeVertex != null) {
			holder.setOrigin(closeVertex);
		}

		return closeVertex;
	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {
		super.onDraw(drawer, viewContext, paintContext);

		Collection<OriLine> lines = paintContext.getPickedLines();

		drawer.selectAssistLineColor();

		for (OriLine line : lines) {
			this.drawVertex(drawer, viewContext, paintContext, line.p0);
			this.drawVertex(drawer, viewContext, paintContext, line.p1);
		}

		this.drawPickCandidateVertex(drawer, viewContext, paintContext);
	}
}
