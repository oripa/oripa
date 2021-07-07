package oripa.domain.paint.byvalue;

import java.awt.geom.AffineTransform;

import javax.vecmath.Vector2d;

import oripa.domain.paint.ObjectGraphicDrawer;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.GraphicMouseAction;

public class LineByValueAction extends GraphicMouseAction {

	private final ValueSetting valueSetting;

	public LineByValueAction(final ValueSetting valueSetting) {
		super();
		setActionState(new SelectingVertexToDrawLine(valueSetting));

		this.valueSetting = valueSetting;
	}

	@Override
	public void onDrag(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {

	}

	@Override
	public void onRelease(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {

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
	public void onDraw(final ObjectGraphicDrawer g2d, final PaintContextInterface context) {
		super.onDraw(g2d, context);

		drawPickCandidateVertex(g2d, context);
		Vector2d v = context.getCandidateVertexToPick();
		if (v == null) {
			return;
		}
		try {
			var angle = valueSetting.getAngle();
			var length = valueSetting.getLength();

			var radianAngle = Math.toRadians(angle);

			g2d.selectColor(context.getLineTypeOfNewLines());
			g2d.selectStroke(
					context.getLineTypeOfNewLines(),
					context.getScale(), context.isZeroLineWidth());

			Vector2d dir = new Vector2d(Math.cos(radianAngle), -Math.sin(radianAngle));
			dir.scale(length);
			var w = new Vector2d(v);
			w.add(dir);
			g2d.drawLine(v, w);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPress(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {

	}

}
