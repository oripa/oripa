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

package oripa.domain.cptool;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.vecmath.Vector2d;

import oripa.geom.RectangleDomain;
import oripa.value.OriLine;

public class RectangleClipper {

	private final static int LEFT = 1;
	private final static int RIGHT = 2;
	private final static int TOP = 4;
	private final static int BOTTOM = 8;

	private final static double EPS = 1.0e-6;

	private final double m_minX;
	private final double m_minY;
	private final double m_maxX;
	private final double m_maxY;

	public RectangleClipper(final double x0, final double y0, final double x1, final double y1) {
		m_minX = x0;
		m_minY = y0;
		m_maxX = x1;
		m_maxY = y1;
	}

	public RectangleClipper(final RectangleDomain domain) {
		m_minX = domain.getLeft();
		m_minY = domain.getTop();
		m_maxX = domain.getRight();
		m_maxY = domain.getBottom();
	}

	/**
	 *
	 * Relaxed version. The domain for clipping is slightly larger than given
	 * domain. The margin of the relaxation is determined by eps.
	 *
	 * @param domain
	 * @param eps
	 */
	public RectangleClipper(final RectangleDomain domain, final double eps) {
		m_minX = domain.getLeft() - eps;
		m_minY = domain.getTop() - eps;
		m_maxX = domain.getRight() + eps;
		m_maxY = domain.getBottom() + eps;
	}

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
	 * @param l
	 * @param p
	 *            will be substituted with the cross point of {@code l} and the
	 *            edge of clipping rectangle.
	 * @return 1 if {@code l} crosses the edge of the rectangle, -1 otherwise.
	 */
	private int calcClippedPoint(final int code, final OriLine l, final Vector2d p) {
		double cx, cy;

		// Outside from the left edge of the window
		if ((code & LEFT) != 0) {
			cy = (l.p1.y - l.p0.y) * (m_minX - l.p0.x) / (l.p1.x - l.p0.x) + l.p0.y;
			if ((cy >= m_minY) && (cy <= m_maxY)) {
				p.x = m_minX;
				p.y = cy;
				return 1;
			}
		}

		// Outside the right edge of the window
		if ((code & RIGHT) != 0) {
			cy = (l.p1.y - l.p0.y) * (m_maxX - l.p0.x) / (l.p1.x - l.p0.x) + l.p0.y;
			if ((cy >= m_minY) && (cy <= m_maxY)) {
				p.x = m_maxX;
				p.y = cy;
				return 1;
			}
		}

		// Outside from the top of the window
		if ((code & TOP) != 0) {
			cx = (l.p1.x - l.p0.x) * (m_minY - l.p0.y) / (l.p1.y - l.p0.y) + l.p0.x;
			if ((cx >= m_minX) && (cx <= m_maxX)) {
				p.x = cx;
				p.y = m_minY;
				return 1;
			}
		}

		// Outside from the bottom of the window
		if ((code & BOTTOM) != 0) {
			cx = (l.p1.x - l.p0.x) * (m_maxY - l.p0.y) / (l.p1.y - l.p0.y) + l.p0.x;
			if ((cx >= m_minX) && (cx <= m_maxX)) {
				p.x = cx;
				p.y = m_maxY;
				return 1;
			}
		}

		return -1; // If it is not clipping, line segment is completely
					// invisible
	}

	/**
	 *
	 * @param l
	 *            an end point of {@code l} will be substituted with the cross
	 *            point of {@code l} and the edge of clipping rectangle.
	 *
	 * @return {@code true} if {@code l} is included in or crosses the clipping
	 *         rectangle.
	 */
	public boolean clip(final OriLine l) {
		// ignore very short line
		if (Math.abs(l.p0.x - m_minX) < EPS
				&& Math.abs(l.p1.x - m_minX) < EPS) {
			return false;
		}
		if (Math.abs(l.p0.x - m_maxX) < EPS
				&& Math.abs(l.p1.x - m_maxX) < EPS) {
			return false;
		}
		if (Math.abs(l.p0.y - m_minY) < EPS
				&& Math.abs(l.p1.y - m_minY) < EPS) {
			return false;
		}
		if (Math.abs(l.p0.y - m_maxY) < EPS
				&& Math.abs(l.p1.y - m_maxY) < EPS) {
			return false;
		}

		int s_code = calcCode(l.p0.x, l.p0.y);
		int e_code = calcCode(l.p1.x, l.p1.y);

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
			if (calcClippedPoint(s_code, l, l.p0) < 0) {
				return false;
			}
		}

		// p1 is in the outside of the rectangle and
		// the line may cross the {left, right, top, bottom} edge of the
		// rectangle.
		if (e_code != 0) {
			if (calcClippedPoint(e_code, l, l.p1) < 0) {
				return false;
			}
		}

		return true;
	}

	/**
	 * tells us whether the given line {@code l} intersects the rectangle.
	 *
	 * @param l
	 * @return whether {@code l} intersects the rectangle.
	 */
	public boolean clipTest(final OriLine l) {
		int s_code = calcCode(l.p0.x, l.p0.y);
		int e_code = calcCode(l.p1.x, l.p1.y);
		if ((s_code == 0) && (e_code == 0)) {
			return true;
		}

		if ((s_code & e_code) != 0) {
			return false;
		}

		var dummy = new Vector2d();
		if (s_code != 0) {
			if (calcClippedPoint(s_code, l, dummy) < 0) {
				return false;
			}
		}

		if (e_code != 0) {
			if (calcClippedPoint(e_code, l, dummy) < 0) {
				return false;
			}
		}

		return true;

	}

	/**
	 * extracts lines which intersects this rectangle from given lines.
	 *
	 * @param lines
	 * @return
	 */
	public Collection<OriLine> selectByArea(final Collection<OriLine> lines) {
		return lines.stream()
				.filter(l -> clipTest(l))
				.collect(Collectors.toList());
	}
}
