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

import java.util.Optional;

import oripa.vecmath.Vector2d;

/**
 * A half-edge is a directed edge on an undirected edge. Half-edge has a pair,
 * which is a half-edge with reversed direction of the half-edge. Each face is
 * surrounded by half-edges in clockwise or counterclockwise direction (it
 * depends on the implementation. ORIPA uses counterclockwise direction on the
 * mathematical coordinate system). Each of surrounding half-edges has a
 * reference to such a face.
 *
 * @author OUCHI Koji
 *
 */
public class OriHalfedge {

	private OriHalfedge next = null;
	private OriHalfedge previous = null;
	private OriHalfedge pair = null;
	private OriEdge edge = null;
	private final OriVertex vertex;
	private final OriFace face;

	/**
	 * mountain/valley value for constructing edges.
	 */
	private int temporaryType = 0;

	/**
	 * temporary position while folding
	 */
	private Vector2d positionWhileFolding = new Vector2d(0, 0);

	/**
	 * position for display after fold.
	 */
	private Vector2d positionForDisplay = new Vector2d(0, 0);

	public OriHalfedge(final OriVertex v, final OriFace f) {
		vertex = v;
		face = f;
		positionWhileFolding = v.getPosition();
	}

	/**
	 * @return next
	 */
	public OriHalfedge getNext() {
		return next;
	}

	/**
	 * @param next
	 *            Sets next
	 */
	public void setNext(final OriHalfedge next) {
		this.next = next;
	}

	/**
	 * @return previous
	 */
	public OriHalfedge getPrevious() {
		return previous;
	}

	/**
	 * @param previous
	 *            Sets previous
	 */
	void setPrevious(final OriHalfedge previous) {
		this.previous = previous;
	}

	/**
	 * @return pair of this half-edge.
	 */
	public Optional<OriHalfedge> getPair() {
		return Optional.ofNullable(pair);
	}

	/**
	 * @param pair
	 *            Sets pair of this half-edge.
	 */
	void setPair(final OriHalfedge pair) {
		this.pair = pair;
	}

	/**
	 * Gets the underlying edge.
	 *
	 * @return edge
	 */
	public OriEdge getEdge() {
		return edge;
	}

	/**
	 * @param edge
	 *            Sets the underlying edge
	 */
	public void setEdge(final OriEdge edge) {
		this.edge = edge;
	}

	/**
	 *
	 * @return the line type of the underlying edge.
	 */
	public int getType() {
		return edge.getType();
	}

	/**
	 * Gets vertex of the start point of this half-edge.
	 *
	 * @return vertex
	 */
	public OriVertex getVertex() {
		return vertex;
	}

	/**
	 * Gets the face containing this half-edge.
	 *
	 * @return face
	 */
	public OriFace getFace() {
		return face;
	}

	/**
	 * @return temporaryType
	 */
	int getTemporaryType() {
		return temporaryType;
	}

	/**
	 * @param temporaryType
	 *            Sets temporaryType
	 */
	void setTemporaryType(final int temporaryType) {
		this.temporaryType = temporaryType;
	}

	/**
	 * @return positionWhileFolding
	 */
	public Vector2d getPositionWhileFolding() {
		return positionWhileFolding;
	}

	public void setPositionWhileFolding(final Vector2d v) {
		positionWhileFolding = v;
	}

	/**
	 * Gets current position of the start point of this half-edge.
	 *
	 * @return current position (the same as {@code getVertex().getPosition()}).
	 */
	public Vector2d getPosition() {
		return vertex.getPosition();
	}

	/**
	 * @return position for display after fold.
	 */
	public Vector2d getPositionForDisplay() {
		return positionForDisplay;
	}

	public void setPositionForDisplay(final Vector2d v) {
		positionForDisplay = v;
	}

	/**
	 * Gets position of the start point of this half-edge before fold.
	 *
	 * @return position before fold (the same as
	 *         {@code getVertex().getPositionBeforeFolding()})
	 */
	public Vector2d getPositionBeforeFolding() {
		return vertex.getPositionBeforeFolding();
	}
}
