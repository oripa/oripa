/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2005-2009 Jun Mitani http://mitani.cs.tsukuba.ac.jp/

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

package oripa.geom;

import java.util.Optional;
import java.util.stream.Stream;

import oripa.util.MathUtil;
import oripa.vecmath.Vector2d;

public class Segment {
	private final Vector2d p0;
	private final Vector2d p1;

	public Segment(final Vector2d p0, final Vector2d p1) {
		this.p0 = p0;
		this.p1 = p1;
	}

	public Segment(final double x0, final double y0, final double x1, final double y1) {
		this.p0 = new Vector2d(x0, y0);
		this.p1 = new Vector2d(x1, y1);
	}

	public Vector2d getP0() {
		return p0;
	}

	public Vector2d getP1() {
		return p1;
	}

	public Stream<Vector2d> pointStream() {
		return Stream.of(getP0(), getP1());
	}

	public Line getLine() {
		var p0 = getP0();
		var p1 = getP1();
		return new Line(p0, p1.subtract(p0));
	}

	public double length() {
		var p0 = getP0();
		var p1 = getP1();

		var dp = p0.subtract(p1);

		return dp.length();
	}

	/**
	 * Calculates the affine value on the line, at the {@code xTested}
	 * coordinate using the y = ax + b expression
	 *
	 * @param xTested
	 *            {@linkplain Double#NaN} if this segment is vertical.
	 */
	public double getAffineYValueAt(final double xTested) {
		var p0 = getP0();
		var p1 = getP1();

		// vertical line does not have y value.
		var angle = p0.subtract(p1).ownAngle();
		if (MathUtil.areRadianEqual(angle, Math.PI / 2)
				|| MathUtil.areRadianEqual(angle, 3 * Math.PI / 2)) {
			return Double.NaN;
		}

		return (p1.getY() - p0.getY()) * (xTested - p0.getX()) / (p1.getX() - p0.getX())
				+ p0.getY();
	}

	/**
	 * Calculates the affine value on the line, at the {@code yTested}
	 * coordinate using the x = ay + b expression
	 *
	 * @param yTested
	 *            {@linkplain Double#NaN} if this segment is horizontal.
	 */
	public double getAffineXValueAt(final double yTested) {
		var p0 = getP0();
		var p1 = getP1();

		// horizontal line does not have x value.
		var angle = p0.subtract(p1).ownAngle();
		if (MathUtil.areRadianEqual(angle, 0)
				|| MathUtil.areRadianEqual(angle, Math.PI)
				|| MathUtil.areRadianEqual(angle, 2 * Math.PI)) {
			return Double.NaN;
		}

		return (p1.getX() - p0.getX()) * (yTested - p0.getY()) / (p1.getY() - p0.getY()) + p0.getX();
	}

	/**
	 * Both distances between the extremities of the lines should be less than
	 * the threshold. The lines can be reversed, so the test has to be done both
	 * ways
	 *
	 * @param s
	 *            segment to compare
	 * @return true if both segments are (at least almost) equals
	 */
	public boolean equals(final Segment s, final double pointEps) {
		if (getP0().equals(s.getP0(), pointEps) && getP1().equals(s.getP1(), pointEps)) {
			return true;
		}
		if (getP0().equals(s.getP1(), pointEps) && getP1().equals(s.getP0(), pointEps)) {
			return true;
		}

		return false;
	}

	/**
	 * Returns {@code true} if an end point of this segment is close enough to
	 * the given segment's end point.
	 *
	 * @param s
	 * @param pointEps
	 * @return {@code true} if one of the end point of this segment equals that
	 *         of the given segment.
	 */
	public boolean sharesEndPoint(final Segment s, final double pointEps) {
		return getSharedEndPoint(s, pointEps).isPresent();
	}

	public Optional<Vector2d> getSharedEndPoint(final Segment s, final double pointEps) {
		return pointStream().filter(p -> s.pointStream().anyMatch(q -> p.equals(q, pointEps))).findFirst();
	}

	@Override
	public String toString() {
		return "(" + p0 + ", " + p1 + ")";
	}

}
