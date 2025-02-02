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
package oripa.geom;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Does triangulation according to two-ear theorem.
 *
 * @author OUCHI Koji
 *
 */
public class TwoEarTriangulation {
	private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/**
	 * returns list of triangles. empty if failed.
	 *
	 * @param polygon
	 * @param eps
	 * @return
	 */
	public List<Polygon> triangulate(final Polygon polygon, final double eps) {

		var triangles = new ArrayList<Polygon>();

		var p = polygon;
		for (int k = 0; k < polygon.verticesCount() - 2; k++) {
			if (p.verticesCount() == 3) {
				break;
			}

			for (int i = 0; i < p.verticesCount(); i++) {
				int i0 = i;
				int i1 = (i + 1) % p.verticesCount();
				int i2 = (i + 2) % p.verticesCount();

				logger.trace("try {},{},{}", i0, i1, i2);

				var v0 = p.getVertex(i0);
				var v1 = p.getVertex(i1);
				var v2 = p.getVertex(i2);

				var e0 = new Segment(v0, v1);
				var e1 = new Segment(v1, v2);
				var e = new Segment(v0, v2);

				// center angle should not be larger than or equal to 180
				// degrees.
				if (!GeomUtil.isStrictlyCCW(v0, v1, v2)) {
					logger.trace("reflex vertex: {}", v1);
					logger.trace(System.lineSeparator()
							+ " remain:" + p + System.lineSeparator()
							+ triangles.size() + " triangles:" + triangles);
					continue;
				}
				if (!crossesPolygonEdge(p, e0, e1, e, i0, i1, i2, eps)) {
					p = p.removeVertex(i1);
					var triangle = new Polygon(List.of(v0, v1, v2));
					triangles.add(triangle);
					break;
				}

			}
		}

		if (p.verticesCount() != 3) {
			logger.trace("failed: fewer cuts " + polygon.verticesCount()
					+ "->" + p.verticesCount() + System.lineSeparator()
					+ " polygon:" + polygon + System.lineSeparator()
					+ " remain:" + p + System.lineSeparator()
					+ " triangles:" + triangles);
			return List.of();
		}

		triangles.add(p);

		if (triangles.size() != polygon.verticesCount() - 2) {
			logger.trace("failed: fewer triangles, #triangle should be " + (polygon.verticesCount() - 2)
					+ " but is " + triangles.size() + System.lineSeparator()
					+ " polygon:" + polygon + System.lineSeparator()
					+ " remain:" + p + System.lineSeparator()
					+ " triangles:" + triangles);
			return List.of();
		}

		return triangles;
	}

	private boolean crossesPolygonEdge(final Polygon p,
			final Segment e0, final Segment e1, final Segment e,
			final int i0, final int i1, final int i2,
			final double eps) {

		if (GeomUtil.isOverlap(e0, e1, eps)) {
			return false;
		}

//		if ((e1.length() < eps)) {
//			return false;
//		}

		for (int j = 0; j < p.verticesCount(); j++) {
			if (j == i0 || j == i1) {
				continue;
			}

			var e_j = p.getEdge(j);

			if (e.equals(e_j, eps)) {
				return true;
			}

			var crossOpt = GeomUtil.getCrossPoint(e, e_j);
			// crosses
			if (crossOpt.isPresent()) {
				// cross point is the endpoint of e
				if (crossOpt.filter(cross -> e.pointStream().anyMatch(v -> v.equals(cross, eps))).isPresent()) {
					// cross point is the angle of the triangle
					continue;
				}
				return true;
			} else if (GeomUtil.isOverlap(e, e_j, eps)) {
				return true;
			}
		}
		return false;
	}
}
