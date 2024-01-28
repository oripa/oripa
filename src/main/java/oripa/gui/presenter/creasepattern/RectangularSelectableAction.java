package oripa.gui.presenter.creasepattern;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.cptool.RectangleClipper;
import oripa.domain.creasepattern.CreasePattern;
import oripa.domain.paint.PaintContext;
import oripa.geom.RectangleDomain;
import oripa.gui.view.creasepattern.ObjectGraphicDrawer;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

public abstract class RectangularSelectableAction extends AbstractGraphicMouseAction {
	private static final Logger logger = LoggerFactory.getLogger(RectangularSelectableAction.class);

	private Vector2d startPoint = null;
	private Vector2d draggingPoint = null;

	@Override
	public void onPress(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {
		startPoint = viewContext.getLogicalMousePoint();
	}

	@Override
	public void onDrag(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {

		draggingPoint = viewContext.getLogicalMousePoint();

	}

	@Override
	public boolean isUsingCtrlKeyOnDrag() {
		return draggingPoint != null;
	}

	@Override
	public void onRelease(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {

		if (startPoint != null && draggingPoint != null) {
			selectByRectangularArea(viewContext, paintContext, differentAction);
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
			final PaintContext paintContext, boolean differentAction);

	private void selectByRectangularArea(final CreasePatternViewContext viewContext,
			final PaintContext paintContext, final boolean differentAction) {
		Collection<OriLine> selectedLines = new ArrayList<>();

		try {
			var domain = RectangleDomain.createFromPoints(List.of(startPoint, draggingPoint));
			RectangleClipper clipper = new RectangleClipper(domain);

			CreasePattern creasePattern = paintContext.getCreasePattern();
			selectedLines = clipper.selectByArea(creasePattern);
		} catch (Exception ex) {
			logger.error("failed to select rectangularly", ex);
		}

		afterRectangularSelection(selectedLines, viewContext, paintContext, differentAction);
	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {

		super.onDraw(drawer, viewContext, paintContext);

		if (startPoint != null && draggingPoint != null) {
			drawer.selectAreaSelectionStroke(viewContext.getScale());
			drawer.selectAreaSelectionColor();

			drawer.drawRectangle(startPoint, draggingPoint);
		}

	}

}
