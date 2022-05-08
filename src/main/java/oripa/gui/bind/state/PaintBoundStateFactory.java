package oripa.gui.bind.state;

import java.awt.event.ActionListener;
import java.util.function.Supplier;

import oripa.appstate.ApplicationState;
import oripa.appstate.StateManager;
import oripa.domain.paint.copypaste.SelectionOriginHolder;
import oripa.gui.bind.state.action.PaintActionSetterFactory;
import oripa.gui.presenter.creasepattern.*;
import oripa.gui.presenter.creasepattern.byvalue.LineByValueAction;
import oripa.gui.presenter.creasepattern.enlarge.EnlargeLineAction;
import oripa.gui.viewsetting.main.ChangeHint;
import oripa.gui.viewsetting.main.MainFrameSetting;
import oripa.gui.viewsetting.main.uipanel.ChangeOnAlterTypeButtonSelected;
import oripa.gui.viewsetting.main.uipanel.ChangeOnAngleSnapButtonSelected;
import oripa.gui.viewsetting.main.uipanel.ChangeOnByValueButtonSelected;
import oripa.gui.viewsetting.main.uipanel.ChangeOnOtherCommandButtonSelected;
import oripa.gui.viewsetting.main.uipanel.ChangeOnPaintInputButtonSelected;
import oripa.gui.viewsetting.main.uipanel.ChangeOnSelectButtonSelected;
import oripa.gui.viewsetting.main.uipanel.UIPanelSetting;
import oripa.resource.StringID;

public class PaintBoundStateFactory {

	private final StateManager<EditMode> stateManager;
	private final PaintActionSetterFactory setterFactory;
	private final MainFrameSetting mainFrameSetting;
	private final UIPanelSetting uiPanelSetting;
	private final SelectionOriginHolder originHolder;
	private final MouseActionHolder actionHolder;

	/**
	 * Constructor
	 */
	public PaintBoundStateFactory(
			final StateManager<EditMode> stateManager,
			final PaintActionSetterFactory setterFactory,
			final MainFrameSetting mainFrameSetting,
			final UIPanelSetting uiPanelSetting,
			final MouseActionHolder actionHolder,
			final SelectionOriginHolder originHolder) {
		this.stateManager = stateManager;
		this.setterFactory = setterFactory;
		this.mainFrameSetting = mainFrameSetting;
		this.uiPanelSetting = uiPanelSetting;
		this.actionHolder = actionHolder;
		this.originHolder = originHolder;
	}

