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

package oripa.domain.cptool;

import java.util.Collection;
import java.util.Optional;

import oripa.geom.RectangleDomain;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

/**
 * Manages OriLine intersection testing (clipping) with Rectangle Domain
 */
public class RectangleClipper {

	// This implementation encodes to flag bits the position of the end points
	// of the given line. Each bit is 1 if the point is outside of the border
	// line of the rectangle. For example, if LEFT bit is 1 then the point is on
	// the left outside of the rectangle. A point is inside of the rectangle if
	// all bits are 0.
	private static final int LEFT = 1;
	private static final int RIGHT = 1 << 1;
	private static final int TOP = 1 << 2;
	private static final int BOTTOM = 1 << 3;

	private final RectangleDomain domain;
	private final RectangleDomain relaxedDomain;

	/**
	 *
	 * The domain for clipping is slightly larger than given domain. The margin
	 * of the relaxation is determined by epsilon.
	 *
	 * @param domain
	 *            Rectangle domain for clipping
	 * @param epsilon
	 *            Margin
	 */
	public RectangleClipper(final RectangleDomain domain, final double epsilon) {
		double minX = domain.getLeft() - epsilon;
		double minY = domain.getTop() - epsilon;
		double maxX = domain.getRight() + epsilon;
		double maxY = domain.getBottom() + epsilon;

		this.domain = domain;
		relaxedDomain = new RectangleDomain(minX, minY, maxX, maxY);
	}

	public RectangleClipper(final double x0, final double y0, final double x1, final double y1, final double epsilon) {
		this(new RectangleDomain(x0, y0, x1, y1), epsilon);
	}

	/**
	 * extracts lines which intersects this rectangle from given {@code lines}
	 *
	 * @param lines
	 * @return Subset of {@code lines}
	 */
	public Collection<OriLine> selectByArea(final Collection<OriLine> lines) {
		return lines.stream()
				.filter(this::intersects)
				.toList();
	}

	/**
	 * Tells us whether the given line {@code line} intersects the rectangle.
	 * The test is inclusive.
	 *
	 * @param line
	 *            to be tested.
	 *
	 * @return {@code true} if {@code line} is included in or crosses the
	 *         clipping rectangle.
	 */
	public boolean intersects(final OriLine line) {
		return clip(line).isPresent();
	}

	/**
	 * Returns a new line that is the result of intersection with the rectangle.
	 * The test is inclusive.
	 *
	 * @param line
	 *            to be clipped.
	 * @return a clipped line.
	 */
	public Optional<OriLine> clip(final OriLine line) {
		var p0 = line.getP0();
		var p1 = line.getP1();

		// first to avoid parameter modification
		final int p0Code = calcCode(p0.getX(), p0.getY());
		final int p1Code = calcCode(p1.getX(), p1.getY());

		// the line is in the rectangle
		if ((p0Code == 0) && (p1Code == 0)) {
			return Optional.of(line);
		}

		// the line is in the {left, right, top, bottom} area.
		if ((p0Code & p1Code) != 0) {
			return Optional.empty();
		}

		var cp0Opt = calcClippedPointOptional(p0Code, line);
		var cp1Opt = calcClippedPointOptional(p1Code, line);

		Optional<OriLine> clippedOpt = Optional.empty();

		if (p0Code != 0 && p1Code != 0) {
			// p0 and p1 are in the outside of the rectangle and
			// the line may cross the two edges of the rectangle.
			clippedOpt = cp0Opt.map(cp0 -> cp1Opt.map(cp1 -> new OriLine(cp0, cp1, line.getType())).orElse(null));
		} else if (p0Code != 0) {
			// p0 is in the outside of the rectangle and p1 is inside of the
			// rectangle.
			// The line may cross the {left, right, top, bottom} edge of the
			// rectangle.
			clippedOpt = cp0Opt.map(cp0 -> new OriLine(cp0, p1, line.getType()));
		} else if (p1Code != 0) {
			// p1 is in the outside of the rectangle and p0 is inside the
			// rectangle.
			// The line may cross the {left, right, top, bottom} edge of the
			// rectangle.
			clippedOpt = cp1Opt.map(cp1 -> new OriLine(p0, cp1, line.getType()));
		}

		return clippedOpt;

		// very short line is not preferable but such test disables to detect a
		// diagonal line touching the corner of the rectangle.
		// return clippedOpt.filter(clipped -> clipped.length() >= eps);
	}

	/**
	 * Calculate quadrant in which Point {@code x, y} lies
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	private int calcCode(final double x, final double y) {
		int code = 0;
		if (x < relaxedDomain.getLeft()) {
			code += LEFT;
		}
		if (x > relaxedDomain.getRight()) {
			code += RIGHT;
		}
		if (y < relaxedDomain.getTop()) {
			code += TOP;
		}
		if (y > relaxedDomain.getBottom()) {
			code += BOTTOM;
		}

		return code;
	}

	/**
	 * finds the coordinates after clipping.
	 *
	 * @param code
	 *            flag bits of an end point of the given line
	 * @param l
	 *            line to be clipped
	 * @return clipped point. Empty if The line doesn't intersect the rectangle.
	 */
	private Optional<Vector2d> calcClippedPointOptional(final int code, final OriLine l) {
		double cx, cy;

		var yRange = relaxedDomain.getYRange();
		// Outside from the left edge of the window
		if ((code & LEFT) != 0) {
			cy = l.getAffineYValueAt(domain.getLeft());
			if (yRange.includes(cy)) {
				double px = domain.getLeft();
				double py = cy;
				return Optional.of(new Vector2d(px, py));
			}
		}

		// Outside the right edge of the window
		if ((code & RIGHT) != 0) {
			cy = l.getAffineYValueAt(domain.getRight());
			if (yRange.includes(cy)) {
				double px = domain.getRight();
				double py = cy;
				return Optional.of(new Vector2d(px, py));
			}
		}

		var xRange = relaxedDomain.getXRange();
		// Outside from the top of the window
		if ((code & TOP) != 0) {
			cx = l.getAffineXValueAt(domain.getTop());
			if (xRange.includes(cx)) {
				double px = cx;
				double py = domain.getTop();
				return Optional.of(new Vector2d(px, py));
			}
		}

		// Outside from the bottom of the window
		if ((code & BOTTOM) != 0) {
			cx = l.getAffineXValueAt(domain.getBottom());
			if (xRange.includes(cx)) {
				double px = cx;
				double py = domain.getBottom();
				return Optional.of(new Vector2d(px, py));
			}
		}

		// If it is not clipping, line segment is completely invisible
		return Optional.empty();
	}

}
