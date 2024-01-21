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
package oripa.domain.paint.copypaste;

import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
public class ShiftedLineFactory {
	public OriLine createShiftedLine(final OriLine line, final double diffX, final double diffY) {

		var p0x = line.getP0().getX() + diffX;
		var p0y = line.getP0().getY() + diffY;

		var p1x = line.getP1().getX() + diffX;
		var p1y = line.getP1().getY() + diffY;

		return new OriLine(p0x, p0y, p1x, p1y, line.getType());
	}

	public Vector2d createOffset(final Vector2d origin, final Vector2d point) {
		return point.subtract(origin);
	}
}
