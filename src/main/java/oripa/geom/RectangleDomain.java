/*
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package oripa.geom;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.vecmath.Vector2d;

import oripa.value.OriLine;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * A rectangle domain fitting to given lines.
 *
 * Position coordinate is the same as screen. (top is smaller)
 */
public class RectangleDomain {

	private double left;
	private double right;
	private double top;
	private double bottom;

	/**
	 * construct this instance fit to given lines
	 */
	public RectangleDomain(final Collection<OriLine> target) {

		initialize();

		for (OriLine line : target) {
			enlarge(line.p0);
			enlarge(line.p1);
		}
	}

	public RectangleDomain() {
		this(Collections.emptyList());
	}

	public RectangleDomain(double x0, double y0, double x1, double y1) {
		this(List.of(new OriLine(x0, y0, x1, y1, OriLine.Type.AUX)));
	}

	private void initialize() {
		left = Double.POSITIVE_INFINITY;
		right = Double.NEGATIVE_INFINITY;
		top = Double.POSITIVE_INFINITY;
		bottom = Double.NEGATIVE_INFINITY;
	}

	/**
	 * Enlarge this domain as it includes given point.
	 */
	public void enlarge(final Vector2d v) {
		left = min(left, v.x);
		right = max(right, v.x);
		top = min(top, v.y);
		bottom = max(bottom, v.y);
	}

	/**
	 * Enlarge this domain as it includes all given points.
	 */
	public void enlarge(final Collection<Vector2d> points) {
		points.forEach(this::enlarge);
	}

	public double getLeft() {
		return left;
	}

	public double getRight() {
		return right;
	}

	public double getTop() {
		return top;
	}

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
		return max(a, b) - min(a, b);
	}
}