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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import javax.vecmath.Vector2d;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import oripa.domain.fold.halfedge.OriEdge;
import oripa.domain.fold.halfedge.OriVertex;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
@ExtendWith(MockitoExtension.class)
class GeneralizedBLBLemmaTest {
	@InjectMocks
	private GeneralizedBigLittleBigLemma blb;

	/**
	 * Test method for
	 * {@link oripa.domain.fold.foldability.GeneralizedBigLittleBigLemma#holds(oripa.domain.fold.halfedge.OriVertex)}.
	 */
	@Test
	void testHolds_birdFoot() {
		var oriVertex = createBirdFoot();

		assertTrue(blb.holds(oriVertex));

		oriVertex = createBirdFoot();

		when(oriVertex.getEdge(1).getType()).thenReturn(OriLine.Type.MOUNTAIN.toInt());
		when(oriVertex.getEdge(3).getType()).thenReturn(OriLine.Type.VALLEY.toInt());

		assertFalse(blb.holds(oriVertex));
	}

	private void stubGetEdge(final OriVertex centralVertex, final List<OriEdge> edgeSpys) {
		for (int i = 0; i < edgeSpys.size(); i++) {
			lenient().doReturn(edgeSpys.get(i)).when(centralVertex).getEdge(i);
			lenient().doReturn(edgeSpys.get(i)).when(centralVertex).getEdge(i + edgeSpys.size());
		}
	}

	private OriVertex createBirdFoot() {
		var oriVertex = spy(new OriVertex(1, 1));
		final int edgeCount = 4;
		when(oriVertex.edgeCount()).thenReturn(edgeCount);

		var v0 = OriVertexFactoryForTest.createVertexSpy(oriVertex, new Vector2d(0, 0), 0, edgeCount);
		var e0 = OriEdgeFactoryForTest.createEdgeSpy(v0, oriVertex, OriLine.Type.MOUNTAIN);

		var v1 = OriVertexFactoryForTest.createVertexSpy(oriVertex, new Vector2d(1, 0), 1, edgeCount);
		var e1 = OriEdgeFactoryForTest.createEdgeSpy(v1, oriVertex, OriLine.Type.VALLEY);

		var v2 = OriVertexFactoryForTest.createVertexSpy(oriVertex, new Vector2d(2, 0), 2, edgeCount);
		var e2 = OriEdgeFactoryForTest.createEdgeSpy(v2, oriVertex, OriLine.Type.MOUNTAIN);

		var v3 = OriVertexFactoryForTest.createVertexSpy(oriVertex, new Vector2d(1, 2), 3, edgeCount);
		var e3 = OriEdgeFactoryForTest.createEdgeSpy(v3, oriVertex, OriLine.Type.MOUNTAIN);

		var edgeSpys = List.of(e0, e1, e2, e3);
		when(oriVertex.edgeStream()).thenAnswer(invocation -> edgeSpys.stream());
		stubGetEdge(oriVertex, edgeSpys);

		return oriVertex;
	}

	@Test
	void testHolds_equalAngles() {
		var oriVertex = createEqualAngles();

		assertTrue(blb.holds(oriVertex));

	}

	private OriVertex createEqualAngles() {
		var oriVertex = spy(new OriVertex(1, 1));
		final int edgeCount = 4;
		when(oriVertex.edgeCount()).thenReturn(edgeCount);

		var v0 = OriVertexFactoryForTest.createVertexSpy(oriVertex, new Vector2d(0, 1), 0, edgeCount);
		var e0 = OriEdgeFactoryForTest.createEdgeSpy(v0, oriVertex, OriLine.Type.MOUNTAIN);

		var v1 = OriVertexFactoryForTest.createVertexSpy(oriVertex, new Vector2d(1, 0), 1, edgeCount);
		var e1 = OriEdgeFactoryForTest.createEdgeSpy(v1, oriVertex, OriLine.Type.VALLEY);

		var v2 = OriVertexFactoryForTest.createVertexSpy(oriVertex, new Vector2d(2, 1), 2, edgeCount);
		var e2 = OriEdgeFactoryForTest.createEdgeSpy(v2, oriVertex, OriLine.Type.MOUNTAIN);

		var v3 = OriVertexFactoryForTest.createVertexSpy(oriVertex, new Vector2d(1, 2), 3, edgeCount);
		var e3 = OriEdgeFactoryForTest.createEdgeSpy(v3, oriVertex, OriLine.Type.MOUNTAIN);

		var edgeSpys = List.of(e0, e1, e2, e3);
		when(oriVertex.edgeStream()).thenAnswer(invocation -> edgeSpys.stream());
		stubGetEdge(oriVertex, edgeSpys);

		return oriVertex;
	}

