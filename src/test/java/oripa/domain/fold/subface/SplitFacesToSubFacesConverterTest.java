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
package oripa.domain.fold.subface;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import oripa.domain.fold.halfedge.OriVertex;
import oripa.domain.fold.subface.test.OriFaceFactoryForTest;

/**
 * @author OUCHI Koji
 *
 */
@ExtendWith(MockitoExtension.class)
class SplitFacesToSubFacesConverterTest {
	@InjectMocks
	private SplitFacesToSubFacesConverter converter;

	/**
	 * Test method for
	 * {@link oripa.domain.fold.subface.SplitFacesToSubFacesConverter#convertToSubFaces(java.util.List)}.
	 */
	@Test
	void testConvertToSubFaces() {
		var splitFace1 = OriFaceFactoryForTest.create10PxSquareMock(0, 0);
		var splitFace2 = OriFaceFactoryForTest.create10PxSquareMock(10, 0);

		when(splitFace1.remove180degreeVertices()).thenReturn(splitFace1);
		when(splitFace1.removeDuplicatedVertices(anyDouble())).thenReturn(splitFace1);
		when(splitFace1.halfedgeCount()).thenReturn(4);

		when(splitFace2.remove180degreeVertices()).thenReturn(splitFace2);
		when(splitFace2.removeDuplicatedVertices(anyDouble())).thenReturn(splitFace2);
		when(splitFace2.halfedgeCount()).thenReturn(4);

		OriVertex vertex = mock();
		Collection<OriVertex> vertices = List.of(vertex);

		var splitFaces = List.of(splitFace1, splitFace2);

		var subFaces = converter.convertToSubFaces(splitFaces, vertices, 1e-6);

		for (int i = 0; i < subFaces.size(); i++) {
			assertSame(splitFaces.get(i), subFaces.get(i).getOutline());
		}
	}

}
