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
package oripa.domain.fold;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import oripa.domain.fold.foldability.GeneralizedBigLittleBigLemma;
import oripa.domain.fold.halfedge.OriVertex;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
class GeneralizedBLBLemmaTest {

	/**
	 * Test method for
	 * {@link oripa.domain.fold.foldability.GeneralizedBigLittleBigLemma#holds(oripa.domain.fold.halfedge.OriVertex)}.
	 */
	@Test
	void testHolds_birdFoot() {
		var oriVertex = createBirdFoot();
		var blb = new GeneralizedBigLittleBigLemma();

		assertTrue(blb.holds(oriVertex));

		oriVertex.edges.get(1).type = OriLine.Type.MOUNTAIN.toInt();
		oriVertex.edges.get(3).type = OriLine.Type.VALLEY.toInt();

		assertFalse(blb.holds(oriVertex));
	}

	private OriVertex createBirdFoot() {
		var oriVertex = new OriVertex(1, 1);
		oriVertex.addEdge(OriEdgeFactoryForTest.createEdge(0, 0, 1, 1, OriLine.Type.MOUNTAIN));
		oriVertex.addEdge(OriEdgeFactoryForTest.createEdge(1, 0, 1, 1, OriLine.Type.VALLEY));
		oriVertex.addEdge(OriEdgeFactoryForTest.createEdge(2, 0, 1, 1, OriLine.Type.MOUNTAIN));

		oriVertex.addEdge(OriEdgeFactoryForTest.createEdge(1, 2, 1, 1, OriLine.Type.MOUNTAIN));

		return oriVertex;
	}

	@Test
	void testHolds_equalAngles() {
		var oriVertex = createEqualAngles();
		var blb = new GeneralizedBigLittleBigLemma();

		assertTrue(blb.holds(oriVertex));

	}

	private OriVertex createEqualAngles() {
		var oriVertex = new OriVertex(1, 1);
		oriVertex.addEdge(OriEdgeFactoryForTest.createEdge(0, 1, 1, 1, OriLine.Type.MOUNTAIN));
		oriVertex.addEdge(OriEdgeFactoryForTest.createEdge(1, 0, 1, 1, OriLine.Type.VALLEY));
		oriVertex.addEdge(OriEdgeFactoryForTest.createEdge(2, 1, 1, 1, OriLine.Type.MOUNTAIN));
		oriVertex.addEdge(OriEdgeFactoryForTest.createEdge(1, 2, 1, 1, OriLine.Type.MOUNTAIN));

		return oriVertex;
	}

	@Test
	void testHolds_twoSequences() {
		var oriVertex = createTwoSequences();
		var blb = new GeneralizedBigLittleBigLemma();

		assertTrue(blb.holds(oriVertex));

		oriVertex.getEdge(0).type = OriLine.Type.VALLEY.toInt();
		oriVertex.getEdge(3).type = OriLine.Type.MOUNTAIN.toInt();

		assertFalse(blb.holds(oriVertex));
	}

	private OriVertex createTwoSequences() {
		var oriVertex = new OriVertex(1, 1);
		oriVertex.addEdge(OriEdgeFactoryForTest.createEdge(0, 0, 1, 1, OriLine.Type.MOUNTAIN));
		oriVertex.addEdge(OriEdgeFactoryForTest.createEdge(1, 0, 1, 1, OriLine.Type.VALLEY));
		oriVertex.addEdge(OriEdgeFactoryForTest.createEdge(2, 0, 1, 1, OriLine.Type.MOUNTAIN));

		oriVertex.addEdge(OriEdgeFactoryForTest.createEdge(2, 2, 1, 1, OriLine.Type.VALLEY));
		oriVertex.addEdge(OriEdgeFactoryForTest.createEdge(1, 2, 1, 1, OriLine.Type.MOUNTAIN));
		oriVertex.addEdge(OriEdgeFactoryForTest.createEdge(0, 2, 1, 1, OriLine.Type.MOUNTAIN));

		return oriVertex;
	}

	@Test
	void testHolds_45deg_135deg() {
		var oriVertex = create45deg135deg();
		var blb = new GeneralizedBigLittleBigLemma();

		assertTrue(blb.holds(oriVertex));
	}

	private OriVertex create45deg135deg() {
		var oriVertex = new OriVertex(0, 0);
		var angle = Math.PI / 8 * 3;
		oriVertex.addEdge(
				OriEdgeFactoryForTest.createEdge(0, 0, Math.cos(angle), Math.signum(angle),
						OriLine.Type.MOUNTAIN));
		angle = Math.PI / 8 * 5;
		oriVertex.addEdge(
				OriEdgeFactoryForTest.createEdge(0, 0, Math.cos(angle), Math.signum(angle),
						OriLine.Type.VALLEY));

		angle = Math.PI / 8 * 9;
		oriVertex.addEdge(
				OriEdgeFactoryForTest.createEdge(0, 0, Math.cos(angle), Math.signum(angle),
						OriLine.Type.MOUNTAIN));
		angle = Math.PI / 8 * (-1);
		oriVertex.addEdge(
				OriEdgeFactoryForTest.createEdge(0, 0, Math.cos(angle), Math.signum(angle),
						OriLine.Type.MOUNTAIN));

		return oriVertex;
	}
}
