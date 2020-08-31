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
package oripa.persistent.foldformat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.value.OriPoint;

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

			var dir = new OriPoint(
					vCoord.get(0) - uCoord.get(0),
					vCoord.get(1) - uCoord.get(1));

			angle = Math.atan2(dir.y, dir.x);

			logger.debug("dir = (" + dir.x + ", " + dir.y + ")");
			logger.debug("angle = " + angle);
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
	 *
	 * @param base
	 *            vertex to be put at zero index.
	 * @param sortedverticesAroundVertex
	 *            vertices in vertices_vertices. it should include {@code base}.
	 * @param coords
	 *            coordinates of the vertices.
	 * @return rotation of sortedverticesAroundVertex.
	 */
	public static List<Integer> rotateVertices(final int base,
			final List<Integer> sortedverticesAroundVertex,
			final List<List<Double>> coords) {

		logger.debug(
				"before rotation: " + sortedverticesAroundVertex + ", base vertex is " + base);

		var baseIndex = sortedverticesAroundVertex.indexOf(base);

		var rotated = new ArrayList<Integer>();

		var verticesSize = sortedverticesAroundVertex.size();
		for (int i = 0; i < verticesSize; i++) {
			rotated.add(sortedverticesAroundVertex.get(
					(i + baseIndex) % verticesSize));
		}

		logger.debug("after rotation: " + rotated + ", base vertex is " + base);
		return rotated;

//		return sortedverticesIndicesAroundEnd.stream()
//				.map(w -> new AngleWithIndices(baseEdge.get(1), w, coords))
//				.sorted((a1, a2) -> (int) Math.signum(a1.angle - a2.angle))
//				.map(a -> a.v)
//				.collect(Collectors.toList());
	}

	public static List<Integer> sortByAngle(final int vertexIndex,
			final List<Integer> verticesIndicesAroundAVertex,
			final List<List<Double>> coords) {

		// sort by angle in counter-clockwise direction.
		return verticesIndicesAroundAVertex.stream()
				.map(v -> new AngleWithIndices(vertexIndex, v, coords))
				.sorted((a1, a2) -> (int) Math.signum(a1.angle - a2.angle))
				.map(a -> a.v)
				.collect(Collectors.toList());
	}

}
