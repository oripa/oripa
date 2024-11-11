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
package oripa.persistence.foldformat;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
public final class Geometry {
	private static final Logger logger = LoggerFactory.getLogger(Geometry.class);

	private static class AngleWithIndices {
		public double angle;
		public int u, v;

		public AngleWithIndices(final int u, final int v, final List<List<Double>> coords) {
			this.u = u;
			this.v = v;

			var uCoord = coords.get(u);
			var vCoord = coords.get(v);

			var dir = Vector2d.fromList(vCoord).subtract(Vector2d.fromList(uCoord));

			angle = Math.atan2(dir.getY(), dir.getX());

			logger.trace("dir = (" + dir.getX() + ", " + dir.getY() + ")");
			logger.trace("angle = " + angle);
		}

		/*
		 * (non Javadoc)
		 *
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "(angle, u, v): " + "(" + angle + "," + u + "," + v + ")";
		}

	}

	/**
	 * sort by angle in counter-clockwise direction.
	 *
	 * @param vertex
	 * @param verticesAroundAVertex
	 * @param coords
	 * @return
	 */
	public static List<Integer> sortByAngle(final int vertex,
			final List<Integer> verticesAroundAVertex,
			final List<List<Double>> coords) {

		return verticesAroundAVertex.stream()
				.map(v -> new AngleWithIndices(vertex, v, coords))
				.sorted((a1, a2) -> (int) Math.signum(a1.angle - a2.angle))
				.map(a -> a.v)
				.toList();
	}

}
