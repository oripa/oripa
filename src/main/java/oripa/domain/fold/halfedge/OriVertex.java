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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.vecmath.Vector2d;

import oripa.util.collection.CollectionUtil;
import oripa.value.OriPoint;

/**
 * Vertex of crease pattern (or graph, more abstractly) with reference to edges
 * incident to it.
 *
 * @author OUCHI Koji
 *
 */
public class OriVertex implements Comparable<OriVertex> {

	/**
	 * position. The constructor sets the given position to this field where the
	 * given position is assumed to be for before fold. This field is assumed to
	 * follow the change by folding. Hence this will be the position after fold.
	 */
	private final Vector2d position = new Vector2d();

	/**
	 * position before fold
	 */
	private final Vector2d positionBeforeFolding = new Vector2d();

	private final ArrayList<OriEdge> edges = new ArrayList<>();
	private final Map<OriVertex, OriEdge> edgeMap = new HashMap<>();

	/**
	 * For outputting file.
	 */
	private int vertexID = 0;

	public OriVertex(final Vector2d position) {
		this.position.set(position);
		positionBeforeFolding.set(position);
	}

	public OriVertex(final double x, final double y) {
		position.set(x, y);
		positionBeforeFolding.set(position);
	}

	/**
	 * @return position
	 */
	public Vector2d getPosition() {
		return position;
	}

	/**
	 * @return positionBeforeFolding
	 */
	public Vector2d getPositionBeforeFolding() {
		return positionBeforeFolding;
	}

	/**
	 *
	 * @return stream of edges incident to this vertex in clockwise direction on
	 *         screen.
	 */
	public Stream<OriEdge> edgeStream() {
		return edges.stream();
	}

	public Iterable<OriEdge> edgeIterable() {
		return edges;
	}

	/**
	 * Stores and sorts edges in counterclockwise direction on the mathematical
	 * coordinate system.
	 *
	 * @param edge
	 *            an edge whose end point is this vertex.
	 */
	public void addEdge(final OriEdge edge) {
		if (!insertEdge(edge)) {
			edges.add(edge);
		}
		edgeMap.put(edge.oppositeVertex(this), edge);
	}

	/**
	 * Inserts the given edge as the angle list keeps in increasing order.
	 *
	 * @param edge
	 * @return
	 */
	private boolean insertEdge(final OriEdge edge) {
		double angle = getAngle(edge);
		for (int i = 0; i < edges.size(); i++) {
			double tmpAngle = getAngle(edges.get(i));
			if (tmpAngle > angle) {
				edges.add(i, edge);
				return true;
			}
		}
		return false;
	}

	private double getAngle(final OriEdge edge) {
		return edge.getAngle(this);
	}

	public OriEdge getPrevEdge(final OriEdge e) {
		return getEdge(edges.lastIndexOf(e) - 1);
	}

	public OriEdge getEdge(final int index) {
		return CollectionUtil.getCircular(edges, index);
	}

	public OriEdge getEdge(final OriVertex vertex) {
		return edgeMap.get(vertex);
	}

	public OriVertex getOppositeVertex(final int index) {
		return getEdge(index).oppositeVertex(this);
	}

	public int edgeCount() {
		return edges.size();
	}

	public boolean isInsideOfPaper() {
		return edgeStream().allMatch(edge -> !edge.isBoundary());
	}

	public boolean hasUnassignedEdge() {
		return edgeStream().anyMatch(edge -> edge.isUnassigned());
	}

	public long countUnassignedEdges() {
		return edgeStream().filter(OriEdge::isUnassigned).count();
	}

	/**
	 * @return vertexID
	 */
	public int getVertexID() {
		return vertexID;
	}

	/**
	 * @param vertexID
	 *            Sets vertexID
	 */
	public void setVertexID(final int vertexID) {
		this.vertexID = vertexID;
	}

	@Override
	public int compareTo(final OriVertex o) {
		return new OriPoint(positionBeforeFolding).compareTo(new OriPoint(o.positionBeforeFolding));
	}

//	/**
//	 * Expects the position before folding is unique.
//	 */
//	@Override
//	public int hashCode() {
//		return Objects.hash(positionBeforeFolding.getX(), positionBeforeFolding.getY());
//	}
//
//	@Override
//	public boolean equals(final Object obj) {
//		if (obj instanceof OriVertex) {
//			var that = (OriVertex) obj;
//			return positionBeforeFolding.getX() == that.positionBeforeFolding.getX()
//					&& positionBeforeFolding.getY() == that.positionBeforeFolding.getY();
//		}
//
//		return false;
//	}

	@Override
	public String toString() {
		return "OriVertex@" + position;
	}
}
