/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package oripa.gui.presenter.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.appstate.CommandStatePopper;
import oripa.appstate.StateManager;
import oripa.domain.creasepattern.CreasePattern;
import oripa.domain.cutmodel.CutModelOutlinesHolder;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.byvalue.ValueSetting;
import oripa.gui.bind.state.PaintBoundStateFactory;
import oripa.gui.bind.state.action.PaintActionSetterFactory;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.creasepattern.byvalue.AngleMeasuringAction;
import oripa.gui.presenter.creasepattern.byvalue.AngleValueInputListener;
import oripa.gui.presenter.creasepattern.byvalue.LengthMeasuringAction;
import oripa.gui.presenter.creasepattern.byvalue.LengthValueInputListener;
import oripa.gui.presenter.main.ModelComputationFacade.ComputationResult;
import oripa.gui.view.FrameView;
import oripa.gui.view.estimation.EstimationResultFrameFactory;
import oripa.gui.view.estimation.EstimationResultFrameView;
import oripa.gui.view.main.UIPanelView;
import oripa.gui.view.model.ModelViewFrameFactory;
import oripa.gui.view.model.ModelViewFrameView;
import oripa.gui.view.util.ChildFrameManager;
import oripa.gui.viewsetting.ViewScreenUpdater;
import oripa.gui.viewsetting.main.MainScreenSetting;
import oripa.gui.viewsetting.main.uipanel.FromLineTypeItemListener;
import oripa.gui.viewsetting.main.uipanel.ToLineTypeItemListener;
import oripa.gui.viewsetting.main.uipanel.UIPanelSetting;
import oripa.resource.StringID;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class UIPanelPresenter {
	private static final Logger logger = LoggerFactory.getLogger(UIPanelPresenter.class);

	private final UIPanelView view;

	private final UIPanelSetting setting;
	private final ValueSetting valueSetting;

	final CutModelOutlinesHolder cutOutlinesHolder;
	final MainScreenSetting mainScreenSetting;

	private ChildFrameManager childFrameManager;

	private final StateManager<EditMode> stateManager;
	private final ViewScreenUpdater screenUpdater;
	private final PaintContext paintContext;
	private final CreasePatternViewContext viewContext;

	private final PaintActionSetterFactory setterFactory;

	private final PaintBoundStateFactory stateFactory;

	private ComputationResult computationResult;

	public UIPanelPresenter(final UIPanelView view,
			final StateManager<EditMode> stateManager,
			final ViewScreenUpdater screenUpdater,
			final CreasePatternViewContext viewContext,
			final PaintContext paintContext,
			final CutModelOutlinesHolder cutOutlinesHolder,
			final PaintActionSetterFactory setterFactory,
			final PaintBoundStateFactory stateFactory,
			final MainScreenSetting mainScreenSetting) {
		this.view = view;

		setting = view.getUIPanelSetting();
		valueSetting = setting.getValueSetting();
		this.screenUpdater = screenUpdater;
		this.paintContext = paintContext;
		this.viewContext = viewContext;

		this.stateManager = stateManager;

		this.setterFactory = setterFactory;
		this.stateFactory = stateFactory;

		this.mainScreenSetting = mainScreenSetting;
		this.cutOutlinesHolder = cutOutlinesHolder;

		addListeners();

		view.initializeButtonSelection();
	}

	public void setChildFrameManager(final ChildFrameManager manager) {
		childFrameManager = manager;
	}

	private void addListeners() {
		// ------------------------------------------------------------
		// edit mode buttons

		view.addEditModeInputLineButtonListener(
				new CommandStatePopper<EditMode>(stateManager, EditMode.INPUT),
				screenUpdater.getKeyListener());

		view.addEditModeLineSelectionButtonListener(
				new CommandStatePopper<EditMode>(stateManager, EditMode.SELECT),
				screenUpdater.getKeyListener());

		var deleteLineState = stateFactory.create(StringID.DELETE_LINE_ID,
				null, null);
		view.addEditModeDeleteLineButtonListener(deleteLineState::performActions, screenUpdater.getKeyListener());

		var lineTypeState = stateFactory.create(StringID.CHANGE_LINE_TYPE_ID,
				null, null);
		view.addEditModeLineTypeButtonListener(lineTypeState::performActions, screenUpdater.getKeyListener());

		view.addAlterLineComboFromListener(new FromLineTypeItemListener(setting));
		view.addAlterLineComboToListener(new ToLineTypeItemListener(setting));

		var addVertexState = stateFactory.create(StringID.ADD_VERTEX_ID,
				null, null);
		view.addEditModeAddVertexButtonListener(addVertexState::performActions, screenUpdater.getKeyListener());

		var deleteVertexState = stateFactory.create(StringID.DELETE_VERTEX_ID,
				null, null);
		view.addEditModeDeleteVertexButtonListener(deleteVertexState::performActions, screenUpdater.getKeyListener());

		// ------------------------------------------------------------
		// selection command buttons

		var selectLineState = stateFactory.create(StringID.SELECT_LINE_ID,
				null, null);
		view.addSelectionButtonListener(selectLineState::performActions, screenUpdater.getKeyListener());

		var enlargementState = stateFactory.create(StringID.ENLARGE_ID,
				null, null);
		view.addEnlargementButtonListener(enlargementState::performActions, screenUpdater.getKeyListener());

		// ------------------------------------------------------------
		// input command buttons

		var directVState = stateFactory.create(StringID.DIRECT_V_ID,
				null, null);
		view.addLineInputDirectVButtonListener(directVState::performActions, screenUpdater.getKeyListener());

		var onVState = stateFactory.create(StringID.ON_V_ID,
				null, null);
		view.addLineInputOnVButtonListener(onVState::performActions, screenUpdater.getKeyListener());

		var verticalLineState = stateFactory.create(StringID.VERTICAL_ID,
				null, null);
		view.addLineInputVerticalLineButtonListener(verticalLineState::performActions, screenUpdater.getKeyListener());

		var angleBisectorState = stateFactory.create(StringID.BISECTOR_ID,
				null, null);
		view.addLineInputAngleBisectorButtonListener(angleBisectorState::performActions,
				screenUpdater.getKeyListener());

		var triangleSplitState = stateFactory.create(StringID.TRIANGLE_ID,
				null, null);
		view.addLineInputTriangleSplitButtonListener(triangleSplitState::performActions,
				screenUpdater.getKeyListener());

		var symmetricState = stateFactory.create(StringID.SYMMETRIC_ID,
				null, null);
		view.addLineInputSymmetricButtonListener(symmetricState::performActions, screenUpdater.getKeyListener());

		var mirrorState = stateFactory.create(StringID.MIRROR_ID,
				null, null);
		view.addLineInputMirrorButtonListener(mirrorState::performActions, screenUpdater.getKeyListener());

		var byValueState = stateFactory.create(StringID.BY_VALUE_ID,
				null, null);
		view.addLineInputByValueButtonListener(byValueState::performActions, screenUpdater.getKeyListener());

		view.addLengthButtonListener(
				setterFactory.create(new LengthMeasuringAction(valueSetting)));
		view.addAngleButtonListener(
				setterFactory.create(new AngleMeasuringAction(valueSetting)));
		view.addLengthTextFieldListener(
				new LengthValueInputListener(valueSetting));
		view.addAngleTextFieldListener(
				new AngleValueInputListener(valueSetting));

		var pbisecState = stateFactory.create(StringID.PERPENDICULAR_BISECTOR_ID,
				null, null);
		view.addLineInputPBisectorButtonListener(pbisecState::performActions, screenUpdater.getKeyListener());

		var angleSnapState = stateFactory.create(StringID.ANGLE_SNAP_ID,
				null, null);
		view.addLineInputAngleSnapButtonListener(angleSnapState::performActions, screenUpdater.getKeyListener());

		view.addAngleStepComboListener(step -> paintContext.setAngleStep(step));

		view.addLineTypeMountainButtonListener(() -> paintContext.setLineTypeOfNewLines(OriLine.Type.MOUNTAIN));
		view.addLineTypeValleyButtonListener(() -> paintContext.setLineTypeOfNewLines(OriLine.Type.VALLEY));
		view.addLineTypeAuxButtonListener(() -> paintContext.setLineTypeOfNewLines(OriLine.Type.AUX));

		// ------------------------------------------------------------
		// grid setting

		view.addDispGridCheckBoxListener(checked -> {
			mainScreenSetting.setGridVisible(checked);
		});
		view.addGridSmallButtonListener(this::makeGridSizeHalf);
		view.addGridLargeButtonListener(this::makeGridSizeTwiceLarge);
		view.addGridChangeButtonListener(this::updateGridDivNum);

		// ------------------------------------------------------------
		// display setting

		view.addDispVertexCheckBoxListener(checked -> {
			logger.debug("vertexVisible at listener: {}", checked);
			mainScreenSetting.setVertexVisible(checked);
		});

		view.addDispMVLinesCheckBoxListener(checked -> {
			logger.debug("mvLineVisible at listener: {}", checked);
			mainScreenSetting.setMVLineVisible(checked);
		});

		view.addDispAuxLinesCheckBoxListener(checked -> {
			logger.debug("auxLineVisible at listener: {}", checked);
			mainScreenSetting.setAuxLineVisible(checked);
		});

		view.addZeroLineWidthCheckBoxListener(checked -> {
			mainScreenSetting.setZeroLineWidth(checked);
			screenUpdater.updateScreen();
		});

		// ------------------------------------------------------------
		// fold

		view.addCheckWindowButtonListener(this::showCheckerWindow);
		view.setModelComputationListener(this::computeModels);
		view.setShowFoldedModelWindowsListener(this::showFoldedModelWindows);
	}

	private void makeGridSizeHalf() {
		setGridDivNumIfValid(paintContext.getGridDivNum() * 2);
	}

	private void makeGridSizeTwiceLarge() {
		setGridDivNumIfValid(paintContext.getGridDivNum() / 2);
	}

	private void updateGridDivNum(final int gridDivNum) {
		setGridDivNumIfValid(gridDivNum);
	}

	private void setGridDivNumIfValid(final int gridDivNum) {
		if (!isValidGridDivNum(gridDivNum)) {
			return;
		}
		paintContext.setGridDivNum(gridDivNum);
		view.setGridDivNum(gridDivNum);

		screenUpdater.updateScreen();
	}

	private boolean isValidGridDivNum(final int gridDivNum) {
		return gridDivNum >= 2 && gridDivNum <= 128;
	}

	/**
	 * display window with foldability checks
	 */
	private void showCheckerWindow() {
		var windowOpener = new CheckerWindowOpener((FrameView) view.getTopLevelView(), childFrameManager);
		windowOpener.showCheckerWindow(paintContext.getCreasePattern(), viewContext.isZeroLineWidth());
	}

	private void computeModels() {
		var modelComputation = new ModelComputationFacade(
				// ask if ORIPA should try to remove duplication.
				view::showCleaningUpDuplicationDialog,
				// clean up the crease pattern
				view::showCleaningUpMessage,
				// folding failed.
				view::showFoldFailureMessage);

		CreasePattern creasePattern = paintContext.getCreasePattern();

		var origamiModels = modelComputation.buildOrigamiModels(creasePattern);

		computationResult = modelComputation.computeModels(
				origamiModels,
				view.getFullEstimation());
	}

	private void showFoldedModelWindows() {
		var parent = (FrameView) view.getTopLevelView();

		var origamiModels = computationResult.getOrigamiModels();
		var foldedModels = computationResult.getFoldedModels();

		ModelViewFrameFactory modelViewFactory = new ModelViewFrameFactory(
				mainScreenSetting,
				childFrameManager);
		ModelViewFrameView modelViewFrame = modelViewFactory.createFrame(parent, origamiModels,
				cutOutlinesHolder, screenUpdater::updateScreen, view.getPaperDomainOfModelChangeListener());

		modelViewFrame.repaint();

		EstimationResultFrameView resultFrame = null;

		if (view.getFullEstimation()) {
			var count = computationResult.countFoldablePatterns();
			if (count == 0) {
				// no answer is found.
				view.showNoAnswerMessage();
			} else if (count > 0) {
				logger.info("foldable layer layout is found.");

				EstimationResultFrameFactory resultFrameFactory = new EstimationResultFrameFactory(
						childFrameManager);
				resultFrame = resultFrameFactory.createFrame(parent, foldedModels);

				resultFrame.setColors(
						view.getEstimationResultFrontColor(),
						view.getEstimationResultBackColor());
				resultFrame.setSaveColorsListener(view.getEstimationResultSaveColorsListener());
				resultFrame.repaint();

				resultFrame.setViewVisible(true);
			}
		}

		putModelIndexChangeListener(modelViewFrame, resultFrame);

		modelViewFrame.setViewVisible(true);
	}

	public void putModelIndexChangeListener(final ModelViewFrameView modelViewFrame,
			final EstimationResultFrameView resultFrame) {
		if (modelViewFrame == null || resultFrame == null) {
			return;
		}
		modelViewFrame.putModelIndexChangeListener(resultFrame,
				e -> {
					logger.debug("modelViewFrame model index change: {} -> {}", e.getOldValue(), e.getNewValue());
					resultFrame.selectModel((Integer) e.getNewValue());
				});
		resultFrame.putModelIndexChangeListener(modelViewFrame,
				e -> {
					logger.debug("resultFrame model index change: {} -> {}", e.getOldValue(), e.getNewValue());
					modelViewFrame.selectModel((Integer) e.getNewValue());
				});
	}
}
