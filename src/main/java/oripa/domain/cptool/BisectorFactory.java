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

		double paperSize = domain.maxWidthHeight();

		Segment bisector = new PseudoLineFactory().create(
				createPerpendicularBisector(v0, v1), paperSize);

		return new OriLine(bisector, lineType);
	}

	public Line createPerpendicularBisector(
			final Vector2d v0, final Vector2d v1) {
		Vector2d cp = v0.add(v1).multiply(0.5);

		var dir = v0.subtract(v1);
		var perpendicularDir = dir.getRightSidePerpendicular();

		return new Line(cp, perpendicularDir);
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

		var dir = GeomUtil.getBisectorVec(v0, v1, v2);
		var cpOpt = GeomUtil.getCrossPoint(
				l.getLine(),
				new Line(v1, dir));

		return cpOpt.map(cp -> new OriLine(v1, cpOpt.get(), lineType)).get();

	}

}
