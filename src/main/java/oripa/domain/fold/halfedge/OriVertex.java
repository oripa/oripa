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

import oripa.util.collection.CollectionUtil;
import oripa.value.OriPoint;
import oripa.vecmath.Vector2d;

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
	 * given position is assumed to be for before fold. This field should be
	 * updated by folding. Hence this will be the position after fold.
	 */
	private Vector2d position = new Vector2d(0, 0);

	/**
	 * position before fold
	 */
	private final Vector2d positionBeforeFolding;

	private final ArrayList<OriEdge> edges = new ArrayList<>();
	private final Map<OriVertex, OriEdge> edgeMap = new HashMap<>();

	/**
	 * For outputting file.
	 */
	private int vertexID = 0;

	public OriVertex(final Vector2d position) {
		this.position = position;
		positionBeforeFolding = position;
	}

	public OriVertex(final double x, final double y) {
		this(new Vector2d(x, y));
	}

	/**
	 * @return position
	 */
	public Vector2d getPosition() {
		return position;
	}

	public void setPosition(final Vector2d v) {
		position = v;
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

	/**
	 * The angle between edges v1-v2 and v2-v3.
	 *
	 * @param v1
	 * @param v2
	 * @param v3
	 * @return 0 to 2 * pi between edges v1-v2 and v2-v3
	 */
	private double getAngleDifference(
			final OriVertex v1, final OriVertex v2, final OriVertex v3) {
		var p = v2.getPositionBeforeFolding();
		var preP = v1.getPositionBeforeFolding().subtract(p);
		var nxtP = v3.getPositionBeforeFolding().subtract(p);

		var prePAngle = preP.ownAngle();
		var nxtPAngle = nxtP.ownAngle();

		if (prePAngle > nxtPAngle) {
			nxtPAngle += 2 * Math.PI;
		}

		return nxtPAngle - prePAngle;

//		return preP.angle(nxtP); // fails if a concave face exists.
	}

	/**
	 * The angle between i-th edge and (i+1)-th edge incident to this vertex.
	 *
	 * @param index
	 * @return 0 to 2 * pi between i-th edge and (i+1)-th edge
	 */
	public double getAngleDifference(final int index) {
		return getAngleDifference(
				getOppositeVertex(index),
				this,
				getOppositeVertex(index + 1));
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
