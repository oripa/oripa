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
public class PseudoLineFactory {
	public Segment create(
			final Vector2d v0, final Vector2d v1, final double paperSize) {

		Vector2d dir = new Vector2d(v0.x - v1.x, v0.y - v1.y);
		dir.normalize();
		dir.scale(paperSize * 8);

		// create new line
		Segment line = new Segment(v0.x - dir.x, v0.y - dir.y,
				v0.x + dir.x, v0.y + dir.y);

		return line;
	}
}
