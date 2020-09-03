package oripa.util.gui;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

/**
 * Utility Module for mouse
 *
 * @author Koji
 *
 */
public class MouseUtility {

	private MouseUtility() {
	}

	/**
	 *
	 * @param event
	 * @return true if Ctrl key is pressed, otherwise false.
	 */
	public static boolean isControlKeyDown(final MouseEvent event) {
		return ((event.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) == MouseEvent.CTRL_DOWN_MASK);
	}

	/**
	 *
	 * @param event
	 * @return true if Shift key is pressed, otherwise false.
	 */
	public static boolean isShiftKeyDown(final MouseEvent event) {
		return ((event.getModifiersEx()
				& MouseEvent.SHIFT_DOWN_MASK) == MouseEvent.SHIFT_DOWN_MASK);
	}

	/**
	 *
	 * @param event
	 * @return true if left button of the mouse is pressed, otherwise false.
	 */
	public static boolean isLeftButtonDown(final MouseEvent event) {
		return (event.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0;
	}

	/**
	 *
	 * @param event
	 * @return true if right button of the mouse is pressed, otherwise false.
	 */
	public static boolean isRightButtonDown(final MouseEvent event) {
		return (event.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) != 0;
	}

	/**
	 *
	 * @param affine
	 * @param p
	 *            A point obtained via {@link MouseEvent}.
	 * @return A point in logical coordinate.
	 */
	public static Point2D.Double getLogicalPoint(final AffineTransform affine, final Point p) {
		Point2D.Double logicalPoint = new Point2D.Double();
		try {
			affine.inverseTransform(p, logicalPoint);
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}

		return logicalPoint;
	}

}
