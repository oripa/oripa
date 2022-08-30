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

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.appstate.CommandStatePopper;
import oripa.appstate.StateManager;
import oripa.domain.cptool.TypeForChange;
import oripa.domain.creasepattern.CreasePattern;
import oripa.domain.cutmodel.CutModelOutlinesHolder;
import oripa.domain.paint.AngleStep;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.byvalue.ValueSetting;
import oripa.gui.bind.state.PaintBoundStateFactory;
import oripa.gui.bind.state.action.PaintActionSetterFactory;
import oripa.gui.presenter.creasepattern.CreasePatternPresentationContext;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.creasepattern.TypeForChangeContext;
import oripa.gui.presenter.creasepattern.byvalue.AngleMeasuringAction;
import oripa.gui.presenter.creasepattern.byvalue.LengthMeasuringAction;
import oripa.gui.presenter.main.ModelComputationFacade.ComputationResult;
import oripa.gui.view.FrameView;
import oripa.gui.view.estimation.EstimationResultFrameFactory;
import oripa.gui.view.estimation.EstimationResultFrameView;
import oripa.gui.view.main.UIPanelView;
import oripa.gui.view.model.ModelViewFrameFactory;
import oripa.gui.view.model.ModelViewFrameView;
import oripa.gui.view.util.ChildFrameManager;
import oripa.gui.viewsetting.KeyProcessing;
import oripa.gui.viewsetting.ViewScreenUpdater;
import oripa.gui.viewsetting.ViewUpdateSupport;
import oripa.gui.viewsetting.main.MainScreenSetting;
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

	private final TypeForChange[] alterLineComboDataFrom = {
			TypeForChange.EMPTY, TypeForChange.MOUNTAIN, TypeForChange.VALLEY, TypeForChange.AUX,
			TypeForChange.CUT };
	private final TypeForChange[] alterLineComboDataTo = {
			TypeForChange.FLIP, TypeForChange.MOUNTAIN, TypeForChange.VALLEY, TypeForChange.AUX,
			TypeForChange.CUT, TypeForChange.DELETE, };

	private final UIPanelSetting setting;
	private final ValueSetting valueSetting;

	final CutModelOutlinesHolder cutOutlinesHolder;
	final MainScreenSetting mainScreenSetting;

	private ChildFrameManager childFrameManager;

	private final StateManager<EditMode> stateManager;
	private final ViewScreenUpdater screenUpdater;
	private final KeyProcessing keyProcessing;
	private final PaintContext paintContext;
	private final CreasePatternViewContext viewContext;

	private final TypeForChangeContext typeForChangeContext;

	private final PaintActionSetterFactory setterFactory;

	private final PaintBoundStateFactory stateFactory;

	private ComputationResult computationResult;

	public UIPanelPresenter(final UIPanelView view,
			final StateManager<EditMode> stateManager,
			final ViewUpdateSupport viewUpdateSupport,
			final CreasePatternPresentationContext presentationContext,
			final PaintContext paintContext,
			final CutModelOutlinesHolder cutOutlinesHolder,
			final PaintActionSetterFactory setterFactory,
			final PaintBoundStateFactory stateFactory,
			final MainScreenSetting mainScreenSetting) {
		this.view = view;

		setting = view.getUIPanelSetting();
		valueSetting = setting.getValueSetting();
		typeForChangeContext = presentationContext.getTypeForChangeContext();
		this.screenUpdater = viewUpdateSupport.getViewScreenUpdater();
		this.keyProcessing = viewUpdateSupport.getKeyProcessing();
		this.paintContext = paintContext;
		this.viewContext = presentationContext.getViewContext();

		this.stateManager = stateManager;

		this.setterFactory = setterFactory;
		this.stateFactory = stateFactory;

		this.mainScreenSetting = mainScreenSetting;
		this.cutOutlinesHolder = cutOutlinesHolder;

		Stream.of(alterLineComboDataFrom).forEach(item -> view.addItemOfAlterLineComboFrom(item.toString()));
		Stream.of(alterLineComboDataTo).forEach(item -> view.addItemOfAlterLineComboTo(item.toString()));

		Stream.of(AngleStep.values()).forEach(item -> view.addItemOfAngleStepCombo(item.toString()));

		addListeners();

		typeForChangeContext.setTypeFrom(alterLineComboDataFrom[0]);
		typeForChangeContext.setTypeTo(alterLineComboDataTo[0]);

		view.initializeButtonSelection(AngleStep.PI_OVER_8.toString(),
				typeForChangeContext.getTypeFrom().toString(),
				typeForChangeContext.getTypeTo().toString());
	}

	public void setChildFrameManager(final ChildFrameManager manager) {
		childFrameManager = manager;
	}

	private void addListeners() {
		// ------------------------------------------------------------
		// edit mode buttons

		view.addEditModeInputLineButtonListener(
				new CommandStatePopper<EditMode>(stateManager, EditMode.INPUT),
				keyProcessing);

		view.addEditModeLineSelectionButtonListener(
				new CommandStatePopper<EditMode>(stateManager, EditMode.SELECT),
				keyProcessing);

		var deleteLineState = stateFactory.create(StringID.DELETE_LINE_ID,
				null, null);
		view.addEditModeDeleteLineButtonListener(deleteLineState::performActions, keyProcessing);

		var lineTypeState = stateFactory.create(StringID.CHANGE_LINE_TYPE_ID,
				null, null);
		view.addEditModeLineTypeButtonListener(lineTypeState::performActions, keyProcessing);

		view.addAlterLineComboFromSelectionListener(
				item -> typeForChangeContext.setTypeFrom(TypeForChange.fromString(item).get()));
		view.addAlterLineComboToSelectionListener(
				item -> typeForChangeContext.setTypeTo(TypeForChange.fromString(item).get()));

		var addVertexState = stateFactory.create(StringID.ADD_VERTEX_ID,
				null, null);
		view.addEditModeAddVertexButtonListener(addVertexState::performActions, keyProcessing);

		var deleteVertexState = stateFactory.create(StringID.DELETE_VERTEX_ID,
				null, null);
		view.addEditModeDeleteVertexButtonListener(deleteVertexState::performActions, keyProcessing);

		// ------------------------------------------------------------
		// selection command buttons

		var selectLineState = stateFactory.create(StringID.SELECT_LINE_ID,
				null, null);
		view.addSelectionButtonListener(selectLineState::performActions, keyProcessing);

		var enlargementState = stateFactory.create(StringID.ENLARGE_ID,
				null, null);
		view.addEnlargementButtonListener(enlargementState::performActions, keyProcessing);

		// ------------------------------------------------------------
		// input command buttons

		var directVState = stateFactory.create(StringID.DIRECT_V_ID,
				null, null);
		view.addLineInputDirectVButtonListener(directVState::performActions, keyProcessing);

		var onVState = stateFactory.create(StringID.ON_V_ID,
				null, null);
		view.addLineInputOnVButtonListener(onVState::performActions, keyProcessing);

		var verticalLineState = stateFactory.create(StringID.VERTICAL_ID,
				null, null);
		view.addLineInputVerticalLineButtonListener(verticalLineState::performActions,
				keyProcessing);

		var angleBisectorState = stateFactory.create(StringID.BISECTOR_ID,
				null, null);
		view.addLineInputAngleBisectorButtonListener(angleBisectorState::performActions,
				keyProcessing);

		var triangleSplitState = stateFactory.create(StringID.TRIANGLE_ID,
				null, null);
		view.addLineInputTriangleSplitButtonListener(triangleSplitState::performActions,
				keyProcessing);

		var symmetricState = stateFactory.create(StringID.SYMMETRIC_ID,
				null, null);
		view.addLineInputSymmetricButtonListener(symmetricState::performActions, keyProcessing);

		var mirrorState = stateFactory.create(StringID.MIRROR_ID,
				null, null);
		view.addLineInputMirrorButtonListener(mirrorState::performActions, keyProcessing);

		var byValueState = stateFactory.create(StringID.BY_VALUE_ID,
				null, null);
		view.addLineInputByValueButtonListener(byValueState::performActions, keyProcessing);

		view.addLengthButtonListener(
				setterFactory.create(new LengthMeasuringAction(valueSetting)));
		view.addAngleButtonListener(
				setterFactory.create(new AngleMeasuringAction(valueSetting)));
		view.addLengthTextFieldListener(valueSetting::setLength);
		view.addAngleTextFieldListener(valueSetting::setAngle);

		var pbisecState = stateFactory.create(StringID.PERPENDICULAR_BISECTOR_ID,
				null, null);
		view.addLineInputPBisectorButtonListener(pbisecState::performActions, keyProcessing);

		var angleSnapState = stateFactory.create(StringID.ANGLE_SNAP_ID,
				null, null);
		view.addLineInputAngleSnapButtonListener(angleSnapState::performActions, keyProcessing);

		view.addAngleStepComboListener(step -> paintContext.setAngleStep(AngleStep.fromString(step).get()));

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
				view.isFullEstimation());
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

		if (view.isFullEstimation()) {
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
