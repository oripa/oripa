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

import oripa.geom.Line;
import oripa.geom.Segment;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
@Deprecated
public class PseudoLineFactory {
	public Segment create(
			final Vector2d v0, final Vector2d v1, final double paperSize) {

		return create(new Segment(v0, v1).getLine(), paperSize);
	}

	public Segment create(final Line line, final double paperSize) {
		var dir = line.getDirection().multiply(paperSize * 8);

		var v0 = line.getPoint();
		Vector2d sv = v0.subtract(dir);

		Vector2d ev = v0.add(dir);

		// create new line
		return new Segment(sv, ev);
	}
}
