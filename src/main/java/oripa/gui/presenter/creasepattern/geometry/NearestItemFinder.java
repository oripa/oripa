package oripa.gui.presenter.creasepattern.geometry;

import java.util.Collection;
import java.util.Optional;

import javax.vecmath.Vector2d;

import oripa.domain.paint.PaintContext;
import oripa.geom.GeomUtil;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.value.CalculationResource;
import oripa.value.OriLine;

/**
 * Logics using ORIPA data and mouse point in geometric form.
 *
 * @author koji
 *
 */
public class NearestItemFinder {

	private static double scaleThreshold(final CreasePatternViewContext context) {
		return CalculationResource.CLOSE_THRESHOLD / context.getScale();
	}

	/**
	 * Returns a vertex sufficiently close to mouse point among the vertices of
	 * crease pattern. Returns {@code null} if no such vertex exists.
	 */
	public static Vector2d pickVertex(final CreasePatternViewContext viewContext, final PaintContext paintContext) {

		NearestPoint nearestPosition = NearestVertexFinder.findAround(
				viewContext.getLogicalMousePoint(), paintContext.getCreasePattern(), paintContext.getGrids(),
				scaleThreshold(viewContext));

		if (nearestPosition != null) {
			return new Vector2d(nearestPosition.point);
		}

		return null;
	}

	/**
	 * Returns a vertex sufficiently close to mouse point among the any points
	 * on the lines of crease pattern. Returns {@code null} if no such vertex
	 * exists.
	 */
	public static Vector2d pickVertexAlongLine(final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {
		var picked = pickVertex(viewContext, paintContext);
		if (picked != null) {
			return picked;
		}

		OriLine l = pickLine(viewContext, paintContext);
		if (l == null) {
			return null;
		}

		var vertexAlongLine = new Vector2d();

		GeomUtil.distancePointToSegment(viewContext.getLogicalMousePoint(), l, vertexAlongLine);

		return vertexAlongLine;
	}

	/**
	 * Returns a vertex sufficiently close to mouse point among end points of
	 * picked lines. Returns {@code null} if no such vertex exists.
	 */
	public static Vector2d pickVertexFromPickedLines(final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {
		NearestPoint nearestPosition = NearestVertexFinder.findNearestVertexFromLines(
				viewContext.getLogicalMousePoint(),
				paintContext.getPickedLines());

		if (nearestPosition.distance < scaleThreshold(viewContext)) {
			return nearestPosition.point;
		}

		return null;
	}

	/**
	 * Returns a OriLine sufficiently close to mouse point. Returns {@code null}
	 * if no such line exists.
	 */
	public static OriLine pickLine(final CreasePatternViewContext viewContext, final PaintContext paintContext) {
		var lines = paintContext.getCreasePattern();
		var mousePoint = viewContext.getLogicalMousePoint();

		double minDistance = Double.MAX_VALUE;
		OriLine bestLine = null;

		for (OriLine line : lines) {
			double dist = GeomUtil.distancePointToSegment(mousePoint, line);
			if (dist < minDistance) {
				minDistance = dist;
				bestLine = line;
			}
		}

		if (minDistance < scaleThreshold(viewContext)) {
			return bestLine;
		} else {
			return null;
		}
	}

	/**
	 * If {@code paintContext} has the latest candidate vertex to pick, this
	 * method returns it. Otherwise, mouse point coordinate in
	 * {@code viewContext} is returned.
	 */
	public static Vector2d getCandidateVertexOrMousePoint(final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {

		Vector2d candidate = paintContext.getCandidateVertexToPick();

		return candidate == null ? viewContext.getLogicalMousePoint() : candidate;
	}

	public static Optional<Vector2d> getNearestInSnapPoints(final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {
		var nearestOpt = NearestVertexFinder.findNearestVertex(
				viewContext.getLogicalMousePoint(),
				paintContext.getSnapPoints());

		return nearestOpt.map(nearest -> nearest.point);
	}

	public static Vector2d getNearestVertex(final CreasePatternViewContext viewContext,
			final Collection<Vector2d> vertices) {
		var nearestOpt = NearestVertexFinder.findNearestVertex(
				viewContext.getLogicalMousePoint(), vertices);

		if (nearestOpt.isEmpty()) {
			return null;
		}

		var nearest = nearestOpt.get();
		if (nearest.distance < scaleThreshold(viewContext)) {
			return nearest.point;
		}

		return null;
	}
}
