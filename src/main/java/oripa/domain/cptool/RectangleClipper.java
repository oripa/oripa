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
import java.util.stream.Collectors;

import oripa.geom.RectangleDomain;
import oripa.util.MathUtil;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

/**
 * Manages OriLine intersection testing (clipping) with Rectangle Domain
 */
//TODO:	cleanup + comments (last three functions)
public class RectangleClipper {

	private static final int LEFT = 1;
	private static final int RIGHT = 2;
	private static final int TOP = 4;
	private static final int BOTTOM = 8;

	private static final double EPSILON = 1.0e-6;

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
		m_minX = domain.getLeft() - epsilon;
		m_minY = domain.getTop() - epsilon;
		m_maxX = domain.getRight() + epsilon;
		m_maxY = domain.getBottom() + epsilon;

		this.domain = domain;
	}

	public RectangleClipper(final RectangleDomain domain) {
		this(domain, 0);
	}

	public RectangleClipper(final double x0, final double y0, final double x1, final double y1) {
		this(new RectangleDomain(x0, y0, x1, y1));
	}

	/**
	 * extracts lines which intersects this rectangle from given {@code lines}
	 *
	 * @param lines
	 * @return Subset of {@code lines}
	 */
	public Collection<OriLine> selectByArea(final Collection<OriLine> lines) {
		return lines.stream()
				.filter(this::clip)
				.collect(Collectors.toList());
	}

	/**
	 * tells us whether the given line {@code line} intersects the rectangle.
	 *
	 * @param line
	 *            an end point of {@code line} will be substituted with the
	 *            cross point of {@code l} and the edge of clipping rectangle.
	 *
	 * @return {@code true} if {@code line} is included in or crosses the
	 *         clipping rectangle.
	 */
	public boolean clip(final OriLine line) {
		if (lineOverlapsBorder(line)) {
			return false;
		}

		// first to avoid parameter modification
		int s_code = calcCode(line.getP0().getX(), line.getP0().getY());
		int e_code = calcCode(line.getP1().getX(), line.getP1().getY());
		// the line is in the rectangle
		if ((s_code == 0) && (e_code == 0)) {
			return true;
		}

		// the line is in the {left, right, top, bottom} area.
		if ((s_code & e_code) != 0) {
			return false;
		}

		// p0 is in the outside of the rectangle and
		// the line may cross the {left, right, top, bottom} edge of the
		// rectangle.
		if (s_code != 0) {
			if (calcClippedPoint(s_code, line) < 0) {
				return false;
			}
		}

		// p1 is in the outside of the rectangle and
		// the line may cross the {left, right, top, bottom} edge of the
		// rectangle.
		if (e_code != 0) {
			return calcClippedPoint(e_code, line) >= 0;
		}

		return true;
	}

	/**
	 * Check if {@code line} overlaps with border of RectangelDomain
	 *
	 * @param line
	 * @return {@code true} if {@code line} overlaps with border
	 */
	private boolean lineOverlapsBorder(final OriLine line) {
		if (MathUtil.areEqual(line.getP0().getX(), m_minX, EPSILON)
				&& MathUtil.areEqual(line.getP1().getX(), m_minX, EPSILON)) {
			return true;
		}
		if (MathUtil.areEqual(line.getP0().getX(), m_maxX, EPSILON)
				&& MathUtil.areEqual(line.getP1().getX(), m_maxX, EPSILON)) {
			return true;
		}
		if (MathUtil.areEqual(line.getP0().getY(), m_minY, EPSILON)
				&& MathUtil.areEqual(line.getP1().getY(), m_minY, EPSILON)) {
			return true;
		}
		if (MathUtil.areEqual(line.getP0().getY(), m_maxY, EPSILON)
				&& MathUtil.areEqual(line.getP1().getY(), m_maxY, EPSILON)) {
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

	private int calcClippedPoint(final int code, final OriLine l) {
		return calcClippedPointOptional(code, l).isPresent() ? 1 : -1;
	}

	/**
	 * finds the coordinates after clipping.
	 *
	 * @param p
	 *            will be substituted with the cross point of {@code l} and the
	 *            edge of clipping rectangle.
	 * @return 1 if {@code l} crosses the edge of the rectangle, -1 otherwise.
	 */
	private Optional<Vector2d> calcClippedPointOptional(final int code, final OriLine l) {
		double cx, cy;

		// Outside from the left edge of the window
		if ((code & LEFT) != 0) {
			cy = l.getAffineYValueAt(domain.getLeft());
			if ((cy >= m_minY) && (cy <= m_maxY)) {
				var px = domain.getLeft();
				var py = cy;
				return Optional.of(new Vector2d(px, py));
			}
		}

		// Outside the right edge of the window
		if ((code & RIGHT) != 0) {
			cy = l.getAffineYValueAt(domain.getRight());
			if ((cy >= m_minY) && (cy <= m_maxY)) {
				var px = domain.getRight();
				var py = cy;
				return Optional.of(new Vector2d(px, py));
			}
		}

		// Outside from the top of the window
		if ((code & TOP) != 0) {
			cx = l.getAffineXValueAt(domain.getTop());
			if ((cx >= m_minX) && (cx <= m_maxX)) {
				var px = cx;
				var py = domain.getTop();
				return Optional.of(new Vector2d(px, py));
			}
		}

		// Outside from the bottom of the window
		if ((code & BOTTOM) != 0) {
			cx = l.getAffineXValueAt(domain.getBottom());
			if ((cx >= m_minX) && (cx <= m_maxX)) {
				var px = cx;
				var py = domain.getBottom();
				return Optional.of(new Vector2d(px, py));
			}
		}

		return Optional.empty(); // If it is not clipping, line segment is
									// completely
		// invisible
	}

}
