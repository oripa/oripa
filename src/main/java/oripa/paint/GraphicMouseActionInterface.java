package oripa.paint;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import javax.vecmath.Vector2d;

import oripa.paint.core.PaintContext;

public interface GraphicMouseActionInterface {

	/**
	 * True if the inherited class uses line-selected marks set by previous action. 
	 * default is false.
	 * @return
	 */
	public abstract boolean needSelect();

	public abstract EditMode getEditMode();

	/**
	 * define action on destroy.
	 * default does clear context selection keeping line-selected marks.
	 * @param context
	 */
	public abstract void destroy(PaintContext context);

	/**
	 * define action for recovering the status of this object 
	 * with given context.
	 * default does nothing.
	 * @param context
	 */
	public abstract void recover(PaintContext context);

	/**
	 * performs action.
	 * 
	 * @param context
	 * @param affine
	 * @param differentAction
	 * 
	 * @return Next mouse action. This class returns {@code this} object.
	 */
	public abstract GraphicMouseActionInterface onLeftClick(PaintContext context,
			AffineTransform affine, boolean differentAction);

	public abstract void doAction(PaintContext context, Point2D.Double point,
			boolean differntAction);

	/**
	 * undo action.
	 * @param context
	 * @param affine
	 * @param differentAction
	 */
	public abstract void onRightClick(PaintContext context,
			AffineTransform affine, boolean differentAction);

	public abstract void undo(PaintContext context);

	/**
	 * searches a vertex and a line close enough to the mouse cursor.
	 * The result is stored into context.pickCandidateL(and V).
	 * 
	 * @param context
	 * @param affine
	 * @param differentAction
	 * @return close vertex. null if not found.
	 */
	public abstract Vector2d onMove(PaintContext context,
			AffineTransform affine, boolean differentAction);

	public abstract void onPress(PaintContext context, AffineTransform affine,
			boolean differentAction);

	public abstract void onDrag(PaintContext context, AffineTransform affine,
			boolean differentAction);

	public abstract void onRelease(PaintContext context,
			AffineTransform affine, boolean differentAction);

	/**
	 * draws selected lines and selected vertices as selected state.
	 * Override for more drawing.
	 * 
	 * @param g2d
	 * @param context
	 */
	public abstract void onDraw(Graphics2D g2d, PaintContext context);

}