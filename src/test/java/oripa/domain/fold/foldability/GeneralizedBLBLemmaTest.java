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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

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

		when(oriVertex.edges.get(1).getType()).thenReturn(OriLine.Type.MOUNTAIN.toInt());
		when(oriVertex.edges.get(3).getType()).thenReturn(OriLine.Type.VALLEY.toInt());

		assertFalse(blb.holds(oriVertex));
	}

	private OriVertex createBirdFoot() {
		var oriVertex = new OriVertex(1, 1);
		oriVertex.addEdge(OriEdgeFactoryForTest.createEdgeSpy(0, 0, 1, 1, OriLine.Type.MOUNTAIN));
		oriVertex.addEdge(OriEdgeFactoryForTest.createEdgeSpy(1, 0, 1, 1, OriLine.Type.VALLEY));
		oriVertex.addEdge(OriEdgeFactoryForTest.createEdgeSpy(2, 0, 1, 1, OriLine.Type.MOUNTAIN));

		oriVertex.addEdge(OriEdgeFactoryForTest.createEdgeSpy(1, 2, 1, 1, OriLine.Type.MOUNTAIN));

		return oriVertex;
	}

	@Test
	void testHolds_equalAngles() {
		var oriVertex = createEqualAngles();

		assertTrue(blb.holds(oriVertex));

	}

	private OriVertex createEqualAngles() {
		var oriVertex = new OriVertex(1, 1);
		oriVertex.addEdge(OriEdgeFactoryForTest.createEdgeSpy(0, 1, 1, 1, OriLine.Type.MOUNTAIN));
		oriVertex.addEdge(OriEdgeFactoryForTest.createEdgeSpy(1, 0, 1, 1, OriLine.Type.VALLEY));
		oriVertex.addEdge(OriEdgeFactoryForTest.createEdgeSpy(2, 1, 1, 1, OriLine.Type.MOUNTAIN));
		oriVertex.addEdge(OriEdgeFactoryForTest.createEdgeSpy(1, 2, 1, 1, OriLine.Type.MOUNTAIN));

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
		var oriVertex = new OriVertex(1, 1);
		oriVertex.addEdge(OriEdgeFactoryForTest.createEdgeSpy(0, 0, 1, 1, OriLine.Type.MOUNTAIN));
		oriVertex.addEdge(OriEdgeFactoryForTest.createEdgeSpy(1, 0, 1, 1, OriLine.Type.VALLEY));
		oriVertex.addEdge(OriEdgeFactoryForTest.createEdgeSpy(2, 0, 1, 1, OriLine.Type.MOUNTAIN));

		oriVertex.addEdge(OriEdgeFactoryForTest.createEdgeSpy(2, 2, 1, 1, OriLine.Type.VALLEY));
		oriVertex.addEdge(OriEdgeFactoryForTest.createEdgeSpy(1, 2, 1, 1, OriLine.Type.MOUNTAIN));
		oriVertex.addEdge(OriEdgeFactoryForTest.createEdgeSpy(0, 2, 1, 1, OriLine.Type.MOUNTAIN));

		return oriVertex;
	}

	@Test
	void testHolds_45deg_135deg() {
		var oriVertex = create45deg135deg();

		assertTrue(blb.holds(oriVertex));
	}

	private OriVertex create45deg135deg() {
		var oriVertex = new OriVertex(0, 0);
		var angle = Math.PI / 8 * 3;
		oriVertex.addEdge(
				OriEdgeFactoryForTest.createEdgeSpy(0, 0, Math.cos(angle), Math.signum(angle),
						OriLine.Type.MOUNTAIN));
		angle = Math.PI / 8 * 5;
		oriVertex.addEdge(
				OriEdgeFactoryForTest.createEdgeSpy(0, 0, Math.cos(angle), Math.signum(angle),
						OriLine.Type.VALLEY));

		angle = Math.PI / 8 * 9;
		oriVertex.addEdge(
				OriEdgeFactoryForTest.createEdgeSpy(0, 0, Math.cos(angle), Math.signum(angle),
						OriLine.Type.MOUNTAIN));
		angle = Math.PI / 8 * (-1);
		oriVertex.addEdge(
				OriEdgeFactoryForTest.createEdgeSpy(0, 0, Math.cos(angle), Math.signum(angle),
						OriLine.Type.MOUNTAIN));

		return oriVertex;
	}
}
