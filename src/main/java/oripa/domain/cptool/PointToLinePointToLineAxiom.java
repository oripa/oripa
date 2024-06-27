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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import oripa.geom.GeomUtil;
import oripa.geom.Line;
import oripa.geom.Segment;
import oripa.util.MathUtil;
import oripa.util.Pair;
import oripa.vecmath.Vector2d;

/**
 * Origami axiom 6
 *
 * @author OUCHI Koji
 *
 */
public class PointToLinePointToLineAxiom {

	/**
	 *
	 * @param p0
	 * @param s0
	 * @param p1
	 * @param s1
	 * @param range
	 * @param pointEps
	 * @return
	 */
	public List<Line> createFoldLines(final Vector2d p0, final Segment s0, final Vector2d p1, final Segment s1,
			final double range,
			final double pointEps) {

		var line0 = s0.getLine();
		var line1 = s1.getLine();

		var crossPointOpt = GeomUtil.getCrossPoint(line0, line1);

		if (crossPointOpt.isEmpty()) {
			return List.of();
		}

		var crossPoint = crossPointOpt.orElseThrow();
		var p0Moved = p0.subtract(crossPoint);
		var p1Moved = p1.subtract(crossPoint);

		var dir0 = line0.getDirection();
		var dir1 = line1.getDirection();

		var theta0 = dir0.ownAngle();
		var theta1 = dir1.ownAngle();

		var results = new ArrayList<Pair<Double, Line>>();

		double initialX0Inverted = rotate(new double[] { p0Moved.getX(), p0Moved.getY() }, -theta0)[0];
		for (double d = 0; d < range; d += range / 50) {
			double x0Inverted_d = initialX0Inverted - range / 2 + d;

			Function<Double, Double> discriminant = (
					x0Inverted) -> solve(x0Inverted, theta0, theta1, p0Moved, p1Moved, pointEps).discriminant;

			try {
				var x0Answer = MathUtil.newtonMethod(discriminant, x0Inverted_d, pointEps, 1e-10 * range);

				if (results.stream().anyMatch(r -> MathUtil.areEqual(x0Answer, r.getV1(), pointEps))) {
					continue;
				}

				var solution = solve(x0Answer, theta0, theta1, p0Moved, p1Moved, pointEps);

				results.add(new Pair<Double, Line>(x0Answer, new Line(
						new Vector2d(solution.xy[0], solution.xy[1]).add(crossPoint),
						new Vector2d(1, solution.slope))));
			} catch (Exception e) {

			}
		}
		return filterPossibleLines(p0, s0, p1, s1,
				results.stream().map(r -> r.getV2()).toList(), pointEps);
	}

	private List<Line> filterPossibleLines(final Vector2d p0, final Segment s0, final Vector2d p1, final Segment s1,
			final List<Line> lines, final double pointEps) {

		return lines.stream()
				.filter(line -> {
					var p0Folded = GeomUtil.getSymmetricPoint(p0, line.getPoint(),
							line.getPoint().add(line.getDirection()));
					var p1Folded = GeomUtil.getSymmetricPoint(p1, line.getPoint(),
							line.getPoint().add(line.getDirection()));

					if (GeomUtil.distancePointToSegment(p0Folded, s0) > pointEps) {
						return false;
					}

					if (GeomUtil.distancePointToSegment(p1Folded, s1) > pointEps) {
						return false;
					}

					return true;

				}).toList();
	}

	private double computeSlope(final double x, final double p, final double q) {
		return (x - p) / q;
	}

	private double rotateSlope(final double slope, final double theta) {
		var tan = Math.tan(theta);
		return (tan + slope) / (1 - slope * tan);
	}

	private double computeYInverted(final double x, final double p, final double q) {
		return (x - p) * (x - p) / (2 * q) + q / 2;
	}

	private static class Solution {
		public double slope;
		public double[] xy;
		public double discriminant;

		public Solution(final double slope, final double[] xy, final double discriminant) {
			this.slope = slope;
			this.xy = xy;
			this.discriminant = discriminant;
		}
	}

	/**
	 * Assumes the given values are translated as the cross point of the
	 * specified lines line0 and line1 becomes (0,0), and computes the tangent
	 * line of the parabola0 induced by the point0 and line0. The returned value
	 * includes a discriminant to see how deeply the line crosses the parabola1.
	 * It is known that this axiom is to fold such that the crease touches two
	 * parabolas whose foci and directrixes are the specified points and the
	 * specified lines, respectively. In this method, first we rotate the
	 * coordinate as the line0 becomes x = 0. Here we call it "inversion" with
	 * line0. Then we compute slope of the parabola0 at given x0 in the inverted
	 * coordinate and rotate it again to invert with line1. We can get the slope
	 * of the tangent line in the rotated coordinate easily. After that, we
	 * consider the simultaneous equations of parabola1 and the tangent line
	 * that fall into quadratic formula. To make the line tangent, we need D =
	 * b^2 - 4ac = 0 for the quadratic equation, which is potentially cubic
	 * formula of x0 and hard to derive the answer analytically.
	 *
	 * @param x0Inverted
	 * @param theta0
	 * @param theta1
	 * @param p0
	 * @param p1
	 * @param pointEps
	 * @return
	 */
	private Solution solve(final double x0Inverted,
			final double theta0, final double theta1, final Vector2d p0, final Vector2d p1, final double pointEps) {

		var point0 = new double[] { p0.getX(), p0.getY() };
		var point1 = new double[] { p1.getX(), p1.getY() };

		var point0Inverted = rotate(point0, -theta0);
		var point1Inverted = rotate(point1, -theta1);

		var p0Inverted = point0Inverted[0];
		var q0Inverted = point0Inverted[1];

		var p1Inverted = point1Inverted[0];
		var q1Inverted = point1Inverted[1];

		var slope0Inverted = computeSlope(x0Inverted, p0Inverted, q0Inverted);

		var slope1Inverted = rotateSlope(slope0Inverted, theta0 - theta1);

		var y0Inverted = computeYInverted(x0Inverted, p0Inverted, q0Inverted);
		var xy0Inverted1 = rotate(new double[] { x0Inverted, y0Inverted }, theta0 - theta1);
		var x0Inverted1 = xy0Inverted1[0];
		var y0Inverted1 = xy0Inverted1[1];

		if (MathUtil.areEqual(q1Inverted, 0, pointEps)) {
			throw new IllegalArgumentException("q1Inverted is 0.");
		}

		var a = -1.0 / (2 * q1Inverted);
		var b = slope1Inverted + p1Inverted / q1Inverted;
		var c = y0Inverted1 - slope1Inverted * x0Inverted1 - q1Inverted / 2
				- p1Inverted * p1Inverted / (2 * q1Inverted);

		var discriminant = b * b - 4.0 * a * c;

		return new Solution(rotateSlope(slope1Inverted, theta1), rotate(xy0Inverted1, theta1), discriminant);
	}

	private double[] rotate(final double[] vector, final double theta) {
		var inverseMatrix = MathUtil.rotationMatrix(theta);
		return MathUtil.product(inverseMatrix, new double[] { vector[0], vector[1] });
	}

}