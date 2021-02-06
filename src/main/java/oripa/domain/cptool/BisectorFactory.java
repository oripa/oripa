package oripa.domain.cptool;

import javax.vecmath.Vector2d;

import oripa.geom.GeomUtil;
import oripa.geom.Line;
import oripa.geom.RectangleDomain;
import oripa.value.OriLine;

public class BisectorFactory {

	/**
	 * create perpendicular bisector line between v0 and v1
	 *
	 * @param v0
	 *            an end point of split line
	 * @param v1
	 *            an end point of split line
	 * @param domain
	 *            including the paper
	 * @param lineType
	 *            the type of the bisector line
	 * @return perpendicular bisector line
	 */
	public OriLine createPerpendicularBisector(
			final Vector2d v0, final Vector2d v1,
			final RectangleDomain domain, final OriLine.Type lineType) {

		Vector2d cp = new Vector2d(v0);
		cp.add(v1);
		cp.scale(0.5);

		double paperSize = domain.maxWidthHeight();

		Vector2d dir = new Vector2d();
		dir.sub(v0, v1);
		double tmp = dir.y;
		dir.y = -dir.x;
		dir.x = tmp;
		dir.scale(paperSize * 8);

		OriLine bisector = new OriLine(
				cp.x - dir.x, cp.y - dir.y,
				cp.x + dir.x, cp.y + dir.y, lineType);

		GeomUtil.clipLine(bisector, domain);

		return bisector;
	}

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
	public OriLine createAngleBisectorLine(
			final Vector2d v0, final Vector2d v1, final Vector2d v2,
			final OriLine l, final OriLine.Type lineType) {

		Vector2d dir = GeomUtil.getBisectorVec(v0, v1, v2);
		Vector2d cp = GeomUtil.getCrossPoint(
				new Line(l.p0, new Vector2d(l.p1.x - l.p0.x, l.p1.y - l.p0.y)),
				new Line(v1, dir));

		OriLine bisector = new OriLine(v1, cp, lineType);
		return bisector;

	}

}
