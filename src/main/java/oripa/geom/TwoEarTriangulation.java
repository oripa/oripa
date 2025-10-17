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

import oripa.util.MathUtil;
import oripa.vecmath.Vector2d;

/**
 * Does triangulation according to two-ear theorem (ear clipping).
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

	/**
	 * returns list of triangles. empty if failed. O(n^2) time algorithm.
	 *
	 * @param polygon
	 * @param eps
	 * @return
	 */
	public List<Polygon> triangulate(final Polygon polygon, final double eps) {
		int n = polygon.verticesCount();

		var triangles = new ArrayList<Polygon>();
		var p = new ArrayList<Vertex>();

		// convex angle can be a part of triangle.
		var isConvex = new Boolean[n];
		var convexes = new LinkedList<Vertex>();
		Arrays.fill(isConvex, false);

		// shortcut
		if (n == 3) {
			triangles.add(polygon);
			return triangles;
		}

		// initialization
		for (int i = 0; i < n; i++) {
			int i0 = (i - 1 + n) % n;
			int i1 = i;
			int i2 = (i + 1) % n;

			var v0 = polygon.getVertex(i0);
			var v1 = polygon.getVertex(i1);
			var v2 = polygon.getVertex(i2);

			var vertex = new Vertex(v1, i1, i0, i2);
			p.add(vertex);

			// center angle v1 should not be larger than or equal to 180
			// degrees.
			if (GeomUtil.isStrictlyCCW(v0, v1, v2, MathUtil.normalizedValueEps())) {
				isConvex[i1] = true;
				convexes.add(vertex);
			}
		}

		// the loop count up to n might be enough but couldn't prove it...
		// but it is true that a polygon always has some convex angle. Otherwise
		// it cannot be closed.
		// the 2*n is a value I assume to be OK.
		int count = 0;
		while (!convexes.isEmpty() && count < 2 * n) {
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

			// if e crosses no other edges then (v0,v1,v2) is a valid triangle
			if (!crossesPolygonEdges(p, e0, e1, e, vertex1.prevIndexOnP, eps)) {

				var triangle = new Polygon(List.of(vertex0.v, vertex1.v, vertex2.v));
				triangles.add(triangle);

				// remove vertex1
				vertex0.nextIndexOnP = vertex1.nextIndexOnP;
				vertex2.prevIndexOnP = vertex1.prevIndexOnP;

				// test vertex0 after removing vertex1
				int i0 = vertex1.prevIndexOnP;
				var vertexPrev0 = p.get(vertex0.prevIndexOnP);
				if (!isConvex[i0]
						&& GeomUtil.isStrictlyCCW(vertexPrev0.v, vertex0.v, vertex2.v, MathUtil.normalizedValueEps())) {
					isConvex[i0] = true;
					convexes.add(vertex0);
				}

				// test vertex2 after removing vertex1
				int i2 = vertex1.nextIndexOnP;
				var vertexNext2 = p.get(vertex2.nextIndexOnP);
				if (!isConvex[i2]
						&& GeomUtil.isStrictlyCCW(vertex0.v, vertex2.v, vertexNext2.v, MathUtil.normalizedValueEps())) {
					isConvex[i2] = true;
					convexes.add(vertex2);
				}

			} else {
				// check it later again after processing other vertices.
				convexes.addLast(vertex1);
			}

		}

		if (triangles.size() != polygon.verticesCount() - 2) {
//			// debug output
//			try {
//				var lines = new ArrayList<OriLine>();
//				for (var t : triangles) {
//					for (int i = 0; i < 3; i++) {
//						lines.add(new OriLine(t.getEdge(i), OriLine.Type.MOUNTAIN));
//					}
//				}
//
//				var creasePattern = new CreasePatternFactory().createCreasePattern(lines);
//
//				new ExporterCP().export(oripa.persistence.doc.Doc.forSaving(creasePattern, null),
//						"debug_triangulation.cp",
//						null);
//			} catch (IllegalArgumentException | IOException e) {
//			}

			logger.debug("failed: fewer triangles, #triangle should be " + (polygon.verticesCount() - 2)
					+ " but is " + triangles.size() + System.lineSeparator()
					+ " polygon:" + polygon + System.lineSeparator()
					+ " triangles:" + triangles);
			return List.of();
		}

		return triangles;
	}

	/**
	 * Walks along given polygon p and test whether e crosses the edges we are
	 * on.
	 *
	 * @param p
	 * @param e0
	 * @param e1
	 * @param e
	 *            edge to be tested
	 * @param i0
	 *            the start point index on p for e
	 * @param eps
	 * @return
	 */
	private boolean crossesPolygonEdges(final List<Vertex> p,
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

			// edge of i0->i1 and i1->2 should not be tested
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
}
