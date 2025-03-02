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

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import oripa.domain.cptool.ElementRemover;
import oripa.domain.cptool.LineAdder;
import oripa.domain.cptool.PointsMerger;
import oripa.domain.creasepattern.CreasePattern;
import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.domain.fold.halfedge.OriFace;

/**
 * @author OUCHI Koji
 *
 */
@ExtendWith(MockitoExtension.class)
class FacesToCreasePatternConverterTest {
	@InjectMocks
	private FacesToCreasePatternConverter converter;
	@Mock
	private CreasePatternFactory cpFactory;
	@Mock
	private LineAdder adder;
	@Mock
	private ElementRemover remover;
	@Mock
	private PointsMerger pointMerger;

	@Mock
	private CreasePattern creasePattern;

	private static final double POINT_EPS = 1e-7;

	@Test
	void testConvertToCreasePattern() {
		OriFace face1 = mock();
		OriFace face2 = mock();

		when(face1.remove180degreeVertices(anyDouble())).thenReturn(face1);
		when(face1.removeDuplicatedVertices(anyDouble())).thenReturn(face1);
		when(face1.halfedgeCount()).thenReturn(3);

		when(face2.remove180degreeVertices(anyDouble())).thenReturn(face2);
		when(face2.removeDuplicatedVertices(anyDouble())).thenReturn(face2);
		when(face2.halfedgeCount()).thenReturn(3);

		var faces = List.of(face1, face2);

		when(cpFactory.createCreasePattern(anyDouble())).thenReturn(creasePattern);
		when(cpFactory.createCreasePattern(anyCollection())).thenReturn(creasePattern);

		when(pointMerger.mergeClosePoints(anyCollection(), anyDouble())).thenReturn(creasePattern);

		var converted = converter.convertToCreasePattern(faces, 100, POINT_EPS);
		assertSame(creasePattern, converted);

		// tried to convert all faces?
		verify(adder, times(faces.size())).addAll(any(), any(), anyDouble());

		verify(remover).removeMeaninglessVertices(creasePattern, POINT_EPS);
	}
}
