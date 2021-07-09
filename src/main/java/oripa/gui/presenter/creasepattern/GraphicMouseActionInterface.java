package oripa.gui.presenter.creasepattern;

import javax.vecmath.Vector2d;

import oripa.domain.paint.PaintContextInterface;

public interface GraphicMouseActionInterface {

	/**
	 * True if the inherited class uses line-selected marks set by previous
	 * action. default is false.
	 *
	 * @return
	 */
	public abstract boolean needSelect();

	public abstract EditMode getEditMode();

	/**
	 * define action on destroy. This method is expected to be called when the
	 * action is switched, before recover() of new action.
	 *
	 * @param context
	 */
	public abstract void destroy(PaintContextInterface context);

	/**
	 * Defines action for recovering the status of this object with given
	 * context. This method should be called when the action is switched, after
	 * destroy() of old action.
	 *
	 * @param context
	 */
	public abstract void recover(PaintContextInterface context);

	/**
	 * performs action.
	 *
	 * @param context
	 * @param affine
	 * @param differentAction
	 * @param screenUpdater
	 *
	 * @return Next mouse action.
	 */
	public GraphicMouseActionInterface onLeftClick(
			final CreasePatternViewContext viewContext, final PaintContextInterface paintContext,
			final boolean differentAction);

	/**
	 * @param context
	 * @param point
	 * @param differntAction
	 * @param screenUpdater
	 */
	public abstract void doAction(PaintContextInterface context, Vector2d point,
			boolean differntAction);

	/**
	 * undo action.
	 *
	 * @param context
	 * @param differentAction
	 */
	public abstract void onRightClick(CreasePatternViewContext viewContext, PaintContextInterface paintContext,
			boolean differentAction);

	public abstract void undo(PaintContextInterface context);

	public abstract void redo(PaintContextInterface context);

	/**
	 * searches a vertex and a line close enough to the mouse cursor. The result
	 * is stored into context.pickCandidateL(and V).
	 *
	 * @param context
	 * @param differentAction
	 * @return close vertex. null if not found.
	 */
	public abstract Vector2d onMove(final CreasePatternViewContext viewContext,
			final PaintContextInterface paintContext, boolean differentAction);

	public abstract void onPress(final CreasePatternViewContext viewContext, final PaintContextInterface paintContext,
			boolean differentAction);

	public abstract void onDrag(final CreasePatternViewContext viewContext, final PaintContextInterface paintContext,
			boolean differentAction);

	public abstract void onRelease(final CreasePatternViewContext viewContext, final PaintContextInterface paintContext,
			boolean differentAction);

	/**
	 * draws selected lines and selected vertices as selected state. Override
	 * for more drawing.
	 *
	 * @param g2d
	 * @param context
	 */
	public abstract void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContextInterface paintContext);

}