	@Test
	void testHolds_twoSequences() {
		var oriVertex = createTwoSequences();

		assertTrue(blb.holds(oriVertex));

		when(oriVertex.getEdge(0).getType()).thenReturn(OriLine.Type.VALLEY.toInt());
		when(oriVertex.getEdge(3).getType()).thenReturn(OriLine.Type.MOUNTAIN.toInt());

		assertFalse(blb.holds(oriVertex));
	}

	private OriVertex createTwoSequences() {
		var oriVertex = spy(new OriVertex(1, 1));
		final int edgeCount = 6;
		when(oriVertex.edgeCount()).thenReturn(edgeCount);

		var v0 = OriVertexFactoryForTest.createVertexSpy(oriVertex, new Vector2d(0, 0), 0, edgeCount);
		var e0 = OriEdgeFactoryForTest.createEdgeSpy(v0, oriVertex, OriLine.Type.MOUNTAIN);

		var v1 = OriVertexFactoryForTest.createVertexSpy(oriVertex, new Vector2d(1, 0), 1, edgeCount);
		var e1 = OriEdgeFactoryForTest.createEdgeSpy(v1, oriVertex, OriLine.Type.VALLEY);

		var v2 = OriVertexFactoryForTest.createVertexSpy(oriVertex, new Vector2d(2, 0), 2, edgeCount);
		var e2 = OriEdgeFactoryForTest.createEdgeSpy(v2, oriVertex, OriLine.Type.MOUNTAIN);

		var v3 = OriVertexFactoryForTest.createVertexSpy(oriVertex, new Vector2d(2, 2), 3, edgeCount);
		var e3 = OriEdgeFactoryForTest.createEdgeSpy(v3, oriVertex, OriLine.Type.VALLEY);

		var v4 = OriVertexFactoryForTest.createVertexSpy(oriVertex, new Vector2d(1, 2), 4, edgeCount);
		var e4 = OriEdgeFactoryForTest.createEdgeSpy(v4, oriVertex, OriLine.Type.MOUNTAIN);

		var v5 = OriVertexFactoryForTest.createVertexSpy(oriVertex, new Vector2d(0, 2), 5, edgeCount);
		var e5 = OriEdgeFactoryForTest.createEdgeSpy(v5, oriVertex, OriLine.Type.MOUNTAIN);

		var edgeSpys = List.of(e0, e1, e2, e3, e4, e5);
		when(oriVertex.edgeStream()).thenAnswer(invocation -> edgeSpys.stream());
		stubGetEdge(oriVertex, edgeSpys);

		return oriVertex;
	}

	@Test
	void testHolds_45deg_135deg() {
		var oriVertex = create45deg135deg();

		assertTrue(blb.holds(oriVertex));
	}

	private OriVertex create45deg135deg() {
		var oriVertex = spy(new OriVertex(0, 0));
		final int edgeCount = 4;
		when(oriVertex.edgeCount()).thenReturn(edgeCount);

		var angle = Math.PI / 8 * 9;
		var v0 = OriVertexFactoryForTest.createVertexSpy(
				oriVertex, new Vector2d(Math.cos(angle), Math.sin(angle)), 0, edgeCount);
		var e0 = OriEdgeFactoryForTest.createEdgeSpy(oriVertex, v0, OriLine.Type.MOUNTAIN);

		angle = Math.PI / 8 * (-1);
		var v1 = OriVertexFactoryForTest.createVertexSpy(
				oriVertex, new Vector2d(Math.cos(angle), Math.sin(angle)), 0, edgeCount);
		var e1 = OriEdgeFactoryForTest.createEdgeSpy(oriVertex, v1, OriLine.Type.MOUNTAIN);

		angle = Math.PI / 8 * 3;
		var v2 = OriVertexFactoryForTest.createVertexSpy(
				oriVertex, new Vector2d(Math.cos(angle), Math.sin(angle)), 0, edgeCount);
		var e2 = OriEdgeFactoryForTest.createEdgeSpy(oriVertex, v2, OriLine.Type.MOUNTAIN);

		angle = Math.PI / 8 * 5;
		var v3 = OriVertexFactoryForTest.createVertexSpy(
				oriVertex, new Vector2d(Math.cos(angle), Math.sin(angle)), 0, edgeCount);
		var e3 = OriEdgeFactoryForTest.createEdgeSpy(oriVertex, v3, OriLine.Type.VALLEY);

		var edgeSpys = List.of(e0, e1, e2, e3);
		when(oriVertex.edgeStream()).thenAnswer(invocation -> edgeSpys.stream());
		stubGetEdge(oriVertex, edgeSpys);

		return oriVertex;
	}
}
