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

import java.util.List;

import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
public class Polygon {
	private final List<Vector2d> vertices;
	private final Vector2d centroid;

	private List<Polygon> triangles;

	public Polygon(final List<Vector2d> vertices) {
		this.vertices = List.copyOf(vertices);

		centroid = GeomUtil.computeCentroid(vertices).orElse(null);
	}

	public Polygon(final Polygon polygon) {
		this(polygon.vertices);
	}

	public Vector2d getVertex(final int i) {
		return vertices.get(i);
	}

	public int verticesCount() {
		return vertices.size();
	}

	public Segment getEdge(final int i) {
		return new Segment(vertices.get(i), vertices.get((i + 1) % verticesCount()));
	}

	public Polygon removeVertex(final int i) {
		var vertex = vertices.get(i);
		return new Polygon(vertices.stream().filter(v -> v != vertex).toList());
	}

	/**
	 *
	 * @return centroid of this polygon. can be null.
	 */
	public Vector2d getCentroid() {
		return centroid;
	}

	/**
	 *
	 * @return inner points of subface
	 */
	public List<Vector2d> getInnerPoints(final double eps) {
		if (verticesCount() < 3) {
			return List.of();
		}
		buildTriangles(eps);
		return triangles.stream().map(Polygon::getCentroid).toList();
	}

	/**
	 * Whether v is inclusively on this polygon (inside or on the edges).
	 * Assumes this polygon is convex.
	 *
	 * @param v
	 *            point to be tested.
	 * @return true if v is inside or on the edges of this face.
	 */
	public boolean includesInclusively(final Vector2d v, final double eps) {
		// If it's on the face's edge, return true
		if (isOnEdge(v, eps)) {
			return true;
		}

		return isInside(v, eps);
	}

	/**
	 * Whether v is strictly inside of this polygon. Assumes this polygon is
	 * convex.
	 *
	 * @param v
	 *            point to be tested.
	 * @return true if v is strictly inside of this face.
	 */
	public boolean includesExclusively(final Vector2d v, final double eps) {
		// If it's on the face's edge, return false
		if (isOnEdge(v, eps)) {
			return false;
		}

		return isInside(v, eps);
	}

	/**
	 * Whether v is on edge of polygon.
	 *
	 * @param v
	 *            point to be tested.
	 * @param eps
	 * @param getPosition
	 *            a function to get the position of face's vertex from
	 *            half-edge. That is, you can designate using position before
	 *            fold or the one after fold.
	 * @return
	 */
	private boolean isOnEdge(final Vector2d v, final double eps) {
		int n = verticesCount();
		// If it's on the polygon's edge, return true
		for (int i = 0; i < n; i++) {
			if (GeomUtil.distancePointToSegment(v, vertices.get(i),
					vertices.get((i + 1) % n)) < eps) {
				return true;
			}
		}
		return false;
	}

	private boolean isInside(final Vector2d v, final double eps) {
		buildTriangles(eps);

		return triangles.stream().anyMatch(triangle -> triangle.isOnEdge(v, eps) || triangle.isInsideConvex(v));
	}

	private void buildTriangles(final double eps) {
		if (triangles == null) {
			var triangulator = new TwoEarTriangulation();

			triangles = triangulator.triangulate(this, eps);

			if (triangles.isEmpty()) {
				triangles = triangulator.triangulate(new Polygon(vertices.reversed()), eps);
			}

			if (triangles.isEmpty()) {
				throw new IllegalStateException("Failed to triangulate a polygon.");
			}
		}

	}

	/**
	 * Whether v is inside of this polygon. This method is very sensitive to the
	 * case that v is very close to the edge. Assumes this polygon is convex.
	 *
	 * @param v
	 *            point to be tested.
	 * @param getPosition
	 *            a function to get the position of face's vertex from
	 *            half-edge. That is, you can designate using position before
	 *            fold or the one after fold.
	 * @return true if v inside of this face.
	 */
	private boolean isInsideConvex(final Vector2d v) {
		int n = verticesCount();

		var baseEdge = getEdge(0);
		boolean baseFlg = GeomUtil.isStrictlyCCW(baseEdge.getP0(),
				baseEdge.getP1(), v);

		for (int i = 1; i < n; i++) {
			var he = getEdge(i);
			if (GeomUtil.isStrictlyCCW(he.getP0(), he.getP1(),
					v) != baseFlg) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Whether this face includes {@code line} entirely. The inclusion test is
	 * inclusive. Assumes this polygon is convex.
	 *
	 * @param segment
	 * @return {@code true} if {@code face} includes {@code line} entirely.
	 */
	public boolean includesInclusively(final Segment segment, final double eps) {
		return includesInclusively(segment.getP0(), eps)
				&& includesInclusively(segment.getP1(), eps);
	}

	@Override
	public String toString() {
		return vertices.toString();
	}

}
