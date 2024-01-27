package oripa.gui.presenter.creasepattern;

import java.util.Optional;

import oripa.domain.paint.PaintContext;
import oripa.gui.view.creasepattern.ObjectGraphicDrawer;
import oripa.vecmath.Vector2d;

public interface GraphicMouseAction {

	/**
	 * True if the inherited class uses line-selected marks set by previous
	 * action. default is false.
	 *
	 * @return
	 */
	public abstract boolean needSelect();

	public default boolean isUsingCtrlKeyOnDrag() {
		return false;
	}

	public abstract EditMode getEditMode();

	/**
	 * define action on destroy. This method is expected to be called when the
	 * action is switched, before recover() of new action.
	 *
	 * @param context
	 */
	public abstract void destroy(PaintContext context);

	/**
	 * Defines action for recovering the status of this object with given
	 * context. This method should be called when the action is switched, after
	 * destroy() of old action.
	 *
	 * @param context
	 */
	public abstract void recover(PaintContext context);

	/**
	 * performs action.
	 *
	 * @param viewContext
	 * @param paintContext
	 * @param differentAction
	 *
	 * @return Next mouse action.
	 */
	public GraphicMouseAction onLeftClick(
			final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction);

	/**
	 * @param context
	 * @param point
	 * @param differntAction
	 */
	public abstract void doAction(PaintContext context, Vector2d point,
			boolean differntAction);

	/**
	 * undo action.
	 *
	 * @param viewContext
	 * @param paintContext
	 * @param differentAction
	 */
	public abstract void onRightClick(CreasePatternViewContext viewContext, PaintContext paintContext,
			boolean differentAction);

	public abstract void undo(PaintContext context);

	public abstract void redo(PaintContext context);

	/**
	 * searches a vertex and a line close enough to the mouse cursor. The result
	 * is stored into candidateVertexToPick and candidateLineToPick properties
	 * of paintContext.
	 *
	 * @param viewContext
	 * @param paintContext
	 * @param differentAction
	 * @return close vertex. Empty if not found.
	 */
	public abstract Optional<Vector2d> onMove(final CreasePatternViewContext viewContext,
			final PaintContext paintContext, boolean differentAction);

	public abstract void onPress(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			boolean differentAction);

	public abstract void onDrag(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			boolean differentAction);

	public abstract void onRelease(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			boolean differentAction);

	/**
	 * draws selected lines and selected vertices as selected state. Override
	 * for more drawing.
	 *
	 * @param drawer
	 * @param viewContext
	 * @param paintContext
	 */
	public abstract void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext);

}