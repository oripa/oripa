/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/

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

import javax.vecmath.Vector2d;

import oripa.geom.Segment;

/**
 * @author OUCHI Koji
 *
 */
public class PseudoRayFactory {

	public Segment create(final Vector2d v, final Vector2d dir, final double paperSize) {
		var d = new Vector2d(dir);
		d.normalize();
		d.scale(paperSize * 4);

		return new Segment(
				v.getX(), v.getY(),
				v.getX() + d.getX(), v.getY() + d.getY());
	}

	public Segment create(final Vector2d v, final double angle, final double paperSize) {
		return create(v, new Vector2d(Math.cos(angle), Math.sin(angle)), paperSize);
	}
}
