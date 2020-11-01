package oripa.domain.cptool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import oripa.geom.RectangleDomain;
import oripa.value.OriLine;
import oripa.value.OriPoint;

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

		var domain = new RectangleDomain(creasePattern);
		var clipper = new oripa.domain.paint.util.RectangleClipper(
				domain.getLeft(), domain.getTop(), domain.getRight(), domain.getBottom());

		double angle = angleDeg * Math.PI / 180.0;

		for (int i = 0; i < repetitionCount; i++) {
			double angleRad = angle * (i + 1);
			OriPoint center = new OriPoint(cx, cy);

			rotatedLines.addAll(selectedLines.stream()
					.map(l -> createRotatedLine(l, center, angleRad))
					.filter(rl -> clipper.clip(rl))
					.collect(Collectors.toList()));
		}

		return rotatedLines;
	}

	private OriLine createRotatedLine(final OriLine line, final OriPoint center,
			final double angleRad) {
		OriPoint r0 = rotateAroundCenter(line.p0, center, angleRad);
		OriPoint r1 = rotateAroundCenter(line.p1, center, angleRad);

		return new OriLine(r0, r1, line.getType());
	}

	private OriPoint rotateAroundCenter(final OriPoint p, final OriPoint center,
			final double angleRad) {

		OriPoint shiftedToCenter = new OriPoint(p.x - center.x, p.y - center.y);

		OriPoint rotated = rotate(shiftedToCenter, angleRad);

		rotated.add(center);

		return rotated;
	}

	private OriPoint rotate(final OriPoint p, final double angleRad) {
		OriPoint rotated = new OriPoint();

		rotated.x = p.x * Math.cos(angleRad) - p.y * Math.sin(angleRad);
		rotated.y = p.x * Math.sin(angleRad) + p.y * Math.cos(angleRad);

		return rotated;
	}
}
