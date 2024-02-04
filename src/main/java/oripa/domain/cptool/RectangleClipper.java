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
import oripa.geom.Segment;
import oripa.util.MathUtil;
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

	private final double eps;

	private final RectangleDomain domain;

	private final double m_minX;
	private final double m_minY;
	private final double m_maxX;
	private final double m_maxY;

	/**
	 *
	 * Relaxed version. The domain for clipping is slightly larger than given
	 * domain. The margin of the relaxation is determined by epsilon.
	 *
	 * @param domain
	 *            Rectangle domain for clipping
	 * @param epsilon
	 *            Margin
	 */
	public RectangleClipper(final RectangleDomain domain, final double epsilon) {
		eps = epsilon;

		m_minX = domain.getLeft() - epsilon;
		m_minY = domain.getTop() - epsilon;
		m_maxX = domain.getRight() + epsilon;
		m_maxY = domain.getBottom() + epsilon;

		this.domain = domain;
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
	 * The test is inclusive but lines along the border of the rectangle will
	 * result in empty.
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
	 * The test is inclusive but lines along the border of the rectangle will
	 * result in empty.
	 *
	 * @param line
	 *            to be clipped.
	 * @return a clipped line.
	 */
	public Optional<OriLine> clip(final OriLine line) {
		if (lineOverlapsBorder(line)) {
			return Optional.empty();
		}

		var p0 = line.getP0();
		var p1 = line.getP1();

		// first to avoid parameter modification
		final int s_code = calcCode(p0.getX(), p0.getY());
		final int e_code = calcCode(p1.getX(), p1.getY());

		// the line is in the rectangle
		if ((s_code == 0) && (e_code == 0)) {
			return Optional.of(line);
		}

		// the line is in the {left, right, top, bottom} area.
		if ((s_code & e_code) != 0) {
			return Optional.empty();
		}

		var cp0Opt = calcClippedPointOptional(s_code, line);
		var cp1Opt = calcClippedPointOptional(e_code, line);

		Optional<OriLine> clippedOpt = Optional.empty();

		if (s_code != 0 && e_code != 0) {
			// p0 and p1 are in the outside of the rectangle and
			// the line may cross the two edges of the rectangle.
			clippedOpt = cp0Opt.map(cp0 -> cp1Opt.map(cp1 -> new OriLine(cp0, cp1, line.getType())).orElse(null));
		} else if (s_code != 0) {
			// p0 is in the outside of the rectangle and p1 is inside of the
			// rectangle.
			// The line may cross the {left, right, top, bottom} edge of the
			// rectangle.
			clippedOpt = cp0Opt.map(cp0 -> new OriLine(cp0, p1, line.getType()));
		} else if (e_code != 0) {
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
	 * Check if {@code line} overlaps with border of RectangelDomain
	 *
	 * @param line
	 * @return {@code true} if {@code line} overlaps with border
	 */
	private boolean lineOverlapsBorder(final Segment line) {
		// the line is along the left
		if (MathUtil.areEqualInclusive(line.getP0().getX(), m_minX, eps)
				&& MathUtil.areEqualInclusive(line.getP1().getX(), m_minX, eps)) {
			return true;
		}
		// the line is along the right
		if (MathUtil.areEqualInclusive(line.getP0().getX(), m_maxX, eps)
				&& MathUtil.areEqualInclusive(line.getP1().getX(), m_maxX, eps)) {
			return true;
		}
		// the line is along the top
		if (MathUtil.areEqualInclusive(line.getP0().getY(), m_minY, eps)
				&& MathUtil.areEqualInclusive(line.getP1().getY(), m_minY, eps)) {
			return true;
		}
		// the line is along the bottom
		if (MathUtil.areEqualInclusive(line.getP0().getY(), m_maxY, eps)
				&& MathUtil.areEqualInclusive(line.getP1().getY(), m_maxY, eps)) {
			return true;
		}
		return false;
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
		if (x < m_minX) {
			code += LEFT;
		}
		if (x > m_maxX) {
			code += RIGHT;
		}
		if (y < m_minY) {
			code += TOP;
		}
		if (y > m_maxY) {
			code += BOTTOM;
		}

		return code;
	}

	/**
	 * finds the coordinates after clipping.
	 *
	 * @param code
	 *            flag bits of a end point of the given line
	 * @param l
	 *            line to be clipped
	 * @return clipped line. Empty if The line doesn't intersect the rectangle.
	 */
	private Optional<Vector2d> calcClippedPointOptional(final int code, final OriLine l) {
		double cx, cy;

		// Outside from the left edge of the window
		if ((code & LEFT) != 0) {
			cy = l.getAffineYValueAt(domain.getLeft());
			if ((cy >= m_minY) && (cy <= m_maxY)) {
				double px = domain.getLeft();
				double py = cy;
				return Optional.of(new Vector2d(px, py));
			}
		}

		// Outside the right edge of the window
		if ((code & RIGHT) != 0) {
			cy = l.getAffineYValueAt(domain.getRight());
			if ((cy >= m_minY) && (cy <= m_maxY)) {
				double px = domain.getRight();
				double py = cy;
				return Optional.of(new Vector2d(px, py));
			}
		}

		// Outside from the top of the window
		if ((code & TOP) != 0) {
			cx = l.getAffineXValueAt(domain.getTop());
			if ((cx >= m_minX) && (cx <= m_maxX)) {
				double px = cx;
				double py = domain.getTop();
				return Optional.of(new Vector2d(px, py));
			}
		}

		// Outside from the bottom of the window
		if ((code & BOTTOM) != 0) {
			cx = l.getAffineXValueAt(domain.getBottom());
			if ((cx >= m_minX) && (cx <= m_maxX)) {
				double px = cx;
				double py = domain.getBottom();
				return Optional.of(new Vector2d(px, py));
			}
		}

		// If it is not clipping, line segment is completely invisible
		return Optional.empty();
	}

}
