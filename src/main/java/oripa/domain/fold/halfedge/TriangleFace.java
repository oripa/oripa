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
import java.util.List;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

public class TriangleFace {

	public TriangleVertex[] v;
	public OriFace face;

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

	public void prepareColor(final double paperSize) {
		for (int i = 0; i < halfEdgeIndices.size(); i++) {
			var he = face.getHalfedge(halfEdgeIndices.get(i));
			v[i].color = new Vector3d(he.getVertexColor());
			v[i].uv = new Vector2d(he.getPositionBeforeFolding().x / paperSize
					+ 0.5, he.getPositionBeforeFolding().y / paperSize + 0.5);
		}
	}
}
