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

import oripa.value.OriLine;

/**
 * Undirected edge of crease pattern (or graph, more abstractly) with reference
 * to half-edges on it.
 *
 * @author OUCHI Koji
 *
 */
public class OriEdge {

	private OriVertex startVertex = null;
	private OriVertex endVertex = null;
	private OriHalfedge left = null;
	private OriHalfedge right = null;
	private int type = 0;

	public OriEdge(final OriVertex startVertex, final OriVertex endVertex, final int type) {
		this.type = type;
		this.startVertex = startVertex;
		this.endVertex = endVertex;
	}

	/**
	 * @return sv
	 */
	public OriVertex getStartVertex() {
		return startVertex;
	}

	/**
	 * @return ev
	 */
	public OriVertex getEndVertex() {
		return endVertex;
	}

	/**
	 * @return left
	 */
	public OriHalfedge getLeft() {
		return left;
	}

	/**
	 * @param left
	 *            Sets left
	 */
	void setLeft(final OriHalfedge left) {
		this.left = left;
	}

	/**
	 * @return right
	 */
	public OriHalfedge getRight() {
		return right;
	}

	/**
	 * @param right
	 *            Sets right
	 */
	void setRight(final OriHalfedge right) {
		this.right = right;
	}

	/**
	 * @return type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type
	 *            Sets type
	 */
	void setType(final int type) {
		this.type = type;
	}

	public OriVertex oppositeVertex(final OriVertex v) {
		return v == startVertex ? endVertex : startVertex;
	}

	public boolean isFoldLine() {
		return type == OriLine.Type.MOUNTAIN.toInt() || type == OriLine.Type.VALLEY.toInt();
	}

}
