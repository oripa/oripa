package oripa.domain.fold.foldability;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import oripa.domain.fold.halfedge.OriVertex;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

class VertexFoldabilityTest {
    private final double UNIT_ANGLE = Math.PI / 8;

    private final VertexFoldability foldability = new VertexFoldability();

    @Test
    void test_foldable() {
        var vertex = createFoldableSpy();

        assertTrue(foldability.holds(vertex));
    }

    @Test
    void test_unfoldable() {
        var vertex = createUnoldableSpy();

        assertFalse(foldability.holds(vertex));
    }

    private OriVertex createFoldableSpy() {
        var oriVertex = spy(new OriVertex(0, 0));
        final int edgeCount = 6;
        when(oriVertex.edgeCount()).thenReturn(edgeCount);

        var v0 = OriVertexFactoryForTest.createVertexSpy(oriVertex, createPoint(1 * UNIT_ANGLE), 0, edgeCount);
        var e0 = OriEdgeFactoryForTest.createEdgeSpy(v0, oriVertex, OriLine.Type.VALLEY);

        var v1 = OriVertexFactoryForTest.createVertexSpy(oriVertex, createPoint(4 * UNIT_ANGLE), 1, edgeCount);
        var e1 = OriEdgeFactoryForTest.createEdgeSpy(v1, oriVertex, OriLine.Type.MOUNTAIN);

        var v2 = OriVertexFactoryForTest.createVertexSpy(oriVertex, createPoint(6 * UNIT_ANGLE), 2, edgeCount);
        var e2 = OriEdgeFactoryForTest.createEdgeSpy(v2, oriVertex, OriLine.Type.MOUNTAIN);

        var v3 = OriVertexFactoryForTest.createVertexSpy(oriVertex, createPoint(8 * UNIT_ANGLE), 3, edgeCount);
        var e3 = OriEdgeFactoryForTest.createEdgeSpy(v3, oriVertex, OriLine.Type.VALLEY);

        var v4 = OriVertexFactoryForTest.createVertexSpy(oriVertex, createPoint(12 * UNIT_ANGLE), 4, edgeCount);
        var e4 = OriEdgeFactoryForTest.createEdgeSpy(v4, oriVertex, OriLine.Type.MOUNTAIN);

        var v5 = OriVertexFactoryForTest.createVertexSpy(oriVertex, createPoint(15 * UNIT_ANGLE), 5, edgeCount);
        var e5 = OriEdgeFactoryForTest.createEdgeSpy(v5, oriVertex, OriLine.Type.MOUNTAIN);

        var edgeSpys = List.of(e0, e1, e2, e3, e4, e5);
        when(oriVertex.edgeStream()).thenAnswer(invocation -> edgeSpys.stream());
        OriVertexFactoryForTest.stubGetEdge(oriVertex, edgeSpys);

        return oriVertex;
    }

    private OriVertex createUnoldableSpy() {
        var oriVertex = spy(new OriVertex(0, 0));
        final int edgeCount = 6;
        when(oriVertex.edgeCount()).thenReturn(edgeCount);

        var v0 = OriVertexFactoryForTest.createVertexSpy(oriVertex, createPoint(1 * UNIT_ANGLE), 0, edgeCount);
        var e0 = OriEdgeFactoryForTest.createEdgeSpy(v0, oriVertex, OriLine.Type.VALLEY);

        var v1 = OriVertexFactoryForTest.createVertexSpy(oriVertex, createPoint(4 * UNIT_ANGLE), 1, edgeCount);
        var e1 = OriEdgeFactoryForTest.createEdgeSpy(v1, oriVertex, OriLine.Type.MOUNTAIN);

        var v2 = OriVertexFactoryForTest.createVertexSpy(oriVertex, createPoint(6 * UNIT_ANGLE), 2, edgeCount);
        var e2 = OriEdgeFactoryForTest.createEdgeSpy(v2, oriVertex, OriLine.Type.MOUNTAIN);

        var v3 = OriVertexFactoryForTest.createVertexSpy(oriVertex, createPoint(8 * UNIT_ANGLE), 3, edgeCount);
        var e3 = OriEdgeFactoryForTest.createEdgeSpy(v3, oriVertex, OriLine.Type.VALLEY);

        var v4 = OriVertexFactoryForTest.createVertexSpy(oriVertex, createPoint(9 * UNIT_ANGLE), 4, edgeCount);
        var e4 = OriEdgeFactoryForTest.createEdgeSpy(v4, oriVertex, OriLine.Type.MOUNTAIN);

        var v5 = OriVertexFactoryForTest.createVertexSpy(oriVertex, createPoint(12 * UNIT_ANGLE), 5, edgeCount);
        var e5 = OriEdgeFactoryForTest.createEdgeSpy(v5, oriVertex, OriLine.Type.MOUNTAIN);

        var edgeSpys = List.of(e0, e1, e2, e3, e4, e5);
        when(oriVertex.edgeStream()).thenAnswer(invocation -> edgeSpys.stream());
        OriVertexFactoryForTest.stubGetEdge(oriVertex, edgeSpys);

        return oriVertex;
    }

    private Vector2d createPoint(final double angle) {
        return new Vector2d(Math.cos(angle), Math.sin(angle));
    }
}
