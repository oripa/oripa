package oripa.paint.geometry;

import java.awt.geom.Point2D;

import javax.vecmath.Vector2d;

import oripa.ORIPA;
import oripa.geom.GeomUtil;
import oripa.paint.PaintContextInterface;
import oripa.paint.creasepattern.CreasePattern;
import oripa.value.CalculationResource;
import oripa.value.OriLine;


/**
 * Logics using ORIPA data and mouse point in geometric form.
 * @author koji
 *
 */
public class NearestVertexFinder {

	private static double scaleThreshold(PaintContextInterface context){
		return CalculationResource.CLOSE_THRESHOLD / context.getScale();
	}
	
	
	// returns the OriLine sufficiently closer to point p
	public static OriLine pickLine(Point2D.Double p, double scale) {
		double minDistance = Double.MAX_VALUE;
		OriLine bestLine = null;

        CreasePattern creasePattern = ORIPA.doc.getCreasePattern();

		for (OriLine line : creasePattern) {
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



	public static Vector2d pickVertex(
			PaintContextInterface context, boolean freeSelection){

		
		NearestPoint nearestPosition;

		nearestPosition = NearestVertexFinderHelper.findAround(context, scaleThreshold(context));
		

		Vector2d picked = null;		

		if(nearestPosition != null){
			picked = new Vector2d(nearestPosition.point);		
		}
		
		if(picked == null && freeSelection == true){
			Point2D.Double currentPoint = context.getLogicalMousePoint();

			OriLine l = pickLine(currentPoint, context.getScale());
			if(l != null) {
				picked = new Vector2d();
				Vector2d cp = new Vector2d(currentPoint.x, currentPoint.y);

				GeomUtil.DistancePointToSegment(cp, l.p0, l.p1, picked);
			}
		}

		return picked;
	}

	public static Vector2d pickVertexFromPickedLines(PaintContextInterface context){

		
		NearestPoint nearestPosition;
		nearestPosition = NearestVertexFinderHelper.findFromPickedLine(context);
		

		Vector2d picked = null; 
		if (nearestPosition.distance < scaleThreshold(context)) {
			picked = nearestPosition.point;
		}
		
		return picked;
	}

	public static OriLine pickLine(PaintContextInterface context) {
		return pickLine(context.getLogicalMousePoint(), context.getScale());
	}

	public static Vector2d getCandidateVertex(PaintContextInterface context, boolean enableMousePoint){

		Vector2d candidate = context.getCandidateVertexToPick();

		if(candidate == null && enableMousePoint){
			Point2D.Double mp = context.getLogicalMousePoint();
			candidate = new Vector2d(mp.x, mp.y);
		}

		return candidate;
	}


}
