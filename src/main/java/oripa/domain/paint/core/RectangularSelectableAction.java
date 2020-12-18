package oripa.domain.paint.core;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.cptool.RectangleClipper;
import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.domain.paint.PaintContextInterface;
import oripa.value.OriLine;

public abstract class RectangularSelectableAction extends GraphicMouseAction {
	private static final Logger logger = LoggerFactory.getLogger(RectangularSelectableAction.class);

	private Point2D.Double startPoint = null;
	private Point2D.Double draggingPoint = null;

	@Override
	public void onPress(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {
		startPoint = context.getLogicalMousePoint();
	}

	@Override
	public void onDrag(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {

		draggingPoint = context.getLogicalMousePoint();

	}

	@Override
	public void onRelease(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {

		if (startPoint != null && draggingPoint != null) {
			selectByRectangularArea(context);
		}

		startPoint = null;
		draggingPoint = null;

	}

	/**
	 * defines what to do for the selected lines.
	 *
	 * @param selectedLines
	 *            lines selected by dragging
	 * @param context
	 */
	protected abstract void afterRectangularSelection(
			Collection<OriLine> selectedLines, PaintContextInterface context);

	protected final void selectByRectangularArea(final PaintContextInterface context) {
		Collection<OriLine> selectedLines = new ArrayList<>();

		try {
			RectangleClipper clipper = new RectangleClipper(
					Math.min(startPoint.x, draggingPoint.x),
					Math.min(startPoint.y, draggingPoint.y),
					Math.max(startPoint.x, draggingPoint.x),
					Math.max(startPoint.y, draggingPoint.y));

			CreasePatternInterface creasePattern = context.getCreasePattern();
			selectedLines = clipper.selectByArea(creasePattern);
		} catch (Exception ex) {
			logger.error("failed to select rectangularly", ex);
		}

		afterRectangularSelection(selectedLines, context);
	}

	@Override
	public void onDraw(final Graphics2D g2d, final PaintContextInterface context) {

		super.onDraw(g2d, context);

		if (startPoint != null && draggingPoint != null) {
			var selector = getElementSelector();
			g2d.setStroke(selector.createAreaSelectionStroke(context.getScale()));
			g2d.setColor(selector.getAreaSelectionColor());
			double sx = Math.min(startPoint.x, draggingPoint.x);
			double sy = Math.min(startPoint.y, draggingPoint.y);
			double w = Math.abs(startPoint.x - draggingPoint.x);
			double h = Math.abs(startPoint.y - draggingPoint.y);
			g2d.draw(new Rectangle2D.Double(sx, sy, w, h));

		}

	}

}
