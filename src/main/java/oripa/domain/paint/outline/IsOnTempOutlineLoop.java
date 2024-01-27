package oripa.domain.paint.outline;

import java.util.Collection;

import oripa.domain.paint.util.PairLoop;
import oripa.geom.GeomUtil;
import oripa.vecmath.Vector2d;

public class IsOnTempOutlineLoop {
	public boolean execute(
			final Collection<Vector2d> outlineVertices, final Vector2d target,
			final double eps) {
		return PairLoop.iterateAll(outlineVertices,
				(p0, p1) -> GeomUtil.distancePointToSegment(target, p0, p1) >= eps).isPresent();
	}
}
