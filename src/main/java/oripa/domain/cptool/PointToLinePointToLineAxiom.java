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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private static Logger logger = LoggerFactory.getLogger(PointToLinePointToLineAxiom.class);

	/**
	 *
	 * @param p0
	 * @param s0
	 * @param p1
	 * @param s1
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
		for (double d = 0; d < range; d += range / 100) {
			double x0Inverted_d = initialX0Inverted - range / 2 + d;

			logger.debug("x0Inverted_d = {}", x0Inverted_d);

			Function<Double, Double> discriminant = (
					x0Inverted) -> solve(x0Inverted, theta0, theta1, p0Moved.getX(), p0Moved.getY(),
							p1Moved.getX(), p1Moved.getY(), pointEps).discriminant;

			try {
				var x0Answer = MathUtil.newtonMethod(discriminant, x0Inverted_d, 1e-4, pointEps);

				if (results.stream().anyMatch(r -> MathUtil.areEqual(x0Answer, r.getV1(), pointEps))) {
					continue;
				}

				var solution = solve(x0Answer, theta0, theta1, p0Moved.getX(), p0Moved.getY(),
						p1Moved.getX(), p1Moved.getY(), pointEps);

				results.add(new Pair<Double, Line>(x0Answer, new Line(
						new Vector2d(solution.xy[0], solution.xy[1]).add(crossPoint),
						new Vector2d(1, solution.slope))));
			} catch (Exception e) {

			}
		}
		return results.stream().map(r -> r.getV2()).toList();
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

	private Solution solve(final double x0Inverted,
			final double theta0, final double theta1, final double p0, final double q0, final double p1,
			final double q1, final double pointEps) {

		var point0 = new double[] { p0, q0 };
		var point1 = new double[] { p1, q1 };

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

		if (MathUtil.areEqual(q1Inverted, 0, pointEps)) {
			throw new IllegalArgumentException("q1Inverted is 0.");
		}

		var a = -1.0 / (2 * q1Inverted);
		var b = slope1Inverted + p1Inverted / q1Inverted;
		var c = xy0Inverted1[1] - slope1Inverted * x0Inverted + q1Inverted / 2
				+ p1Inverted * p1Inverted / (2 * q1Inverted);

		var discriminant = b * b - 4.0 * a * c;

		return new Solution(rotateSlope(slope1Inverted, theta1), rotate(xy0Inverted1, theta1), discriminant);
	}

	private double[] rotate(final double[] vector, final double theta) {
		var inverseMatrix = MathUtil.rotationMatrix(theta);
		return MathUtil.product(inverseMatrix, new double[] { vector[0], vector[1] });
	}

}