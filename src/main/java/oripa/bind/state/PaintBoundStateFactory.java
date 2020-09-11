package oripa.bind.state;

import java.awt.Component;
import java.awt.event.ActionListener;

import oripa.appstate.ApplicationState;
import oripa.appstate.StateManager;
import oripa.bind.state.action.PaintActionSetterFactory;
import oripa.domain.paint.EditMode;
import oripa.domain.paint.GraphicMouseActionInterface;
import oripa.domain.paint.MouseActionHolder;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.ScreenUpdaterInterface;
import oripa.domain.paint.addvertex.AddVertexAction;
import oripa.domain.paint.bisector.AngleBisectorAction;
import oripa.domain.paint.byvalue.LineByValueAction;
import oripa.domain.paint.copypaste.SelectionOriginHolder;
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

	private final StateManager stateManager;
	private final MainFrameSetting mainFrameSetting;
	private final UIPanelSetting uiPanelSetting;
	private final SelectionOriginHolder originHolder;

	/**
	 * Constructor
	 */
	public PaintBoundStateFactory(
			final StateManager stateManager,
			final MainFrameSetting mainFrameSetting,
			final UIPanelSetting uiPanelSetting,
			final SelectionOriginHolder originHolder) {
		this.stateManager = stateManager;
		this.mainFrameSetting = mainFrameSetting;
		this.uiPanelSetting = uiPanelSetting;
		this.originHolder = originHolder;
	}

	/**
	 * Create a state specified by ID
	 *
	 * @param parent
	 * @param actionHolder
	 * @param context
	 * @param screenUpdater
	 * @param id
	 *            A member of StringID
	 * @return
	 */
	public ApplicationState<EditMode> create(final Component parent,
			final MouseActionHolder actionHolder,
			final PaintContextInterface context,
			final ScreenUpdaterInterface screenUpdater,
			final String id) {

		LocalPaintBoundStateFactory stateFactory = new LocalPaintBoundStateFactory(
				parent, stateManager, null);

		final PaintActionSetterFactory setterFactory = new PaintActionSetterFactory(
				actionHolder, screenUpdater, context);

		ApplicationState<EditMode> state = null;

		var changeHint = new ChangeHint(mainFrameSetting, id);

		GraphicMouseActionInterface mouseAction;
		switch (id) {
		case StringID.SELECT_ID:
			mouseAction = new SelectLineAction();
			state = stateFactory.create(
					mouseAction.getEditMode(), setterFactory.create(mouseAction), changeHint,
					new ActionListener[] {
							e -> (new ChangeOnSelectButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			break;

		case StringID.DELETE_LINE_ID:
			mouseAction = new DeleteLineAction();
			state = stateFactory.create(
					mouseAction.getEditMode(), setterFactory.create(mouseAction), changeHint,
					new ActionListener[] {
							e -> (new ChangeOnOtherCommandButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			break;

		case StringID.CHANGE_LINE_TYPE_ID:
			mouseAction = new ChangeLineTypeAction(uiPanelSetting);
			state = stateFactory.create(
					mouseAction.getEditMode(), setterFactory.create(mouseAction), changeHint,
					new ActionListener[] {
							(e) -> (new ChangeOnAlterTypeButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			break;

		case StringID.ADD_VERTEX_ID:
			mouseAction = new AddVertexAction();
			state = stateFactory.create(
					mouseAction.getEditMode(), setterFactory.create(mouseAction), changeHint,
					new ActionListener[] {
							e -> (new ChangeOnOtherCommandButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			break;

		case StringID.DELETE_VERTEX_ID:
			mouseAction = new DeleteVertexAction();
			state = stateFactory.create(
					mouseAction.getEditMode(), setterFactory.create(mouseAction), changeHint,
					new ActionListener[] {
							e -> (new ChangeOnOtherCommandButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			break;

		case StringID.EDIT_CONTOUR_ID:
			mouseAction = new EditOutlineActionWrapper(stateManager, actionHolder);
			state = stateFactory.create(
					mouseAction.getEditMode(), setterFactory.create(mouseAction), changeHint,
					new ActionListener[] {
							e -> (new ChangeOnOtherCommandButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			break;

		case StringID.SELECT_ALL_LINE_ID:
			mouseAction = new SelectLineAction();
			// selecting all lines should be done in other listener
			state = stateFactory.create(
					mouseAction.getEditMode(), setterFactory.create(mouseAction), changeHint,
					new ActionListener[] {
							e -> (new ChangeOnSelectButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			break;

		case StringID.COPY_PASTE_ID:
			mouseAction = new CopyAndPasteActionWrapper(stateManager, false, originHolder);
			state = stateFactory.create(
					mouseAction.getEditMode(), setterFactory.create(mouseAction),
					new CopyPasteErrorListener(context),
					changeHint,
					new ActionListener[] {
							e -> (new ChangeOnSelectButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			break;

		case StringID.CUT_PASTE_ID:
			mouseAction = new CopyAndPasteActionWrapper(stateManager, true, originHolder);
			state = stateFactory.create(
					mouseAction.getEditMode(), setterFactory.create(mouseAction),
					new CopyPasteErrorListener(context),
					changeHint,
					new ActionListener[] {
							e -> (new ChangeOnSelectButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			break;

		default:
			state = createLineInputState(parent, actionHolder, setterFactory, id);
		}

		if (state == null) {
			throw new NullPointerException("Wrong ID for creating state");
		}

		return state;
	}

	private ApplicationState<EditMode> createLineInputState(
			final Component parent, final MouseActionHolder actionHolder,
			final PaintActionSetterFactory setterFactory,
			final String id) {

		var changeHint = new ChangeHint(mainFrameSetting, id);

		LocalPaintBoundStateFactory stateFactory = new LocalPaintBoundStateFactory(parent,
				stateManager,
				new ActionListener[] {
						e -> (new ChangeOnPaintInputButtonSelected(uiPanelSetting))
								.changeViewSetting() });

		ApplicationState<EditMode> state = null;
		GraphicMouseActionInterface mouseAction;
		switch (id) {
		case StringID.DIRECT_V_ID:
			mouseAction = new TwoPointSegmentAction();
			state = stateFactory.create(
					mouseAction.getEditMode(), setterFactory.create(mouseAction), changeHint, null);
			break;

		case StringID.ON_V_ID:
			mouseAction = new TwoPointLineAction();
			state = stateFactory.create(
					mouseAction.getEditMode(), setterFactory.create(mouseAction), changeHint, null);
			break;
		case StringID.VERTICAL_ID:
			mouseAction = new VerticalLineAction();
			state = stateFactory.create(
					mouseAction.getEditMode(), setterFactory.create(mouseAction), changeHint, null);
			break;

		case StringID.BISECTOR_ID:
			mouseAction = new AngleBisectorAction();
			state = stateFactory.create(
					mouseAction.getEditMode(), setterFactory.create(mouseAction), changeHint, null);
			break;

		case StringID.TRIANGLE_ID:
			mouseAction = new TriangleSplitAction();
			state = stateFactory.create(
					mouseAction.getEditMode(), setterFactory.create(mouseAction), changeHint, null);
			break;

		case StringID.SYMMETRIC_ID:
			mouseAction = new SymmetricalLineAction();
			state = stateFactory.create(
					mouseAction.getEditMode(), setterFactory.create(mouseAction), changeHint, null);

			break;
		case StringID.MIRROR_ID:
			mouseAction = new MirrorCopyAction();
			state = stateFactory.create(
					mouseAction.getEditMode(), setterFactory.create(mouseAction), changeHint, null);

			break;

		case StringID.BY_VALUE_ID:
			LocalPaintBoundStateFactory byValueFactory = new LocalPaintBoundStateFactory(
					parent, stateManager, new ActionListener[] {
							e -> (new ChangeOnByValueButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			mouseAction = new LineByValueAction(uiPanelSetting.getValueSetting());
			state = byValueFactory.create(
					mouseAction.getEditMode(), setterFactory.create(mouseAction), changeHint, null);
			break;

		case StringID.PERPENDICULAR_BISECTOR_ID:
			mouseAction = new TwoPointBisectorAction();
			state = stateFactory.create(
					mouseAction.getEditMode(), setterFactory.create(mouseAction), changeHint, null);

		}

		return state;
	}
}
