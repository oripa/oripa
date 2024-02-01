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
package oripa.domain.paint.outline;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalMatchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import oripa.domain.cptool.OverlappingLineExtractor;
import oripa.domain.cptool.Painter;
import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
@ExtendWith(MockitoExtension.class)
class OutlineAdderTest {
	@InjectMocks
	private OutlineAdder adder;

	@Mock
	private OverlappingLineExtractor overlappingExtractor;

	@Mock
	private Painter painter;

	/**
	 * Test method for
	 * {@link oripa.domain.paint.outline.OutlineAdder#addOutlines(oripa.domain.cptool.Painter, java.util.Collection)}.
	 */
	@Test
	void testAddOutlines() {
		var l0 = new OriLine(0, 0, 1, 0, OriLine.Type.MOUNTAIN);
		var l1 = new OriLine(1, 0, 1, 1, OriLine.Type.MOUNTAIN);
		var l2 = new OriLine(1, 1, 0, 1, OriLine.Type.MOUNTAIN);
		var l3 = new OriLine(0, 1, 0, 0, OriLine.Type.MOUNTAIN);
		var l4 = new OriLine(0.5, 0.5, 0.9, 0.5, OriLine.Type.MOUNTAIN);

		var creasePattern = (new CreasePatternFactory()).createCreasePattern(List.of(
				l0, l1, l2, l3, l4));
		final double eps = 1e-5;

		when(painter.getCreasePattern()).thenReturn(creasePattern);
		when(painter.getPointEps()).thenReturn(eps);

		var l0Cut = new OriLine(l0.getP0(), l0.getP1(), OriLine.Type.CUT);
		var l1Cut = new OriLine(l1.getP0(), l1.getP1(), OriLine.Type.CUT);

		when(overlappingExtractor.extract(anyCollection(), eq(l0Cut), eq(eps))).thenReturn(List.of(l0));
		when(overlappingExtractor.extract(anyCollection(), eq(l1Cut), eq(eps))).thenReturn(List.of(l1));
		when(overlappingExtractor.extract(anyCollection(), not(or(eq(l0Cut), eq(l1Cut))), eq(eps)))
				.thenReturn(List.of());

		var outlineVertices = List.of(new Vector2d(0, 0), new Vector2d(1, 0), new Vector2d(1, 1));

		var removedLinesCaptor = ArgumentCaptor.forClass(Collection.class);
		var addedLinesCaptor = ArgumentCaptor.forClass(Collection.class);

		adder.addOutlines(painter, outlineVertices);

		verify(painter).removeLines(removedLinesCaptor.capture());
		verify(painter).addLines(addedLinesCaptor.capture());

		assertEquals(2, removedLinesCaptor.getValue().size());
		assertEquals(outlineVertices.size(), addedLinesCaptor.getValue().size());

	}

}
