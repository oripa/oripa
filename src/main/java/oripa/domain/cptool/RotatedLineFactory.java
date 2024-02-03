package oripa.domain.cptool;

import java.util.ArrayList;
import java.util.Collection;

import oripa.geom.RectangleDomain;
import oripa.value.OriLine;
import oripa.value.OriPoint;
import oripa.vecmath.Vector2d;

public class RotatedLineFactory {

	/**
	 * create copy of selected lines with a rotation around specified center
	 * point. For a line l, this method creates rotatedLine(l, angleDeg * i) for
	 * i = 1 ... repetitionCount.
	 *
	 * @param cx
	 *            x of center
	 * @param cy
	 *            y of center
	 * @param angleDeg
	 *            amount of rotation in degrees
	 * @param repetitionCount
	 * @param paperSize
	 *
	 * @return rotated lines
	 */
	public Collection<OriLine> createRotatedLines(
			final double cx, final double cy, final double angleDeg, final int repetitionCount,
			final Collection<OriLine> selectedLines, final Collection<OriLine> creasePattern) {

		ArrayList<OriLine> rotatedLines = new ArrayList<OriLine>();

		var domain = RectangleDomain.createFromSegments(creasePattern);
		var clipper = new RectangleClipper(domain);

		double angle = angleDeg * Math.PI / 180.0;

		for (int i = 0; i < repetitionCount; i++) {
			double angleRad = angle * (i + 1);
			OriPoint center = new OriPoint(cx, cy);

			rotatedLines.addAll(selectedLines.stream()
					.map(l -> createRotatedLine(l, center, angleRad))
					.filter(rl -> clipper.clip(rl))
					.toList());
		}

		return rotatedLines;
	}

	private OriLine createRotatedLine(final OriLine line, final OriPoint center,
			final double angleRad) {
		var r0 = rotateAroundCenter(line.getP0(), center, angleRad);
		var r1 = rotateAroundCenter(line.getP1(), center, angleRad);

		return new OriLine(r0, r1, line.getType());
	}

	private Vector2d rotateAroundCenter(final Vector2d p, final Vector2d center,
			final double angleRad) {

		var shiftedToOrigin = p.subtract(center);

		var rotated = rotate(shiftedToOrigin, angleRad).add(center);

		return rotated.add(center);
	}

	private OriPoint rotate(final Vector2d p, final double angleRad) {

		var rotatedX = p.getX() * Math.cos(angleRad) - p.getY() * Math.sin(angleRad);
		var rotatedY = p.getX() * Math.sin(angleRad) + p.getY() * Math.cos(angleRad);

		OriPoint rotated = new OriPoint(rotatedX, rotatedY);

		return rotated;
	}
}
