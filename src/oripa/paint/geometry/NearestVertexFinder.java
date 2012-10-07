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
	 * 
	 * Note that {@code nearest} will be affected.
	 * @param p 		target point
	 * @param nearest	current nearest point to p
	 * @param other		new point to be tested
	 * 
	 * @return nearest point to p
	 */
	private static void findNearestOf(
			Point2D.Double p, NearestPoint nearest, Vector2d other){
		
		
		double dist = p.distance(other.x, other.y);
		if (dist < nearest.distance) {
			nearest.point = other;
			nearest.distance = dist;
		}
		
	}

	public static Vector2d findNearestOf(
			Point2D.Double p, Vector2d nearest, Vector2d other){
		
		NearestPoint nearestPoint = new NearestPoint();
		nearestPoint.point = nearest;
		nearestPoint.distance = p.distance(nearest.x, nearest.y);

		NearestVertexFinder.findNearestOf(
				p, nearestPoint, other);
	
		return nearest;
	}


	private static NearestPoint findNearestVertexFromLines(
			Point2D.Double p, Collection<OriLine> lines){

		NearestPoint minPosition = new NearestPoint();

		for (OriLine line : lines) {			

			findNearestOf(p, minPosition, line.p0);
			findNearestOf(p, minPosition, line.p1);
			
		}

		return minPosition;

	}
		
	/**
	 * Find the nearest of p among vertices
	 * @param p
	 * @param vertices
	 * @return nearest point
	 */
	private static NearestPoint findNearestVertex(Point2D.Double p, Collection<Vector2d> vertices){

		NearestPoint minPosition = new NearestPoint();

		for(Vector2d vertex : vertices){
			findNearestOf(p, minPosition, vertex);
		}

		return minPosition;
	}

	/**
	 * find the nearest of current mouse point in the circle whose radius = {@code distance}.
	 * @param context
	 * @param distance
	 * @return nearestPoint in the limit. null if there are no such vertex.
	 */
	public static NearestPoint findAround(PaintContext context, double distance){
		NearestPoint nearestPosition = new NearestPoint();

		Point2D.Double currentPoint = context.getLogicalMousePoint();
		
		
		Collection<Collection<Vector2d>> vertices = ORIPA.doc.getVerticesArea(
				currentPoint.x, currentPoint.y, distance);	
	
		for(Collection<Vector2d> locals : vertices){
			NearestPoint nearest;
			nearest = findNearestVertex(currentPoint, locals);
	
			if(nearest.distance < nearestPosition.distance){
				nearestPosition = nearest;
			}
		}

		if (context.dispGrid) {

			NearestPoint nearestGrid = findNearestVertex(
					currentPoint, context.updateGrids(Globals.gridDivNum));
			
			if(nearestGrid.distance < nearestPosition.distance){
				nearestPosition = nearestGrid;
			}
			
		}
		
		if (nearestPosition.distance >= distance) {
			return null;
		}
		else {
			
//			System.out.println("#area " + vertices.size() + 
//					", #v(area1) " + vertices.iterator().next().size() +
//					", scaled limit = " + distance);
			
		}



		return nearestPosition;
	}

	public static NearestPoint findFromPickedLine(PaintContext context){
		NearestPoint nearestPosition;

		Point2D.Double currentPoint = context.getLogicalMousePoint();
		nearestPosition = findNearestVertexFromLines(
				currentPoint, context.getLines());
		

		return nearestPosition;
	}
}
