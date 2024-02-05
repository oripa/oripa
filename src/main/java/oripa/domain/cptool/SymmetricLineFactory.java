package oripa.domain.cptool;

import java.util.Collection;
import java.util.LinkedList;

import oripa.geom.GeomUtil;
import oripa.geom.Ray;
import oripa.geom.Segment;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

public class SymmetricLineFactory {

	private class BestPair {
		private Segment bestLine = null;
		private Vector2d bestPoint = null;

		/**
		 * @return bestLine
		 */
		public Segment getBestLine() {
			return bestLine;
		}

		/**
		 * @param bestLine
		 *            sets bestLine
		 */
		public void setBestLine(final Segment bestLine) {
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
		 *            sets bestPoint
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
			final Collection<OriLine> creasePattern, final OriLine.Type lineType, final double pointEps)
			throws PainterCommandFailedException {

		BestPair pair = findBestPair(v0, v1, v2, creasePattern, pointEps);

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
	 * @return a line to be the next base line of symmetry and the cross point
	 *         of new symmetric line and the base.
	 */
	private BestPair findBestPair(
			final Vector2d v0, final Vector2d v1, final Vector2d v2,
			final Collection<OriLine> creasePattern, final double pointEps) {
		BestPair bestPair = new BestPair();

		Vector2d v3 = GeomUtil.getSymmetricPoint(v0, v1, v2);
		Ray ray = new Ray(v1, v3.subtract(v1));

		double minDist = Double.MAX_VALUE;
		for (var l : creasePattern) {
			var crossPointOpt = GeomUtil.getCrossPoint(ray, l);
			if (crossPointOpt.isEmpty()) {
				continue;
			}

			var crossPoint = crossPointOpt.get();

			double distance = GeomUtil.distance(crossPoint, v1);
			if (distance < pointEps) {
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
			final Vector2d v0, final Vector2d v1, final Vector2d v2,
			final Collection<OriLine> creasePattern, final OriLine.Type lineType, final double pointEps)
			throws PainterCommandFailedException {

		var autoWalkLines = new LinkedList<Segment>();

		addSymmetricLineAutoWalk(v0, v1, v2, 0, v0, creasePattern, autoWalkLines, pointEps);

		return autoWalkLines.stream().map(l -> new OriLine(l, lineType)).toList();
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
			final Vector2d v0, final Vector2d v1, final Vector2d v2, final int stepCount,
			final Vector2d startV,
			final Collection<OriLine> creasePattern, final Collection<Segment> autoWalkLines,
			final double pointEps) {

		if (stepCount > 36) {
			return;
		}

		BestPair pair = findBestPair(v0, v1, v2, creasePattern, pointEps);

		var bestPoint = pair.getBestPoint();
		var bestLine = pair.getBestLine();

		if (bestPoint == null) {
			return;
		}

		var autoWalk = new Segment(v1, bestPoint);

		autoWalkLines.add(autoWalk);

		if (GeomUtil.areEqual(bestPoint, startV, pointEps)) {
			return;
		}

		var p0 = bestLine.getP0();
		var p1 = bestLine.getP1();

		addSymmetricLineAutoWalk(
				v1, bestPoint, GeomUtil.areEqual(p0, bestPoint, pointEps) ? p1 : p0, stepCount + 1, startV,
				creasePattern, autoWalkLines, pointEps);

	}

}
