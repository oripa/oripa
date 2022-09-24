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

import java.util.List;

import javax.vecmath.Vector2d;

import oripa.domain.fold.halfedge.OriEdge;
import oripa.domain.fold.halfedge.OriVertex;
import oripa.value.OriLine;

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

	static void stubGetEdge(final OriVertex centralVertex, final List<OriEdge> edgeSpys) {
		for (int i = 0; i < edgeSpys.size(); i++) {
			lenient().doReturn(edgeSpys.get(i)).when(centralVertex).getEdge(i);
			lenient().doReturn(edgeSpys.get(i)).when(centralVertex).getEdge(i + edgeSpys.size());
		}
	}

	static OriVertex createBirdFootSpy() {
		var oriVertex = spy(new OriVertex(1, 1));
		final int edgeCount = 4;
		when(oriVertex.edgeCount()).thenReturn(edgeCount);

		var v0 = createVertexSpy(oriVertex, new Vector2d(0, 0), 0, edgeCount);
		var e0 = OriEdgeFactoryForTest.createEdgeSpy(v0, oriVertex, OriLine.Type.MOUNTAIN);

		var v1 = createVertexSpy(oriVertex, new Vector2d(1, 0), 1, edgeCount);
		var e1 = OriEdgeFactoryForTest.createEdgeSpy(v1, oriVertex, OriLine.Type.VALLEY);

		var v2 = createVertexSpy(oriVertex, new Vector2d(2, 0), 2, edgeCount);
		var e2 = OriEdgeFactoryForTest.createEdgeSpy(v2, oriVertex, OriLine.Type.MOUNTAIN);

		var v3 = createVertexSpy(oriVertex, new Vector2d(1, 2), 3, edgeCount);
		var e3 = OriEdgeFactoryForTest.createEdgeSpy(v3, oriVertex, OriLine.Type.MOUNTAIN);

		var edgeSpys = List.of(e0, e1, e2, e3);
		when(oriVertex.edgeStream()).thenAnswer(invocation -> edgeSpys.stream());
		stubGetEdge(oriVertex, edgeSpys);

		return oriVertex;
	}

	static OriVertex createEqualAnglesSpy() {
		var oriVertex = spy(new OriVertex(1, 1));
		final int edgeCount = 4;
		when(oriVertex.edgeCount()).thenReturn(edgeCount);

		var v0 = createVertexSpy(oriVertex, new Vector2d(0, 1), 0, edgeCount);
		var e0 = OriEdgeFactoryForTest.createEdgeSpy(v0, oriVertex, OriLine.Type.MOUNTAIN);

		var v1 = createVertexSpy(oriVertex, new Vector2d(1, 0), 1, edgeCount);
		var e1 = OriEdgeFactoryForTest.createEdgeSpy(v1, oriVertex, OriLine.Type.VALLEY);

		var v2 = createVertexSpy(oriVertex, new Vector2d(2, 1), 2, edgeCount);
		var e2 = OriEdgeFactoryForTest.createEdgeSpy(v2, oriVertex, OriLine.Type.MOUNTAIN);

		var v3 = createVertexSpy(oriVertex, new Vector2d(1, 2), 3, edgeCount);
		var e3 = OriEdgeFactoryForTest.createEdgeSpy(v3, oriVertex, OriLine.Type.MOUNTAIN);

		var edgeSpys = List.of(e0, e1, e2, e3);
		when(oriVertex.edgeStream()).thenAnswer(invocation -> edgeSpys.stream());
		stubGetEdge(oriVertex, edgeSpys);

		return oriVertex;
	}

	static OriVertex createTwoSequencesSpy() {
		var oriVertex = spy(new OriVertex(1, 1));
		final int edgeCount = 6;
		when(oriVertex.edgeCount()).thenReturn(edgeCount);

		var v0 = createVertexSpy(oriVertex, new Vector2d(0, 0), 0, edgeCount);
		var e0 = OriEdgeFactoryForTest.createEdgeSpy(v0, oriVertex, OriLine.Type.MOUNTAIN);

		var v1 = createVertexSpy(oriVertex, new Vector2d(1, 0), 1, edgeCount);
		var e1 = OriEdgeFactoryForTest.createEdgeSpy(v1, oriVertex, OriLine.Type.VALLEY);

		var v2 = createVertexSpy(oriVertex, new Vector2d(2, 0), 2, edgeCount);
		var e2 = OriEdgeFactoryForTest.createEdgeSpy(v2, oriVertex, OriLine.Type.MOUNTAIN);

		var v3 = createVertexSpy(oriVertex, new Vector2d(2, 2), 3, edgeCount);
		var e3 = OriEdgeFactoryForTest.createEdgeSpy(v3, oriVertex, OriLine.Type.VALLEY);

		var v4 = createVertexSpy(oriVertex, new Vector2d(1, 2), 4, edgeCount);
		var e4 = OriEdgeFactoryForTest.createEdgeSpy(v4, oriVertex, OriLine.Type.MOUNTAIN);

		var v5 = createVertexSpy(oriVertex, new Vector2d(0, 2), 5, edgeCount);
		var e5 = OriEdgeFactoryForTest.createEdgeSpy(v5, oriVertex, OriLine.Type.MOUNTAIN);

		var edgeSpys = List.of(e0, e1, e2, e3, e4, e5);
		when(oriVertex.edgeStream()).thenAnswer(invocation -> edgeSpys.stream());
		stubGetEdge(oriVertex, edgeSpys);

		return oriVertex;
	}

	static OriVertex create45deg135degSpy() {
		var oriVertex = spy(new OriVertex(0, 0));
		final int edgeCount = 4;
		when(oriVertex.edgeCount()).thenReturn(edgeCount);

		var angle = Math.PI / 8 * 9;
		var v0 = createVertexSpy(
				oriVertex, new Vector2d(Math.cos(angle), Math.sin(angle)), 0, edgeCount);
		var e0 = OriEdgeFactoryForTest.createEdgeSpy(oriVertex, v0, OriLine.Type.MOUNTAIN);

		angle = Math.PI / 8 * (-1);
		var v1 = createVertexSpy(
				oriVertex, new Vector2d(Math.cos(angle), Math.sin(angle)), 0, edgeCount);
		var e1 = OriEdgeFactoryForTest.createEdgeSpy(oriVertex, v1, OriLine.Type.MOUNTAIN);

		angle = Math.PI / 8 * 3;
		var v2 = createVertexSpy(
				oriVertex, new Vector2d(Math.cos(angle), Math.sin(angle)), 0, edgeCount);
		var e2 = OriEdgeFactoryForTest.createEdgeSpy(oriVertex, v2, OriLine.Type.MOUNTAIN);

		angle = Math.PI / 8 * 5;
		var v3 = createVertexSpy(
				oriVertex, new Vector2d(Math.cos(angle), Math.sin(angle)), 0, edgeCount);
		var e3 = OriEdgeFactoryForTest.createEdgeSpy(oriVertex, v3, OriLine.Type.VALLEY);

		var edgeSpys = List.of(e0, e1, e2, e3);
		when(oriVertex.edgeStream()).thenAnswer(invocation -> edgeSpys.stream());
		stubGetEdge(oriVertex, edgeSpys);

		return oriVertex;
	}

	static OriVertex createWrongAngleShapeSpy() {
		var oriVertex = spy(new OriVertex(1, 1));
		final int edgeCount = 4;
		when(oriVertex.edgeCount()).thenReturn(edgeCount);

		var v0 = createVertexSpy(oriVertex, new Vector2d(0.5, 0), 0, edgeCount);
		var e0 = OriEdgeFactoryForTest.createEdgeSpy(v0, oriVertex, OriLine.Type.MOUNTAIN);

		var v1 = createVertexSpy(oriVertex, new Vector2d(1, 0), 1, edgeCount);
		var e1 = OriEdgeFactoryForTest.createEdgeSpy(v1, oriVertex, OriLine.Type.VALLEY);

		var v2 = createVertexSpy(oriVertex, new Vector2d(2, 0), 2, edgeCount);
		var e2 = OriEdgeFactoryForTest.createEdgeSpy(v2, oriVertex, OriLine.Type.MOUNTAIN);

		var v3 = createVertexSpy(oriVertex, new Vector2d(1, 2), 3, edgeCount);
		var e3 = OriEdgeFactoryForTest.createEdgeSpy(v3, oriVertex, OriLine.Type.MOUNTAIN);

		var edgeSpys = List.of(e0, e1, e2, e3);
		when(oriVertex.edgeStream()).thenAnswer(invocation -> edgeSpys.stream());
		stubGetEdge(oriVertex, edgeSpys);

		return oriVertex;
	}

}
