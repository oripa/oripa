package oripa.domain.cptool;

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
	 * @return Collection containing 2 lines that are the result of division.
	 *         Empty if not need to divide.
	 */
	public Collection<OriLine> divideLine(
			final OriLine line, final Vector2d v,
			final double pointEps) {

		// Usually you don't want to add a vertex too close to the end of the
		// line
		if (line.pointStream().anyMatch(p -> p.equals(v, pointEps))) {
			return List.of();
		}

		// far from the line
		if (GeomUtil.distancePointToSegment(v, line) > pointEps) {
			return List.of();
		}

		return List.of(
				new OriLine(line.getP0(), v, line.getType()),
				new OriLine(v, line.getP1(), line.getType()));
	}

}
