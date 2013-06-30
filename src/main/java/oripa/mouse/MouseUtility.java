package oripa.mouse;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

public class MouseUtility {

	public static boolean isControlButtonPressed(MouseEvent event){
		return ((event.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) 
				== MouseEvent.CTRL_DOWN_MASK);		
	}
	
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
