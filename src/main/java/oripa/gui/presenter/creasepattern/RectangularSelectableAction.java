package oripa.gui.presenter.creasepattern;

import java.util.ArrayList;
import java.util.Collection;

import javax.vecmath.Vector2d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.cptool.RectangleClipper;
import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.domain.paint.PaintContext;
import oripa.value.OriLine;

public abstract class RectangularSelectableAction extends AbstractGraphicMouseAction {
	private static final Logger logger = LoggerFactory.getLogger(RectangularSelectableAction.class);

	private Vector2d startPoint = null;
	private Vector2d draggingPoint = null;

	@Override
	public void onPress(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {
		startPoint = paintContext.getLogicalMousePoint();
	}

	@Override
	public void onDrag(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {

		draggingPoint = paintContext.getLogicalMousePoint();

	}

	@Override
	public void onRelease(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {

		if (startPoint != null && draggingPoint != null) {
			selectByRectangularArea(viewContext, paintContext);
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
			Collection<OriLine> selectedLines, final CreasePatternViewContext viewContext,
			final PaintContext paintContext);

	protected final void selectByRectangularArea(final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {
		Collection<OriLine> selectedLines = new ArrayList<>();

		try {
			RectangleClipper clipper = new RectangleClipper(
					Math.min(startPoint.x, draggingPoint.x),
					Math.min(startPoint.y, draggingPoint.y),
					Math.max(startPoint.x, draggingPoint.x),
					Math.max(startPoint.y, draggingPoint.y));

			CreasePatternInterface creasePattern = paintContext.getCreasePattern();
			selectedLines = clipper.selectByArea(creasePattern);
		} catch (Exception ex) {
			logger.error("failed to select rectangularly", ex);
		}

		afterRectangularSelection(selectedLines, viewContext, paintContext);
	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {

		super.onDraw(drawer, viewContext, paintContext);

		if (startPoint != null && draggingPoint != null) {
			drawer.selectAreaSelectionStroke(paintContext.getScale());
			drawer.selectAreaSelectionColor();

			drawer.drawRectangle(startPoint, draggingPoint);
		}

	}

}
