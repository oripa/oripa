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

import java.util.Collection;

import javax.vecmath.Vector2d;

import oripa.value.OriLine;

/**
 * A rectangle domain fitting to given lines.
 *
 * Position coordinate is the same as screen. (top is smaller)
 */
public class RectangleDomain {
	private double left, right, top, bottom;

	/**
	 * construct this instance fit to given lines
	 *
	 * @param target
	 */
	public RectangleDomain(final Collection<OriLine> target) {

		initialize();

		for (OriLine line : target) {
			enlarge(line.p0);
			enlarge(line.p1);
		}

	}

	public RectangleDomain() {
		initialize();
	}

	private void initialize() {
		left = Double.POSITIVE_INFINITY;
		right = Double.NEGATIVE_INFINITY;
		top = Double.POSITIVE_INFINITY;
		bottom = Double.NEGATIVE_INFINITY;
	}

	/**
	 * Enlarge this domain as it includes given point.
	 *
	 * @param v
	 */
	public void enlarge(final Vector2d v) {
		left = Math.min(left, v.x);
		right = Math.max(right, v.x);
		top = Math.min(top, v.y);
		bottom = Math.max(bottom, v.y);
	}

	/**
	 * Enlarge this domain as it includes all given points.
	 *
	 * @param points
	 */
	public void enlarge(final Collection<Vector2d> points) {
		points.forEach(p -> enlarge(p));
	}

	/**
	 * @return left
	 */
	public double getLeft() {
		return left;
	}

	/**
	 * @return right
	 */
	public double getRight() {
		return right;
	}

	/**
	 * @return top
	 */
	public double getTop() {
		return top;
	}

	/**
	 * @return bottom
	 */
	public double getBottom() {
		return bottom;
	}

	public double getWidth() {
		return computeGap(left, right);
	}

	public double getHeight() {
		return computeGap(top, bottom);
	}

	public double maxWidthHeight() {
		return Math.max(getWidth(), getHeight());
	}

	public double getCenterX() {
		return (left + right) / 2;
	}

	public double getCenterY() {
		return (top + bottom) / 2;
	}

	private double computeGap(final double a, final double b) {
		return Math.max(a, b) - Math.min(a, b);
	}
}