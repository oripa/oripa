/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2005-2009 Jun Mitani http://mitani.cs.tsukuba.ac.jp/

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

package oripa.domain.fold.halfedge;

import java.awt.Color;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import oripa.domain.fold.stackcond.StackConditionOf3Faces;
import oripa.domain.fold.stackcond.StackConditionOf4Faces;
import oripa.geom.GeomUtil;
import oripa.util.collection.CollectionUtil;
import oripa.value.OriLine;

public class OriFace {

	/**
	 * Assumed to contain half-edges in connection order circularly. The
	 * half-edge at index 0 should refer the half-edge at index 1, and for index
	 * k, the half-edge at index k should refer the half-edge at k + 1 by
	 * {@link OriHalfedge#next} field. The last half-edge should refer the
	 * half-edge at index 0.
	 */
	public ArrayList<OriHalfedge> halfedges = new ArrayList<>();

	/**
	 * For drawing the shape after fold
	 */
	public Path2D.Double outline = new Path2D.Double();

	/**
	 * For drawing foldability-check face
	 */
	public Path2D.Double preOutline = new Path2D.Double();

	public ArrayList<OriLine> precreases = new ArrayList<>();

	public boolean faceFront = true;
	public Color color;

	/**
	 * working variable for computing position after fold by the algorithm.
	 */
	public boolean movedByFold = false;
	public int z_order = 0;

	/**
	 * index of stack order for subface (probably). It seems to be used only
	 * while making correct stack order.
	 */
	public int indexForStack = 0;

	/**
	 * ID of this face (Probably)
	 */
	public int faceID = 0;

	public boolean alreadyStacked = false;
	public ArrayList<TriangleFace> triangles = new ArrayList<>();

	public ArrayList<StackConditionOf4Faces> condition4s = new ArrayList<>();
	public ArrayList<StackConditionOf3Faces> condition3s = new ArrayList<>();
	public ArrayList<Integer> condition2s = new ArrayList<>();

	public OriFace() {
		int r = (int) (Math.random() * 255);
		int g = (int) (Math.random() * 255);
		int b = (int) (Math.random() * 255);
		color = new Color(r, g, b);
	}

	public void trianglateAndSetColor(final boolean bUseColor, final boolean bFlip,
			final double paperSize) {
		triangles.clear();

		double min_x = Double.MAX_VALUE;
		double max_x = -Double.MAX_VALUE;
		double min_y = Double.MAX_VALUE;
		double max_y = -Double.MAX_VALUE;

		for (OriHalfedge he : halfedges) {
			min_x = Math.min(min_x, he.vertex.p.x);
			max_x = Math.max(max_x, he.vertex.p.x);
			min_y = Math.min(min_y, he.vertex.p.y);
			max_y = Math.max(max_y, he.vertex.p.y);
		}

		double faceWidth = Math.sqrt((max_x - min_x) * (max_x - min_x)
				+ (max_y - min_y) * (max_y - min_y));

		for (OriHalfedge he : halfedges) {
			double val = 0;
			if (he.edge.type == OriLine.Type.MOUNTAIN.toInt()) {
				val += 1;
			} else if (he.edge.type == OriLine.Type.VALLEY.toInt()) {
				val -= 1;
			}

			if (he.prev.edge.type == OriLine.Type.MOUNTAIN.toInt()) {
				val += 1;
			} else if (he.prev.edge.type == OriLine.Type.VALLEY.toInt()) {
				val -= 1;
			}

			double vv = (val + 2) / 4.0;
			double v = (0.75 + vv * 0.25);

			v *= 0.9 + 0.15 * (Math.sqrt((he.vertex.p.x - min_x)
					* (he.vertex.p.x - min_x)
					+ (he.vertex.p.y - min_y)
							* (he.vertex.p.y - min_y))
					/ faceWidth);

			v = Math.min(1, v);

			if (bUseColor) {
				if (true) {
					if (faceFront ^ bFlip) {
						he.vertexColor.set(v * 0.7, v * 0.7, v);
					} else {
						he.vertexColor.set(v, v * 0.8, v * 0.7);
					}
//				} else {
//					if (faceFront ^ bFlip) {
//						he.vertexColor.set(v, v * 0.6, v * 0.6);
//					} else {
//						he.vertexColor.set(v, v, v * 0.95);
//					}
//
				}
			} else {
				he.vertexColor.set(v, v, v * 0.95);
			}
		}

		int heNum = halfedges.size();
		OriHalfedge startHe = halfedges.get(0);
		for (int i = 1; i < heNum - 1; i++) {
			TriangleFace tri = new TriangleFace(this);
			tri.v[0].p = new Vector2d(startHe.vertex.p);
			tri.v[1].p = new Vector2d(halfedges.get(i).vertex.p);
			tri.v[2].p = new Vector2d(halfedges.get(i + 1).vertex.p);

			tri.v[0].color = new Vector3d(startHe.vertexColor);
			tri.v[1].color = new Vector3d(halfedges.get(i).vertexColor);
			tri.v[2].color = new Vector3d(halfedges.get(i + 1).vertexColor);

			tri.v[0].uv = new Vector2d(startHe.vertex.preP.x / paperSize
					+ 0.5, startHe.vertex.preP.y / paperSize + 0.5);
			tri.v[1].uv = new Vector2d(halfedges.get(i).vertex.preP.x
					/ paperSize + 0.5,
					halfedges.get(i).vertex.preP.y
							/ paperSize + 0.5);
			tri.v[2].uv = new Vector2d(halfedges.get(i + 1).vertex.preP.x
					/ paperSize + 0.5,
					halfedges.get(i + 1).vertex.preP.y
							/ paperSize + 0.5);
			triangles.add(tri);
		}
	}

