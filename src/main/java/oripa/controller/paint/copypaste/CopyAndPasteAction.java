package oripa.controller.paint.copypaste;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D.Double;

import javax.vecmath.Vector2d;

import oripa.controller.paint.EditMode;
import oripa.controller.paint.GraphicMouseActionInterface;
import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.ScreenUpdaterInterface;
import oripa.controller.paint.core.GraphicMouseAction;
import oripa.viewsetting.main.ScreenUpdater;

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
	public void recover(final PaintContextInterface context) {
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
		context.creasePatternUndo().loadUndoInfo();
	}

	@Override
	public GraphicMouseActionInterface onLeftClick(final PaintContextInterface context,
			final boolean differentAction,
			final ScreenUpdater screenUpdater) {
		action.onLeftClick(context, differentAction, screenUpdater);

		return this;
	}

//	@Override
//	public void onRightClick(PaintContext context, AffineTransform affine,
//			boolean differentAction) {
//		// TODO Auto-generated method stub
//		super.onRightClick(context, affine, differentAction);
//	}

	@Override
	public void doAction(final PaintContextInterface context, final Double point,
			final boolean differntAction, final ScreenUpdaterInterface screenUpdater) {
		action.doAction(context, point, differntAction, screenUpdater);
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
		}
		else {
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
		// TODO Auto-generated method stub
		action.onDraw(g2d, context);
	}
}
