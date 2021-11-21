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

package oripa.gui.view.estimation;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OriHalfedge;
import oripa.geom.RectangleDomain;

public class TriangleFace {

	public TriangleVertex[] v;
	private final OriFace face;

	private final List<Integer> halfEdgeIndices;

	public TriangleFace(final OriFace f, final List<Integer> halfEdgeIndices) {
		face = f;
		v = new TriangleVertex[3];
		for (int i = 0; i < 3; i++) {
			v[i] = new TriangleVertex();
		}

		if (halfEdgeIndices.size() != 3) {
			throw new IllegalArgumentException();
		}

		this.halfEdgeIndices = new ArrayList<>(halfEdgeIndices);
	}

	public void initializePositions() {
		for (int i = 0; i < halfEdgeIndices.size(); i++) {
			var he = face.getHalfedge(halfEdgeIndices.get(i));
			v[i].p = new Vector2d(he.getPosition());
		}
	}

	public void setPosition(final int index, final double x, final double y) {
		v[index].p.x = x;
		v[index].p.y = y;
	}

	public Vector2d getPosition(final int index) {
		return v[index].p;
	}

	public void prepareColor(final RectangleDomain paperDomain) {
		for (int i = 0; i < halfEdgeIndices.size(); i++) {
			var he = face.getHalfedge(halfEdgeIndices.get(i));
			v[i].color = new Vector3d(he.getVertexColor());

			double x = (he.getPositionBeforeFolding().x - paperDomain.getCenterX()) / paperDomain.getWidth();
			double y = (he.getPositionBeforeFolding().y - paperDomain.getCenterY()) / paperDomain.getHeight();

			v[i].uv = new Vector2d(x + 0.5, y + 0.5);
		}
	}

	public boolean isFaceFront() {
		return face.isFaceFront();
	}
}
