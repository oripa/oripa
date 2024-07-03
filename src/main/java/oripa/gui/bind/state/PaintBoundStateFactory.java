package oripa.gui.bind.state;

import java.util.Optional;
import java.util.function.Supplier;

import oripa.appstate.ApplicationState;
import oripa.appstate.StateManager;
import oripa.gui.presenter.creasepattern.*;
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
	private final MouseActionSetterFactory setterFactory;
	private final MainFrameSetting mainFrameSetting;
	private final UIPanelSetting uiPanelSetting;
	private final ComplexActionFactory complexActionFactory;

	/**
	 * Constructor
	 */
	public PaintBoundStateFactory(
			final StateManager<EditMode> stateManager,
			final MouseActionSetterFactory setterFactory,
			final MainViewSetting viewSetting,
			final ComplexActionFactory complexActionFactory) {

		this.stateManager = stateManager;
		this.setterFactory = setterFactory;
		this.mainFrameSetting = viewSetting.getMainFrameSetting();
		this.uiPanelSetting = viewSetting.getUiPanelSetting();
		this.complexActionFactory = complexActionFactory;
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

		var changeHint = new ChangeHint(mainFrameSetting, id);

		var stateOpt = switch (id) {
		case StringID.DELETE_LINE_ID -> Optional.of(stateFactory.create(
				new DeleteLineAction(), changeHint, new Runnable[] {
						(new ChangeOnOtherCommandButtonSelected(uiPanelSetting))::changeViewSetting }));

		case StringID.CHANGE_LINE_TYPE_ID -> Optional.of(stateFactory.create(
				complexActionFactory.createTypeChange(),
				changeHint,
				new Runnable[] { (new ChangeOnAlterTypeButtonSelected(uiPanelSetting))::changeViewSetting }));

		case StringID.ADD_VERTEX_ID -> Optional.of(stateFactory.create(
				new AddVertexAction(), changeHint, new Runnable[] {
						(new ChangeOnOtherCommandButtonSelected(uiPanelSetting))::changeViewSetting }));

		case StringID.DELETE_VERTEX_ID -> Optional.of(stateFactory.create(
				new DeleteVertexAction(), changeHint, new Runnable[] {
						(new ChangeOnOtherCommandButtonSelected(uiPanelSetting))::changeViewSetting }));

		case StringID.EDIT_CONTOUR_ID -> Optional.of(stateFactory.create(
				complexActionFactory.createEditOutline(),
				changeHint, new Runnable[] {
						(new ChangeOnOtherCommandButtonSelected(uiPanelSetting))::changeViewSetting }));

		case StringID.COPY_PASTE_ID, StringID.CUT_PASTE_ID, StringID.IMPORT_CP_ID, StringID.SELECT_ALL_LINE_ID, StringID.SELECT_LINE_ID, StringID.ENLARGE_ID -> createLineSelectionState(
				id, errorDetecter, errorHandler);

		default -> createLineInputState(id);
		};

		return stateOpt.orElseThrow();
	}

	private Optional<ApplicationState<EditMode>> createLineSelectionState(
			final String id,
			final Supplier<Boolean> errorDetecter,
			final Runnable errorHandler) {

		var changeHint = new ChangeHint(mainFrameSetting, id);

		LocalPaintBoundStateFactory stateFactory = new LocalPaintBoundStateFactory(
				stateManager, setterFactory,
				new Runnable[] {
						() -> (new ChangeOnSelectButtonSelected(uiPanelSetting))
								.changeViewSetting() });

		var state = switch (id) {
		case StringID.SELECT_LINE_ID -> stateFactory.create(
				new SelectLineAction(), changeHint, null);

		case StringID.ENLARGE_ID -> stateFactory.create(
				new EnlargeLineAction(), changeHint, null);

		case StringID.SELECT_ALL_LINE_ID ->
				// selecting all lines should be done in other listener
				stateFactory.create(new SelectLineAction(), changeHint, null);

		case StringID.COPY_PASTE_ID -> stateFactory.create(
				complexActionFactory.createCopyAndPaste(),
				errorDetecter, errorHandler, changeHint, null);

		case StringID.CUT_PASTE_ID -> stateFactory.create(
				complexActionFactory.createCutAndPaste(),
				errorDetecter, errorHandler, changeHint, null);

		case StringID.IMPORT_CP_ID -> stateFactory.create(
				complexActionFactory.createImport(),
				changeHint, null);
		default -> null;
		};

		return Optional.ofNullable(state);
	}

	private Optional<ApplicationState<EditMode>> createLineInputState(
			final String id) {

		var changeHint = new ChangeHint(mainFrameSetting, id);

		LocalPaintBoundStateFactory stateFactory = new LocalPaintBoundStateFactory(
				stateManager, setterFactory,
				new Runnable[] {
						() -> (new ChangeOnPaintInputButtonSelected(uiPanelSetting))
								.changeViewSetting() });

		var state = switch (id) {
		case StringID.DIRECT_V_ID -> stateFactory.create(
				new TwoPointSegmentAction(), changeHint, null);

		case StringID.ON_V_ID -> stateFactory.create(
				new TwoPointLineAction(), changeHint, null);

		case StringID.VERTICAL_ID -> stateFactory.create(
				new VerticalLineAction(), changeHint, null);

		case StringID.BISECTOR_ID -> stateFactory.create(
				new AngleBisectorAction(), changeHint, null);

		case StringID.LINE_TO_LINE_ID -> stateFactory.create(
				new LineToLineAxiomAction(), changeHint, null);

		case StringID.POINT_TO_LINE_POINT_TO_LINE_ID -> stateFactory.create(
				new PointToLinePointToLineAxiomAction(), changeHint, null);

		case StringID.POINT_TO_LINE_THROUGH_POINT_ID -> stateFactory.create(
				new PointToLineThroughPointAxiomAction(), changeHint, null);

		case StringID.TRIANGLE_ID -> stateFactory.create(
				new TriangleSplitAction(), changeHint, null);

		case StringID.SYMMETRIC_ID -> stateFactory.create(
				new SymmetricalLineAction(), changeHint, null);

		case StringID.MIRROR_ID -> stateFactory.create(
				new MirrorCopyAction(), changeHint, null);

		case StringID.BY_VALUE_ID -> {
			LocalPaintBoundStateFactory byValueFactory = new LocalPaintBoundStateFactory(
					stateManager, setterFactory, new Runnable[] {
							() -> (new ChangeOnByValueButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			yield byValueFactory.create(
					complexActionFactory.createByValue(),
					changeHint, null);
		}

		case StringID.PERPENDICULAR_BISECTOR_ID -> stateFactory.create(
				new PerpendicularBisectorAction(), changeHint, null);

		case StringID.ANGLE_SNAP_ID -> {
			LocalPaintBoundStateFactory angleSnapFactory = new LocalPaintBoundStateFactory(
					stateManager, setterFactory, new Runnable[] {
							() -> (new ChangeOnAngleSnapButtonSelected(uiPanelSetting))
									.changeViewSetting() });
			yield angleSnapFactory.create(
					new AngleSnapAction(), changeHint, null);
		}
		case StringID.SUGGESTION_ID -> stateFactory.create(
				new SuggestionAction(), changeHint, null);

		default -> null;
		};

		return Optional.ofNullable(state);
	}
}
