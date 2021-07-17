package oripa.domain.paint.geometry;

import javax.vecmath.Vector2d;

import oripa.domain.paint.PaintContext;
import oripa.geom.GeomUtil;
import oripa.value.CalculationResource;
import oripa.value.OriLine;

/**
 * Logics using ORIPA data and mouse point in geometric form.
 *
 * @author koji
 *
 */
public class NearestItemFinder {

	private static double scaleThreshold(final PaintContext context) {
		return CalculationResource.CLOSE_THRESHOLD / context.getScale();
	}

	public static Vector2d pickVertex(
			final PaintContext paintContext, final boolean freeSelection) {

		NearestPoint nearestPosition;

		nearestPosition = NearestVertexFinder.findAround(paintContext, scaleThreshold(paintContext));

		Vector2d picked = null;

		if (nearestPosition != null) {
			picked = new Vector2d(nearestPosition.point);
		}

		if (picked == null && freeSelection == true) {
			var currentPoint = paintContext.getLogicalMousePoint();

			OriLine l = pickLine(paintContext);
			if (l != null) {
				picked = new Vector2d();
				Vector2d cp = new Vector2d(currentPoint.x, currentPoint.y);

				GeomUtil.distancePointToSegment(cp, l.p0, l.p1, picked);
			}
		}

		return picked;
	}

	public static Vector2d pickVertexFromPickedLines(
			final PaintContext paintContext) {

		NearestPoint nearestPosition;
		nearestPosition = NearestVertexFinder.findFromPickedLines(paintContext);

		Vector2d picked = null;
		if (nearestPosition.distance < scaleThreshold(paintContext)) {
			picked = nearestPosition.point;
		}

		return picked;
	}

	/**
	 * Returns the OriLine sufficiently close to mouse point.
	 */
	public static OriLine pickLine(
			final PaintContext paintContext) {
		var lines = paintContext.getCreasePattern();
		var mp = paintContext.getLogicalMousePoint();

		double minDistance = Double.MAX_VALUE;
		OriLine bestLine = null;

		for (OriLine line : lines) {
			double dist = GeomUtil.distancePointToSegment(new Vector2d(mp.x, mp.y), line.p0, line.p1);
			if (dist < minDistance) {
				minDistance = dist;
				bestLine = line;
			}
		}

		if (minDistance < scaleThreshold(paintContext)) {
			return bestLine;
		} else {
			return null;
		}
	}

	public static Vector2d getCandidateVertex(
			final PaintContext paintContext,
			final boolean enableMousePoint) {

		Vector2d candidate = paintContext.getCandidateVertexToPick();

		if (candidate == null && enableMousePoint) {
			var mp = paintContext.getLogicalMousePoint();
			candidate = new Vector2d(mp.x, mp.y);
		}

		return candidate;
	}

	public static Vector2d getNearestInAngleSnapCrossPoints(final PaintContext paintContext) {
		return NearestVertexFinder.findNearestVertex(
				paintContext.getLogicalMousePoint(),
				paintContext.getAngleSnapCrossPoints()).point;
	}

}
