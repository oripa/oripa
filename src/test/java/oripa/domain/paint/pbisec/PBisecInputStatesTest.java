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
package oripa.domain.paint.pbisec;

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
class PBisecInputStatesTest extends InputStatesTestBase {

	@BeforeEach
	void setUp() {
		setUp(SelectingFirstVertexForBisector.class);
	}

	private <T> void assertCurrentState(final int expectedVertexCount, final int expectedLineCount,
			final Class<T> expectedClass) {
		assertEquals(expectedVertexCount, context.getVertexCount());
		assertEquals(expectedLineCount, context.getLineCount());
		assertInstanceOf(expectedClass, state);
	}

	@Test
	void testUndo_firstPointState() {
		state = state.undo(context);
		assertCurrentState(0, 0, SelectingFirstVertexForBisector.class);
	}

	@Nested
	class FirstPointIsSelected {
		Vector2d candidate1 = new Vector2d(0, 0);

		@BeforeEach
		void doAction() {
			PBisecInputStatesTest.this.doAction(candidate1);
		}

		@Test
		void testAfterDoAction() {
			assertCurrentState(1, 0, SelectingSecondVertexForBisector.class);
		}

		@Test
		void testUndo_secondPointState() {
			state = state.undo(context);
			assertCurrentState(0, 0, SelectingFirstVertexForBisector.class);
		}

		@Nested
		class SecondPointIsSelected {
			Vector2d candidate2 = new Vector2d(2, 0);

			@BeforeEach
			void doAction() {
				PBisecInputStatesTest.this.doAction(candidate2);
			}

			@Test
			void testAfterDoAction() {
				assertCurrentState(2, 0, SelectingFirstEndPoint.class);
				assertSnapPointExists();
			}

			@Test
			void testUndo_firstSnapState() {
				state = state.undo(context);
				assertCurrentState(1, 0, SelectingSecondVertexForBisector.class);
			}

			@Nested
			class FirstSnapIsSelected {
				Vector2d candidate3 = new Vector2d(0, 0);

				@BeforeEach
				void doAction() {
					PBisecInputStatesTest.this.doAction(candidate3);
				}

				@Test
				void testAfterDoAction() {
					assertCurrentState(3, 0, SelectingSecondEndPoint.class);
				}

				@Test
				void testUndo_secondSnapState() {
					state = state.undo(context);
					assertCurrentState(2, 0, SelectingFirstEndPoint.class);
				}

				@Nested
				class SecondSnapIsSelected {
					Vector2d candidate4 = new Vector2d(1, 5);

					@BeforeEach
					void doAction() {
						PBisecInputStatesTest.this.doAction(candidate4);
					}

					@Test
					void testAfterDoAction() {
						assertCurrentState(0, 0, SelectingFirstVertexForBisector.class);
						assertNewLineInputted();
					}
				}
			}

		}
	}
}
