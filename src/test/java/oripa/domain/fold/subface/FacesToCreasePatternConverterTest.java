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

import oripa.domain.cptool.LineAdder;
import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.domain.creasepattern.CreasePatternInterface;

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
	private CreasePatternInterface creasePattern;

	/**
	 * Test method for
	 * {@link oripa.domain.fold.subface.FacesToCreasePatternConverter#convertToCreasePattern(java.util.List)}.
	 */
	@Test
	void testConvertToCreasePattern() {
		var face1 = OriFaceFactoryForTest.create10PxSquareMock(0, 0);
		var face2 = OriFaceFactoryForTest.create10PxSquareMock(10, 0);
		var faces = List.of(face1, face2);

		when(cpFactory.createCreasePattern(anyCollection())).thenReturn(creasePattern);

		var converted = converter.convertToCreasePattern(faces);
		assertSame(creasePattern, converted);

		// tried to convert all half-edges?
		verify(adder, times(
				faces.stream()
						.mapToInt(f -> f.halfedges.size())
						.sum()))
								.addLine(any(), anyCollection());

		verify(creasePattern).cleanDuplicatedLines();
	}
}
