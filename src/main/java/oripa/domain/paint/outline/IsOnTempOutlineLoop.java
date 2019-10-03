package oripa.domain.paint.outline;

import java.util.Collection;

import javax.vecmath.Vector2d;

import oripa.domain.paint.util.PairLoop;
import oripa.geom.GeomUtil;
import oripa.geom.Line;

public class IsOnTempOutlineLoop implements PairLoop.Block<Vector2d> {

	private Vector2d target;
	private double eps;

	public Vector2d execute(
			final Collection<Vector2d> outlineVertices, final Vector2d v,
			final double eps) {

		target = v;
		this.eps = eps;
		return PairLoop.iterateAll(outlineVertices, this);

	}

	@Override
	public boolean yield(final Vector2d p0, final Vector2d p1) {

		Line line = new Line(p0, new Vector2d(p1.x - p0.x, p1.y - p0.y));

		double distanceToLine = GeomUtil.DistancePointToLine(
				target,
				line);

		if (distanceToLine < eps) {
			return false;
		}

		return true;
	}

}
