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

import java.util.stream.Stream;

import javax.vecmath.Vector2d;

public class Segment {

	private final Vector2d p0;
	private final Vector2d p1;

	public Segment() {
		p0 = new Vector2d();
		p1 = new Vector2d();
	}

	public Segment(final Vector2d p0, final Vector2d p1) {
		this.p0 = p0;
		this.p1 = p1;
	}

	public Segment(final double x0, final double y0, final double x1, final double y1) {
		this();
		this.p0.set(x0, y0);
		this.p1.set(x1, y1);
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
		return new Line(p0, new Vector2d(p1.x - p0.x, p1.y - p0.y));
	}

	/**
	 * Calculates the affine value on the line, at the {@code xTested}
	 * coordinate using the y = ax + b expression
	 *
	 * @param xTested
	 */
	public double getAffineYValueAt(final double xTested) {
		return (getP1().y - getP0().y) * (xTested - getP0().x) / (getP1().x - getP0().x) + getP0().y;
	}

	/**
	 * Calculates the affine value on the line, at the {@code yTested}
	 * coordinate using the x = ay + b expression
	 *
	 * @param yTested
	 */
	public double getAffineXValueAt(final double yTested) {
		return (getP1().x - getP0().x) * (yTested - getP0().y) / (getP1().y - getP0().y) + getP0().x;
	}
}
