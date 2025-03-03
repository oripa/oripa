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

import oripa.vecmath.Vector2d;

public class Line {

	private final Vector2d p; // Passing through point
	private final Vector2d dir; // Unit direction vector

	public Line(final Vector2d p, final Vector2d dir) {
		this.p = p;
		this.dir = dir.normalize();
	}

	/**
	 * Returns a reference point on this line.
	 *
	 * @return
	 */
	public Vector2d getPoint() {
		return p;
	}

	/**
	 * Returns unit vector of this line's direction.
	 *
	 * @return
	 */
	public Vector2d getDirection() {
		return dir;
	}

	/**
	 * True if the given line is parallel to this line.
	 *
	 * @param line
	 * @return
	 */
	public boolean isParallel(final Line line) {
		return dir.isParallel(line.dir);
	}

	/**
	 * Returns {@code true} if the given line is parallel to this line and the
	 * point on the given line is also on this line.
	 *
	 * @param line
	 * @param eps
	 * @return {@code true} if the given line is equal to this line.
	 */
	public boolean equals(final Line line, final double eps) {
		return dir.isParallel(line.dir)
				&& (p.equals(line.p, eps) || p.subtract(line.p).isParallel(line.dir));
	}

	@Override
	public String toString() {
		return "[p=" + p + ", dir=" + dir + "]";
	}

}
