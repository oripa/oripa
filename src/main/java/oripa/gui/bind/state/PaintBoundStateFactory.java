package oripa.gui.bind.state;

import java.util.function.Supplier;

import oripa.appstate.ApplicationState;
import oripa.appstate.StateManager;
import oripa.domain.paint.PaintDomainContext;
import oripa.domain.paint.byvalue.ByValueContext;
import oripa.domain.paint.copypaste.SelectionOriginHolder;
import oripa.gui.bind.state.action.PaintActionSetterFactory;
import oripa.gui.presenter.creasepattern.*;
import oripa.gui.presenter.creasepattern.byvalue.LineByValueAction;
import oripa.gui.presenter.creasepattern.enlarge.EnlargeLineAction;
import oripa.gui.view.main.MainFrameSetting;
import oripa.gui.view.main.MainViewSetting;
import oripa.gui.view.main.UIPanelSetting;
import oripa.gui.viewchange.main.ChangeHint;
import oripa.gui.viewchange.main.uipanel.ChangeOnAlterTypeButtonSelected;
import oripa.gui.viewchange.main.uipanel.ChangeOnAngleSnapButtonSelected;
import oripa.gui.viewchange.main.uipanel.ChangeOnByValueButtonSelected;
import oripa.gui.viewchange.main.uipanel.ChangeOnOtherCommandButtonSelected;
import oripa.gui.viewchange.main.uipanel.ChangeOnPaintInputButtonSelected;
import oripa.gui.viewchange.main.uipanel.ChangeOnSelectButtonSelected;
import oripa.resource.StringID;

public class PaintBoundStateFactory {

	private final StateManager<EditMode> stateManager;
	private final PaintActionSetterFactory setterFactory;
	private final MainFrameSetting mainFrameSetting;
	private final UIPanelSetting uiPanelSetting;
	private final ByValueContext byValueContext;
	private final SelectionOriginHolder originHolder;
	private final MouseActionHolder actionHolder;
	private final TypeForChangeContext typeForChangeContext;

