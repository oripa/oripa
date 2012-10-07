package oripa.doc.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

import javax.vecmath.Vector2d;

import oripa.doc.CalculationResource;
import oripa.geom.GeomUtil;
import oripa.geom.OriLine;

public class AddLine {
	class PointComparatorX implements Comparator<Vector2d> {

		@Override
		public int compare(Vector2d v1, Vector2d v2) {
			if(v1.x == v2.x){
				return 0;
			}
			return v1.x > v2.x ? 1 : -1;
		}
	}

	class PointComparatorY implements Comparator<Vector2d> {

		@Override
		public int compare(Vector2d v1, Vector2d v2) {
			if(v1.y == v2.y){
				return 0;
			}
			return ((Vector2d) v1).y > ((Vector2d) v2).y ? 1 : -1;
		}
	}


	/**
	 * Specified procedure for adding aux line.
	 * @param inputLine
	 * @param currentLines
	 * @return true if the line is aux.
	 */
	public boolean addAuxLine(OriLine inputLine, Collection<OriLine> currentLines){

		if(inputLine.typeVal != OriLine.TYPE_NONE){
			return false;
		}

		LinkedList<OriLine> toBeAdded = new LinkedList<>();

		// If it intersects other line, divide them
		for (Iterator<OriLine> iterator = currentLines.iterator(); iterator.hasNext();) {
			OriLine line = iterator.next();


			// Inputted line does not intersect
			if (line.typeVal != OriLine.TYPE_NONE) {
				continue;
			}
			Vector2d crossPoint = GeomUtil.getCrossPoint(inputLine, line);
			if (crossPoint == null) {
				continue;
			}

			iterator.remove();

			if (GeomUtil.Distance(line.p0, crossPoint) > CalculationResource.POINT_EPS) {
				toBeAdded.add(new OriLine(line.p0, crossPoint, line.typeVal));
			}

			if (GeomUtil.Distance(line.p1, crossPoint) > CalculationResource.POINT_EPS) {
				toBeAdded.add(new OriLine(line.p1, crossPoint, line.typeVal));
			}

			//crossingLines.add(line);
		}

		for(OriLine line : toBeAdded){
			currentLines.add(line);
		}

		return true;
	}

	/**
	 * Adds a new OriLine, also searching for intersections with others 
	 * that would cause their mutual division
	 * 
	 * @param inputLine
	 * @param currentLines	current line list. it will be affected as 
	 * 						new lines are added and unnecessary lines are removed.
	 */

	public void addLine(OriLine inputLine, Collection<OriLine> currentLines) {
		//ArrayList<OriLine> crossingLines = new ArrayList<OriLine>(); // for debug? 

		ArrayList<Vector2d> points = new ArrayList<Vector2d>();
		points.add(inputLine.p0);
		points.add(inputLine.p1);

		// If it already exists, do nothing
		for (OriLine line : currentLines) {
			if (GeomUtil.isSameLineSegment(line, inputLine)) {
				return;
			}
		}

		if (inputLine.typeVal == OriLine.TYPE_NONE){
			// for the case of aux input
			addAuxLine(inputLine, currentLines);
		}
		else{   
			for (OriLine line : currentLines) {

				// Dont devide if the type of line is aux is Aux
				if (//inputLine.typeVal != OriLine.TYPE_NONE && 
						line.typeVal == OriLine.TYPE_NONE) {
					continue;
				}

				// If the intersection is on the end of the line, skip
				if (GeomUtil.Distance(inputLine.p0, line.p0) < CalculationResource.POINT_EPS ||
						GeomUtil.Distance(inputLine.p0, line.p1) < CalculationResource.POINT_EPS||
						GeomUtil.Distance(inputLine.p1, line.p0) < CalculationResource.POINT_EPS||
						GeomUtil.Distance(inputLine.p1, line.p1) < CalculationResource.POINT_EPS) {
					continue;
				}

				if (GeomUtil.DistancePointToSegment(line.p0, inputLine.p0, inputLine.p1) < CalculationResource.POINT_EPS) {
					points.add(line.p0);
				}
				if (GeomUtil.DistancePointToSegment(line.p1, inputLine.p0, inputLine.p1) < CalculationResource.POINT_EPS) {
					points.add(line.p1);
				}

				// Calculates the intersection
				Vector2d crossPoint = GeomUtil.getCrossPoint(inputLine, line);
				if (crossPoint != null) {
					points.add(crossPoint);
				}

			}
		}
		// the sort is done on longer direction in order to suppress underflow error???
		boolean sortByX = Math.abs(inputLine.p0.x - inputLine.p1.x) > Math.abs(inputLine.p0.y - inputLine.p1.y);
		if (sortByX) {
			Collections.sort(points, new PointComparatorX());
		} else {
			Collections.sort(points, new PointComparatorY());
		}

		Vector2d prePoint = points.get(0);

		// add new lines sequentially
		for (int i = 1; i < points.size(); i++) {
			Vector2d p = points.get(i);
			// remove very short line
			if (GeomUtil.Distance(prePoint, p) < CalculationResource.POINT_EPS) {
				continue;
			}

			currentLines.add(new OriLine(prePoint, p, inputLine.typeVal));
			prePoint = p;
		}

	}

}
