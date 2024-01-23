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

import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import oripa.domain.cptool.Painter;
import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
@ExtendWith(MockitoExtension.class)
class CloseTempOutlineTest {
	@InjectMocks
	private CloseTempOutline closer;
	@Mock
	private OutlineAdder adder;
	@Mock
	private OutsideLineRemover remover;
	@Mock
	private Painter painter;

	/**
	 * Test method for
	 * {@link oripa.domain.paint.outline.CloseTempOutline#execute(java.util.Collection)}.
	 */
	@Test
	void testExecute() {
//		var creasePattern = mock(CreasePatternInterface.class);
		var l0 = new OriLine(0, 0, 1, 0, OriLine.Type.MOUNTAIN);
		var l1 = new OriLine(1, 0, 1, 1, OriLine.Type.MOUNTAIN);
		var l2 = new OriLine(1, 1, 0, 1, OriLine.Type.MOUNTAIN);
		var l3 = new OriLine(0, 1, 0, 0, OriLine.Type.MOUNTAIN);
		var l4 = new OriLine(0.5, 0.5, 0.9, 0.5, OriLine.Type.MOUNTAIN);

		var creasePattern = (new CreasePatternFactory()).createCreasePattern(List.of(
				l0, l1, l2, l3, l4));

		when(painter.getCreasePattern()).thenReturn(creasePattern);

		var outlineVertices = List.of(new Vector2d(0, 0), new Vector2d(1, 0), new Vector2d(1, 1));

		closer.execute(outlineVertices, painter);

		verify(adder).addOutlines(painter, outlineVertices);
		verify(remover).removeLinesOutsideOfOutlines(painter, outlineVertices);
	}

}
