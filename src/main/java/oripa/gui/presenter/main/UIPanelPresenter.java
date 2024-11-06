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

import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.appstate.StatePopperFactory;
import oripa.domain.cptool.TypeForChange;
import oripa.domain.creasepattern.CreasePattern;
import oripa.domain.cutmodel.CutModelOutlinesHolder;
import oripa.domain.fold.EstimationResultRules;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.domain.paint.AngleStep;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.PaintDomainContext;
import oripa.domain.paint.byvalue.ByValueContext;
import oripa.gui.bind.state.BindingObjectFactoryFacade;
import oripa.gui.presenter.creasepattern.CreasePatternPresentationContext;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.creasepattern.TypeForChangeContext;
import oripa.gui.presenter.creasepattern.byvalue.AngleMeasuringAction;
import oripa.gui.presenter.creasepattern.byvalue.LengthMeasuringAction;
import oripa.gui.presenter.estimation.EstimationResultFramePresenter;
import oripa.gui.presenter.main.ModelComputationFacade.ComputationResult;
import oripa.gui.presenter.main.ModelComputationFacade.ComputationType;
import oripa.gui.presenter.model.ModelViewFramePresenter;
import oripa.gui.presenter.plugin.GraphicMouseActionPlugin;
import oripa.gui.view.FrameView;
import oripa.gui.view.ViewScreenUpdater;
import oripa.gui.view.estimation.EstimationResultFrameView;
import oripa.gui.view.file.FileChooserFactory;
import oripa.gui.view.main.KeyProcessing;
import oripa.gui.view.main.PainterScreenSetting;
import oripa.gui.view.main.SubFrameFactory;
import oripa.gui.view.main.UIPanelView;
import oripa.gui.view.main.ViewUpdateSupport;
import oripa.gui.view.model.ModelViewFrameView;
import oripa.resource.StringID;
import oripa.util.MathUtil;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class UIPanelPresenter {
	private static final Logger logger = LoggerFactory.getLogger(UIPanelPresenter.class);

	private final UIPanelView view;
	private final SubFrameFactory subFrameFactory;
	private final FileChooserFactory fileChooserFactory;

	private final TypeForChange[] alterLineComboDataFrom = {
			TypeForChange.EMPTY, TypeForChange.MOUNTAIN, TypeForChange.VALLEY, TypeForChange.UNASSIGNED,
			TypeForChange.AUX, TypeForChange.CUT };
	private final TypeForChange[] alterLineComboDataTo = {
			TypeForChange.FLIP, TypeForChange.MOUNTAIN, TypeForChange.VALLEY, TypeForChange.UNASSIGNED,
			TypeForChange.AUX, TypeForChange.CUT, TypeForChange.DELETE, };

	private final ComputationType[] computationTypeComboData = {
			ComputationType.FULL, ComputationType.FIRST_ONLY, ComputationType.X_RAY };

	private final ByValueContext byValueContext;

	final CutModelOutlinesHolder cutOutlinesHolder;
	final PainterScreenSetting mainScreenSetting;

	private final StatePopperFactory<EditMode> statePopperFactory;
	private final ViewScreenUpdater screenUpdater;
	private final KeyProcessing keyProcessing;
	private final PaintContext paintContext;
	private final CreasePatternViewContext viewContext;

	private final TypeForChangeContext typeForChangeContext;

	private final BindingObjectFactoryFacade bindingFactory;

	private ComputationResult computationResult;

	private String lastResultFilePath;

	public UIPanelPresenter(final UIPanelView view,
			final SubFrameFactory subFrameFactory,
			final FileChooserFactory fileChooserFactory,
			final StatePopperFactory<EditMode> statePopperFactory,
			final ViewUpdateSupport viewUpdateSupport,
			final CreasePatternPresentationContext presentationContext,
			final PaintDomainContext domainContext,
			final CutModelOutlinesHolder cutOutlinesHolder,
			final BindingObjectFactoryFacade bindingFactory,
			final PainterScreenSetting mainScreenSetting) {
		this.view = view;
		this.subFrameFactory = subFrameFactory;

		this.fileChooserFactory = fileChooserFactory;

		this.byValueContext = domainContext.getByValueContext();
		typeForChangeContext = presentationContext.getTypeForChangeContext();
		this.screenUpdater = viewUpdateSupport.getViewScreenUpdater();
		this.keyProcessing = viewUpdateSupport.getKeyProcessing();
		this.paintContext = domainContext.getPaintContext();
		this.viewContext = presentationContext.getViewContext();

		this.statePopperFactory = statePopperFactory;

		this.bindingFactory = bindingFactory;

		this.mainScreenSetting = mainScreenSetting;
		this.cutOutlinesHolder = cutOutlinesHolder;

		Stream.of(alterLineComboDataFrom).forEach(item -> view.addItemOfAlterLineComboFrom(item.toString()));
		Stream.of(alterLineComboDataTo).forEach(item -> view.addItemOfAlterLineComboTo(item.toString()));
		Stream.of(computationTypeComboData).forEach(item -> view.addItemOfComputationTypeCombo(item.toString()));
		Stream.of(AngleStep.values()).forEach(item -> view.addItemOfAngleStepCombo(item.toString()));

		addListeners();

		typeForChangeContext.setTypeFrom(alterLineComboDataFrom[0]);
		typeForChangeContext.setTypeTo(alterLineComboDataTo[0]);

		view.initializeButtonSelection(AngleStep.PI_OVER_8.toString(),
				typeForChangeContext.getTypeFrom().toString(),
				typeForChangeContext.getTypeTo().toString(),
				ComputationType.FULL.toString());

		updateValuePanelFractionDigits();
	}

	public void addPlugins(final List<GraphicMouseActionPlugin> plugins) {
		for (var plugin : plugins) {
			var state = bindingFactory.createState(plugin);

			view.addMouseActionPluginListener(plugin.getName(), state::performActions, keyProcessing);
		}
	}

	private void addListeners() {
		// ------------------------------------------------------------
		// edit mode buttons

		view.addEditModeInputLineButtonListener(
				statePopperFactory.createForCommand(EditMode.INPUT),
				keyProcessing);

		view.addEditModeLineSelectionButtonListener(
				statePopperFactory.createForCommand(EditMode.SELECT),
				keyProcessing);

		var deleteLineState = bindingFactory.createState(StringID.DELETE_LINE_ID);
		view.addEditModeDeleteLineButtonListener(deleteLineState::performActions, keyProcessing);

		var lineTypeState = bindingFactory.createState(StringID.CHANGE_LINE_TYPE_ID);
		view.addEditModeLineTypeButtonListener(lineTypeState::performActions, keyProcessing);

		view.addAlterLineComboFromSelectionListener(
				item -> typeForChangeContext.setTypeFrom(TypeForChange.fromString(item).get()));
		view.addAlterLineComboToSelectionListener(
				item -> typeForChangeContext.setTypeTo(TypeForChange.fromString(item).get()));

		var addVertexState = bindingFactory.createState(StringID.ADD_VERTEX_ID);
		view.addEditModeAddVertexButtonListener(addVertexState::performActions, keyProcessing);

		var deleteVertexState = bindingFactory.createState(StringID.DELETE_VERTEX_ID);
		view.addEditModeDeleteVertexButtonListener(deleteVertexState::performActions, keyProcessing);

		// ------------------------------------------------------------
		// selection command buttons

		var selectLineState = bindingFactory.createState(StringID.SELECT_LINE_ID);
		view.addSelectionButtonListener(selectLineState::performActions, keyProcessing);

		var enlargementState = bindingFactory.createState(StringID.ENLARGE_ID);
		view.addEnlargementButtonListener(enlargementState::performActions, keyProcessing);

		// ------------------------------------------------------------
		// input command buttons

		var directVState = bindingFactory.createState(StringID.DIRECT_V_ID);
		view.addLineInputDirectVButtonListener(directVState::performActions, keyProcessing);

		var onVState = bindingFactory.createState(StringID.ON_V_ID);
		view.addLineInputOnVButtonListener(onVState::performActions, keyProcessing);

		var verticalLineState = bindingFactory.createState(StringID.VERTICAL_ID);
		view.addLineInputVerticalLineButtonListener(verticalLineState::performActions,
				keyProcessing);

		var angleBisectorState = bindingFactory.createState(StringID.BISECTOR_ID);
		view.addLineInputAngleBisectorButtonListener(angleBisectorState::performActions,
				keyProcessing);

		var lineToLineState = bindingFactory.createState(StringID.LINE_TO_LINE_ID);
		view.addLineInputLineToLineAxiomButtonListener(lineToLineState::performActions, keyProcessing);

		var p2ltpState = bindingFactory.createState(StringID.POINT_TO_LINE_THROUGH_POINT_ID);
		view.addLineInputP2LTPAxiomButtonListener(p2ltpState::performActions, keyProcessing);

		var p2lp2lState = bindingFactory.createState(StringID.POINT_TO_LINE_POINT_TO_LINE_ID);
		view.addLineInputP2LP2LAxiomButtonListener(p2lp2lState::performActions, keyProcessing);

		var p2llState = bindingFactory.createState(StringID.POINT_TO_LINE_LINE_PERPENDICULAR_ID);
		view.addLineInputP2LLAxiomButtonListener(p2llState::performActions, keyProcessing);

		var triangleSplitState = bindingFactory.createState(StringID.TRIANGLE_ID);
		view.addLineInputTriangleSplitButtonListener(triangleSplitState::performActions,
				keyProcessing);

		var symmetricState = bindingFactory.createState(StringID.SYMMETRIC_ID);
		view.addLineInputSymmetricButtonListener(symmetricState::performActions, keyProcessing);

		var mirrorState = bindingFactory.createState(StringID.MIRROR_ID);
		view.addLineInputMirrorButtonListener(mirrorState::performActions, keyProcessing);

		var byValueState = bindingFactory.createState(StringID.BY_VALUE_ID);
		view.addLineInputByValueButtonListener(byValueState::performActions, keyProcessing);

		view.addLengthButtonListener(
				bindingFactory.createActionSetter(new LengthMeasuringAction(byValueContext)));
		view.addAngleButtonListener(
				bindingFactory.createActionSetter(new AngleMeasuringAction(byValueContext)));
		view.addLengthTextFieldListener(byValueContext::setLength);
		view.addAngleTextFieldListener(byValueContext::setAngle);

		var pbisecState = bindingFactory.createState(StringID.PERPENDICULAR_BISECTOR_ID);
		view.addLineInputPBisectorButtonListener(pbisecState::performActions, keyProcessing);

		var angleSnapState = bindingFactory.createState(StringID.ANGLE_SNAP_ID);
		view.addLineInputAngleSnapButtonListener(angleSnapState::performActions, keyProcessing);

		view.addAngleStepComboListener(step -> paintContext.setAngleStep(AngleStep.fromString(step).get()));

		var suggestionState = bindingFactory.createState(StringID.SUGGESTION_ID);
		view.addLineInputSuggestionButtonListener(suggestionState::performActions, keyProcessing);

		view.addLineTypeMountainButtonListener(() -> paintContext.setLineTypeOfNewLines(OriLine.Type.MOUNTAIN));
		view.addLineTypeValleyButtonListener(() -> paintContext.setLineTypeOfNewLines(OriLine.Type.VALLEY));
		view.addLineTypeUnassignedButtonListener(() -> paintContext.setLineTypeOfNewLines(OriLine.Type.UNASSIGNED));
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

		byValueContext.addPropertyChangeListener(ByValueContext.ANGLE,
				e -> view.setByValueAngle((double) e.getNewValue()));
		byValueContext.addPropertyChangeListener(ByValueContext.LENGTH,
				e -> view.setByValueLength((double) e.getNewValue()));

		// ------------------------------------------------------------
		// fold

		view.addCheckWindowButtonListener(() -> showCheckerWindow());
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
		return gridDivNum >= 2 && gridDivNum <= 256;
	}

	/**
	 * Updates text fields' format setting based on eps in context.
	 */
	public void updateValuePanelFractionDigits() {
		view.setValuePanelFractionDigits(
				computeValuePanelFractionDigits(paintContext.getPointEps()),
				computeValuePanelFractionDigits(MathUtil.angleDegreeEps()));
	}

	private int computeValuePanelFractionDigits(final double eps) {
		// 1 digit is added for precision.
		return (int) Math.floor(Math.abs(Math.log10(eps))) + 1;
	}

	/**
	 * display window with foldability checks
	 */
	private void showCheckerWindow() {
		var windowOpener = new CheckerWindowOpener((FrameView) view.getTopLevelView(), subFrameFactory);
		windowOpener.showCheckerWindow(paintContext.getCreasePattern(), viewContext.isZeroLineWidth(),
				paintContext.getPointEps());
	}

	private void showCheckerWindow(final OrigamiModel origamiModel, final EstimationResultRules estimationRules) {
		var windowOpener = new CheckerWindowOpener((FrameView) view.getTopLevelView(), subFrameFactory);
		windowOpener.showCheckerWindow(paintContext.getCreasePattern(), origamiModel, estimationRules,
				viewContext.isZeroLineWidth(),
				paintContext.getPointEps());
	}

	private void computeModels() {
		var modelComputation = new ModelComputationFacade(
				// ask if ORIPA should try to remove duplication.
				view::showCleaningUpDuplicationDialog,
				// clean up the crease pattern
				view::showCleaningUpMessage,
				// folding failed.
				view::showFoldFailureMessage,
				paintContext.getPointEps());

		CreasePattern creasePattern = paintContext.getCreasePattern();

		var origamiModels = modelComputation.buildOrigamiModels(creasePattern);

		computationResult = modelComputation.computeModels(
				origamiModels,
				getComputationType());
	}

	private ComputationType getComputationType() {
		return ComputationType.fromString(view.getComputationType()).get();
	}

	private void showFoldedModelWindows() {
		var parent = (FrameView) view.getTopLevelView();

		if (!computationResult.allLocallyFlatFoldable()) {
			view.showLocalFlatFoldabilityViolationMessage();
			showCheckerWindow();
			return;
		}

		var origamiModels = computationResult.origamiModels();
		var foldedModels = computationResult.foldedModels();

		ModelViewFrameView modelViewFrame = subFrameFactory.createModelViewFrame(parent,
				view.getPaperDomainOfModelChangeListener());

		var modelViewPresenter = new ModelViewFramePresenter(
				modelViewFrame,
				fileChooserFactory,
				mainScreenSetting,
				origamiModels,
				cutOutlinesHolder,
				screenUpdater::updateScreen,
				paintContext.getPointEps());
		modelViewPresenter.setViewVisible(true);

		EstimationResultFrameView resultFrame = null;

		if (getComputationType().isLayerOrdering()) {
			if (!computationResult.allGloballyFlatFoldable()) {
				// wrong crease pattern exists.
				view.showNoAnswerMessage();
				logger.debug("estimation rules: {}", computationResult.getEstimationResultRules());
				showCheckerWindow(computationResult.getMergedOrigamiModel(),
						computationResult.getEstimationResultRules());
			} else {
				logger.info("foldable layer layout is found.");

				resultFrame = subFrameFactory.createResultFrame(parent);

				resultFrame.setColors(
						view.getEstimationResultFrontColor(),
						view.getEstimationResultBackColor());
				resultFrame.setSaveColorsListener(view.getEstimationResultSaveColorsListener());
				// resultFrame.repaint();

				var resultFramePresenter = new EstimationResultFramePresenter(
						resultFrame,
						fileChooserFactory,
						foldedModels,
						paintContext.getPointEps(),
						lastResultFilePath,
						path -> lastResultFilePath = path);

				resultFramePresenter.setViewVisible(true);
			}
		}

		putModelIndexChangeListener(modelViewFrame, resultFrame);

		modelViewPresenter.setViewVisible(true);
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
