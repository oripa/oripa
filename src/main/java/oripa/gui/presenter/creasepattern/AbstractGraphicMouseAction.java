package oripa.gui.presenter.creasepattern;

import javax.vecmath.Vector2d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.paint.ActionState;
import oripa.domain.paint.BasicUndo;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.geometry.NearestItemFinder;
import oripa.value.OriLine;

public abstract class AbstractGraphicMouseAction implements GraphicMouseActionInterface {

	private static Logger logger = LoggerFactory.getLogger(AbstractGraphicMouseAction.class);

	private EditMode editMode = EditMode.INPUT;
	private boolean needSelect = false;
	private ActionState state;

	protected final void setActionState(final ActionState state) {
		this.state = state;
	}

	protected final ActionState getActionState() {
		return state;
	}

	@Override
	public final boolean needSelect() {
		return needSelect;
	}

	protected final void setNeedSelect(final boolean selectable) {
		this.needSelect = selectable;
	}

	protected final void setEditMode(final EditMode mode) {
		editMode = mode;
	}

	@Override
	public final EditMode getEditMode() {
		return editMode;
	}

	/**
	 * define action on destroy. default does clear context selection keeping
	 * line-selected marks.
	 *
	 * @param context
	 */
	@Override
	public void destroy(final PaintContext context) {
		context.clear(false);
	}

	/**
	 * This method is called at the first step of
	 * {@link #recover(PaintContext)}. After this method is done,
	 * {@code recover()} resets the {@code .selected} property of all lines in
	 * crease pattern if {@link #needSelect()} is false.
	 *
	 * @param context
	 */
	protected void recoverImpl(final PaintContext context) {
	}

	/**
	 * calls {@link #recoverImpl(PaintContext)}, and if
	 * {@link #needSelect()} returns false, then calls
	 * {@code context.getPainter().resetSelectedOriLines()}.
	 */
	@Override
	public final void recover(final PaintContext context) {

		recoverImpl(context);

		if (!needSelect()) {
			context.getPainter().resetSelectedOriLines();
		}
	}

	@Override
	public GraphicMouseActionInterface onLeftClick(
			final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {
		var clickPoint = paintContext.getLogicalMousePoint();

		doAction(paintContext, clickPoint, differentAction);
		return this;
	}

	@Override
	public void doAction(final PaintContext context, final Vector2d point,
			final boolean differntAction) {

		state = state.doAction(context,
				point, differntAction);

	}

	@Override
	public void onRightClick(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean doSpecial) {

		logger.info(this.getClass().getName());
		logger.info("before undo " + paintContext.toString());

		undo(paintContext);

		logger.info("after undo " + paintContext.toString());

	}

	@Override
	public void undo(final PaintContext context) {
		state = BasicUndo.undo(state, context);
	}

	/*
	 * (non Javadoc)
	 *
	 * @see
	 * oripa.domain.paint.GraphicMouseActionInterface#redo(oripa.domain.paint.
	 * PaintContextInterface)
	 */
	@Override
	public void redo(final PaintContext context) {
		if (!context.creasePatternUndo().canRedo()) {
			return;
		}
		destroy(context);
		recover(context);
		context.creasePatternUndo().redo();
	}

	@Override
	public Vector2d onMove(
			final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {

		setCandidateVertexOnMove(viewContext, paintContext, differentAction);
		setCandidateLineOnMove(viewContext, paintContext);

		return paintContext.getCandidateVertexToPick();
	}

	protected final void setCandidateVertexOnMove(
			final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {
		paintContext.setCandidateVertexToPick(
				NearestItemFinder.pickVertex(
						paintContext, differentAction));
	}

	protected final void setCandidateLineOnMove(final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {
		paintContext.setCandidateLineToPick(
				NearestItemFinder.pickLine(
						paintContext));
	}

	@Override
	public void onPress(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {

	}

	@Override
	public void onDrag(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {

	}

	@Override
	public void onRelease(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {

	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {
		drawPickedLines(drawer, viewContext, paintContext);
		drawPickedVertices(drawer, viewContext, paintContext, paintContext.getLineTypeOfNewLines());

	}

//	protected ElementSelector getElementSelector() {
//		return selector;
//	}
//
//	protected GraphicItemConverter getGraphicItemConverter() {
//		return converter;
//	}

	private void drawPickedLines(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {
		for (OriLine line : paintContext.getPickedLines()) {
			drawer.selectSelectedItemColor();
			drawer.selectSelectedLineStroke(
					paintContext.getScale(), viewContext.isZeroLineWidth());

			drawLine(drawer, line);
		}

	}

	private void drawPickedVertices(final ObjectGraphicDrawer drawer,
			final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final OriLine.Type lineType) {

		for (Vector2d vertex : paintContext.getPickedVertices()) {
			drawer.selectColor(lineType);

			drawVertex(drawer, viewContext, paintContext, vertex);
		}
	}

	/**
	 * Draws the given vertex as an small rectangle.
	 */
	protected void drawVertex(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext,
			final Vector2d vertex) {
		double scale = paintContext.getScale();
		drawer.selectMouseActionVertexSize(scale);

		drawer.drawVertex(vertex);
	}

	protected void drawPickCandidateVertex(final ObjectGraphicDrawer drawer,
			final CreasePatternViewContext viewContext, final PaintContext paintContext) {
		Vector2d candidate = paintContext.getCandidateVertexToPick();
		if (candidate != null) {
			drawer.selectCandidateItemColor();
			drawVertex(drawer, viewContext, paintContext, candidate);
		}
	}

	protected void drawLine(final ObjectGraphicDrawer drawer, final OriLine line) {
		drawer.drawLine(line);
	}

	protected void drawLine(final ObjectGraphicDrawer drawer, final Vector2d p0, final Vector2d p1) {
		drawer.drawLine(p0, p1);

	}

	protected void drawPickCandidateLine(final ObjectGraphicDrawer drawer,
			final CreasePatternViewContext viewContext, final PaintContext paintContext) {
		OriLine candidate = paintContext.getCandidateLineToPick();
		if (candidate != null) {
			drawer.selectCandidateItemColor();
			drawer.selectCandidateLineStroke(
					paintContext.getScale(), viewContext.isZeroLineWidth());

			drawLine(drawer, candidate);
		}
	}

	/**
	 * draws the line between the most recently selected vertex and the closest
	 * vertex sufficiently to the mouse cursor. if every vertex is far from
	 * cursor, this method uses the cursor point instead of close vertex.
	 *
	 * @param drawer
	 * @param context
	 */
	protected void drawTemporaryLine(final ObjectGraphicDrawer drawer,
			final CreasePatternViewContext viewContext, final PaintContext paintContext) {

		if (paintContext.getVertexCount() == 0) {
			return;
		}

		Vector2d picked = paintContext.peekVertex();

		drawer.selectColor(paintContext.getLineTypeOfNewLines());

		drawer.selectStroke(paintContext.getLineTypeOfNewLines(),
				paintContext.getScale(), viewContext.isZeroLineWidth());

		drawLine(drawer, picked,
				NearestItemFinder.getCandidateVertex(paintContext, true));

	}

}
