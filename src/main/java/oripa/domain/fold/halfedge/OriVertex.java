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
import java.util.Collections;
import java.util.List;

import javax.vecmath.Vector2d;

import oripa.util.collection.CollectionUtil;

/**
 * Vertex of crease pattern (or graph, more abstractly) with reference to edges
 * incident to it.
 *
 * @author OUCHI Koji
 *
 */
public class OriVertex {

	/**
	 * position. The constructor sets the given position to this field where the
	 * given position is assumed to be for before fold. This field is assumed to
	 * follow the change by folding. Hence this will be the position after fold.
	 */
	// TODO: hide this field and add getter.
	public Vector2d p = new Vector2d();

	/**
	 * position before fold
	 */
	// TODO: hide this field and add getter.
	public Vector2d preP = new Vector2d();

	public Vector2d tmpVec = new Vector2d();
	private final ArrayList<OriEdge> edges = new ArrayList<>();
	public boolean tmpFlg = false;
	public int tmpInt = 0;

	public OriVertex(final Vector2d p) {
		this.p.set(p);
		preP.set(p);
	}

	public OriVertex(final double x, final double y) {
		p.set(x, y);
		preP.set(p);
	}

	/**
	 * @return unmodifiable list of edges incident to this vertex in clockwise
	 *         direction on screen.
	 */
	public List<OriEdge> getEdges() {
		return Collections.unmodifiableList(edges);
	}

	/**
	 * Stores and sorts edges in clockwise direction.
	 *
	 * @param edge
	 *            an edge whose end point is this vertex.
	 */
	public void addEdge(final OriEdge edge) {
		if (!insertEdge(edge)) {
			edges.add(edge);
		}
	}

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
		Vector2d dir = new Vector2d();
		var sv = edge.getStartVertex();
		if (sv == this) {
			var ev = edge.getEndVertex();
			dir.set(ev.p.x - this.p.x, ev.p.y - this.p.y);
		} else {
			dir.set(sv.p.x - this.p.x, sv.p.y - this.p.y);
		}

		return Math.atan2(dir.y, dir.x);

	}

	public OriEdge getPrevEdge(final OriEdge e) {
		return getEdge(edges.lastIndexOf(e) - 1);
	}

	public OriEdge getEdge(final int index) {
		return CollectionUtil.getCircular(edges, index);
	}

	public OriVertex getOppositeVertex(final int index) {
		return getEdge(index).oppositeVertex(this);
	}

}
