package oripa.bind.state;

import static org.mockito.Mockito.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import oripa.appstate.ApplicationState;
import oripa.appstate.StateManager;
import oripa.bind.state.action.PaintActionSetter;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.creasepattern.GraphicMouseAction;
import oripa.gui.presenter.creasepattern.MouseActionHolder;
import oripa.gui.viewsetting.ChangeViewSetting;

public class LocalPaintBoundStateFactoryTest {

	@Nested
	public class CreateWithErrorListenerTests {
		@Test
		void errorOccurs() {
			var parent = mock(JFrame.class);
			var basicAction1 = mock(ActionListener.class);
			var basicAction2 = mock(ActionListener.class);
			var stateManager = mock(StateManager.class);

			var factory = new LocalPaintBoundStateFactory(parent,
					stateManager,
					new ActionListener[] { basicAction1, basicAction2 });

			var actionHolder = mock(MouseActionHolder.class);
			var mouseAction = mock(GraphicMouseAction.class);
			var action1 = mock(ActionListener.class);
			var action2 = mock(ActionListener.class);

			// assume that some error occurs.
			var errorListener = mock(ErrorListener.class);
			var event = mock(ActionEvent.class);
			when(errorListener.isError(event)).thenReturn(true);

			var changeHint = mock(ChangeViewSetting.class);

			var actionSetter = mock(PaintActionSetter.class);
			ApplicationState<EditMode> state = factory.create(
					EditMode.INPUT, actionSetter, errorListener, changeHint,
					new ActionListener[] { action1, action2 });

			// run the target method
			state.performActions(event);

			// error handling should happen.
			verify(errorListener).isError(event);
			verify(errorListener).onError(parent, event);

			// no action listener should be called.
			verify(basicAction1, never()).actionPerformed(event);
			verify(basicAction2, never()).actionPerformed(event);
			verify(action1, never()).actionPerformed(event);
			verify(action2, never()).actionPerformed(event);
			verify(actionHolder, never()).setMouseAction(mouseAction);
			verify(changeHint, never()).changeViewSetting();
		}

		@Test
		void noErrors() {
			var parent = mock(JFrame.class);
			var stateManager = mock(StateManager.class);
			var basicAction1 = mock(ActionListener.class);
			var basicAction2 = mock(ActionListener.class);

			var factory = new LocalPaintBoundStateFactory(parent,
					stateManager,
					new ActionListener[] { basicAction1, basicAction2 });

			var action1 = mock(ActionListener.class);
			var action2 = mock(ActionListener.class);

			// assume that no error occurs.
			var errorListener = mock(ErrorListener.class);
			var event = mock(ActionEvent.class);
			when(errorListener.isError(event)).thenReturn(false);

			var changeHint = mock(ChangeViewSetting.class);

			var paintActionSetter = mock(PaintActionSetter.class);

			ApplicationState<EditMode> state = factory.create(
					EditMode.INPUT, paintActionSetter, errorListener, changeHint,
					new ActionListener[] { action1, action2 });

			// run the target method
			state.performActions(event);

			// error handling should never happen.
			verify(errorListener).isError(event);
			verify(errorListener, never()).onError(parent, event);

			verify(paintActionSetter).actionPerformed(event);

			verify(action1).actionPerformed(event);
			verify(action2).actionPerformed(event);

			verify(changeHint).changeViewSetting();
		}
	}

}
