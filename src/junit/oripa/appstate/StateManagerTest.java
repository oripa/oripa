package junit.oripa.appstate;

import static org.junit.Assert.*;

import org.junit.Test;

import oripa.ORIPA;
import oripa.appstate.ApplicationState;
import oripa.appstate.StateManager;
import oripa.bind.state.PaintBoundStateFactory;
import oripa.doc.Doc;
import oripa.paint.EditMode;
import oripa.paint.Globals;
import oripa.paint.PaintContext;
import oripa.paint.segment.TwoPointSegmentAction;
import oripa.paint.selectline.SelectLineAction;
import oripa.resource.StringID;

public class StateManagerTest {

	@Test
	public void test() {
		
		StateManager manager = StateManager.getInstance();
		PaintBoundStateFactory stateFactory = new PaintBoundStateFactory(null,  null);
		
		PaintContext context = PaintContext.getInstance();
		ORIPA.doc = new Doc();
		
		manager.push(stateFactory.create(
				new TwoPointSegmentAction(), StringID.Command.DIRECT_V_ID, null));
		manager.getCurrent().performActions(null);
		assertEquals(Globals.getMouseAction().getClass(), TwoPointSegmentAction.class);
		
		manager.push(stateFactory.create(
				new SelectLineAction(context), StringID.Command.SELECT_ID, null));
		manager.getCurrent().performActions(null);
		assertEquals(Globals.getMouseAction().getClass(), SelectLineAction.class);

		ApplicationState<EditMode> popped = manager.popLastInputCommand();
		popped.performActions(null);
		
		assertEquals(Globals.getMouseAction().getClass(), TwoPointSegmentAction.class);
		
	}

}
