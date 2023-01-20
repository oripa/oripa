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
package oripa.util;

import javax.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
public class MathUtil {
	public static double normalizeAngle(final double angle) {
		final double TWO_PI = 2 * Math.PI;
		return (TWO_PI + angle) % TWO_PI;
	}

	public static double angleOf(final Vector2d v) {
		return normalizeAngle(Math.atan2(v.getY(), v.getX()));
	}

	public static double angleRadianEps() {
		return 1e-5;
	}

	public static double normalizedValueEps() {
		return 1e-6;
	}

}
