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

import org.junit.jupiter.api.Test;

import oripa.domain.fold.halfedge.OriVertex;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
class KawasakiTheoremTest {

	/**
	 * Test method for
	 * {@link oripa.domain.fold.foldability.KawasakiTheorem#holds(oripa.domain.fold.halfedge.OriVertex)}.
	 */
	@Test
	void testHolds_birdFoot() {
		var vertex = createBirdFoot();
		var kawasaki = new KawasakiTheorem();

		assertTrue(kawasaki.holds(vertex));
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
	void testHolds_wrongShape() {
		var vertex = createWrongShape();
		var kawasaki = new KawasakiTheorem();

		assertFalse(kawasaki.holds(vertex));
	}

	private OriVertex createWrongShape() {
		var oriVertex = new OriVertex(1, 1);
		oriVertex.addEdge(OriEdgeFactoryForTest.createEdge(0.5, 0, 1, 1, OriLine.Type.MOUNTAIN));
		oriVertex.addEdge(OriEdgeFactoryForTest.createEdge(1, 0, 1, 1, OriLine.Type.VALLEY));
		oriVertex.addEdge(OriEdgeFactoryForTest.createEdge(2, 0, 1, 1, OriLine.Type.MOUNTAIN));

		oriVertex.addEdge(OriEdgeFactoryForTest.createEdge(1, 2, 1, 1, OriLine.Type.MOUNTAIN));

		return oriVertex;
	}
}
