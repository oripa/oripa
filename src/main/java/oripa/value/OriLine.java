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

package oripa.value;

import static java.lang.Math.abs;
import static oripa.geom.GeomUtil.EPS;

import java.util.Objects;

import javax.vecmath.Vector2d;

import oripa.geom.Line;
import oripa.geom.RectangleDomain;
import oripa.geom.Segment;

public class OriLine implements Comparable<OriLine> {

	private static final int TYPE_AUX = 0;
	private static final int TYPE_CUT = 1;
	private static final int TYPE_MOUNTAIN = 2;
	private static final int TYPE_VALLEY = 3;
	private static final int TYPE_CUT_MODEL = 4;

	public boolean isVertical() {
		return abs(p0.x - p1.x) < EPS;
	}

	public boolean contains(final OriPoint oriPoint) {
		var rectangleDomain = new RectangleDomain(p0.x, p0.y, p1.x, p1.y);
		if (isVertical()) {
			return abs(getAffineXValueAt(oriPoint.y) - oriPoint.x) < EPS && rectangleDomain.contains(oriPoint);
		}
		return abs(getAffineYValueAt(oriPoint.x) - oriPoint.y) < EPS && rectangleDomain.contains(oriPoint);
	}

	public OriPoint middlePoint() {
		return new OriPoint((p0.x + p1.x) / 2, (p0.y + p1.y) / 2);
	}

	public enum Type {

		AUX(TYPE_AUX),
		CUT(TYPE_CUT),
		MOUNTAIN(TYPE_MOUNTAIN),
		VALLEY(TYPE_VALLEY),
		CUT_MODEL(TYPE_CUT_MODEL);

		private final int val;

		Type(final int val) {
			this.val = val;
		}

		public int toInt() {
			return val;
		}

		public static Type fromInt(final int val) throws IllegalArgumentException {
			Type type;
			switch (val) {

			case TYPE_CUT:
				type = CUT;
				break;

			case TYPE_MOUNTAIN:
				type = MOUNTAIN;
				break;

			case TYPE_VALLEY:
				type = VALLEY;
				break;

			case TYPE_CUT_MODEL:
				type = CUT_MODEL;
				break;

			case TYPE_AUX:
				type = AUX;
				break;

			default:
				throw new IllegalArgumentException();
			}

			return type;
		}
	}

	private Type type = Type.AUX;

	public boolean selected;
	public OriPoint p0 = new OriPoint();
	public OriPoint p1 = new OriPoint();

	public OriLine() {
	}

	public OriLine(final OriLine l) {
		selected = l.selected;
		p0.set(l.p0);
		p1.set(l.p1);
		type = l.type;
	}

	public OriLine(final Vector2d p0, final Vector2d p1, final Type type) {
		this.type = type;
		this.p0.set(p0);
		this.p1.set(p1);
	}

	public OriLine(final double x0, final double y0, final double x1, final double y1,
			final Type type) {
		this.type = type;
		this.p0.set(x0, y0);
		this.p1.set(x1, y1);
	}

	public void setType(final Type type) {
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	public boolean isBoundary() {
		return type == Type.CUT;
	}

	public boolean isMV() {
		return type == Type.MOUNTAIN || type == Type.VALLEY;
	}

	public boolean isAux() {
		return type == Type.AUX;
	}

	@Override
	public String toString() {
		return "" + p0 + "" + p1;
	}

	public Segment getSegment() {
		return new Segment(p0, p1);
	}

	public Line getLine() {
		return new Line(p0, new Vector2d(p1.x - p0.x, p1.y - p0.y));
	}

	/**
	 * creates a line such that p0 < p1.
	 *
	 * @return a canonical line.
	 */
	public OriLine createCanonical() {
		return p0.compareTo(p1) > 0
				? new OriLine(p1, p0, type)
				: new OriLine(p0, p1, type);
	}

	/**
	 * Calculates the affine value on the line, at the {@code xTested}
	 * coordinate using the y = ax + b expression
	 */
	public double getAffineYValueAt(final double xTested) {
		return (p1.y - p0.y) * (xTested - p0.x) / (p1.x - p0.x) + p0.y;
	}

	/**
	 * Calculates the affine value on the line, at the {@code yTested}
	 * coordinate using the x = ay + b expression
	 */
	public double getAffineXValueAt(final double yTested) {
		return (p1.x - p0.x) * (yTested - p0.y) / (p1.y - p0.y) + p0.x;
	}

	/**
	 * gives order to this class's object.
	 *
	 * line type is not in comparison because there is only one line in the real
	 * world if the two ends of the line are determined.
	 */
	@Override
	public int compareTo(final OriLine that) {

		int comparison00 = this.p0.compareTo(that.p0);
		int comparison11 = this.p1.compareTo(that.p1);

		if (comparison00 == 0) {
			return comparison11;
		}

		return comparison00;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof OriLine)) {
			return false;
		}

		OriLine that = (OriLine) obj;

		// same direction?
		int comparison00 = this.p0.compareTo(that.p0);
		int comparison11 = this.p1.compareTo(that.p1);
		if (comparison00 == 0 && comparison11 == 0) {
			return this.type.equals(that.type);
		}

		// reversed direction?
		int comparison01 = this.p0.compareTo(that.p1);
		int comparison10 = this.p1.compareTo(that.p0);
		if (comparison01 == 0 && comparison10 == 0) {
			return this.type.equals(that.type);
		}

		// differs
		return false;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if (p0.compareTo(p1) < 0) {
			return Objects.hash(p0, p1);
		}
		return Objects.hash(p1, p0);
	}
}
