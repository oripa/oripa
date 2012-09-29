package oripa.paint.geometry;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.LinkedList;

import javax.vecmath.Vector2d;

import oripa.ORIPA;
import oripa.geom.GeomUtil;
import oripa.geom.OriLine;
import oripa.paint.PaintContext;


/**
 * Logics using ORIPA data and mouse point in geometric form.
 * @author koji
 *
 */
public class GeometricOperation {

	// returns the OriLine sufficiently closer to point p
	public static OriLine pickLine(Point2D.Double p, double scale) {
		double minDistance = Double.MAX_VALUE;
		OriLine bestLine = null;

		for (OriLine line : ORIPA.doc.lines) {
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



//	public static boolean pickPointOnLine(Point2D.Double p, Object[] line_vertex, double scale) {
//		double minDistance = Double.MAX_VALUE;
//		OriLine bestLine = null;
//		Vector2d nearestPoint = new Vector2d();
//		Vector2d tmpNearestPoint = new Vector2d();
//
//		for (OriLine line : ORIPA.doc.lines) {
//			double dist = GeomUtil.DistancePointToSegment(new Vector2d(p.x, p.y), line.p0, line.p1, tmpNearestPoint);
//			if (dist < minDistance) {
//				minDistance = dist;
//				bestLine = line;
//				nearestPoint.set(tmpNearestPoint);
//			}
//		}
//
//		if (minDistance / scale < 5) {
//			line_vertex[0] = bestLine;
//			line_vertex[1] = nearestPoint;
//			return true;
//		} else {
//			return false;
//		}
//	}


	public static Vector2d pickVertex(
			PaintContext context, boolean freeSelection){

		
		NearestPoint nearestPosition;

		nearestPosition = NearestVertexFinder.find(context);
		

		Vector2d picked = null; 
		if (nearestPosition.distance < 10.0 / context.scale) {
			picked = nearestPosition.point;
		}
		
		

		if(picked == null && freeSelection == true){
			Point2D.Double currentPoint = context.getLogicalMousePoint();

			OriLine l = pickLine(currentPoint, context.scale);
			if(l != null) {
				picked = new Vector2d();
				Vector2d cp = new Vector2d(currentPoint.x, currentPoint.y);

				GeomUtil.DistancePointToSegment(cp, l.p0, l.p1, picked);
			}
		}

		return picked;
	}

	public static Vector2d pickVertexFromPickedLines(PaintContext context){

		
		NearestPoint nearestPosition;
		nearestPosition = NearestVertexFinder.findFromPickedLine(context);
		

		Vector2d picked = null; 
		if (nearestPosition.distance < 10.0 / context.scale) {
			picked = nearestPosition.point;
		}
		
		return picked;
	}

	public static OriLine pickLine(PaintContext context) {
		return pickLine(context.getLogicalMousePoint(), context.scale);
	}

	public static Vector2d getCandidateVertex(PaintContext context, boolean enableMousePoint){

		Vector2d candidate = context.pickCandidateV;

		if(candidate == null && enableMousePoint){
			Point2D.Double mp = context.getLogicalMousePoint();
			candidate = new Vector2d(mp.x, mp.y);
		}

		return candidate;
	}

	public static Collection<OriLine> shiftLines(Collection<OriLine> lines, double diffX, double diffY){
		LinkedList<OriLine> shiftedLines = new LinkedList<>();

		for (OriLine l : lines) {

			double sx = l.p0.x + diffX;
			double sy = l.p0.y + diffY;

			double ex = l.p1.x + diffX;
			double ey = l.p1.y + diffY;

			OriLine line = new OriLine(sx, sy, ex, ey, l.typeVal);
			shiftedLines.add(line);
		}

		return shiftedLines;
	}

}
