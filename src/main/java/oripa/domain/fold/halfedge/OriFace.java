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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import oripa.geom.GeomUtil;
import oripa.geom.Polygon;
import oripa.geom.Segment;
import oripa.util.collection.CollectionUtil;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

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

	private List<OriLine> precreases = new ArrayList<>();

	private boolean faceFront = true;

	public Color colorForDebug;

	/**
	 * working variable for computing position after fold by the algorithm.
	 */
	private boolean movedByFold = false;

	/**
	 * ID of this face.
	 */
	private int faceID = -1;

	private Polygon polygon;
	private Polygon polygonBeforeFolding;

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

	public void setPrecreases(final Collection<OriLine> precreases) {
		this.precreases = new ArrayList<>(precreases);
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
	 * this method breaks the edge informations in each vertex.
	 *
	 * @return
	 */
	public OriFace remove180degreeVertices(final double eps) {
		int count = halfedgeCount();
		var toDelete = new ArrayList<Integer>();

		for (int i = 0; i < count; i++) {
			int j = (i + 1) % count;
			var he0 = halfedges.get(i);
			var he1 = halfedges.get(j);

			if (GeomUtil.CCWcheck(he0.getPosition(), he1.getPosition(), he1.getNext().getPosition()) == 0) {
				toDelete.add(j);
			}
		}

		// remove from back to front
		toDelete.sort(Comparator.reverseOrder());
		for (int i : toDelete) {
			halfedges.remove(i);
		}

		makeHalfedgeLoop(eps);

		return this;
	}

	/**
	 * this method breaks the edge informations in each vertex.
	 *
	 * @return
	 */
	public OriFace removeDuplicatedVertices(final double eps) {
		int count = halfedgeCount();
		var toDelete = new ArrayList<Integer>();

		for (int i = 0; i < count; i++) {
			int j = (i + 1) % count;
			var he0 = halfedges.get(i);
			var he1 = halfedges.get(j);

			if (he0.getPosition().equals(he1.getPosition(), eps)) {
				toDelete.add(j);
			}
		}

		// remove from back to front
		toDelete.sort(Comparator.reverseOrder());
		for (int i : toDelete) {
			halfedges.remove(i);
		}

		makeHalfedgeLoop(eps);

		return this;
	}

	/**
	 * Link each half-edge to previous one and next one in the
	 * {@link #halfedges}. Then builds the polygon of this face.
	 */
	public void makeHalfedgeLoop(final double eps) {
		for (int i = 0; i < halfedges.size(); i++) {
			OriHalfedge pre_he = CollectionUtil.getCircular(halfedges, i - 1);
			OriHalfedge he = halfedges.get(i);
			OriHalfedge nxt_he = CollectionUtil.getCircular(halfedges, i + 1);

			he.setNext(nxt_he);
			he.setPrevious(pre_he);
		}

		polygon = toPolygon(eps);
		polygonBeforeFolding = toPolygonBeforeFolding(eps);
	}

	public List<Vector2d> createOutlineVerticesAfterFolding() {
		return halfedgeStream()
				.map(OriHalfedge::getPositionForDisplay)
				.toList();
	}

	public List<Vector2d> createOutlineVerticesBeforeFolding() {
		var centroidOpt = getCentroidBeforeFolding();

		if (centroidOpt.isEmpty()) {
			return List.of();
		}

		double rate = 0.5;
		return halfedgeStream()
				.map(he -> GeomUtil.computeDividingPoint(rate, he.getPositionBeforeFolding(), centroidOpt.get()))
				.toList();
	}

	/**
	 * Computes centroid.
	 *
	 * @return centroid of this face before folding
	 */
	public Optional<Vector2d> getCentroidBeforeFolding() {
		return GeomUtil.computeCentroid(halfedges.stream()
				.map(OriHalfedge::getPositionBeforeFolding)
				.toList());
	}

	/**
	 * Computes centroid.
	 *
	 * @return centroid of this face with current position
	 *         {@link OriVertex#getPosition()} of half-edges.
	 */
	public Optional<Vector2d> getCentroid() {
		return GeomUtil.computeCentroid(halfedges.stream()
				.map(OriHalfedge::getPosition)
				.toList());
	}

	/**
	 * Creates a polygon of this face with vertex duplication reduction.
	 *
	 * @return
	 */
	public Polygon toPolygon(final double eps) {
		var vertices = halfedges.stream()
				.map(OriHalfedge::getPosition)
				.toList();

		if (!isFaceFront()) {
			vertices = vertices.reversed();
		}

		return new Polygon(removeDuplications(vertices, eps));
	}

	public Polygon toPolygonBeforeFolding(final double eps) {
		var vertices = halfedges.stream()
				.map(OriHalfedge::getPositionBeforeFolding)
				.toList();

		return new Polygon(removeDuplications(vertices, eps));
	}

	private List<Vector2d> removeDuplications(final List<Vector2d> vertices, final double eps) {
		return vertices.stream()
				.filter(v -> vertices.stream()
						.noneMatch(u -> v != u && v.equals(u, eps)))
				.toList();
	}

	/**
	 * Updates the polygon of this face.
	 */
	public void refreshPositions(final double eps) {
		polygon = toPolygon(eps);
	}

	/**
	 * Triangulates the polygon of this face if necessary for inclusion tests
	 * and inner points. If positions of vertices changed, call
	 * {@link #refreshPositions(double)} and then call this method.
	 *
	 * @param eps
	 */
	public void buildTriangles(final double eps) {
		polygon.buildTriangles(eps);
	}

	public List<Vector2d> getInnerPoints(final double eps) {
		if (polygon == null) {
			refreshPositions(eps);
		}
		return polygon.getInnerPoints(eps);
	}

	/**
	 * Whether v is inclusively on this face (inside or on the edges). The test
	 * is done with current position {@link OriVertex#getPosition()} of the
	 * polygon of this face.
	 *
	 * @param v
	 *            point to be tested.
	 * @return true if v is inside or on the edges of this face.
	 */
	public boolean includesInclusively(final Vector2d v, final double eps) {
		return polygon.includesInclusively(v, eps);
	}

	/**
	 * Whether v is strictly inside of this face where the positions are ones
	 * after folding.
	 *
	 * @param v
	 *            point to be tested.
	 * @return true if v is strictly inside of this face.
	 */
	public boolean includesExclusively(final Vector2d v, final double eps) {
		return polygon.includesExclusively(v, eps);
	}

	public boolean includesExclusivelyBeforeFolding(final Vector2d v, final double eps) {
		return polygonBeforeFolding.includesExclusively(v, eps);
	}

	/**
	 * Whether this face includes {@code line} entirely. This method uses
	 * current position. The inclusion test is inclusive.
	 *
	 * @param segment
	 * @return {@code true} if {@code face} includes {@code line} entirely.
	 */
	public boolean includesInclusively(final Segment segment, final double eps) {
		return polygon.includesInclusively(segment, eps);
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

		return str + " id=" + faceID;
	}
}
