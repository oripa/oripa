package oripa.domain.paint.segment;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import oripa.domain.paint.test.InputStatesTestBase;
import oripa.vecmath.Vector2d;

class SegmentInputStatesTest extends InputStatesTestBase {
	@BeforeEach
	void setUp() {
		setUp(SelectingFirstVertexForSegment.class);
	}

	private <T> void assertCurrentState(final int expectedVertexCount, final Class<T> expectedClass) {
		assertEquals(expectedVertexCount, context.getVertexCount());
		assertInstanceOf(expectedClass, state);
	}

	@Test
	void testUndo_firstState() {
		state = state.undo(context);
		assertCurrentState(0, SelectingFirstVertexForSegment.class);
	}

	@Nested
	class FirstEndPointIsSelected {
		Vector2d candidate1 = new Vector2d(1, 1);

		@BeforeEach
		void doAction() {
			SegmentInputStatesTest.this.doAction(candidate1);
		}

		@Test
		void testAfterDoAction() {
			assertCurrentState(1, SelectingSecondVertexForSegment.class);
		}

		@Test
		void testUndo_secondState() {
			state = state.undo(context);
			assertCurrentState(0, SelectingFirstVertexForSegment.class);
		}

		@Nested
		class SecondEndPointIsSelected {
			Vector2d candidate2 = new Vector2d(2, 2);

			@BeforeEach
			void doAction() {
				SegmentInputStatesTest.this.doAction(candidate2);
			}

			@Test
			void testAfterDoAction_NewLineShouldBePut() {
				assertCurrentState(0, SelectingFirstVertexForSegment.class);
				assertNewLineInputted();
			}
		}

		@Nested
		class SecondEndPointIsNotSelected {
			Vector2d candidate2 = null;

			@BeforeEach
			void doAction() {
				SegmentInputStatesTest.this.doAction(candidate2);
			}

			@Test
			void testAfterDoAction_NoChanges() {
				assertCurrentState(1, SelectingSecondVertexForSegment.class);
			}
		}

	}

	@Nested
	class FirstEndPointIsNotSelected {

		@BeforeEach
		void doAction() {
			SegmentInputStatesTest.this.doAction((Vector2d) null);
		}

		@Test
		void testAfterDoAction_NoChanges() {
			assertCurrentState(0, SelectingFirstVertexForSegment.class);
		}

	}

}
