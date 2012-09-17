package oripa.paint.geometry;

import java.awt.geom.Point2D;
import java.util.Collection;

import javax.vecmath.Vector2d;

import oripa.ORIPA;
import oripa.geom.OriLine;
import oripa.paint.Globals;
import oripa.paint.PaintContext;

public class NearestVertexFinder {
	/**
	 * Note that {@code nearest} will be affected.
	 * @param p
	 * @param nearest
	 * @param other
	 * @return nearest point
	 */
	private static NearestPoint findNearestOf(
			Point2D.Double p, NearestPoint nearest, Vector2d other){
		
		
		double dist = p.distance(other.x, other.y);
		if (dist < nearest.distance) {
			nearest.point = other;
			nearest.distance = dist;
		}
		
		return nearest;
	}

	private static NearestPoint findNearestOrigamiVertex(Point2D.Double p){

		NearestPoint minPosition = new NearestPoint();

		for (OriLine line : ORIPA.doc.lines) {			

			minPosition = findNearestOf(p, minPosition, line.p0);
			minPosition = findNearestOf(p, minPosition, line.p1);
			
		}

		return minPosition;

	}

		
	private static NearestPoint findNearestVertex(Point2D.Double p, Collection<Vector2d> vertices){

		NearestPoint minPosition = new NearestPoint();

		for(Vector2d vertex : vertices){
			minPosition = findNearestOf(p, minPosition, vertex);
		}

		return minPosition;
	}

	public static NearestPoint find(PaintContext context){
		NearestPoint nearestPosition;

		Point2D.Double currentPoint = context.getLogicalMousePoint();
		nearestPosition = findNearestOrigamiVertex(currentPoint);
		
		if (context.dispGrid) {

			NearestPoint nearestGrid = findNearestVertex(
					currentPoint, context.updateGrids(Globals.gridDivNum));
			
			if(nearestGrid.distance < nearestPosition.distance){
				nearestPosition = nearestGrid;
			}
			
		}

		return nearestPosition;
	}

}
