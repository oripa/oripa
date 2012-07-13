package oripa.paint;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import javax.vecmath.Vector2d;

import oripa.Constants;
import oripa.Globals;
import oripa.ORIPA;
import oripa.geom.GeomUtil;
import oripa.geom.OriLine;

public class GeometricalOperation {
	public static Point2D.Double getLogicalPoint(AffineTransform affine, Point p){		
		// Gets the logical coordinates of the click

		Point2D.Double logicalPoint = new Point2D.Double();
		
		try {
			affine.inverseTransform(p, logicalPoint);
		} catch (NoninvertibleTransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logicalPoint = null;
		}
		
		return logicalPoint;
	}
	  // returns the OriLine sufficiently closer to point p
    public static OriLine pickLine(Point2D.Double p, double scale) {
        double minDistance = Double.MAX_VALUE;
        OriLine bestLine = null;

        for (OriLine line : ORIPA.doc.lines) {
            if (Globals.editMode == Constants.EditMode.DELETE_LINE) {
            }
            double dist = GeomUtil.DistancePointToSegment(new Vector2d(p.x, p.y), line.p0, line.p1);
            if (dist < minDistance) {
                minDistance = dist;
                bestLine = line;
            }
        }

        if (minDistance / scale < 10) {
            return bestLine;
        } else {
            return null;
        }
    }

    
    
    public static boolean pickPointOnLine(Point2D.Double p, Object[] line_vertex, double scale) {
        double minDistance = Double.MAX_VALUE;
        OriLine bestLine = null;
        Vector2d nearestPoint = new Vector2d();
        Vector2d tmpNearestPoint = new Vector2d();

        for (OriLine line : ORIPA.doc.lines) {
            double dist = GeomUtil.DistancePointToSegment(new Vector2d(p.x, p.y), line.p0, line.p1, tmpNearestPoint);
            if (dist < minDistance) {
                minDistance = dist;
                bestLine = line;
                nearestPoint.set(tmpNearestPoint);
            }
        }

        if (minDistance / scale < 5) {
            line_vertex[0] = bestLine;
            line_vertex[1] = nearestPoint;
            return true;
        } else {
            return false;
        }
    }

    public static Vector2d pickVertex(Point2D.Double p, double scale, boolean dispGrid) {
        double minDistance = Double.MAX_VALUE;
        Vector2d minPosition = new Vector2d();

        for (OriLine line : ORIPA.doc.lines) {
            double dist0 = p.distance(line.p0.x, line.p0.y);
            if (dist0 < minDistance) {
                minDistance = dist0;
                minPosition.set(line.p0);
            }
            double dist1 = p.distance(line.p1.x, line.p1.y);
            if (dist1 < minDistance) {
                minDistance = dist1;
                minPosition.set(line.p1);
            }
        }

        if (dispGrid) {
            double step = ORIPA.doc.size / Globals.gridDivNum;
            for (int ix = 0; ix < Globals.gridDivNum + 1; ix++) {
                for (int iy = 0; iy < Globals.gridDivNum + 1; iy++) {
                    double x = -ORIPA.doc.size / 2 + step * ix;
                    double y = -ORIPA.doc.size / 2 + step * iy;
                    double dist = p.distance(x, y);
                    if (dist < minDistance) {
                        minDistance = dist;
                        minPosition.set(x, y);
                    }
                }
            }
        }

        if (minDistance < 10.0 / scale) {
            return minPosition;
        } else {
            return null;
        }
    }

	public static Vector2d pickVertex(MouseContext context, 
			Point2D.Double currentPoint, boolean freeSelection){
		Vector2d picked = pickVertex(
				currentPoint, 
				context.scale, context.dispGrid);

//		log(currentPoint.x, currentPoint.y);
		
		if(picked == null && freeSelection == true){
			
			OriLine l = pickLine(currentPoint, context.scale);
			if(l != null) {
				picked = new Vector2d();
				Vector2d cp = new Vector2d(currentPoint.x, currentPoint.y);

				GeomUtil.DistancePointToSegment(cp, l.p0, l.p1, picked);
			}
		}

		return picked;
	}

	
    public static OriLine pickLine(MouseContext context, 
    		Point2D.Double currentPoint) {
    	return pickLine(currentPoint, context.scale);
    }
    
	public static Vector2d getCandidateVertex(MouseContext context, boolean enableMousePoint){
		
		Vector2d candidate = context.pickCandidateV;

		if(candidate == null && enableMousePoint){
			Point2D.Double mp = context.mousePoint;
			candidate = new Vector2d(mp.x, mp.y);
		}

		return candidate;
	}

}
