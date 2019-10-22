package oripa.domain.paint.copypaste;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D.Double;

import javax.vecmath.Vector2d;

import oripa.domain.paint.EditMode;
import oripa.domain.paint.GraphicMouseActionInterface;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.GraphicMouseAction;

public class CopyAndPasteAction extends GraphicMouseAction {

	private final ChangeOriginAction originAction = new ChangeOriginAction();
	private final PasteAction pasteAction = new PasteAction();

	private GraphicMouseActionInterface action = pasteAction;

	public CopyAndPasteAction() {
		setEditMode(EditMode.COPY);
		setNeedSelect(true);
	}

	private final OriginHolder originHolder = OriginHolder.getInstance();

	@Override
	protected void recoverImpl(final PaintContextInterface context) {
		originHolder.resetOrigin(context);
		action = pasteAction;
		action.recover(context);
	}

	@Override
	public void destroy(final PaintContextInterface context) {
		originHolder.setOrigin(null);
		action.destroy(context);
	}

	@Override
	public void undo(final PaintContextInterface context) {
		context.creasePatternUndo().undo();
	}

	@Override
	public GraphicMouseActionInterface onLeftClick(final PaintContextInterface context,
			final boolean differentAction) {
		action.onLeftClick(context, differentAction);

		return this;
	}

	@Override
	public void doAction(final PaintContextInterface context, final Double point,
			final boolean differntAction) {
		action.doAction(context, point, differntAction);
	}

	@Override
	public void onPress(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {
		action.onPress(context, affine, differentAction);
	}

	@Override
	public void onDrag(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {
		action.onDrag(context, affine, differentAction);
	}

	@Override
	public void onRelease(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {
		action.onRelease(context, affine, differentAction);
	}

	/**
	 *
	 * @param changingOrigin
	 *            {@code true} for selecting origin, {@code false} for pasting.
	 */
	public void changeAction(final boolean changingOrigin) {
		if (changingOrigin) {
			action = originAction;
		} else {
			action = pasteAction;
		}
	}

	@Override
	public Vector2d onMove(final PaintContextInterface context, final AffineTransform affine,
			final boolean changingOrigin) {

		changeAction(changingOrigin);

		return action.onMove(context, affine, changingOrigin);
	}

	@Override
	public void onDraw(final Graphics2D g2d, final PaintContextInterface context) {
		action.onDraw(g2d, context);
	}
}
