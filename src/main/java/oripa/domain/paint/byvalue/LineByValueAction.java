package oripa.domain.paint.byvalue;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

import javax.vecmath.Vector2d;

import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.GraphicMouseAction;
import oripa.domain.paint.core.PaintConfig;
import oripa.domain.paint.util.ElementSelector;

public class LineByValueAction extends GraphicMouseAction {

	public LineByValueAction() {
		setActionState(new SelectingVertexToDrawLine());
	}

	@Override
	public void onDrag(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRelease(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non Javadoc)
	 *
	 * @see
	 * oripa.domain.paint.core.GraphicMouseAction#recover(oripa.domain.paint.
	 * PaintContextInterface)
	 */
	@Override
	protected void recoverImpl(final PaintContextInterface context) {
		context.clear(true);
	}

	@Override
	public void onDraw(final Graphics2D g2d, final PaintContextInterface context) {
		super.onDraw(g2d, context);

		drawPickCandidateVertex(g2d, context);
		Vector2d v = context.getCandidateVertexToPick();
		if (v != null) {
			try {
				double length = ValueDB.getInstance().getLength();
				double angle = ValueDB.getInstance().getAngle();

				angle = Math.toRadians(angle);

				ElementSelector selector = new ElementSelector();
				g2d.setColor(selector.selectColorByLineType(PaintConfig.inputLineType));
				g2d.setStroke(selector.selectStroke(PaintConfig.inputLineType));

				Vector2d dir = new Vector2d(Math.cos(angle), -Math.sin(angle));
				dir.scale(length);
				g2d.draw(new Line2D.Double(v.x, v.y, v.x + dir.x, v.y + dir.y));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onPress(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {
		// TODO Auto-generated method stub

	}

}