	/**
	 * Constructor
	 */
	public PaintBoundStateFactory(
			final StateManager<EditMode> stateManager,
			final PaintActionSetterFactory setterFactory,
			final MainViewSetting viewSetting,
			final CreasePatternPresentationContext presentationContext,
			final PaintDomainContext domainContext) {

		this.stateManager = stateManager;
		this.setterFactory = setterFactory;
		this.mainFrameSetting = viewSetting.getMainFrameSetting();
		this.uiPanelSetting = viewSetting.getUiPanelSetting();
		this.byValueContext = domainContext.getByValueContext();
		this.actionHolder = presentationContext.getActionHolder();
		this.originHolder = domainContext.getSelectionOriginHolder();
		this.typeForChangeContext = presentationContext.getTypeForChangeContext();
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
				stateManager, setterFactory, null);

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
			state = stateFactory.create(
					new DeleteLineAction(), changeHint, new Runnable[] {
							() -> (new ChangeOnOtherCommandButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			break;

		case StringID.CHANGE_LINE_TYPE_ID:
			state = stateFactory.create(
					new ChangeLineTypeAction(typeForChangeContext),
					changeHint, new Runnable[] {
							() -> (new ChangeOnAlterTypeButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			break;

		case StringID.ADD_VERTEX_ID:
			state = stateFactory.create(
					new AddVertexAction(), changeHint, new Runnable[] {
							() -> (new ChangeOnOtherCommandButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			break;

		case StringID.DELETE_VERTEX_ID:
			state = stateFactory.create(
					new DeleteVertexAction(), changeHint, new Runnable[] {
							() -> (new ChangeOnOtherCommandButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			break;

		case StringID.EDIT_CONTOUR_ID:
			// TODO make the command as usual one and get rid of the wrapper.
			state = stateFactory.create(
					new EditOutlineActionWrapper(stateManager, actionHolder),
					changeHint, new Runnable[] {
							() -> (new ChangeOnOtherCommandButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			break;

		case StringID.COPY_PASTE_ID:
		case StringID.CUT_PASTE_ID:
		case StringID.IMPORT_CP_ID:
		case StringID.SELECT_ALL_LINE_ID:
		case StringID.SELECT_LINE_ID:
		case StringID.ENLARGE_ID:
			state = createLineSelectionState(id, errorDetecter, errorHandler);
			break;

		default:
			state = createLineInputState(id);
		}

		if (state == null) {
			throw new NullPointerException("Wrong ID for creating state");
		}

		return state;
	}

	private ApplicationState<EditMode> createLineSelectionState(
			final String id,
			final Supplier<Boolean> errorDetecter,
			final Runnable errorHandler) {

		var changeHint = new ChangeHint(mainFrameSetting, id);

		LocalPaintBoundStateFactory stateFactory = new LocalPaintBoundStateFactory(
				stateManager, setterFactory,
				new Runnable[] {
						() -> (new ChangeOnSelectButtonSelected(uiPanelSetting))
								.changeViewSetting() });

		switch (id) {
		case StringID.SELECT_LINE_ID:
			return stateFactory.create(
					new SelectLineAction(), changeHint, null);

		case StringID.ENLARGE_ID:
			return stateFactory.create(
					new EnlargeLineAction(), changeHint, null);

		case StringID.SELECT_ALL_LINE_ID:
			// selecting all lines should be done in other listener
			return stateFactory.create(
					new SelectLineAction(), changeHint, null);

		case StringID.COPY_PASTE_ID:
			return stateFactory.create(
					new CopyAndPasteActionWrapper(stateManager, false, originHolder),
					errorDetecter, errorHandler, changeHint, null);

		case StringID.CUT_PASTE_ID:
			return stateFactory.create(
					new CopyAndPasteActionWrapper(stateManager, true, originHolder),
					errorDetecter, errorHandler, changeHint, null);

		case StringID.IMPORT_CP_ID:
			return stateFactory.create(
					new CopyAndPasteActionWrapper(stateManager, true, originHolder),
					changeHint, null);

		}

		return null;
	}

	private ApplicationState<EditMode> createLineInputState(
			final String id) {

		var changeHint = new ChangeHint(mainFrameSetting, id);

		LocalPaintBoundStateFactory stateFactory = new LocalPaintBoundStateFactory(
				stateManager, setterFactory,
				new Runnable[] {
						() -> (new ChangeOnPaintInputButtonSelected(uiPanelSetting))
								.changeViewSetting() });

		switch (id) {
		case StringID.DIRECT_V_ID:
			return stateFactory.create(
					new TwoPointSegmentAction(), changeHint, null);

		case StringID.ON_V_ID:
			return stateFactory.create(
					new TwoPointLineAction(), changeHint, null);

		case StringID.VERTICAL_ID:
			return stateFactory.create(
					new VerticalLineAction(), changeHint, null);

		case StringID.BISECTOR_ID:
			return stateFactory.create(
					new AngleBisectorAction(), changeHint, null);

		case StringID.TRIANGLE_ID:
			return stateFactory.create(
					new TriangleSplitAction(), changeHint, null);

		case StringID.SYMMETRIC_ID:
			return stateFactory.create(
					new SymmetricalLineAction(), changeHint, null);

		case StringID.MIRROR_ID:
			return stateFactory.create(
					new MirrorCopyAction(), changeHint, null);

		case StringID.BY_VALUE_ID:
			LocalPaintBoundStateFactory byValueFactory = new LocalPaintBoundStateFactory(
					stateManager, setterFactory, new Runnable[] {
							() -> (new ChangeOnByValueButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			return byValueFactory.create(
					new LineByValueAction(byValueContext),
					changeHint, null);

		case StringID.PERPENDICULAR_BISECTOR_ID:
			return stateFactory.create(
					new PerpendicularBisectorAction(), changeHint, null);

		case StringID.ANGLE_SNAP_ID:
			LocalPaintBoundStateFactory angleSnapFactory = new LocalPaintBoundStateFactory(
					stateManager, setterFactory, new Runnable[] {
							() -> (new ChangeOnAngleSnapButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			return angleSnapFactory.create(
					new AngleSnapAction(), changeHint, null);
		}

		return null;
	}
}
