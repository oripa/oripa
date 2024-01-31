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

import java.util.Objects;
import java.util.stream.Stream;

import oripa.geom.Segment;
import oripa.vecmath.Vector2d;

public class OriLine extends Segment implements Comparable<OriLine> {

	private static final int TYPE_AUX = 0;
	private static final int TYPE_CUT = 1;
	private static final int TYPE_MOUNTAIN = 2;
	private static final int TYPE_VALLEY = 3;
	private static final int TYPE_UNASSIGNED = 4;
	private static final int TYPE_CUT_MODEL = 9;

	public enum Type {

		AUX(TYPE_AUX),
		CUT(TYPE_CUT),
		MOUNTAIN(TYPE_MOUNTAIN),
		VALLEY(TYPE_VALLEY),
		CUT_MODEL(TYPE_CUT_MODEL),
		UNASSIGNED(TYPE_UNASSIGNED);

		private final int val;

		Type(final int val) {
			this.val = val;
		}

		public int toInt() {
			return val;
		}

		public static Type fromInt(final int val) throws IllegalArgumentException {
			return switch (val) {

			case TYPE_CUT -> CUT;

			case TYPE_MOUNTAIN -> MOUNTAIN;

			case TYPE_VALLEY -> VALLEY;

			case TYPE_CUT_MODEL -> CUT_MODEL;

			case TYPE_UNASSIGNED -> UNASSIGNED;

			case TYPE_AUX -> AUX;

			default -> throw new IllegalArgumentException();
			};
		}
	}

	private Type type = Type.AUX;

	private boolean selected;
	private final OriPoint p0;
	private final OriPoint p1;

	public OriLine(final OriLine l) {
		super(l.p0, l.p1);
		p0 = l.p0;
		p1 = l.p1;
		selected = l.selected;
		type = l.type;
	}

	public OriLine(final Vector2d p0, final Vector2d p1, final Type type) {
		super(p0, p1);
		this.p0 = new OriPoint(p0);
		this.p1 = new OriPoint(p1);
		this.type = type;
	}

	public OriLine(final double x0, final double y0, final double x1, final double y1,
			final Type type) {
		super(new OriPoint(x0, y0), new OriPoint(x1, y1));
		this.type = type;
		this.p0 = new OriPoint(x0, y0);
		this.p1 = new OriPoint(x1, y1);
	}

	public OriLine(final Segment segment, final Type type) {
		this(segment.getP0(), segment.getP1(), type);
	}

	public Stream<OriPoint> oriPointStream() {
		return Stream.of(p0, p1);
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

	@Override
	public Vector2d getP0() {
		return p0;
	}

	public OriPoint getOriPoint0() {
		return p0;
	}

	@Override
	public Vector2d getP1() {
		return p1;
	}

	public OriPoint getOriPoint1() {
		return p1;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(final boolean selected) {
		this.selected = selected;
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

	public boolean isFoldLine() {
		return type == Type.MOUNTAIN || type == Type.VALLEY || type == Type.UNASSIGNED;
	}

	public boolean isUnassigned() {
		return type == Type.UNASSIGNED;
	}

	public boolean isAux() {
		return type == Type.AUX;
	}

	@Override
	public String toString() {
		return "" + p0 + "" + p1 + "," + type + "," + hashCode();
	}

	@Override
	public int hashCode() {
		// assure that the order of points is the same
		if (p0.compareTo(p1) < 0) {
			return Objects.hash(p0, p1, this.type);
		}
		return Objects.hash(p1, p0, this.type);
	}
}
