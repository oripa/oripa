package oripa.gui.presenter.creasepattern;

import java.util.Optional;

import oripa.domain.paint.ActionState;
import oripa.domain.paint.BasicUndo;
import oripa.domain.paint.PaintContext;
import oripa.gui.presenter.creasepattern.geometry.NearestItemFinder;
import oripa.gui.view.creasepattern.ObjectGraphicDrawer;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

/**
 * Template for mouse action implementation. The constructor of the
 * implementation should set the initial action state by
 * {@link #setActionState(ActionState)}. Typical item-drawing methods are
 * provided as protected ones.
 *
 * @author OUCHI Koji
 *
 */
public abstract class AbstractGraphicMouseAction implements GraphicMouseAction {

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
	 * Clears context selection keeping line-selected marks by default. Override
	 * if more actions are needed.
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
	 * Calls {@link #recoverImpl(PaintContext)}, and if {@link #needSelect()}
	 * returns false, then calls
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
	public void doAction(final PaintContext context, final Vector2d point,
			final boolean differntAction) {

		state = state.doAction(context,
				point, differntAction);

	}

	/**
	 * Calls {@link BasicUndo#undo(ActionState, PaintContext)} by default.
	 */
	@Override
	public void undo(final PaintContext context) {
		state = BasicUndo.undo(state, context);
	}

	/**
	 * Restores previous crease pattern if possible, and behaves as if this
	 * action is reset: this method calls {@link #destroy(PaintContext)} and
	 * {@link #recover(PaintContext)}, then executes redo of crease pattern.
	 */
	@Override
	public void redo(final PaintContext context) {
		if (!context.creasePatternUndo().canRedo()) {
			return;
		}
		destroy(context);
		recover(context);
		context.creasePatternUndo().redo();
		context.refreshCreasePattern();
	}

	/**
	 * Searches a vertex and a line close enough to the mouse cursor. The result
	 * is stored into candidateVertexToPick and candidateLineToPick properties
	 * of paintContext. Override for more details.
	 *
	 * @param viewContext
	 * @param paintContext
	 * @param differentAction
	 * @return close vertex. Empty if not found.
	 */
	@Override
	public Optional<Vector2d> onMove(
			final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {

		setCandidateVertexOnMove(viewContext, paintContext, differentAction);
		setCandidateLineOnMove(viewContext, paintContext);

		return paintContext.getCandidateVertexToPick();
	}

	/**
	 *
	 * @param viewContext
	 * @param paintContext
	 * @param differentAction
	 *            true to set vertex along line, otherwise this method will set
	 *            the vertex in crease pattern nearest to mouse point.
	 */
	protected final void setCandidateVertexOnMove(
			final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {
		Optional<Vector2d> vOpt = differentAction ? NearestItemFinder.pickVertexAlongLine(viewContext, paintContext)
				: NearestItemFinder.pickVertex(viewContext, paintContext);

		paintContext.setCandidateVertexToPick(vOpt.orElse(null));
	}

	protected final void setCandidateLineOnMove(final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {
		paintContext.setCandidateLineToPick(
				NearestItemFinder.pickLine(viewContext, paintContext).orElse(null));
	}

	/**
	 * Does nothing by default.
	 */
	@Override
	public void onPress(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {

	}

	/**
	 * Does nothing by default.
	 */
	@Override
	public void onDrag(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {

	}

	/**
	 * Does nothing by default.
	 */
	@Override
	public void onRelease(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {

	}

	/**
	 * Draws selected lines and selected vertices as selected state. Override
	 * for more drawing.
	 *
	 * @param drawer
	 * @param viewContext
	 * @param paintContext
	 */
	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {
		drawPickedLines(drawer, viewContext, paintContext);
		drawPickedVertices(drawer, viewContext, paintContext, paintContext.getLineTypeOfNewLines());
	}

	private void drawPickedLines(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {
		for (OriLine line : paintContext.getPickedLines()) {
			drawer.selectSelectedItemColor();
			drawer.selectSelectedLineStroke(
					viewContext.getScale(), viewContext.isZeroLineWidth());

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
		double scale = viewContext.getScale();
		drawer.selectMouseActionVertexSize(scale);

		drawer.drawVertex(vertex);
	}

	protected void drawPickCandidateVertex(final ObjectGraphicDrawer drawer,
			final CreasePatternViewContext viewContext, final PaintContext paintContext) {
		var candidateOpt = paintContext.getCandidateVertexToPick();
		candidateOpt.ifPresent(candidate -> {
			drawer.selectCandidateItemColor();
			drawVertex(drawer, viewContext, paintContext, candidate);
		});
	}

	protected void drawLine(final ObjectGraphicDrawer drawer, final OriLine line) {
		drawer.drawLine(line);
	}

	protected void drawLine(final ObjectGraphicDrawer drawer, final Vector2d p0, final Vector2d p1) {
		drawer.drawLine(p0, p1);

	}

	protected void drawPickCandidateLine(final ObjectGraphicDrawer drawer,
			final CreasePatternViewContext viewContext, final PaintContext paintContext) {
		var candidateOpt = paintContext.getCandidateLineToPick();
		candidateOpt.ifPresent(candidate -> {
			drawer.selectCandidateItemColor();
			drawer.selectCandidateLineStroke(
					viewContext.getScale(), viewContext.isZeroLineWidth());

			drawLine(drawer, candidate);
		});
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

		var pickedOpt = paintContext.peekVertex();

		pickedOpt.ifPresent(picked -> {
			drawer.selectColor(paintContext.getLineTypeOfNewLines());

			drawer.selectStroke(paintContext.getLineTypeOfNewLines(),
					viewContext.getScale(), viewContext.isZeroLineWidth());

			drawLine(drawer, picked,
					NearestItemFinder.getCandidateVertexOrMousePoint(viewContext, paintContext));
		});

	}

	protected void drawSnapPoints(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {
		drawer.selectAssistLineColor();

		paintContext.getSnapPoints()
				.forEach(p -> drawVertex(drawer, viewContext, paintContext, p));
	}

}
