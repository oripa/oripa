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
package oripa.domain.paint.suggestion;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import oripa.domain.paint.test.InputStatesTestBase;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
class SuggestionInputStatesTest extends InputStatesTestBase {

	@BeforeEach
	void setUp() {
		setUp(SelectingStartPoint.class);
		var lines = List.of(new OriLine(0, 0, 1, 0, OriLine.Type.MOUNTAIN),
				new OriLine(0, 0, 0, 1, OriLine.Type.MOUNTAIN),
				new OriLine(0, 0, -1, 0, OriLine.Type.MOUNTAIN));

		context.getPainter().addLines(lines);
	}

	private <T> void assertCurrentState(final int expectedVertexCount, final Class<T> expectedClass) {
		assertEquals(expectedVertexCount, context.getVertexCount());
		assertInstanceOf(expectedClass, state);
	}

	@Test
	void testUndo_firstState() {
		state = state.undo(context);
		assertCurrentState(0, SelectingStartPoint.class);
	}

	@Nested
	class FirstEndPointIsSelected {
		Vector2d candidate1 = new Vector2d(0, 0);

		@BeforeEach
		void doAction() {
			SuggestionInputStatesTest.this.doAction(candidate1);
		}

		@Test
		void testAfterDoAction() {
			assertCurrentState(1, SelectingEndPoint.class);
		}

		@Test
		void testUndo_secondState() {
			state = state.undo(context);
			assertCurrentState(0, SelectingStartPoint.class);
		}

		@Nested
		class SecondEndPointIsSelected {
			Vector2d candidate2 = new Vector2d(0, -1);

			@BeforeEach
			void doAction() {
				SuggestionInputStatesTest.this.doAction(candidate2);
			}

			@Test
			void testAfterDoAction_NewLineShouldBePut() {
				assertCurrentState(0, SelectingStartPoint.class);
				assertNewLineInputted();
			}
		}

		@Nested
		class SecondEndPointIsNotSelected {
			Vector2d candidate2 = null;

			@BeforeEach
			void doAction() {
				SuggestionInputStatesTest.this.doAction(candidate2);
			}

			@Test
			void testAfterDoAction_NoChanges() {
				assertCurrentState(1, SelectingEndPoint.class);
			}
		}

	}

}
