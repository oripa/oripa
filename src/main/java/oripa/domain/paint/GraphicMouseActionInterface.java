package oripa.domain.paint;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D.Double;

import javax.vecmath.Vector2d;

import oripa.viewsetting.main.ScreenUpdater;

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
	 * define action on destroy. default does clear context selection keeping
	 * line-selected marks.
	 * 
	 * @param context
	 */
	public abstract void destroy(PaintContextInterface context);

	/**
	 * define action for recovering the status of this object with given
	 * context. default does nothing.
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
	 * @return Next mouse action. This class returns {@code this} object.
	 */
	public GraphicMouseActionInterface onLeftClick(
			final PaintContextInterface context, final boolean differentAction,
			final ScreenUpdater screenUpdater);

	/**
	 * @param context
	 * @param point
	 * @param differntAction
	 * @param screenUpdater
	 */
	public abstract void doAction(PaintContextInterface context, Double point,
			boolean differntAction,
			ScreenUpdaterInterface screenUpdater);

	/**
	 * undo action.
	 * 
	 * @param context
	 * @param affine
	 * @param differentAction
	 */
	public abstract void onRightClick(PaintContextInterface context,
			AffineTransform affine, boolean differentAction);

	public abstract void undo(PaintContextInterface context);

	/**
	 * searches a vertex and a line close enough to the mouse cursor. The result
	 * is stored into context.pickCandidateL(and V).
	 * 
	 * @param context
	 * @param affine
	 * @param differentAction
	 * @return close vertex. null if not found.
	 */
	public abstract Vector2d onMove(PaintContextInterface context,
			AffineTransform affine, boolean differentAction);

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
	public abstract void onDraw(Graphics2D g2d, PaintContextInterface context);

}