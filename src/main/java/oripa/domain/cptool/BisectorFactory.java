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
	 * @param v1
	 * @param creasePattern
	 * @param paperSize
	 * @param lineType
	 *            {@link OriLine#TYPE_VALLEY} etc.
	 */
	public OriLine createPerpendicularBisector(
			final Vector2d v0, final Vector2d v1,
			final RectangleDomain domain, final OriLine.Type lineType) {

		Vector2d cp = new Vector2d(v0);
		cp.add(v1);
		cp.scale(0.5);

		double paperSize = Math.max(domain.getWidth(), domain.getHeight());

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
	 * @param v1
	 * @param v2
	 * @param l
	 * @param lineType
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