	/**
	 * Link each half-edge to previous one and next one in the
	 * {@link #halfedges}.
	 */
	public void makeHalfedgeLoop() {
		for (int i = 0; i < halfedges.size(); i++) {
			OriHalfedge pre_he = CollectionUtil.getCircular(halfedges, i - 1);
			OriHalfedge he = halfedges.get(i);
			OriHalfedge nxt_he = CollectionUtil.getCircular(halfedges, i + 1);

			he.next = nxt_he;
			he.prev = pre_he;
		}
	}

	public void printInfo() {
		System.out.println("OriFace");
		for (OriHalfedge he : halfedges) {
			System.out.println(he.vertex.p);
		}
	}

	/**
	 * Constructs {@code outline} field, which is for showing this face after
	 * fold in graphic.
	 */
	public void setOutline() {
		outline = createPath(halfedges.stream()
				.map(he -> he.positionForDisplay)
				.collect(Collectors.toList()));
	}

	/**
	 * Constructs {@code preOutline} field, which is for showing this face
	 * before fold in graphic.
	 */
	public void setPreOutline() {
		Vector2d centerP = getCentroidBeforeFolding();
		double rate = 0.5;
		preOutline = createPath(halfedges.stream()
				.map(he -> new Vector2d(
						he.vertex.preP.x * rate + centerP.x * (1.0 - rate),
						he.vertex.preP.y * rate + centerP.y * (1.0 - rate)))
				.collect(Collectors.toList()));
	}

	private Path2D.Double createPath(final List<Vector2d> vertices) {
		var path = new Path2D.Double();
		path.moveTo(vertices.get(0).x, vertices.get(0).y);
		for (int i = 1; i < halfedges.size(); i++) {
			path.lineTo(vertices.get(i).x, vertices.get(i).y);
		}
		path.closePath();
		return path;
	}

	/**
	 * Computes centroid.
	 *
	 * @return centroid of this face before folding
	 */
	public Vector2d getCentroidBeforeFolding() {
		return GeomUtil.computeCentroid(halfedges.stream()
				.map(he -> he.vertex.preP)
				.collect(Collectors.toList()));
	}

	/**
	 * Computes centroid.
	 *
	 * @return centroid of this face with current position {@link OriVertex#p}
	 *         of half-edges.
	 */
	public Vector2d getCentroidAfterFolding() {
		return GeomUtil.computeCentroid(halfedges.stream()
				.map(he -> he.vertex.p)
				.collect(Collectors.toList()));
	}

	/**
	 * Whether v is inclusively on this face (inside or on the edges). The test
	 * is done with current position {@link OriVertex#p} of half-edges.
	 *
	 * @param v
	 *            point to be tested.
	 * @return true if v is inside or on the edges of this face.
	 */
	public boolean isOnFaceInclusive(final Vector2d v) {
		// If it's on the faces edge, return true
		if (isOnEdge(v, GeomUtil.EPS, he -> he.vertex.p)) {
			return true;
		}

		return isInside(v, GeomUtil.EPS, he -> he.vertex.p);
	}

	/**
	 * Whether v is strictly inside of face where the positions are ones after
	 * folding.
	 *
	 * @param v
	 *            point to be tested.
	 * @param eps
	 * @return
	 */
	public boolean isOnFoldedFaceExclusively(final Vector2d v, final double eps) {
		// If its on the faces edge, return false
		if (isOnEdge(v, eps, he -> he.positionAfterFolded)) {
			return false;
		}

		return isInside(v, eps, he -> he.positionAfterFolded);
	}

	/**
	 * Whether v is on edge of face.
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
	private boolean isOnEdge(final Vector2d v, final double eps,
			final Function<OriHalfedge, Vector2d> getPosition) {
		int heNum = halfedges.size();

		// If its on the face's edge, return true
		for (int i = 0; i < heNum; i++) {
			OriHalfedge he = halfedges.get(i);
			if (GeomUtil.distancePointToSegment(v, getPosition.apply(he),
					getPosition.apply(he.next)) < eps) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Whether v is inside of this face. This method is very sensitive to the
	 * case that v is very close to the edge of this face.
	 *
	 * @param v
	 *            point to be tested.
	 * @param eps
	 * @param getPosition
	 *            a function to get the position of face's vertex from
	 *            half-edge. That is, you can designate using position before
	 *            fold or the one after fold.
	 * @return true if v inside of this face.
	 */
	private boolean isInside(final Vector2d v, final double eps,
			final Function<OriHalfedge, Vector2d> getPosition) {
		int heNum = halfedges.size();

		OriHalfedge baseHe = halfedges.get(0);
		boolean baseFlg = GeomUtil.CCWcheck(getPosition.apply(baseHe),
				getPosition.apply(baseHe.next), v);

		for (int i = 1; i < heNum; i++) {
			OriHalfedge he = halfedges.get(i);
			if (GeomUtil.CCWcheck(getPosition.apply(he), getPosition.apply(he.next),
					v) != baseFlg) {
				return false;
			}
		}

		return true;
	}

}
