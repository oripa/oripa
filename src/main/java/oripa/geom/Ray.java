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

public class Ray {

	private final Vector2d p;
	private final Vector2d dir;

	public Ray(final Vector2d p, final Vector2d dir) {
		this.p = p;
		this.dir = dir.normalize();
	}

	public Ray(final Vector2d v, final double angle) {
		this(v, Vector2d.unitVector(angle));
	}

	/**
	 * Returns the end point of this ray.
	 *
	 * @return
	 */
	public Vector2d getEndPoint() {
		return p;
	}

	/**
	 * Returns unit vector of this ray's direction.
	 *
	 * @return
	 */
	public Vector2d getDirection() {
		return dir;
	}
}
