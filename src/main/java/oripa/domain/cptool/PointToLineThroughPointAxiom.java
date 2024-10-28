/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package oripa.domain.cptool;

import static java.lang.Math.sqrt;

import java.util.List;

import oripa.geom.GeomUtil;
import oripa.geom.Line;
import oripa.geom.Segment;
import oripa.util.MathUtil;
import oripa.vecmath.Vector2d;

/**
 * Axiom 5
 *
 * @author OUCHI Koji
 *
 */
public class PointToLineThroughPointAxiom {

	/**
	 * Fold as {@code p} gets onto {@code s} and the crease passes {@code c}.
	 *
	 * @param p
	 * @param c
	 * @param s
	 * @param pointEps
	 * @return
	 */
	public List<Line> createFoldLine(final Vector2d p, final Vector2d c, final Segment s, final double pointEps) {
		var line = s.getLine();
		var p0 = line.getPoint();
		var p1 = p0.add(line.getDirection());
		var dp01 = line.getDirection();

		var lines = solve(p, c, p0, p1, 1e-10).stream()
				.map(parameter -> p0.add(dp01.multiply(parameter)))
				.map(d -> new Line(p.add(d.subtract(p).multiply(0.5)), d.subtract(p).getRightSidePerpendicular()))
				.toList();

		return filterFoldLine(p, s, lines, pointEps);
	}

	private List<Line> filterFoldLine(final Vector2d p, final Segment s, final List<Line> lines,
			final double pointEps) {
		return lines.stream()
				.filter(line -> {
					var folded = GeomUtil.getSymmetricPoint(p, line.getPoint(),
							line.getPoint().add(line.getDirection()));
					return MathUtil.isZero(GeomUtil.distancePointToSegment(folded, s), pointEps);
				})
				.toList();
	}

	private List<Double> solve(final Vector2d p, final Vector2d q, final Vector2d p0, final Vector2d p1,
			final double eps) {
		double x0 = p0.getX();
		double y0 = p0.getY();
		double x1 = p1.getX();
		double y1 = p1.getY();
		double xc = q.getX();
		double yc = q.getY();
		double r = p.distance(q);

		double a = (x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0);
		double b = 2 * (x1 - x0) * (x0 - xc) + 2 * (y1 - y0) * (y0 - yc);
		double c = xc * xc + yc * yc + x0 * x0 + y0 * y0 - 2 * (xc * x0 + yc * y0) - r * r;

		double discriminant = b * b - 4 * a * c;

		if (discriminant < -eps) {
			return List.of();
		}
		if (MathUtil.isZero(discriminant, eps)) {
			return List.of(-b / (2 * a));
		}

		return List.of((-b + sqrt(discriminant)) / (2 * a), (-b - sqrt(discriminant)) / (2 * a));
	}
}
