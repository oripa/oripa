package oripa.gui.presenter.creasepattern.geometry;

import java.util.Collection;
import java.util.Optional;

import oripa.domain.paint.PaintContext;
import oripa.geom.GeomUtil;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.value.CalculationResource;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

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
	public static Optional<Vector2d> pickVertex(final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {

		var nearestPositionOpt = NearestVertexFinder.findAround(
				viewContext.getLogicalMousePoint(), paintContext.getCreasePattern(), paintContext.getGrids(),
				scaleThreshold(viewContext));

		return nearestPositionOpt.map(nearestPosition -> nearestPosition.point);
	}

	/**
	 * Returns a vertex sufficiently close to mouse point among the any points
	 * on the lines of crease pattern. Returns {@code null} if no such vertex
	 * exists.
	 */
	public static Optional<Vector2d> pickVertexAlongLine(final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {
		var pickedOpt = pickVertex(viewContext, paintContext);
		if (pickedOpt.isPresent()) {
			return pickedOpt;
		}

		var lineOpt = pickLine(viewContext, paintContext);

		return lineOpt.map(line -> GeomUtil.computeNearestPointToSegment(viewContext.getLogicalMousePoint(), line));
	}

	/**
	 * Returns a vertex sufficiently close to mouse point among end points of
	 * picked lines. Returns {@code null} if no such vertex exists.
	 */
	public static Optional<Vector2d> pickVertexFromPickedLines(final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {
		NearestPoint nearestPosition = NearestVertexFinder.findNearestVertexFromLines(
				viewContext.getLogicalMousePoint(),
				paintContext.getPickedLines());

		if (nearestPosition.distance < scaleThreshold(viewContext)) {
			return Optional.of(nearestPosition.point);
		}

		return Optional.empty();
	}

	/**
	 * Returns a OriLine sufficiently close to mouse point. Returns {@code null}
	 * if no such line exists.
	 */
	public static Optional<OriLine> pickLine(final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {
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
			return Optional.of(bestLine);
		} else {
			return Optional.empty();
		}
	}

	/**
	 * If {@code paintContext} has the latest candidate vertex to pick, this
	 * method returns it. Otherwise, mouse point coordinate in
	 * {@code viewContext} is returned.
	 */
	public static Vector2d getCandidateVertexOrMousePoint(final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {

		var candidateOpt = paintContext.getCandidateVertexToPick();

		return candidateOpt.orElse(viewContext.getLogicalMousePoint());
	}

	public static Optional<Vector2d> getNearestInSnapPoints(final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {
		var nearestOpt = NearestVertexFinder.findNearestVertex(
				viewContext.getLogicalMousePoint(),
				paintContext.getSnapPoints());

		return nearestOpt.map(nearest -> nearest.point);
	}

	public static Optional<Vector2d> getNearestVertex(final CreasePatternViewContext viewContext,
			final Collection<Vector2d> vertices) {
		var nearestOpt = NearestVertexFinder.findNearestVertex(
				viewContext.getLogicalMousePoint(), vertices);

		return nearestOpt.filter(nearest -> nearest.distance < scaleThreshold(viewContext))
				.map(nearest -> nearest.point);
	}
}
