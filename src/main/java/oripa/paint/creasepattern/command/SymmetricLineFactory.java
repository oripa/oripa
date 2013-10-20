package oripa.paint.creasepattern.command;

import java.util.Collection;
import java.util.LinkedList;

import javax.vecmath.Vector2d;

import oripa.geom.GeomUtil;
import oripa.geom.Ray;
import oripa.paint.core.PaintConfig;
import oripa.value.CalculationResource;
import oripa.value.OriLine;

public class SymmetricLineFactory {

	private class BestPair {
		private OriLine bestLine = null;
		private Vector2d bestPoint = null;
		/**
		 * @return bestLine
		 */
		public OriLine getBestLine() {
			return bestLine;
		}
		/**
		 * @param bestLine bestLineを登録する
		 */
		public void setBestLine(OriLine bestLine) {
			this.bestLine = bestLine;
		}
		/**
		 * @return bestPoint
		 */
		public Vector2d getBestPoint() {
			return bestPoint;
		}
		/**
		 * @param bestPoint bestPointを登録する
		 */
		public void setBestPoint(Vector2d bestPoint) {
			this.bestPoint = bestPoint;
		}
		
	}
	/**
	 * v1-v2 is the symmetry line, v0-v1 is the subject to be copied. 
	 * @param v0
	 * @param v1
	 * @param v2
	 * @param creasePattern
	 */
	public OriLine createSymmetricLine(
			Vector2d v0, Vector2d v1, Vector2d v2,
			Collection<OriLine> creasePattern) {

		BestPair pair = findBestPair(v0, v1, v2, creasePattern);
		Vector2d bestPoint = pair.getBestPoint();
		
		if (bestPoint == null) {
			return null;
		}
		return new OriLine(v1, bestPoint, PaintConfig.inputLineType);
	}

	private BestPair findBestPair(
			Vector2d v0, Vector2d v1, Vector2d v2,
			Collection<OriLine> creasePattern) {
		BestPair bestPair = new BestPair();
		
		Vector2d v3 = GeomUtil.getSymmetricPoint(v0, v1, v2);
		Ray ray = new Ray(v1, new Vector2d(v3.x - v1.x, v3.y - v1.y));

		double minDist = Double.MAX_VALUE;
		for (OriLine l : creasePattern) {
			Vector2d crossPoint = GeomUtil.getCrossPoint(ray, l.getSegment());
			if (crossPoint == null) {
				continue;
			}
			double distance = GeomUtil.Distance(crossPoint, v1);
			if (distance < CalculationResource.POINT_EPS) {
				continue;
			}

			if (distance < minDist) {
				minDist = distance;
				bestPair.setBestPoint(crossPoint);
				bestPair.setBestLine(l);
			}
		}

		if (bestPair.getBestPoint() == null) {
			return null;
		}

		return bestPair;
	}
	
	

	/**
	 * This method generates possible rebouncing of the fold.
	 * 
	 * @param v0 terminal point of the line to be copied
	 * @param v1 connecting point of symmetry line and the line to be copied.
	 * @param v2 terminal point of symmetry line
	 * @param startV
	 * @param creasePattern
	 * @return
	 */
	public Collection<OriLine> createSymmetricLineAutoWalk(
			Vector2d v0, Vector2d v1, Vector2d v2, Vector2d startV,
			Collection<OriLine> creasePattern) {
	
		LinkedList<OriLine> autoWalkLines = new LinkedList<>();

		addSymmetricLineAutoWalk(v0, v1, v2, 0, startV, creasePattern, autoWalkLines);

		return autoWalkLines;
	}
	
	
	// v1-v2 is the symmetry line, v0-v1 is the sbject to be copied.
	// automatically generates possible rebouncing of the fold (used when Ctrl is pressed)
	private void addSymmetricLineAutoWalk(
			Vector2d v0, Vector2d v1, Vector2d v2, int stepCount, Vector2d startV,
			Collection<OriLine> creasePattern, Collection<OriLine> autoWalkLines) {
		stepCount++;
		if (stepCount > 36) {
			return;
		}

		BestPair pair = findBestPair(v0, v1, v2, creasePattern);
		Vector2d bestPoint = pair.getBestPoint();
		OriLine bestLine =  pair.getBestLine();
		
		if (bestPoint == null) {
			return;
		}

		OriLine autoWalk = new OriLine(
				v1, bestPoint, PaintConfig.inputLineType);
		

		autoWalkLines.add(autoWalk);

		if (GeomUtil.Distance(bestPoint, startV) < CalculationResource.POINT_EPS) {
			return;
		}

		addSymmetricLineAutoWalk(
				v1, bestPoint, bestLine.p0, stepCount, startV,
				creasePattern, autoWalkLines);

	}

}
