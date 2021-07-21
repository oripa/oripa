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

	public static Vector2d pickVertex(final PaintContext paintContext) {

		NearestPoint nearestPosition = NearestVertexFinder.findAround(paintContext, scaleThreshold(paintContext));

		if (nearestPosition != null) {
			return new Vector2d(nearestPosition.point);
		}

		return null;
	}

	public static Vector2d pickVertexAlongLine(final PaintContext paintContext) {
		var picked = pickVertex(paintContext);
		if (picked != null) {
			return picked;
		}

		OriLine l = pickLine(paintContext);
		if (l == null) {
			return null;
		}

		var vertexAlongLine = new Vector2d();

		GeomUtil.distancePointToSegment(paintContext.getLogicalMousePoint(), l.p0, l.p1, vertexAlongLine);

		return vertexAlongLine;
	}

	public static Vector2d pickVertexFromPickedLines(final PaintContext paintContext) {
		NearestPoint nearestPosition = NearestVertexFinder.findFromPickedLines(paintContext);

		if (nearestPosition.distance < scaleThreshold(paintContext)) {
			return nearestPosition.point;
		}

		return null;
	}

	/**
	 * Returns the OriLine sufficiently close to mouse point.
	 */
	public static OriLine pickLine(final PaintContext paintContext) {
		var lines = paintContext.getCreasePattern();
		var mousePoint = paintContext.getLogicalMousePoint();

		double minDistance = Double.MAX_VALUE;
		OriLine bestLine = null;

		for (OriLine line : lines) {
			double dist = GeomUtil.distancePointToSegment(mousePoint, line.p0, line.p1);
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

	/**
	 * If {@code paintContext} has the latest candidate vertex to pick, this
	 * method return it. Otherwise, mouse point coordinate in the context is
	 * returned.
	 */
	public static Vector2d getCandidateVertexOrMousePoint(final PaintContext paintContext) {

		Vector2d candidate = paintContext.getCandidateVertexToPick();

		return candidate == null ? paintContext.getLogicalMousePoint() : candidate;
	}

	public static Vector2d getNearestInAngleSnapCrossPoints(final PaintContext paintContext) {
		return NearestVertexFinder.findNearestVertex(
				paintContext.getLogicalMousePoint(),
				paintContext.getAngleSnapCrossPoints()).point;
	}

}
