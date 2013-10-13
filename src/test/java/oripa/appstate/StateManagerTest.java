package oripa.appstate;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import oripa.ORIPA;
import oripa.appstate.ApplicationState;
import oripa.appstate.StateManager;
import oripa.bind.EditOutlineActionWrapper;
import oripa.bind.state.PaintBoundStateFactory;
import oripa.doc.Doc;
import oripa.paint.EditMode;
import oripa.paint.addvertex.AddVertexAction;
import oripa.paint.bisector.AngleBisectorAction;
import oripa.paint.byvalue.LineByValueAction;
import oripa.paint.core.PaintConfig;
import oripa.paint.deleteline.DeleteLineAction;
import oripa.paint.deletevertex.DeleteVertexAction;
import oripa.paint.line.TwoPointLineAction;
import oripa.paint.linetype.ChangeLineTypeAction;
import oripa.paint.mirror.MirrorCopyAction;
import oripa.paint.pbisec.TwoPointBisectorAction;
import oripa.paint.segment.TwoPointSegmentAction;
import oripa.paint.selectline.SelectLineAction;
import oripa.resource.StringID;

public class StateManagerTest {

	@Test
	public void test() {
		
		StateManager manager = StateManager.getInstance();
		PaintBoundStateFactory stateFactory = new PaintBoundStateFactory();
		
		ORIPA.doc = new Doc();
		
		manager.push(stateFactory.create(
				null, StringID.DIRECT_V_ID));
		manager.getCurrent().performActions(null);
		assertEquals(PaintConfig.getMouseAction().getClass(), TwoPointSegmentAction.class);
		
		manager.push(stateFactory.create(
				null, StringID.SELECT_ID));
		manager.getCurrent().performActions(null);
		assertEquals(PaintConfig.getMouseAction().getClass(), SelectLineAction.class);

		ApplicationState<EditMode> popped = manager.popLastInputCommand();
		popped.performActions(null);
		
		assertEquals(PaintConfig.getMouseAction().getClass(), TwoPointSegmentAction.class);

		
		
		manager.push(stateFactory.create(
				null, StringID.ADD_VERTEX_ID));
		manager.getCurrent().performActions(null);
		assertEquals(PaintConfig.getMouseAction().getClass(), AddVertexAction.class);

		manager.push(stateFactory.create(
				null, StringID.BISECTOR_ID));
		manager.getCurrent().performActions(null);
		assertEquals(PaintConfig.getMouseAction().getClass(), AngleBisectorAction.class);

		manager.push(stateFactory.create(
				null, StringID.BY_VALUE_ID));
		manager.getCurrent().performActions(null);
		assertEquals(PaintConfig.getMouseAction().getClass(), LineByValueAction.class);

		manager.push(stateFactory.create(
				null, StringID.CHANGE_LINE_TYPE_ID));
		manager.getCurrent().performActions(null);
		assertEquals(PaintConfig.getMouseAction().getClass(), ChangeLineTypeAction.class);

//		PaintContext context = PaintContext.getInstance();
//		OriLine line = new OriLine(0, 0, 10, 10, 1);
//		line.selected = true;
//		context.pushLine(line);
//		manager.push(stateFactory.create(
//				null, StringID.COPY_PASTE_ID));
//		manager.getCurrent().performActions(null);
//		assertEquals(Globals.getMouseAction().getClass(), CopyAndPasteActionWrapper.class);
//
//		manager.push(stateFactory.create(
//				null, StringID.CUT_PASTE_ID));
//		manager.getCurrent().performActions(null);
//		assertEquals(Globals.getMouseAction().getClass(), CopyAndPasteActionWrapper.class);
		
		manager.push(stateFactory.create(
				null, StringID.DELETE_LINE_ID));
		manager.getCurrent().performActions(null);
		assertEquals(PaintConfig.getMouseAction().getClass(), DeleteLineAction.class);
		
		manager.push(stateFactory.create(
				null, StringID.DELETE_VERTEX_ID));
		manager.getCurrent().performActions(null);
		assertEquals(PaintConfig.getMouseAction().getClass(), DeleteVertexAction.class);
		
		manager.push(stateFactory.create(
				null, StringID.EDIT_CONTOUR_ID));
		manager.getCurrent().performActions(null);
		assertEquals(PaintConfig.getMouseAction().getClass(), EditOutlineActionWrapper.class);
		
		manager.push(stateFactory.create(
				null, StringID.MIRROR_ID));
		manager.getCurrent().performActions(null);
		assertEquals(PaintConfig.getMouseAction().getClass(), MirrorCopyAction.class);
		
		manager.push(stateFactory.create(
				null, StringID.ON_V_ID));
		manager.getCurrent().performActions(null);
		assertEquals(PaintConfig.getMouseAction().getClass(), TwoPointLineAction.class);
		
		manager.push(stateFactory.create(
				null, StringID.PERPENDICULAR_BISECTOR_ID));
		manager.getCurrent().performActions(null);
		assertEquals(PaintConfig.getMouseAction().getClass(), TwoPointBisectorAction.class);
		
		
	}

}
