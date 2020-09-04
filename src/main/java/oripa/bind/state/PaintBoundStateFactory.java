package oripa.bind.state;

import java.awt.Component;
import java.awt.event.ActionListener;

import oripa.appstate.ApplicationState;
import oripa.bind.EditOutlineActionWrapper;
import oripa.bind.copypaste.CopyAndPasteActionWrapper;
import oripa.bind.copypaste.CopyPasteErrorListener;
import oripa.domain.paint.EditMode;
import oripa.domain.paint.MouseActionHolder;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.ScreenUpdaterInterface;
import oripa.domain.paint.addvertex.AddVertexAction;
import oripa.domain.paint.bisector.AngleBisectorAction;
import oripa.domain.paint.byvalue.LineByValueAction;
import oripa.domain.paint.copypaste.OriginHolder;
import oripa.domain.paint.deleteline.DeleteLineAction;
import oripa.domain.paint.deletevertex.DeleteVertexAction;
import oripa.domain.paint.line.TwoPointLineAction;
import oripa.domain.paint.linetype.ChangeLineTypeAction;
import oripa.domain.paint.mirror.MirrorCopyAction;
import oripa.domain.paint.pbisec.TwoPointBisectorAction;
import oripa.domain.paint.segment.TwoPointSegmentAction;
import oripa.domain.paint.selectline.SelectLineAction;
import oripa.domain.paint.symmetric.SymmetricalLineAction;
import oripa.domain.paint.triangle.TriangleSplitAction;
import oripa.domain.paint.vertical.VerticalLineAction;
import oripa.resource.StringID;
import oripa.viewsetting.main.ChangeHint;
import oripa.viewsetting.main.MainFrameSetting;
import oripa.viewsetting.main.uipanel.ChangeOnAlterTypeButtonSelected;
import oripa.viewsetting.main.uipanel.ChangeOnByValueButtonSelected;
import oripa.viewsetting.main.uipanel.ChangeOnOtherCommandButtonSelected;
import oripa.viewsetting.main.uipanel.ChangeOnPaintInputButtonSelected;
import oripa.viewsetting.main.uipanel.ChangeOnSelectButtonSelected;
import oripa.viewsetting.main.uipanel.UIPanelSetting;

//FIXME this ID-based approach is not smart.
// We should implement button factories for each command.
public class PaintBoundStateFactory {

	private final MainFrameSetting mainFrameSetting;
	private final UIPanelSetting uiPanelSetting;
	private final OriginHolder originHolder;

	/**
	 * Constructor
	 */
	public PaintBoundStateFactory(final MainFrameSetting mainFrameSetting,
			final UIPanelSetting uiPanelSetting, final OriginHolder originHolder) {
		this.mainFrameSetting = mainFrameSetting;
		this.uiPanelSetting = uiPanelSetting;
		this.originHolder = originHolder;
	}

