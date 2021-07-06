package oripa.domain.paint.copypaste;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import javax.vecmath.Vector2d;

import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.domain.paint.EditMode;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.GraphicMouseAction;
import oripa.domain.paint.geometry.NearestItemFinder;
import oripa.domain.paint.geometry.NearestVertexFinder;
import oripa.domain.paint.util.GraphicItemConverter;
import oripa.value.OriLine;

public class PasteAction extends GraphicMouseAction {

	private final SelectionOriginHolder originHolder;
	private final ShiftedLineFactory factory = new ShiftedLineFactory();

	public PasteAction(final SelectionOriginHolder originHolder) {
		this.originHolder = originHolder;

		setEditMode(EditMode.INPUT);
		setNeedSelect(true);

		setActionState(new PastingOnVertex(originHolder));
	}

	@Override
	protected void recoverImpl(final PaintContextInterface context) {
		context.clear(false);

		context.startPasting();

		CreasePatternInterface creasePattern = context.getCreasePattern();

		creasePattern.stream()
				.filter(line -> line.selected)
				.forEach(line -> context.pushLine(line));
	}

	/**
	 * Clear context and mark lines as unselected.
	 */
	@Override
	public void destroy(final PaintContextInterface context) {
		context.clear(true);
		context.finishPasting();
	}

	@Override
	public void onDrag(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {

	}

	@Override
	public void onRelease(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {

	}

	@Override
	public Vector2d onMove(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {

		setCandidateVertexOnMove(context, differentAction);
		Vector2d closeVertex = context.getCandidateVertexToPick();

		// to get the vertex which disappeared by cutting.
		Vector2d closeVertexOfLines = NearestItemFinder.pickVertexFromPickedLines(context);

		if (closeVertex == null) {
			closeVertex = closeVertexOfLines;
		}

		Point2D.Double current = context.getLogicalMousePoint();
		if (closeVertex != null && closeVertexOfLines != null) {
			// get the nearest to current
			closeVertex = NearestVertexFinder.findNearestOf(
					current, closeVertex, closeVertexOfLines);

		}

		context.setCandidateVertexToPick(closeVertex);

		return closeVertex;
	}

	@Override
	public void onDraw(final Graphics2D g2d, final PaintContextInterface context) {

		super.onDraw(g2d, context);

		drawPickCandidateVertex(g2d, context);

		Vector2d origin = originHolder.getOrigin(context);

		if (origin == null) {
			return;
		}

		var selector = getElementSelector();
		g2d.setColor(selector.getSelectedItemColor());
		drawVertex(g2d, context, origin);

		var candidateVertex = context.getCandidateVertexToPick();

		Vector2d offset = candidateVertex == null ? factory.createOffset(origin, context.getLogicalMousePoint())
				: factory.createOffset(origin, candidateVertex);

		g2d.setColor(selector.getAssistLineColor());

		// shift and draw the lines to be pasted.
		for (OriLine l : context.getPickedLines()) {
			var shifted = factory.createShiftedLine(l, offset.x, offset.y);
			g2d.draw(GraphicItemConverter.toLine2D(shifted));
		}
	}

	@Override
	public void onPress(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {
	}

}
