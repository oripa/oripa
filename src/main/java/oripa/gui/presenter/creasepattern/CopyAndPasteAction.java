package oripa.gui.presenter.creasepattern;

import javax.vecmath.Vector2d;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.copypaste.SelectionOriginHolder;

public class CopyAndPasteAction extends GraphicMouseAction {

	private final SelectionOriginHolder originHolder;
	private final ChangeOriginAction originAction;
	private final PasteAction pasteAction;

	private GraphicMouseActionInterface action;

	public CopyAndPasteAction(final SelectionOriginHolder originHolder) {
		this.originHolder = originHolder;

		originAction = new ChangeOriginAction(originHolder);
		pasteAction = new PasteAction(originHolder);

		action = pasteAction;

		setEditMode(EditMode.COPY);
		setNeedSelect(true);
	}

	@Override
	protected void recoverImpl(final PaintContext context) {
		originHolder.resetOrigin(context);
		action = pasteAction;
		action.recover(context);
	}

	@Override
	public void destroy(final PaintContext context) {
		originHolder.setOrigin(null);
		action.destroy(context);
	}

	@Override
	public void undo(final PaintContext context) {
		context.creasePatternUndo().undo();
	}

	@Override
	public GraphicMouseActionInterface onLeftClick(final CreasePatternViewContext viewContext,
			final PaintContext paintContext,
			final boolean differentAction) {
		action.onLeftClick(viewContext, paintContext, differentAction);

		return this;
	}

	@Override
	public void doAction(final PaintContext context, final Vector2d point,
			final boolean differentAction) {
		action.doAction(context, point, differentAction);
	}

	@Override
	public void onPress(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {
		action.onPress(viewContext, paintContext, differentAction);
	}

	@Override
	public void onDrag(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {
		action.onDrag(viewContext, paintContext, differentAction);
	}

	@Override
	public void onRelease(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {
		action.onRelease(viewContext, paintContext, differentAction);
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
	public Vector2d onMove(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean changingOrigin) {

		changeAction(changingOrigin);

		return action.onMove(viewContext, paintContext, changingOrigin);
	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {
		action.onDraw(drawer, viewContext, paintContext);
	}
}
