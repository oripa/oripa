package oripa.domain.paint.core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.vecmath.Vector2d;

import oripa.domain.paint.EditMode;
import oripa.domain.paint.GraphicMouseActionInterface;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.geometry.NearestItemFinder;
import oripa.domain.paint.util.ElementSelector;
import oripa.value.OriLine;

public abstract class GraphicMouseAction implements GraphicMouseActionInterface {

	private EditMode editMode = EditMode.INPUT;
	private boolean needSelect = false;
	private ActionState state;

	protected void log(final double x, final double y) {
		System.out.println(x + "," + y);

	}

	protected void log(final Vector2d p) {
		if (p == null) {
			return;
		}
		log(p.x, p.y);
	}

	protected final void setActionState(final ActionState state) {
		this.state = state;
	}

	protected final ActionState getActionState() {
		return state;
	}

	protected final boolean currentStateIs(final Class<? extends ActionState> s) {
		return state.equals(s);
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

	@Override
	public void destroy(final PaintContextInterface context) {
		context.clear(false);
	}

	@Override
	public void recover(final PaintContextInterface context) {
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
			final boolean differentAction) {

		undo(context);
	}

	@Override
	public void undo(final PaintContextInterface context) {
		state = BasicUndo.undo(state, context);
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
		drawPickedVertices(g2d, context);

	}

	private void drawPickedLines(final Graphics2D g2d, final PaintContextInterface context) {
		for (OriLine line : context.getPickedLines()) {
			g2d.setColor(LineSetting.LINE_COLOR_PICKED);
			g2d.setStroke(LineSetting.STROKE_PICKED);

			drawLine(g2d, line);
		}

	}

	private void drawPickedVertices(final Graphics2D g2d,
			final PaintContextInterface context) {
		ElementSelector selector = new ElementSelector();

		for (Vector2d vertex : context.getPickedVertices()) {
			g2d.setColor(selector
					.selectColorByLineType(PaintConfig.inputLineType));

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
		g2d.fill(new Rectangle2D.Double(x - 5.0 / scale,
				y - 5.0 / scale, 10.0 / scale, 10.0 / scale));

	}

	protected void drawPickCandidateVertex(final Graphics2D g2d,
			final PaintContextInterface context) {
		Vector2d candidate = context.getCandidateVertexToPick();
		if (candidate != null) {
			g2d.setColor(LineSetting.LINE_COLOR_CANDIDATE);
			drawVertex(g2d, context, candidate.x, candidate.y);
		}
	}

	protected void drawLine(final Graphics2D g2d, final OriLine line) {
		g2d.draw(new Line2D.Double(line.p0.x, line.p0.y,
				line.p1.x, line.p1.y));

	}

	protected void drawLine(final Graphics2D g2d, final Vector2d p0, final Vector2d p1) {
		g2d.draw(new Line2D.Double(p0.x, p0.y,
				p1.x, p1.y));

	}

	protected void drawPickCandidateLine(final Graphics2D g2d,
			final PaintContextInterface context) {
		OriLine candidate = context.getCandidateLineToPick();
		if (candidate != null) {
			g2d.setColor(LineSetting.LINE_COLOR_CANDIDATE);
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
		ElementSelector selector = new ElementSelector();

		if (context.getVertexCount() > 0) {
			Vector2d picked = context.peekVertex();

			Color color = selector
					.selectColorByLineType(PaintConfig.inputLineType);
			g2d.setColor(color);
			drawLine(g2d, picked,
					NearestItemFinder.getCandidateVertex(context, true));
		}

	}

}