	/**
	 * Create a state specified by ID
	 *
	 * @param id
	 *            A member of StringID
	 * @param errorDetecter
	 *            should detect whether the application is ready to perform the
	 *            action of the new state or not. This should return true if an
	 *            error occurs. This can be null if no error check is needed.
	 * @param errorHandler
	 *            should handle error the {@code errorDetecter} detected. This
	 *            can be null if no error check is needed.
	 *
	 *
	 * @return
	 */
	public ApplicationState<EditMode> create(
			final String id,
			final Supplier<Boolean> errorDetecter,
			final Runnable errorHandler) {

		LocalPaintBoundStateFactory stateFactory = new LocalPaintBoundStateFactory(
				stateManager, null);

		ApplicationState<EditMode> state = null;

		var changeHint = new ChangeHint(mainFrameSetting, id);

		switch (id) {
//		case StringID.SELECT_ID:
//			state = createState(
//					stateFactory, setterFactory, new SelectLineAction(), changeHint,
//					new ActionListener[] {
//							e -> (new ChangeOnSelectButtonSelected(uiPanelSetting))
//									.changeViewSetting() });
//			break;

		case StringID.DELETE_LINE_ID:
			state = createState(
					stateFactory, setterFactory, new DeleteLineAction(), changeHint,
					new ActionListener[] {
							e -> (new ChangeOnOtherCommandButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			break;

		case StringID.CHANGE_LINE_TYPE_ID:
			state = createState(
					stateFactory, setterFactory,
					new ChangeLineTypeAction(uiPanelSetting), changeHint,
					new ActionListener[] {
							(e) -> (new ChangeOnAlterTypeButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			break;

		case StringID.ADD_VERTEX_ID:
			state = createState(
					stateFactory, setterFactory, new AddVertexAction(), changeHint,
					new ActionListener[] {
							e -> (new ChangeOnOtherCommandButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			break;

		case StringID.DELETE_VERTEX_ID:
			state = createState(
					stateFactory, setterFactory, new DeleteVertexAction(), changeHint,
					new ActionListener[] {
							e -> (new ChangeOnOtherCommandButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			break;

		case StringID.EDIT_CONTOUR_ID:
			state = createState(
					stateFactory, setterFactory,
					new EditOutlineActionWrapper(stateManager, actionHolder), changeHint,
					new ActionListener[] {
							e -> (new ChangeOnOtherCommandButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			break;

		case StringID.SELECT_ALL_LINE_ID:
			// selecting all lines should be done in other listener
			state = createState(
					stateFactory, setterFactory, new SelectLineAction(), changeHint,
					new ActionListener[] {
							e -> (new ChangeOnSelectButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			break;

		case StringID.COPY_PASTE_ID:
			state = createState(
					stateFactory, setterFactory,
					new CopyAndPasteActionWrapper(stateManager, false, originHolder),
					errorDetecter, errorHandler, changeHint,
					new ActionListener[] {
							e -> (new ChangeOnSelectButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			break;

		case StringID.CUT_PASTE_ID:
			state = createState(
					stateFactory, setterFactory,
					new CopyAndPasteActionWrapper(stateManager, true, originHolder),
					errorDetecter, errorHandler, changeHint,
					new ActionListener[] {
							e -> (new ChangeOnSelectButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			break;

		case StringID.IMPORT_CP_ID:
			state = createState(
					stateFactory, setterFactory,
					new CopyAndPasteActionWrapper(stateManager, true, originHolder),
					changeHint,
					new ActionListener[] {
							e -> (new ChangeOnSelectButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			break;

		case StringID.SELECT_LINE_ID:
		case StringID.ENLARGE_ID:
			state = createLineSelectionState(setterFactory, id);
			break;

		default:
			state = createLineInputState(setterFactory, id);
		}

		if (state == null) {
			throw new NullPointerException("Wrong ID for creating state");
		}

		return state;
	}

	private ApplicationState<EditMode> createLineSelectionState(
			final PaintActionSetterFactory setterFactory,
			final String id) {

		var changeHint = new ChangeHint(mainFrameSetting, id);

		LocalPaintBoundStateFactory stateFactory = new LocalPaintBoundStateFactory(
				stateManager,
				new ActionListener[] {
						e -> (new ChangeOnSelectButtonSelected(uiPanelSetting))
								.changeViewSetting() });

		switch (id) {
		case StringID.SELECT_LINE_ID:
			return createState(
					stateFactory, setterFactory, new SelectLineAction(), changeHint,
					null);
		case StringID.ENLARGE_ID:
			return createState(
					stateFactory, setterFactory, new EnlargeLineAction(), changeHint,
					null);

		}

		return null;
	}

	private ApplicationState<EditMode> createLineInputState(
			final PaintActionSetterFactory setterFactory,
			final String id) {

		var changeHint = new ChangeHint(mainFrameSetting, id);

		LocalPaintBoundStateFactory stateFactory = new LocalPaintBoundStateFactory(
				stateManager,
				new ActionListener[] {
						e -> (new ChangeOnPaintInputButtonSelected(uiPanelSetting))
								.changeViewSetting() });

		switch (id) {
		case StringID.DIRECT_V_ID:
			return createState(
					stateFactory, setterFactory, new TwoPointSegmentAction(), changeHint, null);

		case StringID.ON_V_ID:
			return createState(
					stateFactory, setterFactory, new TwoPointLineAction(), changeHint, null);

		case StringID.VERTICAL_ID:
			return createState(
					stateFactory, setterFactory, new VerticalLineAction(), changeHint, null);

		case StringID.BISECTOR_ID:
			return createState(
					stateFactory, setterFactory, new AngleBisectorAction(), changeHint, null);

		case StringID.TRIANGLE_ID:
			return createState(
					stateFactory, setterFactory, new TriangleSplitAction(), changeHint, null);

		case StringID.SYMMETRIC_ID:
			return createState(
					stateFactory, setterFactory, new SymmetricalLineAction(), changeHint, null);

		case StringID.MIRROR_ID:
			return createState(
					stateFactory, setterFactory, new MirrorCopyAction(), changeHint, null);

		case StringID.BY_VALUE_ID:
			LocalPaintBoundStateFactory byValueFactory = new LocalPaintBoundStateFactory(
					stateManager, new ActionListener[] {
							e -> (new ChangeOnByValueButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			return createState(
					byValueFactory, setterFactory,
					new LineByValueAction(uiPanelSetting.getValueSetting()), changeHint, null);

		case StringID.PERPENDICULAR_BISECTOR_ID:
			return createState(
					stateFactory, setterFactory, new PerpendicularBisectorAction(), changeHint, null);

		case StringID.ANGLE_SNAP_ID:
			LocalPaintBoundStateFactory angleSnapFactory = new LocalPaintBoundStateFactory(
					stateManager, new ActionListener[] {
							e -> (new ChangeOnAngleSnapButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			return createState(
					angleSnapFactory, setterFactory, new AngleSnapAction(), changeHint, null);
		}

		return null;
	}

	private ApplicationState<EditMode> createState(
			final LocalPaintBoundStateFactory stateFactory,
			final PaintActionSetterFactory setterFactory,
			final GraphicMouseAction mouseAction,
			final ChangeHint changeHint,
			final ActionListener[] actions) {

		return stateFactory.create(
				mouseAction.getEditMode(), setterFactory.create(mouseAction),
				changeHint, actions);
	}

	private ApplicationState<EditMode> createState(
			final LocalPaintBoundStateFactory stateFactory,
			final PaintActionSetterFactory setterFactory,
			final GraphicMouseAction mouseAction,
			final Supplier<Boolean> errorDetecter,
			final Runnable errorHandler,
			final ChangeHint changeHint,
			final ActionListener[] actions) {

		return stateFactory.create(
				mouseAction.getEditMode(), setterFactory.create(mouseAction),
				errorDetecter, errorHandler, changeHint, actions);
	}

}
