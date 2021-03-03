package oripa.domain.paint.outline;

import java.util.Collection;

import javax.vecmath.Vector2d;

import oripa.domain.paint.util.PairLoop;
import oripa.geom.GeomUtil;

public class IsOnTempOutlineLoop {

	private Vector2d target;
	private double eps;

	public Vector2d execute(
			final Collection<Vector2d> outlineVertices, final Vector2d v,
			final double eps) {

		target = v;
		this.eps = eps;

		return PairLoop.iterateAll(outlineVertices,
				(p0, p1) -> GeomUtil.distancePointToSegment(target, p0, p1) >= eps);
	}
}
