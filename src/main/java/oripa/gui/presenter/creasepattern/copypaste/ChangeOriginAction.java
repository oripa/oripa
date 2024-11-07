package oripa.gui.presenter.creasepattern.copypaste;

import java.util.Collection;
import java.util.Optional;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.copypaste.SelectionOriginHolder;
import oripa.gui.presenter.creasepattern.AbstractGraphicMouseAction;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.presenter.creasepattern.GraphicMouseAction;
import oripa.gui.presenter.creasepattern.geometry.NearestItemFinder;
import oripa.gui.view.creasepattern.ObjectGraphicDrawer;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

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
	public void doAction(final PaintContext context, final boolean differntAction) {

	}

	@Override
	public void undo(final PaintContext context) {
	}

	@Override
	public Optional<Vector2d> onMove(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {
		var closeVertexOpt = NearestItemFinder.pickVertexFromPickedLines(viewContext, paintContext);
		paintContext.setCandidateVertexToPick(closeVertexOpt.orElse(null));

		closeVertexOpt.ifPresent(closeVertex -> holder.setOrigin(closeVertex));

		return closeVertexOpt;
	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {
		super.onDraw(drawer, viewContext, paintContext);

		Collection<OriLine> lines = paintContext.getPickedLines();

		drawer.selectAssistLineColor();

		for (OriLine line : lines) {
			this.drawVertex(drawer, viewContext, line.getP0());
			this.drawVertex(drawer, viewContext, line.getP1());
		}

		this.drawPickCandidateVertex(drawer, viewContext, paintContext);
	}
}
