package oripa.domain.paint.core;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.vecmath.Vector2d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.paint.EditMode;
import oripa.domain.paint.GraphicMouseActionInterface;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.geometry.NearestItemFinder;
import oripa.domain.paint.util.ElementSelector;
import oripa.domain.paint.util.GraphicItemConverter;
import oripa.value.OriLine;

public abstract class GraphicMouseAction implements GraphicMouseActionInterface {

	private static Logger logger = LoggerFactory.getLogger(GraphicMouseAction.class);

	private EditMode editMode = EditMode.INPUT;
	private boolean needSelect = false;
	private ActionState state;

	private final ElementSelector selector = new ElementSelector();

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
		Point2D.Double clickPoint = context.getLogicalMousePoint();

		doAction(context, clickPoint, differentAction);
		return this;
	}

	@Override
	public void doAction(final PaintContextInterface context, final Point2D.Double point,
			final boolean differntAction) {

		state = state.doAction(context,
				point, differntAction);

	}

	@Override
	public void onRightClick(final PaintContextInterface context,
			final AffineTransform affine,
			final boolean doSpecial) {

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
			final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {

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
	public abstract void onPress(PaintContextInterface context,
			AffineTransform affine, boolean differentAction);

	@Override
	public abstract void onDrag(PaintContextInterface context,
			AffineTransform affine, boolean differentAction);

	@Override
	public abstract void onRelease(PaintContextInterface context,
			AffineTransform affine, boolean differentAction);

	@Override
	public void onDraw(final Graphics2D g2d, final PaintContextInterface context) {
		drawPickedLines(g2d, context);
		drawPickedVertices(g2d, context, context.getLineTypeOfNewLines());

	}

	protected ElementSelector getElementSelector() {
		return selector;
	}

	private void drawPickedLines(final Graphics2D g2d, final PaintContextInterface context) {
		for (OriLine line : context.getPickedLines()) {
			g2d.setColor(selector.getSelectedItemColor());
			g2d.setStroke(selector.createSelectedLineStroke(
					context.getScale(), context.isZeroLineWidth()));

			drawLine(g2d, line);
		}

	}

	private void drawPickedVertices(final Graphics2D g2d,
			final PaintContextInterface context, final OriLine.Type lineType) {

		for (Vector2d vertex : context.getPickedVertices()) {
			g2d.setColor(selector
					.getColor(lineType));

			drawVertex(g2d, context, vertex.x, vertex.y);
		}
	}

	/**
	 * draw a picked vertex as an small rectangle at (x, y)
	 *
	 * @param g2d
	 * @param context
	 * @param x
	 * @param y
	 */
	protected void drawVertex(final Graphics2D g2d, final PaintContextInterface context,
			final double x, final double y) {
		double scale = context.getScale();
		double vertexSize = selector.createMouseActionVertexSize(scale);
		double vertexHalfSize = vertexSize / 2;

		g2d.fill(new Rectangle2D.Double(
				x - vertexHalfSize, y - vertexHalfSize,
				vertexSize, vertexSize));
	}

	protected void drawPickCandidateVertex(final Graphics2D g2d,
			final PaintContextInterface context) {
		Vector2d candidate = context.getCandidateVertexToPick();
		if (candidate != null) {
			g2d.setColor(selector.getCandidateItemColor());
			drawVertex(g2d, context, candidate.x, candidate.y);
		}
	}

	protected void drawLine(final Graphics2D g2d, final OriLine line) {
		g2d.draw(GraphicItemConverter.toLine2D(line));

	}

	protected void drawLine(final Graphics2D g2d, final Vector2d p0, final Vector2d p1) {
		g2d.draw(new Line2D.Double(p0.x, p0.y, p1.x, p1.y));

	}

	protected void drawPickCandidateLine(final Graphics2D g2d,
			final PaintContextInterface context) {
		OriLine candidate = context.getCandidateLineToPick();
		if (candidate != null) {
			g2d.setColor(selector.getCandidateItemColor());
			g2d.setStroke(selector.createCandidateLineStroke(
					context.getScale(), context.isZeroLineWidth()));

			drawLine(g2d, candidate);
		}
	}

	/**
	 * draws the line between the most recently selected vertex and the closest
	 * vertex sufficiently to the mouse cursor. if every vertex is far from
	 * cursor, this method uses the cursor point instead of close vertex.
	 *
	 * @param g2d
	 * @param context
	 */
	protected void drawTemporaryLine(final Graphics2D g2d,
			final PaintContextInterface context) {

		if (context.getVertexCount() == 0) {
			return;
		}

		Vector2d picked = context.peekVertex();

		g2d.setColor(selector.getColor(context.getLineTypeOfNewLines()));

		g2d.setStroke(selector.createStroke(context.getLineTypeOfNewLines(),
				context.getScale(), context.isZeroLineWidth()));

		drawLine(g2d, picked,
				NearestItemFinder.getCandidateVertex(context, true));

	}

}
