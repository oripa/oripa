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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.vecmath.Vector2d;

/**
 * Does triangulation according to two-ear theorem.
 *
 * @author OUCHI Koji
 *
 */
public class TwoEarTriangulation {
	private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private class Vertex {
		Vector2d v;
		int originalIndex;
		int prevIndexOnP;
		int nextIndexOnP;

		Vertex(final Vector2d v, final int originalIndex, final int prevIndexOnP, final int nextIndexOnP) {
			this.v = v;
			this.originalIndex = originalIndex;
			this.prevIndexOnP = prevIndexOnP;
			this.nextIndexOnP = nextIndexOnP;
		}

		@Override
		public String toString() {
			return "" + v + " original:" + originalIndex + " prevP:" + prevIndexOnP + " nextP:" + nextIndexOnP;
		}
	}

	public List<Polygon> triangulate(final Polygon polygon, final double eps) {
		int n = polygon.verticesCount();

		var triangles = new ArrayList<Polygon>();
		var pIndices = new ArrayList<Integer>();
		var p = new ArrayList<Vertex>();

		var isConvex = new Boolean[n];
		var convexes = new LinkedList<Vertex>();
		Arrays.fill(isConvex, false);

		if (n == 3) {
			triangles.add(polygon);
			return triangles;
		}

		for (int i = 0; i < n; i++) {
			int i0 = (i - 1 + n) % n;
			int i1 = i;
			int i2 = (i + 1) % n;

			var v0 = polygon.getVertex(i0);
			var v1 = polygon.getVertex(i1);
			var v2 = polygon.getVertex(i2);

			var vertex = new Vertex(v1, i1, i0, i2);
			p.add(vertex);

			// center angle should not be larger than or equal to 180
			// degrees.
			if (GeomUtil.isStrictlyCCW(v0, v1, v2)) {
				isConvex[i1] = true;
				convexes.add(vertex);
			}
			pIndices.add(i0);
		}

		int count = 0;
		while (!convexes.isEmpty() && count < n) {
			count++;
			logger.trace("p: " + p);
			logger.trace("convexes: " + convexes);
			logger.trace("triangles: " + triangles);

			var vertex1 = convexes.removeFirst();
			var vertex0 = p.get(vertex1.prevIndexOnP);
			var vertex2 = p.get(vertex1.nextIndexOnP);

			if (vertex1.prevIndexOnP == vertex1.nextIndexOnP) {
				break;
			}

			var e0 = new Segment(vertex0.v, vertex1.v);
			var e1 = new Segment(vertex1.v, vertex2.v);
			var e = new Segment(vertex0.v, vertex2.v);

			if (!crossesPolygonEdge(p, e0, e1, e, vertex1.prevIndexOnP, eps)) {

				var triangle = new Polygon(List.of(vertex0.v, vertex1.v, vertex2.v));
				triangles.add(triangle);

				// remove vertex1
				vertex0.nextIndexOnP = vertex1.nextIndexOnP;
				vertex2.prevIndexOnP = vertex1.prevIndexOnP;

				// test vertex0
				int i0 = vertex1.prevIndexOnP;
				var vertexPrev0 = p.get(vertex0.prevIndexOnP);
				if (!isConvex[i0] && GeomUtil.isStrictlyCCW(vertexPrev0.v, vertex0.v, vertex1.v)) {
					isConvex[i0] = true;
					convexes.add(vertex0);
				}

				// test vertex2
				int i2 = vertex1.nextIndexOnP;
				var vertexNext2 = p.get(vertex2.nextIndexOnP);
				if (!isConvex[i2] && GeomUtil.isStrictlyCCW(vertex1.v, vertex2.v, vertexNext2.v)) {
					isConvex[i2] = true;
					convexes.add(vertex2);
				}

			} else {
				convexes.addLast(vertex1);
			}

		}

		if (triangles.size() != polygon.verticesCount() - 2) {
			logger.debug("failed: fewer triangles, #triangle should be " + (polygon.verticesCount() - 2)
					+ " but is " + triangles.size() + System.lineSeparator()
					+ " polygon:" + polygon + System.lineSeparator()
					+ " triangles:" + triangles);
			return List.of();
		}

		return triangles;
	}

	private boolean crossesPolygonEdge(final List<Vertex> p,
			final Segment e0, final Segment e1, final Segment e,
			final int i0,
			final double eps) {

		if (GeomUtil.isOverlap(e0, e1, eps)) {
			return false;
		}

		var vertex0 = p.get(i0);
		var vertex1 = p.get(vertex0.nextIndexOnP);
		int i1 = vertex0.nextIndexOnP;
		int i2 = vertex1.nextIndexOnP;

		// triangle
		if (i0 == p.get(i2).nextIndexOnP) {
			return false;
		}

		var j = -1;

		while (j != i0) {

			if (vertex0.nextIndexOnP == i1 || vertex0.nextIndexOnP == i2) {
				j = vertex0.nextIndexOnP;
				vertex0 = vertex1;
				vertex1 = p.get(vertex1.nextIndexOnP);
				continue;
			}

			var e_j = new Segment(vertex0.v, vertex1.v);

			if (e.equals(e_j, eps)) {
				return true;
			}

			var crossOpt = GeomUtil.getCrossPoint(e, e_j);
			// crosses
			if (crossOpt.isPresent()) {
				// cross point is the endpoint of e
				if (crossOpt.filter(cross -> e.pointStream().anyMatch(v -> v.equals(cross, eps))).isPresent()) {
					// cross point is the angle of the triangle
					j = vertex0.nextIndexOnP;
					vertex0 = vertex1;
					vertex1 = p.get(vertex1.nextIndexOnP);
					continue;
				}
				return true;
			} else if (GeomUtil.isOverlap(e, e_j, eps)) {
				return true;
			}
			j = vertex0.nextIndexOnP;
			vertex0 = vertex1;
			vertex1 = p.get(vertex1.nextIndexOnP);
		}
		return false;
	}

	/**
	 * returns list of triangles. empty if failed.
	 *
	 * @param polygon
	 * @param eps
	 * @return
	 */
	public List<Polygon> triangulateNaive(final Polygon polygon, final double eps) {

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
				if (!crossesPolygonEdgeNaive(p, e0, e1, e, i0, i1, i2, eps)) {
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

	private boolean crossesPolygonEdgeNaive(final Polygon p,
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
