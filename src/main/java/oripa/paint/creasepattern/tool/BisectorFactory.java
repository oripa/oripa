package oripa.paint.creasepattern.tool;

import javax.vecmath.Vector2d;

import oripa.geom.GeomUtil;
import oripa.geom.Line;
import oripa.paint.core.PaintConfig;
import oripa.resource.Constants;
import oripa.value.OriLine;

public class BisectorFactory {

	/**
	 * create perpendicular bisector line between v0 and v1
	 * @param v0
	 * @param v1
	 * @param creasePattern
	 * @param paperSize
	 */
	public OriLine createPerpendicularBisector(
			Vector2d v0, Vector2d v1,
			double paperSize) {

		Vector2d cp = new Vector2d(v0);
		cp.add(v1);
		cp.scale(0.5);

		Vector2d dir = new Vector2d();
		dir.sub(v0, v1);
		double tmp = dir.y;
		dir.y = -dir.x;
		dir.x = tmp;
		dir.scale(Constants.DEFAULT_PAPER_SIZE * 8);

		OriLine bisector = new OriLine(
				cp.x - dir.x, cp.y - dir.y,
				cp.x + dir.x, cp.y + dir.y, PaintConfig.inputLineType);

		GeomUtil.clipLine(bisector, paperSize / 2);
		
		return bisector;
	}

	/**
	 * create a bisector line from v1 to given line.
	 * @param v0
	 * @param v1
	 * @param v2
	 * @param l
	 * @param creasePattern
	 */
	public OriLine createAngleBisectorLine(
			Vector2d v0, Vector2d v1, Vector2d v2,
			OriLine l) {
		
		Vector2d dir = GeomUtil.getBisectorVec(v0, v1, v2);
		Vector2d cp = GeomUtil.getCrossPoint(
				new Line(l.p0, new Vector2d(l.p1.x - l.p0.x, l.p1.y - l.p0.y)),
				new Line(v1, dir));

		OriLine bisector = new OriLine(v1, cp, PaintConfig.inputLineType);
		return bisector;

	}

}
