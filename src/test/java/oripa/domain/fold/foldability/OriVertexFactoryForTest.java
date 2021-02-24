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
package oripa.domain.fold.foldability;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import javax.vecmath.Vector2d;

import oripa.domain.fold.halfedge.OriVertex;

/**
 * @author OUCHI Koji
 *
 */
class OriVertexFactoryForTest {

	static OriVertex createVertexSpy(final OriVertex centralVertex, final Vector2d p, final int index,
			final int edgeCount) {
		var vertex = spy(new OriVertex(p));
		lenient().doReturn(vertex).when(centralVertex).getOppositeVertex(index);
		lenient().doReturn(vertex).when(centralVertex).getOppositeVertex(index + edgeCount);
		lenient().doReturn(centralVertex).when(vertex).getOppositeVertex(anyInt());

		return vertex;
	}
}
