package oripa.bind.state;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import oripa.appstate.ApplicationState;
import oripa.domain.cptool.Painter;
import oripa.domain.paint.EditMode;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.GraphicMouseAction;
import oripa.resource.StringID;

public class LocalPaintBoundStateFactoryTest {

	@Nested
	public class CreateWithErrorListenerTests {
		@Test
		void errorOccurs() {
			var parent = mock(JFrame.class);
			var basicAction1 = mock(ActionListener.class);
			var basicAction2 = mock(ActionListener.class);

			var factory = new LocalPaintBoundStateFactory(parent,
					new ActionListener[] { basicAction1, basicAction2 });

			var mouseAction = mock(GraphicMouseAction.class);
			var context = mock(PaintContextInterface.class);
			var textID = StringID.DIRECT_V_ID;
			var action1 = mock(ActionListener.class);
			var action2 = mock(ActionListener.class);

			// assume that some error occurs.
			var errorListener = mock(ErrorListener.class);
			var event = mock(ActionEvent.class);
			when(errorListener.isError(event)).thenReturn(true);

			ApplicationState<EditMode> state = factory.create(
					mouseAction, errorListener, context, textID,
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
		}

		@Test
		void noErrors() {
			var parent = mock(JFrame.class);
			var basicAction1 = mock(ActionListener.class);
			var basicAction2 = mock(ActionListener.class);

			var factory = new LocalPaintBoundStateFactory(parent,
					new ActionListener[] { basicAction1, basicAction2 });

			var mouseAction = mock(GraphicMouseAction.class);
			var context = mock(PaintContextInterface.class);
			var painter = mock(Painter.class);
			when(context.getPainter()).thenReturn(painter);

			var textID = StringID.DIRECT_V_ID;
			var action1 = mock(ActionListener.class);
			var action2 = mock(ActionListener.class);

			// assume that no error occurs.
			var errorListener = mock(ErrorListener.class);
			var event = mock(ActionEvent.class);
			when(errorListener.isError(event)).thenReturn(false);

			ApplicationState<EditMode> state = factory.create(
					mouseAction, errorListener, context, textID,
					new ActionListener[] { action1, action2 });

			// run the target method
			state.performActions(event);

			// error handling should never happen.
			verify(errorListener).isError(event);
			verify(errorListener, never()).onError(parent, event);

			verify(mouseAction).recover(context);

			// every action listener should be called.
			verify(basicAction1).actionPerformed(event);
			verify(basicAction2).actionPerformed(event);
			verify(action1).actionPerformed(event);
			verify(action2).actionPerformed(event);

		}
	}

}
