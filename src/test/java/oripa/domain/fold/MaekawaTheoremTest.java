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

import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
class MaekawaTheoremTest {

	/**
	 * Test method for
	 * {@link oripa.domain.fold.MaekawaTheorem#holds(oripa.domain.fold.OriVertex)}.
	 */
	@Test
	void testHolds_birdFoot() {
		var vertex = createBirdFoot();
		var maekawa = new MaekawaTheorem();

		assertTrue(maekawa.holds(vertex));

		vertex.getEdge(0).type = OriLine.Type.VALLEY.toInt();

		assertFalse(maekawa.holds(vertex));
	}

	private OriVertex createBirdFoot() {
		var oriVertex = new OriVertex(1, 1);
		oriVertex.addEdge(OriEdgeFactoryForTest.createEdge(0, 0, 1, 1, OriLine.Type.MOUNTAIN));
		oriVertex.addEdge(OriEdgeFactoryForTest.createEdge(1, 0, 1, 1, OriLine.Type.VALLEY));
		oriVertex.addEdge(OriEdgeFactoryForTest.createEdge(2, 0, 1, 1, OriLine.Type.MOUNTAIN));

		oriVertex.addEdge(OriEdgeFactoryForTest.createEdge(1, 2, 1, 1, OriLine.Type.MOUNTAIN));

		return oriVertex;
	}

}
