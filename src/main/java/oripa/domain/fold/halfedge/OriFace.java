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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import oripa.domain.fold.stackcond.StackConditionOf3Faces;
import oripa.domain.fold.stackcond.StackConditionOf4Faces;
import oripa.geom.GeomUtil;
import oripa.geom.RectangleDomain;
import oripa.util.collection.CollectionUtil;
import oripa.value.OriLine;

/**
 * Face of crease pattern (or graph, more abstractly) with reference to
 * half-edges surrounding it.
 *
 * @author OUCHI Koji
 *
 */
public class OriFace {

	/**
	 * Assumed to contain half-edges in connection order circularly. The
	 * half-edge at index 0 should refer the half-edge at index 1, and for index
	 * k, the half-edge at index k should refer the half-edge at k + 1 by
	 * {@link OriHalfedge#next} field. The last half-edge should refer the
	 * half-edge at index 0.
	 */
	private final List<OriHalfedge> halfedges = new ArrayList<>();

	private final List<OriLine> precreases = new ArrayList<>();

	private boolean faceFront = true;

	public Color colorForDebug;

	/**
	 * working variable for computing position after fold by the algorithm.
	 */
	private boolean movedByFold = false;

	/**
	 * index of stack order for subface. It seems to be used only while making
	 * correct stack order.
	 */
	private int indexForStack = 0;

	/**
	 * ID of this face.
	 */
	private int faceID = 0;

	private boolean alreadyStacked = false;
	private final List<TriangleFace> triangles = new ArrayList<>();

	private final List<StackConditionOf4Faces> stackConditionsOf4Faces = new ArrayList<>();
	private final List<StackConditionOf3Faces> stackConditionsOf3Faces = new ArrayList<>();
	private final List<Integer> stackConditionsOf2Faces = new ArrayList<>();

	public OriFace() {
		int r = (int) (Math.random() * 255);
		int g = (int) (Math.random() * 255);
		int b = (int) (Math.random() * 255);
		colorForDebug = new Color(r, g, b);
	}

	/**
	 * @return half-edges as {@link Iterable}.
	 */
	public Iterable<OriHalfedge> halfedgeIterable() {
		return halfedges;
	}

	/**
	 *
	 * @return half-edges as {@link Stream}.
	 */
	public Stream<OriHalfedge> halfedgeStream() {
		return halfedges.stream();
	}

	/**
	 * half-edge list is assumed to contain half-edges in connection order
	 * circularly. The half-edge at index 0 should refer the half-edge at index
	 * 1, and for index k, the half-edge at index k should refer the half-edge
	 * at k + 1 by {@link OriHalfedge#next} field. The last half-edge should
	 * refer the half-edge at index 0.
	 *
	 * @param halfedge
	 *            to be added to the last of half-edge list.
	 */
	public void addHalfedge(final OriHalfedge halfedge) {
		halfedges.add(halfedge);
	}

	/**
	 * Returns the element at the specified position in the half-edge list.
	 *
	 * @param index
	 *            index of the element to return.
	 * @return the element at the specified position in the half-edge list.
	 */
	public OriHalfedge getHalfedge(final int index) {
		return halfedges.get(index);
	}

	/**
	 * Returns the number of half-edges surrounding this face.
	 *
	 * @return the number of half-edges surrounding this face.
	 */
	public int halfedgeCount() {
		return halfedges.size();
	}

	/**
	 *
	 * @return precreases as {@link Iterable}.
	 */
	public Iterable<OriLine> precreaseIterable() {
		return precreases;
	}

	/**
	 *
	 * @return precreases as {@link Stream}.
	 */
	public Stream<OriLine> precreaseStream() {
		return precreases.stream();
	}

	public void addAllPrecreases(final Collection<OriLine> precreases) {
		this.precreases.addAll(precreases);
	}

	public boolean hasPrecreases() {
		return !precreases.isEmpty();
	}

	/**
	 * @return faceFront. Initialized by {@code true}.
	 */
	public boolean isFaceFront() {
		return faceFront;
	}

	public void invertFaceFront() {
		faceFront = !faceFront;
	}

	/**
	 * @return movedByFold. Initialized by {@code false}.
	 */
	public boolean isMovedByFold() {
		return movedByFold;
	}

	/**
	 * @param movedByFold
	 *            Sets movedByFold
	 */
	public void setMovedByFold(final boolean movedByFold) {
		this.movedByFold = movedByFold;
	}

	/**
	 * @return indexForStack
	 */
	public int getIndexForStack() {
		return indexForStack;
	}

	/**
	 * @param indexForStack
	 *            Sets indexForStack
	 */
	public void setIndexForStack(final int indexForStack) {
		this.indexForStack = indexForStack;
	}

	/**
	 * Sets -1 to indexForStack.
	 */
	public void clearIndexForStack() {
		this.indexForStack = -1;
	}

