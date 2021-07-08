package oripa.domain.paint;

import java.awt.geom.AffineTransform;

import javax.vecmath.Vector2d;

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
			final PaintContextInterface context, final boolean differentAction);

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
	public abstract void onRightClick(PaintContextInterface context, boolean differentAction);

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
	public abstract Vector2d onMove(PaintContextInterface context, boolean differentAction);

	public abstract void onPress(PaintContextInterface context, AffineTransform affine,
			boolean differentAction);

	public abstract void onDrag(PaintContextInterface context, AffineTransform affine,
			boolean differentAction);

	public abstract void onRelease(PaintContextInterface context,
			AffineTransform affine, boolean differentAction);

	/**
	 * draws selected lines and selected vertices as selected state. Override
	 * for more drawing.
	 *
	 * @param g2d
	 * @param context
	 */
	public abstract void onDraw(ObjectGraphicDrawer drawer, PaintContextInterface context);

}