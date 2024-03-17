/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/

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
package oripa.renderer.estimation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author OUCHI Koji
 *
 */
class TriangleFaceFactory {
	public List<TriangleFace> createAll(final Face face) {
		var triangles = new ArrayList<TriangleFace>();

		int heNum = face.halfedgeCount();
		for (int i = 1; i < heNum - 1; i++) {
			TriangleFace tri = new TriangleFace(face, List.of(0, i, i + 1));
			triangles.add(tri);
		}

		triangles.forEach(triangle -> triangle.initializePositions());

		return triangles;
	}
}
