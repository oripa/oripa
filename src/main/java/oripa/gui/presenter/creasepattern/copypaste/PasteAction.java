package oripa.gui.presenter.creasepattern.copypaste;

import java.util.Optional;

import oripa.domain.creasepattern.CreasePattern;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.copypaste.PastingOnVertex;
import oripa.domain.paint.copypaste.SelectionOriginHolder;
import oripa.domain.paint.copypaste.ShiftedLineFactory;
import oripa.gui.presenter.creasepattern.AbstractGraphicMouseAction;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.creasepattern.geometry.NearestItemFinder;
import oripa.gui.presenter.creasepattern.geometry.NearestVertexFinder;
import oripa.gui.view.creasepattern.ObjectGraphicDrawer;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

public class PasteAction extends AbstractGraphicMouseAction {

	private final SelectionOriginHolder originHolder;
	private final ShiftedLineFactory factory = new ShiftedLineFactory();

	public PasteAction(final SelectionOriginHolder originHolder) {
		this.originHolder = originHolder;

		setEditMode(EditMode.INPUT);
		setNeedSelect(true);

		setActionState(new PastingOnVertex(originHolder));
	}

	@Override
	protected void recoverImpl(final PaintContext context) {
		context.clear(false);

		context.startPasting();

		CreasePattern creasePattern = context.getCreasePattern();

		creasePattern.stream()
				.filter(line -> line.selected)
				.forEach(line -> context.pushLine(line));
	}

	/**
	 * Clear context and mark lines as unselected.
	 */
	@Override
	public void destroy(final PaintContext context) {
		context.clear(true);
		context.finishPasting();
	}

	@Override
	public Optional<Vector2d> onMove(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {

		setCandidateVertexOnMove(viewContext, paintContext, false);
		var closeVertexOpt = paintContext.getCandidateVertexToPick();

		// to get the vertex which disappeared by cutting.
		var closeVertexOfLinesOpt = NearestItemFinder.pickVertexFromPickedLines(viewContext, paintContext);

		var current = viewContext.getLogicalMousePoint();
		var closeVertex = closeVertexOpt
				.map(closeV -> closeVertexOfLinesOpt
						.map(closeVertexOfLines -> NearestVertexFinder.findNearestOf(
								current, closeV, closeVertexOfLines))
						.orElse(closeV))
				.orElse(closeVertexOfLinesOpt.orElse(current));

		paintContext.setCandidateVertexToPick(closeVertex);

		return Optional.ofNullable(closeVertex);
	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {

		super.onDraw(drawer, viewContext, paintContext);

		drawPickCandidateVertex(drawer, viewContext, paintContext);

		Vector2d origin = originHolder.getOrigin(paintContext);

		if (origin == null) {
			return;
		}

		drawer.selectSelectedItemColor();
		drawVertex(drawer, viewContext, paintContext, origin);

		var candidateVertexOpt = paintContext.getCandidateVertexToPick();

		var point = candidateVertexOpt.orElse(viewContext.getLogicalMousePoint());
		var offset = factory.createOffset(origin, point);

		drawer.selectAssistLineColor();

		// shift and draw the lines to be pasted.
		for (OriLine l : paintContext.getPickedLines()) {
			var shifted = factory.createShiftedLine(l, offset.getX(), offset.getY());
			drawer.drawLine(shifted);
		}
	}
}
