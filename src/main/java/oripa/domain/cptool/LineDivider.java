package oripa.domain.cptool;

import java.util.ArrayList;
import java.util.Collection;

import javax.vecmath.Vector2d;

import oripa.geom.GeomUtil;
import oripa.value.OriLine;

public class LineDivider {
	/**
	 *
	 * @param line
	 * @param v
	 * @return collection containing 2 lines that are the result of division.
	 *         null if not need to divides
	 */
	public Collection<OriLine> divideLine(
			final OriLine line, final Vector2d v,
			final double pointEps) {

		ArrayList<OriLine> divided = new ArrayList<>(2);

		// Normally you don't want to add a vertex too close to the end of the
		// line
		if (GeomUtil.distance(line.p0, v) < pointEps
				|| GeomUtil.distance(line.p1, v) < pointEps) {
			return null;
		}

		// far from the line
		if (GeomUtil.distancePointToSegment(v, line) > pointEps) {
			return null;
		}

		divided.add(new OriLine(line.p0, v, line.getType()));
		divided.add(new OriLine(v, line.p1, line.getType()));

		return divided;
	}

}
