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

package oripa.value;

import javax.vecmath.Vector2d;

import oripa.geom.Line;
import oripa.geom.Segment;

public class OriLine implements Comparable<OriLine> {

	private static final int TYPE_NONE = 0;
	private static final int TYPE_CUT = 1;
	private static final int TYPE_RIDGE = 2;
	private static final int TYPE_VALLEY = 3;
	private static final int TYPE_CUT_MODEL = 4;

	public enum Type {

		NONE(TYPE_NONE), CUT(TYPE_CUT), RIDGE(TYPE_RIDGE), VALLEY(TYPE_VALLEY), CUT_MODEL(
				TYPE_CUT_MODEL);

		private int val;

		private Type(final int val) {
			this.val = val;
		}

		public int toInt() {
			return val;
		}

		public static Type fromInt(final int val) {
			Type type;
			switch (val) {

			case TYPE_CUT:
				type = CUT;
				break;

			case TYPE_RIDGE:
				type = RIDGE;
				break;

			case TYPE_VALLEY:
				type = VALLEY;
				break;

			case TYPE_CUT_MODEL:
				type = CUT_MODEL;
				break;

			case TYPE_NONE:
				type = NONE;
				break;

			default:
				throw new IllegalArgumentException();
			}

			return type;
		}
	};

	private Type type = Type.NONE;

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

	@Override
	public String toString() {
		return "" + p0 + "" + p1;
	}

//	public void changeToNextType() {
//		switch (type) {
//		case VALLEY:
//			type = Type.NONE;
//			break;
//		case RIDGE:
//			type = Type.VALLEY;
//			break;
//		case CUT:
//			type = Type.RIDGE;
//			break;
//		case NONE:
//			type = Type.RIDGE;
//			break;
//		default:
//		}
//	}

	public Segment getSegment() {
		return new Segment(p0, p1);
	}

	public Line getLine() {
		return new Line(p0, new Vector2d(p1.x - p0.x, p1.y - p0.y));
	}

	/**
	 * gives order to this class's object.
	 *
	 * line type is not in comparison because there is only one line in the real
	 * world if the two ends of the line are determined.
	 *
	 * @param oline
	 * @return
	 */
	@Override
	public int compareTo(final OriLine oline) {

		int comparison00 = this.p0.compareTo(oline.p0);
		int comparison11 = this.p1.compareTo(oline.p1);

		if (comparison00 == 0) {
			return comparison11;
		}

		return comparison00;
	}

	/**
	 *
	 * line type is not compared because there is only one line in the real
	 * world if the two ends of the line are determined.
	 */
	@Override
	public boolean equals(final Object obj) {

		// same class?
		if (!(obj instanceof OriLine)) {
			return false;
		}

		OriLine oline = (OriLine) obj;
		int comparison00 = this.p0.compareTo(oline.p0);
		int comparison01 = this.p0.compareTo(oline.p1);
		int comparison10 = this.p1.compareTo(oline.p0);
		int comparison11 = this.p1.compareTo(oline.p1);

		// same direction?
		if (comparison00 == 0 && comparison11 == 0) {
			return true;
		}

		// reversed direction?
		if (comparison01 == 0 && comparison10 == 0) {
			return true;
		}

		// differs
		return false;
	}

}
