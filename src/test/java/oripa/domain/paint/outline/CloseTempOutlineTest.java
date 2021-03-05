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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.List;

import javax.vecmath.Vector2d;

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

/**
 * @author OUCHI Koji
 *
 */
@ExtendWith(MockitoExtension.class)
class CloseTempOutlineTest {
	@InjectMocks
	private CloseTempOutline closer;
	@Mock
	private Painter painter;
	@Mock
	private OverlappingLineExtractor overlappingExtractor;

	/**
	 * Test method for
	 * {@link oripa.domain.paint.outline.CloseTempOutline#execute(java.util.Collection)}.
	 */
	@Test
	void testExecute() {
//		var creasePattern = mock(CreasePatternInterface.class);
		var creasePattern = (new CreasePatternFactory()).createCreasePattern(List.of(
				new OriLine(0, 0, 1, 0, OriLine.Type.CUT), new OriLine(1, 0, 1, 1, OriLine.Type.CUT),
				new OriLine(1, 1, 0, 1, OriLine.Type.CUT), new OriLine(0, 1, 0, 0, OriLine.Type.CUT),
				new OriLine(0.5, 0.5, 0.9, 0.5, OriLine.Type.CUT)));

		when(painter.getCreasePattern()).thenReturn(creasePattern);

		var outlineVertices = List.of(new Vector2d(0, 0), new Vector2d(1, 0), new Vector2d(1, 1));
		closer.execute(outlineVertices);

		var lineCaptor = ArgumentCaptor.forClass(Collection.class);

		verify(painter, times(2)).removeLines(anyCollection());
		verify(painter).addLines(lineCaptor.capture());
		assertEquals(outlineVertices.size(), lineCaptor.getValue().size());
	}

}
