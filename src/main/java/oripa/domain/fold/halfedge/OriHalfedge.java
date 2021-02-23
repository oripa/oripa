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

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

/**
 * A half-edge is a directed edge on an undirected edge. Half-edge has a pair,
 * which is a half-edge with reversed direction of the half-edge. Each face is
 * surrounded by half-edges in clockwise or counterclockwise direction (it
 * depends on the implementation. ORIPA uses counterclockwise direction on the
 * screen). Each of surrounding half-edges has a reference to such a face.
 *
 * @author OUCHI Koji
 *
 */
public class OriHalfedge {

	public OriHalfedge next = null;
	public OriHalfedge prev = null;
	public OriHalfedge pair = null;
	private OriEdge edge = null;
	private OriVertex vertex = null;
	private OriFace face = null;

	/**
	 * mountain/valley value
	 */
	public int type = 0;

	/**
	 * temporary position while folding
	 */
	public Vector2d tmpVec = new Vector2d();

	public Vector2d positionForDisplay = new Vector2d();
	// TODO: should be replaced with getPosition()?
	public Vector2d positionAfterFolded = new Vector2d();
	public Vector3d vertexColor = new Vector3d();

	public OriHalfedge(final OriVertex v, final OriFace f) {
		vertex = v;
		face = f;
		tmpVec.set(v.p);
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
	void setEdge(final OriEdge edge) {
		this.edge = edge;
	}

	/**
	 *
	 * @return the line type of the underlying edge.
	 */
	public int getType() {
		return edge.type;
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
	 * Gets current position of the start point of this half-edge.
	 *
	 * @return current position (the same as {@code getVertex().p}).
	 */
	public Vector2d getPosition() {
		return vertex.p;
	}

	/**
	 * Gets position of the start point of this half-edge before fold.
	 *
	 * @return position before fold (the same as {@code getVertex().preP})
	 */
	public Vector2d getPositionBeforeFolding() {
		return vertex.preP;
	}
}
