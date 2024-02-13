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
package oripa.domain.paint.line;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import oripa.domain.paint.test.InputStatesTestBase;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
class LineInputStatesTest extends InputStatesTestBase {

	@BeforeEach
	void setUp() {
		setUp(SelectingFirstVertexForLine.class);
	}

	private <T> void assertCurrentState(final int expectedVertexCount, final Class<T> expectedClass) {
		assertEquals(expectedVertexCount, context.getVertexCount());
		assertInstanceOf(expectedClass, state);
	}

	@Test
	void testUndo_firstPointState() {
		state = state.undo(context);
		assertCurrentState(0, SelectingFirstVertexForLine.class);
	}

	@Nested
	class FirstPointIsSelected {
		Vector2d candidate1 = new Vector2d(1, 1);

		@BeforeEach
		void doAction() {
			context.setCandidateVertexToPick(candidate1);
			state = state.doAction(context, null, false);
		}

		@Test
		void testAfterDoAction() {
			assertCurrentState(1, SelectingSecondVertexForLine.class);
		}

		@Test
		void testUndo_secondState() {
			state = state.undo(context);
			assertCurrentState(0, SelectingFirstVertexForLine.class);
		}

		@Nested
		class SecondPointIsSelected {
			Vector2d candidate2 = new Vector2d(2, 2);

			@BeforeEach
			void doAction() {
				context.setCandidateVertexToPick(candidate2);
				state = state.doAction(context, null, false);
			}

			@Test
			void testAfterDoAction() {
				assertCurrentState(2, SelectingFirstEndPoint.class);
				assertTrue(context.getSnapPoints().size() > 0);
			}

			@Test
			void testUndo_firstSnapState() {
				state = state.undo(context);
				assertCurrentState(1, SelectingSecondVertexForLine.class);
			}

			@Nested
			class FirstSnapIsSelected {
				Vector2d candidate3 = new Vector2d(3, 3);

				@BeforeEach
				void doAction() {
					context.setCandidateVertexToPick(candidate3);
					state = state.doAction(context, null, false);
				}

				@Test
				void testAfterDoAction() {
					assertCurrentState(3, SelectingSecondEndPoint.class);
				}

				@Test
				void testUndo_firstSnapState() {
					state = state.undo(context);
					assertCurrentState(2, SelectingFirstEndPoint.class);
				}

				@Nested
				class SecondSnapIsSelected {
					Vector2d candidate4 = new Vector2d(4, 4);

					@BeforeEach
					void doAction() {
						context.setCandidateVertexToPick(candidate4);
						state = state.doAction(context, null, false);
					}

					@Test
					void testAfterDoAction() {
						assertCurrentState(0, SelectingFirstVertexForLine.class);
					}
				}

			}

		}
	}
}
