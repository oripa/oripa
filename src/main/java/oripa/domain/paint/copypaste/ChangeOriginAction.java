package oripa.domain.paint.copypaste;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D.Double;
import java.util.Collection;

import javax.vecmath.Vector2d;

import oripa.domain.paint.GraphicMouseActionInterface;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.GraphicMouseAction;
import oripa.domain.paint.geometry.NearestItemFinder;
import oripa.value.OriLine;

public class ChangeOriginAction extends GraphicMouseAction {

	private final SelectionOriginHolder holder;

	/**
	 * Constructor
	 */
	public ChangeOriginAction(final SelectionOriginHolder holder) {
		this.holder = holder;
	}

	@Override
	public GraphicMouseActionInterface onLeftClick(final PaintContextInterface context,
			final boolean keepDoing) {

		return this;
	}

	@Override
	public void doAction(final PaintContextInterface context, final Double point,
			final boolean differntAction) {

	}

	@Override
	public void undo(final PaintContextInterface context) {
	}

	@Override
	public void onPress(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {

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
		Vector2d closeVertex = NearestItemFinder.pickVertexFromPickedLines(context);
		context.setCandidateVertexToPick(closeVertex);

		if (closeVertex != null) {
			holder.setOrigin(closeVertex);
		}

		return closeVertex;
	}

	@Override
	public void onDraw(final Graphics2D g2d, final PaintContextInterface context) {
		super.onDraw(g2d, context);

		Collection<OriLine> lines = context.getPickedLines();

		g2d.setColor(Color.MAGENTA);

		for (OriLine line : lines) {
			this.drawVertex(g2d, context, line.p0.x, line.p0.y);
			this.drawVertex(g2d, context, line.p1.x, line.p1.y);
		}

		this.drawPickCandidateVertex(g2d, context);
	}
}