	/**
	 * @return ID of this face.
	 */
	public int getFaceID() {
		return faceID;
	}

	/**
	 * @param faceID
	 *            Sets ID of this face.
	 */
	public void setFaceID(final int faceID) {
		this.faceID = faceID;
	}

	/**
	 * @return whether this face is already put in a subface's stack.
	 */
	public boolean isAlreadyStacked() {
		return alreadyStacked;
	}

	/**
	 * @param alreadyStacked
	 *            Sets alreadyStacked
	 */
	public void setAlreadyStacked(final boolean alreadyStacked) {
		this.alreadyStacked = alreadyStacked;
	}

	/**
	 * @return triangles
	 */
	public Stream<TriangleFace> triangleStream() {
		return triangles.stream();
	}

	/**
	 *
	 * @param condition
	 *            for a correct stack of subface.
	 */
	public void addStackConditionOf4Faces(final StackConditionOf4Faces condition) {
		stackConditionsOf4Faces.add(condition);
	}

	/**
	 * Removes all elements from {@link #stackConditionsOf4Faces}.
	 */
	public void clearStackConditionsOf4Faces() {
		stackConditionsOf4Faces.clear();
	}

	/**
	 *
	 * @return stack conditions of 4 faces as {@link Stream}.
	 */
	public Stream<StackConditionOf4Faces> stackConditionOf4FacesStream() {
		return stackConditionsOf4Faces.stream();
	}

	/**
	 *
	 * @param condition
	 *            for a correct stack of subface.
	 */
	public void addStackConditionOf3Faces(final StackConditionOf3Faces condition) {
		stackConditionsOf3Faces.add(condition);
	}

	/**
	 * Removes all elements from {@link #stackConditionsOf3Faces}.
	 */
	public void clearStackConditionsOf3Faces() {
		stackConditionsOf3Faces.clear();
	}

	/**
	 *
	 * @return stack conditions of 3 faces as {@link Stream}.
	 */
	public Stream<StackConditionOf3Faces> stackConditionOf3FacesStream() {
		return stackConditionsOf3Faces.stream();
	}

	public void addStackConditionOf2Faces(final Integer upperFaceORMatIndex) {
		stackConditionsOf2Faces.add(upperFaceORMatIndex);
	}

	/**
	 * Removes all elements from {@link #stackConditionsOf2Faces}.
	 */
	public void clearStackConditionsOf2Faces() {
		stackConditionsOf2Faces.clear();
	}

	/**
	 *
	 * @return stack conditions of 2 faces as {@link Stream}.
	 */
	public Stream<Integer> stackConditionsOf2FacesStream() {
		return stackConditionsOf2Faces.stream();
	}

