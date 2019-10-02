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
    boolean needSelect();

	EditMode getEditMode();

	/**
	 * define action on destroy.
	 * default does clear context selection keeping line-selected marks.
	 * @param context
	 */
    void destroy(PaintContext context);

	/**
	 * define action for recovering the status of this object 
	 * with given context.
	 * default does nothing.
	 * @param context
	 */
    void recover(PaintContext context);

	/**
	 * performs action.
	 * 
	 * @param context
	 * @param affine
	 * @param differentAction
	 * 
	 * @return Next mouse action. This class returns {@code this} object.
	 */
    GraphicMouseActionInterface onLeftClick(PaintContext context, AffineTransform affine, boolean differentAction);

	void doAction(PaintContext context, Point2D.Double point, boolean differntAction);

	/**
	 * undo action.
	 * @param context
	 * @param affine
	 * @param differentAction
	 */
    void onRightClick(PaintContext context, AffineTransform affine, boolean differentAction);

	void undo(PaintContext context);

	/**
	 * searches a vertex and a line close enough to the mouse cursor.
	 * The result is stored into context.pickCandidateL(and V).
	 * 
	 * @param context
	 * @param affine
	 * @param differentAction
	 * @return close vertex. null if not found.
	 */
    Vector2d onMove(PaintContext context, AffineTransform affine, boolean differentAction);

	void onPress(PaintContext context, AffineTransform affine, boolean differentAction);

	void onDrag(PaintContext context, AffineTransform affine, boolean differentAction);

	void onRelease(PaintContext context, AffineTransform affine, boolean differentAction);

	/**
	 * draws selected lines and selected vertices as selected state.
	 * Override for more drawing.
	 * 
	 * @param g2d
	 * @param context
	 */
    void onDraw(Graphics2D g2d, PaintContext context);

}