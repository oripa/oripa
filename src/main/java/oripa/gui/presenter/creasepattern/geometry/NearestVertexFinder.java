package oripa.gui.presenter.creasepattern.geometry;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import oripa.domain.creasepattern.CreasePattern;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

public class NearestVertexFinder {
	/**
	 *
	 * Note that {@code nearest} will be affected.
	 *
	 * @param p
	 *            target point
	 * @param nearest
	 *            current nearest point to p
	 * @param other
	 *            new point to be tested
	 */
	private static void findNearestOf(
			final Vector2d p, final NearestPoint nearest, final Vector2d other) {

		double dist = p.distance(other);
		if (dist < nearest.distance) {
			nearest.point = other;
			nearest.distance = dist;
		}

	}

	public static Vector2d findNearestOf(
			final Vector2d p, final Vector2d nearest, final Vector2d other) {

		NearestPoint nearestPoint = new NearestPoint();
		nearestPoint.point = nearest;
		nearestPoint.distance = p.distance(nearest);

		NearestVertexFinder.findNearestOf(
				p, nearestPoint, other);

		return nearestPoint.point;
	}

	public static NearestPoint findNearestVertexFromLines(
			final Vector2d p, final Collection<OriLine> lines) {

		NearestPoint minPosition = new NearestPoint();

		for (OriLine line : lines) {

			findNearestOf(p, minPosition, line.getP0());
			findNearestOf(p, minPosition, line.getP1());

		}

		return minPosition;

	}

	/**
	 * Find the nearest of p among vertices
	 *
	 * @param p
	 * @param vertices
	 * @return nearest point
	 */
	public static Optional<NearestPoint> findNearestVertex(final Vector2d p,
			final Collection<Vector2d> vertices) {

		if (vertices.isEmpty()) {
			return Optional.empty();
		}

		NearestPoint minPosition = new NearestPoint();

		for (Vector2d vertex : vertices) {
			findNearestOf(p, minPosition, vertex);
		}

		return Optional.of(minPosition);
	}

	/**
	 * find the nearest of current mouse point in the circle whose radius =
	 * {@code distance}.
	 *
	 * @param context
	 * @param distance
	 * @return nearestPoint in the limit. Empty if there are no such vertex.
	 */
	public static Optional<NearestPoint> findAround(
			final Vector2d mousePoint,
			final CreasePattern creasePattern,
			final Collection<Vector2d> grids,
			final double distance) {

		var currentPoint = mousePoint;

		Collection<Collection<Vector2d>> vertices = creasePattern.getVerticesInArea(
				currentPoint.getX(), currentPoint.getY(), distance);

		var targetVertices = Stream
				.concat(vertices.stream().flatMap(Collection::stream),
						grids.stream())
				.toList();

		var nearestOpt = findNearestVertex(currentPoint, targetVertices);

		return nearestOpt.filter(nearest -> nearest.distance < distance);
	}
}
