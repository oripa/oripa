package oripa.gui.presenter.creasepattern.geometry;

import java.util.Collection;
import java.util.Optional;

import javax.vecmath.Vector2d;

import oripa.domain.creasepattern.CreasePattern;
import oripa.geom.GeomUtil;
import oripa.value.OriLine;

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
	 *
	 * @return nearest point to p
	 */
	private static void findNearestOf(
			final Vector2d p, final NearestPoint nearest, final Vector2d other) {

		double dist = GeomUtil.distance(p, other);
		if (dist < nearest.distance) {
			nearest.point = other;
			nearest.distance = dist;
		}

	}

	public static Vector2d findNearestOf(
			final Vector2d p, final Vector2d nearest, final Vector2d other) {

		NearestPoint nearestPoint = new NearestPoint();
		nearestPoint.point = nearest;
		nearestPoint.distance = GeomUtil.distance(p, nearest);

		NearestVertexFinder.findNearestOf(
				p, nearestPoint, other);

		return nearestPoint.point;
	}

	public static NearestPoint findNearestVertexFromLines(
			final Vector2d p, final Collection<OriLine> lines) {

		NearestPoint minPosition = new NearestPoint();

		for (OriLine line : lines) {

			findNearestOf(p, minPosition, line.p0);
			findNearestOf(p, minPosition, line.p1);

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
	 * @return nearestPoint in the limit. null if there are no such vertex.
	 */
	public static NearestPoint findAround(
			final Vector2d mousePoint,
			final CreasePattern creasePattern,
			final Collection<Vector2d> grids,
			final double distance) {
		NearestPoint nearestPosition = new NearestPoint();

		var currentPoint = mousePoint;

		Collection<Collection<Vector2d>> vertices = creasePattern.getVerticesInArea(
				currentPoint.x, currentPoint.y, distance);

		for (Collection<Vector2d> locals : vertices) {
			var nearestOpt = findNearestVertex(currentPoint, locals);

			if (nearestOpt.isEmpty()) {
				continue;
			}

			var nearest = nearestOpt.get();

			if (nearest.distance < nearestPosition.distance) {
				nearestPosition = nearest;
			}
		}

		var nearestGridOpt = findNearestVertex(
				currentPoint, grids);

		if (nearestGridOpt.isPresent()) {
			NearestPoint nearestGrid = nearestGridOpt.get();
			if (nearestGrid.distance < nearestPosition.distance) {
				nearestPosition = nearestGrid;
			}
		}

		if (nearestPosition.distance >= distance) {
			return null;
		} else {

//			System.out.println("#area " + vertices.size() +
//					", #v(area1) " + vertices.iterator().next().size() +
//					", scaled limit = " + distance);

		}

		return nearestPosition;
	}
}