	public void triangulateAndSetColor(final boolean bUseColor, final boolean bFlip, final double paperSize) {
		triangles.clear();

		var domain = new RectangleDomain();
		for (OriHalfedge he : halfedges) {
			domain.enlarge(he.getPosition());
		}
		double minX = domain.getLeft();
		double minY = domain.getTop();

		double faceWidth = Math.sqrt(domain.getWidth() * domain.getWidth()
				+ domain.getHeight() * domain.getHeight());

		for (OriHalfedge he : halfedges) {
			double val = 0;
			if (he.getType() == OriLine.Type.MOUNTAIN.toInt()) {
				val += 1;
			} else if (he.getType() == OriLine.Type.VALLEY.toInt()) {
				val -= 1;
			}

			var prevHe = he.getPrevious();
			if (prevHe.getType() == OriLine.Type.MOUNTAIN.toInt()) {
				val += 1;
			} else if (prevHe.getType() == OriLine.Type.VALLEY.toInt()) {
				val -= 1;
			}

			double vv = (val + 2) / 4.0;
			double v = (0.75 + vv * 0.25);

			var position = he.getPosition();
			v *= 0.9 + 0.15 * (Math.sqrt((position.x - minX)
					* (position.x - minX)
					+ (position.y - minY)
							* (position.y - minY))
					/ faceWidth);

			v = Math.min(1, v);

			if (bUseColor) {
				if (true) {
					if (faceFront ^ bFlip) {
						he.getVertexColor().set(v * 0.7, v * 0.7, v);
					} else {
						he.getVertexColor().set(v, v * 0.8, v * 0.7);
					}
//					} else {
//						if (faceFront ^ bFlip) {
//							he.vertexColor.set(v, v * 0.6, v * 0.6);
//						} else {
//							he.vertexColor.set(v, v, v * 0.95);
//						}
//
				}
			} else {
				he.getVertexColor().set(v, v, v * 0.95);
			}
		}

		int heNum = halfedges.size();
		OriHalfedge startHe = halfedges.get(0);
		for (int i = 1; i < heNum - 1; i++) {
			TriangleFace tri = new TriangleFace(this);
			tri.v[0].p = new Vector2d(startHe.getPosition());
			tri.v[1].p = new Vector2d(halfedges.get(i).getPosition());
			tri.v[2].p = new Vector2d(halfedges.get(i + 1).getPosition());

			tri.v[0].color = new Vector3d(startHe.getVertexColor());
			tri.v[1].color = new Vector3d(halfedges.get(i).getVertexColor());
			tri.v[2].color = new Vector3d(halfedges.get(i + 1).getVertexColor());

			tri.v[0].uv = new Vector2d(startHe.getPositionBeforeFolding().x / paperSize
					+ 0.5, startHe.getPositionBeforeFolding().y / paperSize + 0.5);
			tri.v[1].uv = new Vector2d(halfedges.get(i).getPositionBeforeFolding().x
					/ paperSize + 0.5,
					halfedges.get(i).getPositionBeforeFolding().y
							/ paperSize + 0.5);
			tri.v[2].uv = new Vector2d(halfedges.get(i + 1).getPositionBeforeFolding().x
					/ paperSize + 0.5,
					halfedges.get(i + 1).getPositionBeforeFolding().y
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

			he.setNext(nxt_he);
			he.setPrevious(pre_he);
		}
	}

	public List<Vector2d> createOutlineVerticesAfterFolding() {
		return halfedgeStream()
				.map(OriHalfedge::getPositionForDisplay)
				.collect(Collectors.toList());
	}

	public List<Vector2d> createOutlineVerticesBeforeFolding() {
		Vector2d centerP = getCentroidBeforeFolding();
		double rate = 0.5;
		return halfedgeStream()
				.map(he -> new Vector2d(
						he.getPositionBeforeFolding().x * rate + centerP.x * (1.0 - rate),
						he.getPositionBeforeFolding().y * rate + centerP.y * (1.0 - rate)))
				.collect(Collectors.toList());
	}

	/**
	 * Computes centroid.
	 *
	 * @return centroid of this face before folding
	 */
	public Vector2d getCentroidBeforeFolding() {
		return GeomUtil.computeCentroid(halfedges.stream()
				.map(OriHalfedge::getPositionBeforeFolding)
				.collect(Collectors.toList()));
	}

	/**
	 * Computes centroid.
	 *
	 * @return centroid of this face with current position
	 *         {@link OriVertex#getPosition()} of half-edges.
	 */
	public Vector2d getCentroid() {
		return GeomUtil.computeCentroid(halfedges.stream()
				.map(OriHalfedge::getPosition)
				.collect(Collectors.toList()));
	}

	/**
	 * Whether v is inclusively on this face (inside or on the edges). The test
	 * is done with current position {@link OriVertex#getPosition()} of
	 * half-edges.
	 *
	 * @param v
	 *            point to be tested.
	 * @return true if v is inside or on the edges of this face.
	 */
	public boolean isOnFaceInclusively(final Vector2d v) {
		// If it's on the face's edge, return true
		if (isOnEdge(v, GeomUtil.EPS, OriHalfedge::getPosition)) {
			return true;
		}

		return isInside(v, OriHalfedge::getPosition);
	}

	/**
	 * Whether v is strictly inside of this face where the positions are ones
	 * after folding.
	 *
	 * @param v
	 *            point to be tested.
	 * @return true if v is strictly inside of this face.
	 */
	public boolean isOnFaceExclusively(final Vector2d v, final double eps) {
		// If it's on the face's edge, return false
		if (isOnEdge(v, eps, OriHalfedge::getPosition)) {
			return false;
		}

		return isInside(v, OriHalfedge::getPosition);
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

		// If it's on the face's edge, return true
		for (OriHalfedge he : halfedges) {
			if (GeomUtil.distancePointToSegment(v, getPosition.apply(he),
					getPosition.apply(he.getNext())) < eps) {
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
	 * @param getPosition
	 *            a function to get the position of face's vertex from
	 *            half-edge. That is, you can designate using position before
	 *            fold or the one after fold.
	 * @return true if v inside of this face.
	 */
	private boolean isInside(final Vector2d v, final Function<OriHalfedge, Vector2d> getPosition) {
		int heNum = halfedges.size();

		OriHalfedge baseHe = halfedges.get(0);
		boolean baseFlg = GeomUtil.CCWcheck(getPosition.apply(baseHe),
				getPosition.apply(baseHe.getNext()), v);

		for (int i = 1; i < heNum; i++) {
			OriHalfedge he = halfedges.get(i);
			if (GeomUtil.CCWcheck(getPosition.apply(he), getPosition.apply(he.getNext()),
					v) != baseFlg) {
				return false;
			}
		}

		return true;
	}

	/* (non Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		var str = "OriFace:";
		str += halfedges.stream()
				.map(he -> he.getPosition().toString())
				.collect(Collectors.joining(","));

		return str;
	}
}
