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
package oripa.gui.presenter.main.logic;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
import oripa.domain.creasepattern.CreasePattern;
import oripa.domain.fold.EstimationResultRules;
import oripa.domain.fold.FoldedModel;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.domain.paint.PaintContext;
import oripa.gui.presenter.main.PainterScreenPresenter;
import oripa.gui.presenter.main.logic.ModelComputationFacade.ComputationResult;
import oripa.gui.presenter.main.logic.ModelComputationFacade.ComputationType;
import oripa.gui.view.FrameView;
import oripa.gui.view.estimation.EstimationResultFrameView;
import oripa.gui.view.main.SubFrameFactory;
import oripa.gui.view.main.UIPanelView;
import oripa.gui.view.model.ModelViewFrameView;

/**
 * @author OUCHI Koji
 *
 */
public class SubFramePresentationLogic {
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final UIPanelView view;
	private final PainterScreenPresenter mainScreenPresenter;
	private final ModelIndexChangeSupport modelIndexChangeSupport;
	private final SubFrameFactory subFrameFactory;

	private final SubFramePresenterFactory subFramePresenterFactory;

	private final ModelComputationFacadeFactory computationFacadeFactory;
	private ComputationResult computationResult;

	private final PaintContext paintContext;

	private String lastResultFilePath;

	@Inject
	public SubFramePresentationLogic(
			final UIPanelView view,
			final PainterScreenPresenter mainScreenPresenter,
			final ModelIndexChangeSupport modelIndexChangeSupport,
			final SubFrameFactory subFrameFactory,
			final SubFramePresenterFactory subFramePresenterFactory,
			final ModelComputationFacadeFactory computationFacadeFactory,
			final PaintContext paintContext) {

		this.view = view;
		this.mainScreenPresenter = mainScreenPresenter;
		this.modelIndexChangeSupport = modelIndexChangeSupport;
		this.subFrameFactory = subFrameFactory;
		this.subFramePresenterFactory = subFramePresenterFactory;
		this.computationFacadeFactory = computationFacadeFactory;
		this.paintContext = paintContext;

	}

	/**
	 * display window with foldability checks
	 */
	public void showCheckerWindow() {
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

	public void computeModels() {
		var modelComputation = computationFacadeFactory.createModelComputationFacade(
				view,
				paintContext.getPointEps());

		CreasePattern creasePattern = paintContext.getCreasePattern();

		var origamiModels = modelComputation.buildOrigamiModels(creasePattern);

		try {
			computationResult = modelComputation.computeModels(
					origamiModels,
					getComputationType());
		} catch (Exception e) {
			computationResult = null;
			throw e;
		}
	}

	private ComputationType getComputationType() {
		return ComputationType.fromString(view.getComputationType()).get();
	}

	public void showFoldedModelWindows() {
		var parent = (FrameView) view.getTopLevelView();

		if (!computationResult.allLocallyFlatFoldable()) {
			view.showLocalFlatFoldabilityViolationMessage();
			showCheckerWindow();
			return;
		}

		var origamiModels = computationResult.origamiModels();
		var foldedModels = computationResult.foldedModels();

		ModelViewFrameView modelViewFrame = subFrameFactory.createModelViewFrame(parent);

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

		putModelIndexChangeListener(modelViewFrame, resultFrame, foldedModels);

		modelViewPresenter.setViewVisible(true);
	}

	private void putModelIndexChangeListener(final ModelViewFrameView modelViewFrame,
			final EstimationResultFrameView resultFrame,
			final List<FoldedModel> foldedModels) {
		modelIndexChangeSupport.removeListeners();

		if (modelViewFrame == null || resultFrame == null) {
			return;
		}
		// bind change event from view to presenter support
		modelViewFrame.putModelIndexChangeListener(modelIndexChangeSupport,
				e -> {
					logger.debug("modelViewFrame model index change: {} -> {}", e.getOldValue(), e.getNewValue());
					modelIndexChangeSupport.setIndex((int) e.getNewValue());
				});
		resultFrame.putModelIndexChangeListener(modelIndexChangeSupport,
				e -> {
					logger.debug("resultFrame model index change: {} -> {}", e.getOldValue(), e.getNewValue());
					modelIndexChangeSupport.setIndex((int) e.getNewValue());
				});

		// bind change event from presenter support to view/presenter
		modelIndexChangeSupport.putListener(modelViewFrame, e -> {
			modelViewFrame.selectModel((Integer) e.getNewValue());
		});
		modelIndexChangeSupport.putListener(resultFrame, e -> {
			resultFrame.selectModel((Integer) e.getNewValue());
		});
		modelIndexChangeSupport.putListener(mainScreenPresenter, e -> {
			var domain = foldedModels.get((int) e.getNewValue()).origamiModel().createPaperDomain();
			logger.debug("model index change: update domain {}", domain);
			mainScreenPresenter.setPaperDomainOfModel(domain);
		});

		modelIndexChangeSupport.setIndex(0);
	}

}
