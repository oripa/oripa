package oripa.domain.cptool;

import oripa.geom.GeomUtil;
import oripa.geom.Line;
import oripa.geom.RectangleDomain;
import oripa.geom.Segment;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

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
			final RectangleDomain domain, final OriLine.Type lineType, final double pointEps) {

		Vector2d cp = v0.addition(v1).multiply(0.5);

		double paperSize = domain.maxWidthHeight();

		Vector2d dir = v0.subtract(v1).normalization();
		double tmp = dir.getY();
		dir = new Vector2d(tmp, -dir.getX()).multiply(paperSize * 8);

		Segment bisector = new Segment(
				cp.subtract(dir),
				cp.addition(dir));

		return new OriLine(bisector, lineType);
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
				l.getLine(),
				new Line(v1, dir));

		OriLine bisector = new OriLine(v1, cp, lineType);
		return bisector;

	}

}
