package oripa.domain.cptool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import oripa.geom.GeomUtil;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

public class LineDivider {
	/**
	 *
	 * @param line
	 * @param v
	 * @return collection containing 2 lines that are the result of division.
	 *         empty if not need to divides
	 */
	public Collection<OriLine> divideLine(
			final OriLine line, final Vector2d v,
			final double pointEps) {

		ArrayList<OriLine> divided = new ArrayList<>(2);

		// Normally you don't want to add a vertex too close to the end of the
		// line
		if (GeomUtil.distance(line.getP0(), v) < pointEps
				|| GeomUtil.distance(line.getP1(), v) < pointEps) {
			return List.of();
		}

		// far from the line
		if (GeomUtil.distancePointToSegment(v, line) > pointEps) {
			return List.of();
		}

		divided.add(new OriLine(line.getP0(), v, line.getType()));
		divided.add(new OriLine(v, line.getP1(), line.getType()));

		return divided;
	}

}
