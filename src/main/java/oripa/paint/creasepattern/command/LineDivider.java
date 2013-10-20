package oripa.paint.creasepattern.command;

import java.util.Collection;

import javax.vecmath.Vector2d;

import oripa.geom.GeomUtil;
import oripa.value.OriLine;

public class LineDivider {

	public boolean divideLineInCollection(
			OriLine line, Vector2d v,
			Collection<OriLine> creasePattern, double paperSize) {
		
		// Normally you don't want to add a vertex too close to the end of the line
		if (GeomUtil.Distance(line.p0, v) < paperSize * 0.001
				|| GeomUtil.Distance(line.p1, v) < paperSize * 0.001) {
			return false;
		}

		OriLine l0 = new OriLine(line.p0, v, line.typeVal);
		OriLine l1 = new OriLine(v, line.p1, line.typeVal);
		creasePattern.remove(line);
		creasePattern.add(l0);
		creasePattern.add(l1);

		return true;
	}

}
