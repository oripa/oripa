package oripa.mouse;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

/**
 * Utility Module for mouse
 * @author Koji
 *
 */
public class MouseUtility {

	private MouseUtility() {}
	
	/**
	 * 
	 * @param event
	 * @return true if Ctrl key is pressed, otherwise false.
	 */
	public static boolean isControlKeyPressed(MouseEvent event){
		return ((event.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) 
				== MouseEvent.CTRL_DOWN_MASK);		
	}
	
	/**
	 * 
	 * @param affine
	 * @param p A point obtained via {@link MouseEvent}.
	 * @return A point in logical coordinate.
	 */
    public static Point2D.Double getLogicalPoint(AffineTransform affine, Point p){
    	Point2D.Double logicalPoint = new Point2D.Double();
        try {
			affine.inverseTransform(p, logicalPoint);
		} catch (NoninvertibleTransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return logicalPoint;
    }

}
