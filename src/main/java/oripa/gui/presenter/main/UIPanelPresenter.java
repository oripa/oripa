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

import oripa.domain.cptool.TypeForChange;
import oripa.domain.creasepattern.CreasePattern;
import oripa.domain.fold.EstimationResultRules;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.domain.paint.AngleStep;
import oripa.domain.paint.PaintContext;
import oripa.gui.presenter.creasepattern.TypeForChangeContext;
import oripa.gui.presenter.main.logic.GridDivNumPresentationLogic;
import oripa.gui.presenter.main.logic.ModelComputationFacade.ComputationResult;
import oripa.gui.presenter.main.logic.ModelComputationFacade.ComputationType;
import oripa.gui.presenter.main.logic.ModelComputationFacadeFactory;
import oripa.gui.presenter.main.logic.ModelIndexChangeListenerPutter;
import oripa.gui.presenter.main.logic.UIPanelPaintMenuListenerRegistration;
import oripa.gui.presenter.plugin.GraphicMouseActionPlugin;
import oripa.gui.view.FrameView;
import oripa.gui.view.estimation.EstimationResultFrameView;
import oripa.gui.view.main.PainterScreenSetting;
import oripa.gui.view.main.SubFrameFactory;
import oripa.gui.view.main.UIPanelView;
import oripa.gui.view.model.ModelViewFrameView;
import oripa.util.MathUtil;

/**
 * @author OUCHI Koji
 *
 */
public class UIPanelPresenter {
	private static final Logger logger = LoggerFactory.getLogger(UIPanelPresenter.class);

	private final UIPanelView view;
	private final SubFrameFactory subFrameFactory;

	private final UIPanelPaintMenuListenerRegistration paintMenuListenerRegistration;
	private final GridDivNumPresentationLogic gridDivNumPresentationLogic;

	private final ModelIndexChangeListenerPutter modelIndexChangeListenerPutter;
	private final SubFramePresenterFactory subFramePresenterFactory;

	private final TypeForChange[] alterLineComboDataFrom = {
			TypeForChange.EMPTY, TypeForChange.MOUNTAIN, TypeForChange.VALLEY, TypeForChange.UNASSIGNED,
			TypeForChange.AUX, TypeForChange.CUT };
	private final TypeForChange[] alterLineComboDataTo = {
			TypeForChange.FLIP, TypeForChange.MOUNTAIN, TypeForChange.VALLEY, TypeForChange.UNASSIGNED,
			TypeForChange.AUX, TypeForChange.CUT, TypeForChange.DELETE, };

	private final ComputationType[] computationTypeComboData = {
			ComputationType.FULL, ComputationType.FIRST_ONLY, ComputationType.X_RAY };

	private final PainterScreenSetting mainScreenSetting;

	private final PaintContext paintContext;

	private final ModelComputationFacadeFactory computationFacadeFactory;
	private ComputationResult computationResult;

	private String lastResultFilePath;

	public UIPanelPresenter(final UIPanelView view,
			final SubFrameFactory subFrameFactory,
			final SubFramePresenterFactory subFramePresenterFactory,
			final UIPanelPaintMenuListenerRegistration paintMenuListenerRegistration,
			final GridDivNumPresentationLogic gridDivNumPresentationLogic,
			final ModelIndexChangeListenerPutter modelIndexChangeListenerPutter,
			final ModelComputationFacadeFactory computationFacadeFactory,
			final TypeForChangeContext typeForChangeContext,
			final PaintContext paintContext,
			final PainterScreenSetting mainScreenSetting) {

		this.view = view;
		this.subFrameFactory = subFrameFactory;

		this.paintMenuListenerRegistration = paintMenuListenerRegistration;
		this.gridDivNumPresentationLogic = gridDivNumPresentationLogic;

		this.modelIndexChangeListenerPutter = modelIndexChangeListenerPutter;
		this.subFramePresenterFactory = subFramePresenterFactory;
		this.computationFacadeFactory = computationFacadeFactory;

		this.paintContext = paintContext;

		this.mainScreenSetting = mainScreenSetting;

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
		paintMenuListenerRegistration.addPlugins(plugins);
	}

	private void addListeners() {

		paintMenuListenerRegistration.register();

		// ------------------------------------------------------------
		// grid setting

		view.addDispGridCheckBoxListener(checked -> {
			mainScreenSetting.setGridVisible(checked);
		});
		view.addGridSmallButtonListener(gridDivNumPresentationLogic::makeGridSizeHalf);
		view.addGridLargeButtonListener(gridDivNumPresentationLogic::makeGridSizeTwiceLarge);
		view.addGridChangeButtonListener(gridDivNumPresentationLogic::updateGridDivNum);

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
		var frame = subFrameFactory.createFoldabilityFrame((FrameView) view.getTopLevelView());
		var presenter = subFramePresenterFactory.createFoldabilityCheckFrameViewPresenter(
				frame,
				paintContext.getCreasePattern(),
				paintContext.getPointEps());

		presenter.setViewVisible(true);
	}

	private void showCheckerWindow(final OrigamiModel origamiModel, final EstimationResultRules estimationRules) {
		var frame = subFrameFactory.createFoldabilityFrame((FrameView) view.getTopLevelView());
		var presenter = subFramePresenterFactory.createFoldabilityCheckFrameViewPresenter(
				frame,
				paintContext.getCreasePattern(),
				origamiModel,
				estimationRules,
				paintContext.getPointEps());

		presenter.setViewVisible(true);
	}

	private void computeModels() {
		var modelComputation = computationFacadeFactory.createModelComputationFacade(
				view,
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

		var modelViewPresenter = subFramePresenterFactory.createModelViewFramePresenter(
				modelViewFrame,
				origamiModels,
				paintContext.getPointEps());
		modelViewPresenter.setViewVisible(true);

		EstimationResultFrameView resultFrame = null;

		if (getComputationType().isLayerOrdering()) {
			if (!computationResult.allGloballyFlatFoldable()) {
				// wrong crease pattern exists.
				view.showNoAnswerMessage();
				logger.debug("estimation rules: {}", computationResult.getEstimationResultRules());
				showCheckerWindow(
						computationResult.getMergedOrigamiModel(),
						computationResult.getEstimationResultRules());
			} else {
				logger.info("foldable layer layout is found.");

				resultFrame = subFrameFactory.createResultFrame(parent);

				resultFrame.setColors(
						view.getEstimationResultFrontColor(),
						view.getEstimationResultBackColor());
				resultFrame.setSaveColorsListener(view.getEstimationResultSaveColorsListener());
				// resultFrame.repaint();

				var resultFramePresenter = subFramePresenterFactory.createEstimationResultFramePresenter(
						resultFrame,
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

	private void putModelIndexChangeListener(final ModelViewFrameView modelViewFrame,
			final EstimationResultFrameView resultFrame) {
		modelIndexChangeListenerPutter.put(modelViewFrame, resultFrame);
	}
}
