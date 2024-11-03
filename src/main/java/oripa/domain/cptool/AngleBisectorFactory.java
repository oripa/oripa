package oripa.domain.cptool;

import oripa.geom.GeomUtil;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

public class AngleBisectorFactory {

	/**
	 * create a bisector line from v1 to given line.
	 *
	 * @param v0
	 *            the end point of a line incident to the angle point
	 * @param v1
	 *            the vertex of the angle
	 * @param v2
	 *            the end point of a line incident to the angle point
	 * @param l
	 *            a line which will cross the bisector line. the cross point
	 *            will be the end point of the bisector line.
	 * @param lineType
	 *            the type of the bisector line
	 * @return angle bisector line
	 */
	public OriLine create(
			final Vector2d v0, final Vector2d v1, final Vector2d v2,
			final OriLine l, final OriLine.Type lineType) {

		var cpOpt = GeomUtil.getCrossPoint(l.getLine(), GeomUtil.getBisectorLine(v0, v1, v2));

		return cpOpt.map(cp -> new OriLine(v1, cp, lineType)).get();

	}

}
