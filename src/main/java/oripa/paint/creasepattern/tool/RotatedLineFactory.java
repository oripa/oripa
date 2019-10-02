package oripa.paint.creasepattern.tool;

import java.util.ArrayList;
import java.util.Collection;

import oripa.value.OriLine;
import oripa.value.OriPoint;

public class RotatedLineFactory {

	/**
	 * create copy of selected lines with a rotation around specified center point.
	 * For a line l, this method creates rotatedLine(l, angleDeg * i) for i = 1 ... repetitionCount.
	 * 
	 * @param cx       x of center
	 * @param cy       y of center
	 * @param angleDeg         amount of rotation in degrees
	 * @param repetitionCount  
	 * @param paperSize
	 * 
	 * @return rotated lines
	 */
	public Collection<OriLine> createRotatedLines(
			double cx, double cy, double angleDeg, int repetitionCount,
			Collection<OriLine> creasePattern, double paperSize) {

				
		ArrayList<OriLine> rotatedLines = new ArrayList<>();


		oripa.geom.RectangleClipper clipper =
				new oripa.geom.RectangleClipper(
						-paperSize / 2, -paperSize / 2, paperSize / 2, paperSize / 2);

		double angle = angleDeg * Math.PI / 180.0;

		for (int i = 0; i < repetitionCount; i++) {
			double angleRad = angle * (i + 1);
			for (OriLine l : creasePattern) {
				if (!l.selected) {
					continue;
				}

				OriPoint center = new OriPoint(cx, cy);

				OriPoint r0 = rotateAroundCenter(l.p0, center, angleRad);
				OriPoint r1 = rotateAroundCenter(l.p1, center, angleRad);
				
				OriLine rotatedLine = new OriLine(r0, r1, l.getTypeValue());

				if (clipper.clip(rotatedLine)) {
					rotatedLines.add(rotatedLine);
				}
			}
		}

		return rotatedLines;
	}

	private OriPoint rotateAroundCenter(OriPoint p, OriPoint center, double angleRad) {

		OriPoint shiftedToCenter =
				new OriPoint(p.x - center.x, p.y - center.y);

		OriPoint rotated = rotate(shiftedToCenter, angleRad);

		rotated.add(center);
		
		return rotated;
	}
	
	private OriPoint rotate(OriPoint p, double angleRad) {
		OriPoint rotated = new OriPoint();
		
		rotated.x = p.x * Math.cos(angleRad) - p.y * Math.sin(angleRad);
		rotated.y = p.x * Math.sin(angleRad) + p.y * Math.cos(angleRad);
		
		return rotated;
	}
}
