package oripa.gui.presenter.creasepattern;

import javax.vecmath.Vector2d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.paint.ActionState;
import oripa.domain.paint.BasicUndo;
import oripa.domain.paint.PaintContextInterface;
import oripa.gui.presenter.creasepattern.geometry.NearestItemFinder;
import oripa.value.OriLine;

public abstract class GraphicMouseAction implements GraphicMouseActionInterface {

	private static Logger logger = LoggerFactory.getLogger(GraphicMouseAction.class);

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
	public void destroy(final PaintContextInterface context) {
		context.clear(false);
	}

	/**
	 * This method is called at the first step of
	 * {@link #recover(PaintContextInterface)}. After this method is done,
	 * {@code recover()} resets the {@code .selected} property of all lines in
	 * crease pattern if {@link #needSelect()} is false.
	 *
	 * @param context
	 */
	protected void recoverImpl(final PaintContextInterface context) {
	}

	/**
	 * calls {@link #recoverImpl(PaintContextInterface)}, and if
	 * {@link #needSelect()} returns false, then calls
	 * {@code context.getPainter().resetSelectedOriLines()}.
	 */
	@Override
	public final void recover(final PaintContextInterface context) {

		recoverImpl(context);

		if (!needSelect()) {
			context.getPainter().resetSelectedOriLines();
		}
	}

	@Override
	public GraphicMouseActionInterface onLeftClick(
			final PaintContextInterface context, final boolean differentAction) {
		var clickPoint = context.getLogicalMousePoint();

		doAction(context, clickPoint, differentAction);
		return this;
	}

	@Override
	public void doAction(final PaintContextInterface context, final Vector2d point,
			final boolean differntAction) {

		state = state.doAction(context,
				point, differntAction);

	}

	@Override
	public void onRightClick(final PaintContextInterface context, final boolean doSpecial) {

		logger.info(this.getClass().getName());
		logger.info("before undo " + context.toString());

		undo(context);

		logger.info("after undo " + context.toString());

	}

	@Override
	public void undo(final PaintContextInterface context) {
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
	public void redo(final PaintContextInterface context) {
		if (!context.creasePatternUndo().canRedo()) {
			return;
		}
		destroy(context);
		recover(context);
		context.creasePatternUndo().redo();
	}

	@Override
	public Vector2d onMove(
			final PaintContextInterface context, final boolean differentAction) {

		setCandidateVertexOnMove(context, differentAction);
		setCandidateLineOnMove(context);

		return context.getCandidateVertexToPick();
	}

	protected final void setCandidateVertexOnMove(
			final PaintContextInterface context, final boolean differentAction) {
		context.setCandidateVertexToPick(
				NearestItemFinder.pickVertex(
						context, differentAction));
	}

	protected final void setCandidateLineOnMove(final PaintContextInterface context) {
		context.setCandidateLineToPick(
				NearestItemFinder.pickLine(
						context));
	}

	@Override
	public void onPress(final PaintContextInterface context, final boolean differentAction) {

	}

	@Override
	public void onDrag(final PaintContextInterface context, final boolean differentAction) {

	}

	@Override
	public void onRelease(final PaintContextInterface context, final boolean differentAction) {

	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final PaintContextInterface context) {
		drawPickedLines(drawer, context);
		drawPickedVertices(drawer, context, context.getLineTypeOfNewLines());

	}

//	protected ElementSelector getElementSelector() {
//		return selector;
//	}
//
//	protected GraphicItemConverter getGraphicItemConverter() {
//		return converter;
//	}

	private void drawPickedLines(final ObjectGraphicDrawer drawer, final PaintContextInterface context) {
		for (OriLine line : context.getPickedLines()) {
			drawer.selectSelectedItemColor();
			drawer.selectSelectedLineStroke(
					context.getScale(), context.isZeroLineWidth());

			drawLine(drawer, line);
		}

	}

	private void drawPickedVertices(final ObjectGraphicDrawer drawer,
			final PaintContextInterface context, final OriLine.Type lineType) {

		for (Vector2d vertex : context.getPickedVertices()) {
			drawer.selectColor(lineType);

			drawVertex(drawer, context, vertex);
		}
	}

	/**
	 * Draws the given vertex as an small rectangle.
	 */
	protected void drawVertex(final ObjectGraphicDrawer drawer, final PaintContextInterface context,
			final Vector2d vertex) {
		double scale = context.getScale();
		drawer.selectMouseActionVertexSize(scale);

		drawer.drawVertex(vertex);
	}

	protected void drawPickCandidateVertex(final ObjectGraphicDrawer drawer,
			final PaintContextInterface context) {
		Vector2d candidate = context.getCandidateVertexToPick();
		if (candidate != null) {
			drawer.selectCandidateItemColor();
			drawVertex(drawer, context, candidate);
		}
	}

	protected void drawLine(final ObjectGraphicDrawer drawer, final OriLine line) {
		drawer.drawLine(line);
	}

	protected void drawLine(final ObjectGraphicDrawer drawer, final Vector2d p0, final Vector2d p1) {
		drawer.drawLine(p0, p1);

	}

	protected void drawPickCandidateLine(final ObjectGraphicDrawer drawer,
			final PaintContextInterface context) {
		OriLine candidate = context.getCandidateLineToPick();
		if (candidate != null) {
			drawer.selectCandidateItemColor();
			drawer.selectCandidateLineStroke(
					context.getScale(), context.isZeroLineWidth());

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
			final PaintContextInterface context) {

		if (context.getVertexCount() == 0) {
			return;
		}

		Vector2d picked = context.peekVertex();

		drawer.selectColor(context.getLineTypeOfNewLines());

		drawer.selectStroke(context.getLineTypeOfNewLines(),
				context.getScale(), context.isZeroLineWidth());

		drawLine(drawer, picked,
				NearestItemFinder.getCandidateVertex(context, true));

	}

}
