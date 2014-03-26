package oripa.appstate;

import static org.junit.Assert.*;

import org.junit.Test;

import oripa.bind.EditOutlineActionWrapper;
import oripa.bind.state.PaintBoundStateFactory;
import oripa.domain.paint.EditMode;
import oripa.domain.paint.MouseActionHolder;
import oripa.domain.paint.PaintContextFactory;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.addvertex.AddVertexAction;
import oripa.domain.paint.bisector.AngleBisectorAction;
import oripa.domain.paint.byvalue.LineByValueAction;
import oripa.domain.paint.deleteline.DeleteLineAction;
import oripa.domain.paint.deletevertex.DeleteVertexAction;
import oripa.domain.paint.line.TwoPointLineAction;
import oripa.domain.paint.linetype.ChangeLineTypeAction;
import oripa.domain.paint.mirror.MirrorCopyAction;
import oripa.domain.paint.pbisec.TwoPointBisectorAction;
import oripa.domain.paint.segment.TwoPointSegmentAction;
import oripa.domain.paint.selectline.SelectLineAction;
import oripa.persistent.doc.Doc;
import oripa.resource.StringID;

public class StateManagerTest {
	private final MouseActionHolder actionHolder = MouseActionHolder
			.getInstance();

	@Test
	public void test() {

		StateManager manager = StateManager.getInstance();
		PaintBoundStateFactory stateFactory = new PaintBoundStateFactory();

		Doc doc = new Doc();
		PaintContextFactory contextFactory = new PaintContextFactory();
		PaintContextInterface context = contextFactory.createContext();

		context.setCreasePattern(doc.getCreasePattern());

		manager.push(stateFactory.create(
				null, context, StringID.DIRECT_V_ID));
		manager.getCurrent().performActions(null);
		assertEquals(actionHolder.getMouseAction().getClass(),
				TwoPointSegmentAction.class);

		manager.push(stateFactory.create(
				null, context, StringID.SELECT_ID));
		manager.getCurrent().performActions(null);
		assertEquals(actionHolder.getMouseAction().getClass(),
				SelectLineAction.class);

		ApplicationState<EditMode> popped = manager.popLastInputCommand();
		popped.performActions(null);

		assertEquals(actionHolder.getMouseAction().getClass(),
				TwoPointSegmentAction.class);

		manager.push(stateFactory.create(
				null, context, StringID.ADD_VERTEX_ID));
		manager.getCurrent().performActions(null);
		assertEquals(actionHolder.getMouseAction().getClass(),
				AddVertexAction.class);

		manager.push(stateFactory.create(
				null, context, StringID.BISECTOR_ID));
		manager.getCurrent().performActions(null);
		assertEquals(actionHolder.getMouseAction().getClass(),
				AngleBisectorAction.class);

		manager.push(stateFactory.create(
				null, context, StringID.BY_VALUE_ID));
		manager.getCurrent().performActions(null);
		assertEquals(actionHolder.getMouseAction().getClass(),
				LineByValueAction.class);

		manager.push(stateFactory.create(
				null, context, StringID.CHANGE_LINE_TYPE_ID));
		manager.getCurrent().performActions(null);
		assertEquals(actionHolder.getMouseAction().getClass(),
				ChangeLineTypeAction.class);

		// PaintContext context = PaintContext.getInstance();
		// OriLine line = new OriLine(0, 0, 10, 10, 1);
		// line.selected = true;
		// context.pushLine(line);
		// manager.push(stateFactory.create(
		// null, StringID.COPY_PASTE_ID));
		// manager.getCurrent().performActions(null);
		// assertEquals(Globals.getMouseAction().getClass(),
		// CopyAndPasteActionWrapper.class);
		//
		// manager.push(stateFactory.create(
		// null, StringID.CUT_PASTE_ID));
		// manager.getCurrent().performActions(null);
		// assertEquals(Globals.getMouseAction().getClass(),
		// CopyAndPasteActionWrapper.class);

		manager.push(stateFactory.create(
				null, context, StringID.DELETE_LINE_ID));
		manager.getCurrent().performActions(null);
		assertEquals(actionHolder.getMouseAction().getClass(),
				DeleteLineAction.class);

		manager.push(stateFactory.create(
				null, context, StringID.DELETE_VERTEX_ID));
		manager.getCurrent().performActions(null);
		assertEquals(actionHolder.getMouseAction().getClass(),
				DeleteVertexAction.class);

		manager.push(stateFactory.create(
				null, context, StringID.EDIT_CONTOUR_ID));
		manager.getCurrent().performActions(null);
		assertEquals(actionHolder.getMouseAction().getClass(),
				EditOutlineActionWrapper.class);

		manager.push(stateFactory.create(
				null, context, StringID.MIRROR_ID));
		manager.getCurrent().performActions(null);
		assertEquals(actionHolder.getMouseAction().getClass(),
				MirrorCopyAction.class);

		manager.push(stateFactory.create(
				null, context, StringID.ON_V_ID));
		manager.getCurrent().performActions(null);
		assertEquals(actionHolder.getMouseAction().getClass(),
				TwoPointLineAction.class);

		manager.push(stateFactory.create(
				null, context, StringID.PERPENDICULAR_BISECTOR_ID));
		manager.getCurrent().performActions(null);
		assertEquals(actionHolder.getMouseAction().getClass(),
				TwoPointBisectorAction.class);

	}

}
