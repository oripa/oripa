package oripa.bind.state;

import java.awt.Component;
import java.awt.event.ActionListener;

import oripa.appstate.ApplicationState;
import oripa.bind.EditOutlineActionWrapper;
import oripa.bind.copypaste.CopyAndPasteActionWrapper;
import oripa.bind.copypaste.CopyPasteErrorListener;
import oripa.domain.paint.EditMode;
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
import oripa.domain.paint.selectline.SelectAllLineAction;
import oripa.domain.paint.selectline.SelectLineAction;
import oripa.domain.paint.symmetric.SymmetricalLineAction;
import oripa.domain.paint.triangle.TriangleSplitAction;
import oripa.domain.paint.vertical.VerticalLineAction;
import oripa.resource.StringID;
import oripa.viewsetting.ViewChangeListener;
import oripa.viewsetting.main.uipanel.ChangeOnAlterTypeButtonSelected;
import oripa.viewsetting.main.uipanel.ChangeOnByValueButtonSelected;
import oripa.viewsetting.main.uipanel.ChangeOnOtherCommandButtonSelected;
import oripa.viewsetting.main.uipanel.ChangeOnPaintInputButtonSelected;
import oripa.viewsetting.main.uipanel.ChangeOnSelectButtonSelected;

//FIXME this ID-based approach is not smart.
// We should implement button factories for each command.
public class PaintBoundStateFactory {

	/**
	 * Create a state specified by ID
	 *
	 * @param parent
	 * @param id
	 *            A member of StringID
	 * @return
	 */
	public ApplicationState<EditMode> create(final Component parent,
			final PaintContextInterface context,
			final String id) {

		LocalPaintBoundStateFactory stateFactory = new LocalPaintBoundStateFactory(parent, null);

		ApplicationState<EditMode> state = null;

		switch (id) {
		case StringID.SELECT_ID:
			state = stateFactory.create(
					new SelectLineAction(), id,
					new ActionListener[] { new ViewChangeListener(
							new ChangeOnSelectButtonSelected()) });
			break;

		case StringID.DELETE_LINE_ID:
			state = stateFactory.create(
					new DeleteLineAction(), id,
					new ActionListener[] { new ViewChangeListener(
							new ChangeOnOtherCommandButtonSelected()) });
			break;

		case StringID.CHANGE_LINE_TYPE_ID:
			state = stateFactory.create(
					new ChangeLineTypeAction(), id,
					new ActionListener[] { new ViewChangeListener(
							new ChangeOnAlterTypeButtonSelected()) });
			break;

		case StringID.ADD_VERTEX_ID:
			state = stateFactory.create(new AddVertexAction(), id,
					new ActionListener[] { new ViewChangeListener(
							new ChangeOnOtherCommandButtonSelected()) });
			break;

		case StringID.DELETE_VERTEX_ID:
			state = stateFactory.create(new DeleteVertexAction(), id,
					new ActionListener[] { new ViewChangeListener(
							new ChangeOnOtherCommandButtonSelected()) });
			break;

		case StringID.EDIT_CONTOUR_ID:
			state = stateFactory.create(
					new EditOutlineActionWrapper(), id,
					new ActionListener[] { new ViewChangeListener(
							new ChangeOnOtherCommandButtonSelected()) });
			break;

		case StringID.SELECT_ALL_LINE_ID:
			state = stateFactory.create(
					new SelectAllLineAction(context), id,
					new ActionListener[] { new ViewChangeListener(
							new ChangeOnSelectButtonSelected()) });
			break;

		case StringID.COPY_PASTE_ID:
			state = stateFactory.create(
					new CopyAndPasteActionWrapper(false),
					new CopyPasteErrorListener(context),
					context, id,
					new ActionListener[] { new ViewChangeListener(
							new ChangeOnSelectButtonSelected()) });
			break;

		case StringID.CUT_PASTE_ID:
			state = stateFactory.create(
					new CopyAndPasteActionWrapper(true),
					new CopyPasteErrorListener(context),
					context, id,
					new ActionListener[] { new ViewChangeListener(
							new ChangeOnSelectButtonSelected()) });
			break;

		default:
			state = createLineInputState(parent, id);
		}

		if (state == null) {
			throw new NullPointerException("Wrong ID for creating state");
		}

		return state;
	}

	private ApplicationState<EditMode> createLineInputState(
			final Component parent, final String id) {

		LocalPaintBoundStateFactory stateFactory = new LocalPaintBoundStateFactory(parent,
				new ActionListener[] { new ViewChangeListener(
						new ChangeOnPaintInputButtonSelected()) });

		ApplicationState<EditMode> state = null;
		switch (id) {
		case StringID.DIRECT_V_ID:

			state = stateFactory.create(new TwoPointSegmentAction(),
					id, null);
			break;

		case StringID.ON_V_ID:
			state = stateFactory.create(new TwoPointLineAction(),
					id, null);
			break;
		case StringID.VERTICAL_ID:
			state = stateFactory.create(new VerticalLineAction(),
					id, null);
			break;

		case StringID.BISECTOR_ID:
			state = stateFactory.create(new AngleBisectorAction(),
					id, null);
			break;

		case StringID.TRIANGLE_ID:
			state = stateFactory.create(new TriangleSplitAction(),
					id, null);

			break;

		case StringID.SYMMETRIC_ID:
			state = stateFactory.create(new SymmetricalLineAction(),
					id, null);

			break;
		case StringID.MIRROR_ID:
			state = stateFactory.create(new MirrorCopyAction(),
					id, null);

			break;

		case StringID.BY_VALUE_ID:
			LocalPaintBoundStateFactory byValueFactory = new LocalPaintBoundStateFactory(
					parent, new ActionListener[] { new ViewChangeListener(
							new ChangeOnByValueButtonSelected()) });

			state = byValueFactory.create(new LineByValueAction(),
					id, null);

			break;

		case StringID.PERPENDICULAR_BISECTOR_ID:
			state = stateFactory.create(new TwoPointBisectorAction(),
					id, null);

		}

		return state;
	}
}
