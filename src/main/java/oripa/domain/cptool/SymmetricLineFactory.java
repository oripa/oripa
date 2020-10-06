package oripa.domain.cptool;

import java.util.Collection;
import java.util.LinkedList;

import javax.vecmath.Vector2d;

import oripa.geom.GeomUtil;
import oripa.geom.Ray;
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
		 * @param bestLine
		 *            bestLineを登録する
		 */
		public void setBestLine(final OriLine bestLine) {
			this.bestLine = bestLine;
		}

		/**
		 * @return bestPoint
		 */
		public Vector2d getBestPoint() {
			return bestPoint;
		}

		/**
		 * @param bestPoint
		 *            bestPointを登録する
		 */
		public void setBestPoint(final Vector2d bestPoint) {
			this.bestPoint = bestPoint;
		}

	}

	/**
	 * v1-v2 is the symmetry line, v0-v1 is the subject to be copied.
	 *
	 * @param v0
	 * @param v1
	 * @param v2
	 * @param creasePattern
	 * @throws PainterCommandFailedException
	 */
	public OriLine createSymmetricLine(
			final Vector2d v0, final Vector2d v1, final Vector2d v2,
			final Collection<OriLine> creasePattern, final OriLine.Type lineType)
			throws PainterCommandFailedException {

		BestPair pair = findBestPair(v0, v1, v2, creasePattern);

		Vector2d bestPoint = pair.getBestPoint();

		if (bestPoint == null) {
			throw new PainterCommandFailedException(
					"failed to find the terminal of symmetric line");
		}

		return new OriLine(v1, bestPoint, lineType);
	}

	/**
	 * Core logic for creating symmetric line
	 *
	 * @param v0
	 * @param v1
	 * @param v2
	 * @param creasePattern
	 * @return
	 */
	private BestPair findBestPair(
			final Vector2d v0, final Vector2d v1, final Vector2d v2,
			final Collection<OriLine> creasePattern) {
		BestPair bestPair = new BestPair();

		Vector2d v3 = GeomUtil.getSymmetricPoint(v0, v1, v2);
		Ray ray = new Ray(v1, new Vector2d(v3.x - v1.x, v3.y - v1.y));

		double minDist = Double.MAX_VALUE;
		for (OriLine l : creasePattern) {
			Vector2d crossPoint = GeomUtil.getCrossPoint(ray, l.getSegment());
			if (crossPoint == null) {
				continue;
			}
			double distance = GeomUtil.distance(crossPoint, v1);
			if (distance < CalculationResource.POINT_EPS) {
				continue;
			}

			if (distance < minDist) {
				minDist = distance;
				bestPair.setBestPoint(crossPoint);
				bestPair.setBestLine(l);
			}
		}

		return bestPair;
	}

	/**
	 * This method generates possible rebouncing of the fold.
	 *
	 * @param v0
	 *            terminal point of the line to be copied
	 * @param v1
	 *            connecting point of symmetry line and the line to be copied.
	 * @param v2
	 *            terminal point of symmetry line
	 * @param startV
	 * @param creasePattern
	 *
	 * @return a collection of auto walk line
	 * @throws PainterCommandFailedException
	 */
	public Collection<OriLine> createSymmetricLineAutoWalk(
			final Vector2d v0, final Vector2d v1, final Vector2d v2, final Vector2d startV,
			final Collection<OriLine> creasePattern, final OriLine.Type lineType)
			throws PainterCommandFailedException {

		LinkedList<OriLine> autoWalkLines = new LinkedList<>();

		addSymmetricLineAutoWalk(v0, v1, v2, 0, startV, creasePattern, autoWalkLines, lineType);

		return autoWalkLines;
	}

	/**
	 * add new symmetric line to {@code autoWalkLines} recursively.
	 *
	 * @param v0
	 * @param v1
	 * @param v2
	 * @param stepCount
	 * @param startV
	 * @param creasePattern
	 * @param autoWalkLines
	 */
	private void addSymmetricLineAutoWalk(
			final Vector2d v0, final Vector2d v1, final Vector2d v2, int stepCount,
			final Vector2d startV,
			final Collection<OriLine> creasePattern, final Collection<OriLine> autoWalkLines,
			final OriLine.Type lineType) {

		// FIXME this method does not detect loop path. it causes meaningless
		// recursion.

		stepCount++;
		if (stepCount > 36) {
			return;
		}

		BestPair pair = findBestPair(v0, v1, v2, creasePattern);

		Vector2d bestPoint = pair.getBestPoint();
		OriLine bestLine = pair.getBestLine();

		if (bestPoint == null) {
			return;
		}

		OriLine autoWalk = new OriLine(
				v1, bestPoint, lineType);

		autoWalkLines.add(autoWalk);

		if (GeomUtil.distance(bestPoint, startV) < CalculationResource.POINT_EPS) {
			return;
		}

		addSymmetricLineAutoWalk(
				v1, bestPoint, bestLine.p0, stepCount, startV,
				creasePattern, autoWalkLines, lineType);

	}

}
