package oripa.gui.bind.state;

import static org.mockito.Mockito.*;

import java.util.function.Supplier;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import oripa.appstate.ApplicationState;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.creasepattern.GraphicMouseAction;
import oripa.gui.presenter.creasepattern.MouseActionHolder;
import oripa.gui.presenter.creasepattern.MouseActionSetter;
import oripa.gui.presenter.creasepattern.MouseActionSetterFactory;
import oripa.gui.viewchange.ChangeViewSetting;

public class LocalPaintBoundStateFactoryTest {

	@Nested
	public class CreateWithErrorListenerTests {
		@Test
		void errorOccurs() {
			var basicAction1 = mock(Runnable.class);
			var basicAction2 = mock(Runnable.class);
			var stateManager = mock(EditModeStateManager.class);
			var setterFactory = mock(MouseActionSetterFactory.class);

			var factory = new LocalPaintBoundStateFactory(
					stateManager,
					setterFactory,
					new Runnable[] { basicAction1, basicAction2 });

			var actionHolder = mock(MouseActionHolder.class);
			var mouseAction = mock(GraphicMouseAction.class);
			when(mouseAction.getEditMode()).thenReturn(EditMode.INPUT);
			var action1 = mock(Runnable.class);
			var action2 = mock(Runnable.class);

			// assume that some error occurs.
			var errorDetecter = mock(Supplier.class);
			var errorHandler = mock(Runnable.class);
			when(errorDetecter.get()).thenReturn(true);

			var changeHint = mock(ChangeViewSetting.class);

			@SuppressWarnings("unchecked")
			ApplicationState<EditMode> state = factory.create(
					mouseAction, errorDetecter, errorHandler, changeHint,
					new Runnable[] { action1, action2 });

			// run the target method
			state.performActions();

			// error handling should happen.
			verify(errorDetecter).get();
			verify(errorHandler).run();

			// no action listener should be called.
			verify(basicAction1, never()).run();
			verify(basicAction2, never()).run();
			verify(action1, never()).run();
			verify(action2, never()).run();
			verify(actionHolder, never()).setMouseAction(mouseAction);
			verify(changeHint, never()).changeViewSetting();
		}

		@Test
		void noErrors() {
			var stateManager = mock(EditModeStateManager.class);

			var setterFactory = mock(MouseActionSetterFactory.class);
			var paintActionSetter = mock(MouseActionSetter.class);
			var mouseAction = mock(GraphicMouseAction.class);
			when(mouseAction.getEditMode()).thenReturn(EditMode.INPUT);

			when(setterFactory.create(mouseAction)).thenReturn(paintActionSetter);

			var basicAction1 = mock(Runnable.class);
			var basicAction2 = mock(Runnable.class);

			var factory = new LocalPaintBoundStateFactory(
					stateManager,
					setterFactory,
					new Runnable[] { basicAction1, basicAction2 });

			var action1 = mock(Runnable.class);
			var action2 = mock(Runnable.class);

			// assume that no error occurs.
			var errorDetecter = mock(Supplier.class);
			var errorHandler = mock(Runnable.class);

			when(errorDetecter.get()).thenReturn(false);

			var changeHint = mock(ChangeViewSetting.class);

			@SuppressWarnings("unchecked")
			ApplicationState<EditMode> state = factory.create(
					mouseAction, errorDetecter, errorHandler, changeHint,
					new Runnable[] { action1, action2 });

			// run the target method
			state.performActions();

			// error handling should never happen.
			verify(errorDetecter).get();
			verify(errorHandler, never()).run();

			verify(paintActionSetter).run();

			verify(action1).run();
			verify(action2).run();

			verify(changeHint).changeViewSetting();
		}
	}

}