	/**
	 * Create a state specified by ID
	 *
	 * @param parent
	 * @param id
	 *            A member of StringID
	 * @return
	 */
	public ApplicationState<EditMode> create(final Component parent,
			final MouseActionHolder actionHolder,
			final PaintContextInterface context,
			final ScreenUpdaterInterface screenUpdater,
			final String id) {

		LocalPaintBoundStateFactory stateFactory = new LocalPaintBoundStateFactory(parent, null);

		ApplicationState<EditMode> state = null;

		var changeHint = new ChangeHint(mainFrameSetting, id);

		switch (id) {
		case StringID.SELECT_ID:
			state = stateFactory.create(
					actionHolder, new SelectLineAction(), context, screenUpdater, changeHint,
					new ActionListener[] {
							e -> (new ChangeOnSelectButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			break;

		case StringID.DELETE_LINE_ID:
			state = stateFactory.create(
					actionHolder, new DeleteLineAction(), context, screenUpdater, changeHint,
					new ActionListener[] {
							e -> (new ChangeOnOtherCommandButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			break;

		case StringID.CHANGE_LINE_TYPE_ID:
			state = stateFactory.create(
					actionHolder, new ChangeLineTypeAction(uiPanelSetting), context, screenUpdater,
					changeHint,
					new ActionListener[] {
							(e) -> (new ChangeOnAlterTypeButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			break;

		case StringID.ADD_VERTEX_ID:
			state = stateFactory.create(
					actionHolder, new AddVertexAction(), context, screenUpdater, changeHint,
					new ActionListener[] {
							e -> (new ChangeOnOtherCommandButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			break;

		case StringID.DELETE_VERTEX_ID:
			state = stateFactory.create(
					actionHolder, new DeleteVertexAction(), context, screenUpdater, changeHint,
					new ActionListener[] {
							e -> (new ChangeOnOtherCommandButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			break;

		case StringID.EDIT_CONTOUR_ID:
			state = stateFactory.create(
					actionHolder, new EditOutlineActionWrapper(actionHolder), context,
					screenUpdater, changeHint,
					new ActionListener[] {
							e -> (new ChangeOnOtherCommandButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			break;

		case StringID.SELECT_ALL_LINE_ID:
			// selecting all lines should be done in other listener
			state = stateFactory.create(
					actionHolder, new SelectLineAction(), context, screenUpdater, changeHint,
					new ActionListener[] {
							e -> (new ChangeOnSelectButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			break;

		case StringID.COPY_PASTE_ID:
			state = stateFactory.create(
					actionHolder,
					new CopyAndPasteActionWrapper(false, originHolder),
					new CopyPasteErrorListener(context),
					context, screenUpdater, changeHint,
					new ActionListener[] {
							e -> (new ChangeOnSelectButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			break;

		case StringID.CUT_PASTE_ID:
			state = stateFactory.create(
					actionHolder,
					new CopyAndPasteActionWrapper(true, originHolder),
					new CopyPasteErrorListener(context),
					context, screenUpdater, changeHint,
					new ActionListener[] {
							e -> (new ChangeOnSelectButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			break;

		default:
			state = createLineInputState(parent, actionHolder, context, screenUpdater, id);
		}

		if (state == null) {
			throw new NullPointerException("Wrong ID for creating state");
		}

		return state;
	}

	private ApplicationState<EditMode> createLineInputState(
			final Component parent, final MouseActionHolder actionHolder,
			final PaintContextInterface context,
			final ScreenUpdaterInterface screenUpdater,
			final String id) {

		var changeHint = new ChangeHint(mainFrameSetting, id);

		LocalPaintBoundStateFactory stateFactory = new LocalPaintBoundStateFactory(parent,
				new ActionListener[] {
						e -> (new ChangeOnPaintInputButtonSelected(uiPanelSetting))
								.changeViewSetting() });

		ApplicationState<EditMode> state = null;
		switch (id) {
		case StringID.DIRECT_V_ID:

			state = stateFactory.create(
					actionHolder, new TwoPointSegmentAction(),
					context, screenUpdater, changeHint, null);
			break;

		case StringID.ON_V_ID:
			state = stateFactory.create(
					actionHolder, new TwoPointLineAction(),
					context, screenUpdater, changeHint, null);
			break;
		case StringID.VERTICAL_ID:
			state = stateFactory.create(
					actionHolder, new VerticalLineAction(),
					context, screenUpdater, changeHint, null);
			break;

		case StringID.BISECTOR_ID:
			state = stateFactory.create(
					actionHolder, new AngleBisectorAction(),
					context, screenUpdater, changeHint, null);
			break;

		case StringID.TRIANGLE_ID:
			state = stateFactory.create(
					actionHolder, new TriangleSplitAction(),
					context, screenUpdater, changeHint, null);

			break;

		case StringID.SYMMETRIC_ID:
			state = stateFactory.create(
					actionHolder, new SymmetricalLineAction(),
					context, screenUpdater, changeHint, null);

			break;
		case StringID.MIRROR_ID:
			state = stateFactory.create(
					actionHolder, new MirrorCopyAction(),
					context, screenUpdater, changeHint, null);

			break;

		case StringID.BY_VALUE_ID:
			LocalPaintBoundStateFactory byValueFactory = new LocalPaintBoundStateFactory(
					parent, new ActionListener[] {
							e -> (new ChangeOnByValueButtonSelected(uiPanelSetting))
									.changeViewSetting() });

			state = byValueFactory.create(
					actionHolder, new LineByValueAction(uiPanelSetting.getValueSetting()),
					context, screenUpdater, changeHint, null);

			break;

		case StringID.PERPENDICULAR_BISECTOR_ID:
			state = stateFactory.create(
					actionHolder, new TwoPointBisectorAction(),
					context, screenUpdater, changeHint, null);

		}

		return state;
	}
}
