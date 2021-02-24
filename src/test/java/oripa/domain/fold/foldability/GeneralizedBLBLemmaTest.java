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
		var oriVertex = OriVertexFactoryForTest.createBirdFootSpy();

		assertTrue(blb.holds(oriVertex));

		when(oriVertex.getEdge(1).getType()).thenReturn(OriLine.Type.MOUNTAIN.toInt());
		when(oriVertex.getEdge(3).getType()).thenReturn(OriLine.Type.VALLEY.toInt());

		assertFalse(blb.holds(oriVertex));
	}

	@Test
	void testHolds_equalAngles() {
		var oriVertex = OriVertexFactoryForTest.createEqualAnglesSpy();

		assertTrue(blb.holds(oriVertex));

	}

	@Test
	void testHolds_twoSequences() {
		var oriVertex = OriVertexFactoryForTest.createTwoSequencesSpy();

		assertTrue(blb.holds(oriVertex));

		when(oriVertex.getEdge(0).getType()).thenReturn(OriLine.Type.VALLEY.toInt());
		when(oriVertex.getEdge(3).getType()).thenReturn(OriLine.Type.MOUNTAIN.toInt());

		assertFalse(blb.holds(oriVertex));
	}

	@Test
	void testHolds_45deg_135deg() {
		var oriVertex = OriVertexFactoryForTest.create45deg135degSpy();

		assertTrue(blb.holds(oriVertex));
	}
}
