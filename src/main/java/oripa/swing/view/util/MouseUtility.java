package oripa.swing.view.util;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import javax.swing.SwingUtilities;

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
	 * @return true if the event was fired by left button of the mouse.
	 */
	public static boolean isLeftButtonEvent(final MouseEvent event) {
		return SwingUtilities.isLeftMouseButton(event);
	}

	/**
	 *
	 * @param event
	 * @return true if the event was fired by right button of the mouse.
	 */
	public static boolean isRightButtonEvent(final MouseEvent event) {
		return SwingUtilities.isRightMouseButton(event);
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
		}
		return logicalPoint;
	}

}
