package oripa.appstate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import oripa.bind.state.PaintBoundState;
import oripa.domain.paint.EditMode;

public class StateManagerTest {

	@Test
	public void testByScenario() {
		StateManager manager = StateManager.getInstance();

		var inputState = createMockedState(EditMode.INPUT);
		manager.push(inputState);

		var addVertexState = createMockedState(EditMode.ADD_VERTEX);
		manager.push(addVertexState);

		var deleteLineState = createMockedState(EditMode.DELETE_LINE);
		manager.push(deleteLineState);

		assertEquals(deleteLineState, manager.getCurrent());
		assertEquals(addVertexState, manager.pop());

		manager.push(deleteLineState);

		assertEquals(inputState, manager.popLastInputCommand());

		// copy(cut) state won't keep as previous state
		var selectState = createMockedState(EditMode.SELECT);
		manager.push(selectState);

		var copyState = createMockedState(EditMode.COPY);
		manager.push(copyState);
		assertEquals(copyState, manager.getCurrent());

		manager.push(deleteLineState);

		assertEquals(selectState, manager.pop());
	}

	private PaintBoundState createMockedState(final EditMode mode) {
		var state = mock(PaintBoundState.class);
		when(state.getGroup()).thenReturn(mode);
		return state;

	}
}
