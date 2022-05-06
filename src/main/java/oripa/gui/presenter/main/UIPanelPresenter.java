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

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.appstate.CommandStatePopper;
import oripa.appstate.StateManager;
import oripa.domain.creasepattern.CreasePattern;
import oripa.domain.cutmodel.CutModelOutlinesHolder;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.byvalue.ValueSetting;
import oripa.domain.paint.copypaste.SelectionOriginHolder;
import oripa.gui.bind.state.PaintBoundStateFactory;
import oripa.gui.bind.state.action.PaintActionSetterFactory;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.creasepattern.MouseActionHolder;
import oripa.gui.presenter.creasepattern.byvalue.AngleMeasuringAction;
import oripa.gui.presenter.creasepattern.byvalue.AngleValueInputListener;
import oripa.gui.presenter.creasepattern.byvalue.LengthMeasuringAction;
import oripa.gui.presenter.creasepattern.byvalue.LengthValueInputListener;
import oripa.gui.presenter.main.ModelComputationFacade.ComputationResult;
import oripa.gui.view.estimation.EstimationResultFrame;
import oripa.gui.view.estimation.EstimationResultFrameFactory;
import oripa.gui.view.main.DialogWhileFolding;
import oripa.gui.view.main.MainDialogService;
import oripa.gui.view.main.UIPanelView;
import oripa.gui.view.model.ModelViewFrame;
import oripa.gui.view.model.ModelViewFrameFactory;
import oripa.gui.view.util.ChildFrameManager;
import oripa.gui.view.util.Dialogs;
import oripa.gui.viewsetting.ViewScreenUpdater;
import oripa.gui.viewsetting.main.MainFrameSetting;
import oripa.gui.viewsetting.main.MainScreenSetting;
import oripa.gui.viewsetting.main.uipanel.FromLineTypeItemListener;
import oripa.gui.viewsetting.main.uipanel.ToLineTypeItemListener;
import oripa.gui.viewsetting.main.uipanel.UIPanelSetting;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class UIPanelPresenter {
	private static final Logger logger = LoggerFactory.getLogger(UIPanelPresenter.class);

	UIPanelView view;

	private final ResourceHolder resources = ResourceHolder.getInstance();

	private final MainDialogService dialogService = new MainDialogService(resources);

	private final UIPanelSetting setting;
	private final ValueSetting valueSetting;

	final CutModelOutlinesHolder cutOutlinesHolder;
	final MainScreenSetting mainScreenSetting;

	private ChildFrameManager childFrameManager;

	private final MouseActionHolder actionHolder;
	private final StateManager<EditMode> stateManager;
	private final ViewScreenUpdater screenUpdater;
	private final PaintContext paintContext;
	private final CreasePatternViewContext viewContext;

	private final MainFrameSetting mainFrameSetting;
	private final SelectionOriginHolder originHolder;

	public UIPanelPresenter(final UIPanelView view,
			final StateManager<EditMode> stateManager,
			final ViewScreenUpdater screenUpdater,
			final MouseActionHolder actionHolder,
			final CreasePatternViewContext viewContext,
			final PaintContext paintContext,
			final CutModelOutlinesHolder cutOutlinesHolder,
			final MainFrameSetting mainFrameSetting,
			final MainScreenSetting mainScreenSetting) {
		this.view = view;

		setting = view.getUIPanelSetting();
		valueSetting = setting.getValueSetting();
		this.screenUpdater = screenUpdater;
		this.paintContext = paintContext;
		this.viewContext = viewContext;

		this.stateManager = stateManager;

		this.actionHolder = actionHolder;
		this.mainFrameSetting = mainFrameSetting;

		this.mainScreenSetting = mainScreenSetting;
		this.cutOutlinesHolder = cutOutlinesHolder;

		originHolder = mainScreenSetting.getSelectionOriginHolder();

		addListeners();

		view.initializeButtonSelection();
	}

	public void setChildFrameManager(final ChildFrameManager manager) {
		childFrameManager = manager;
	}

	private void addListeners() {
		PaintActionSetterFactory setterFactory = new PaintActionSetterFactory(
				actionHolder, screenUpdater, paintContext);

		var stateFactory = new PaintBoundStateFactory(stateManager, mainFrameSetting, setting, originHolder);

		// ------------------------------------------------------------
		// edit mode buttons

		view.addEditModeInputLineButtonListener(
				new CommandStatePopper<EditMode>(stateManager, EditMode.INPUT),
				screenUpdater.getKeyListener());

		view.addEditModeLineSelectionButtonListener(
				new CommandStatePopper<EditMode>(stateManager, EditMode.SELECT),
				screenUpdater.getKeyListener());

		var deleteLineState = stateFactory.create(view.asPanel(), actionHolder, paintContext, screenUpdater,
				StringID.DELETE_LINE_ID);
		view.addEditModeDeleteLineButtonListener(deleteLineState::performActions, screenUpdater.getKeyListener());

		var lineTypeState = stateFactory.create(view.asPanel(), actionHolder, paintContext, screenUpdater,
				StringID.CHANGE_LINE_TYPE_ID);
		view.addEditModeLineTypeButtonListener(lineTypeState::performActions, screenUpdater.getKeyListener());

		view.addAlterLineComboFromListener(new FromLineTypeItemListener(setting));
		view.addAlterLineComboToListener(new ToLineTypeItemListener(setting));

		var addVertexState = stateFactory.create(view.asPanel(), actionHolder, paintContext, screenUpdater,
				StringID.ADD_VERTEX_ID);
		view.addEditModeAddVertexButtonListener(addVertexState::performActions, screenUpdater.getKeyListener());

		var deleteVertexState = stateFactory.create(view.asPanel(), actionHolder, paintContext, screenUpdater,
				StringID.DELETE_VERTEX_ID);
		view.addEditModeDeleteVertexButtonListener(deleteVertexState::performActions, screenUpdater.getKeyListener());

		// ------------------------------------------------------------
		// selection command buttons

		var selectLineState = stateFactory.create(view.asPanel(), actionHolder, paintContext, screenUpdater,
				StringID.SELECT_LINE_ID);
		view.addSelectionButtonListener(selectLineState::performActions, screenUpdater.getKeyListener());

		var enlargementState = stateFactory.create(view.asPanel(), actionHolder, paintContext, screenUpdater,
				StringID.ENLARGE_ID);
		view.addEnlargementButtonListener(enlargementState::performActions, screenUpdater.getKeyListener());

		// ------------------------------------------------------------
		// input command buttons

		var directVState = stateFactory.create(view.asPanel(), actionHolder, paintContext, screenUpdater,
				StringID.DIRECT_V_ID);
		view.addLineInputDirectVButtonListener(directVState::performActions, screenUpdater.getKeyListener());

		var onVState = stateFactory.create(view.asPanel(), actionHolder, paintContext, screenUpdater,
				StringID.ON_V_ID);
		view.addLineInputOnVButtonListener(onVState::performActions, screenUpdater.getKeyListener());

		var verticalLineState = stateFactory.create(view.asPanel(), actionHolder, paintContext, screenUpdater,
				StringID.VERTICAL_ID);
		view.addLineInputVerticalLineButtonListener(verticalLineState::performActions, screenUpdater.getKeyListener());

		var angleBisectorState = stateFactory.create(view.asPanel(), actionHolder, paintContext, screenUpdater,
				StringID.BISECTOR_ID);
		view.addLineInputAngleBisectorButtonListener(angleBisectorState::performActions,
				screenUpdater.getKeyListener());

		var triangleSplitState = stateFactory.create(view.asPanel(), actionHolder, paintContext, screenUpdater,
				StringID.TRIANGLE_ID);
		view.addLineInputTriangleSplitButtonListener(triangleSplitState::performActions,
				screenUpdater.getKeyListener());

		var symmetricState = stateFactory.create(view.asPanel(), actionHolder, paintContext, screenUpdater,
				StringID.SYMMETRIC_ID);
		view.addLineInputSymmetricButtonListener(symmetricState::performActions, screenUpdater.getKeyListener());

		var mirrorState = stateFactory.create(view.asPanel(), actionHolder, paintContext, screenUpdater,
				StringID.MIRROR_ID);
		view.addLineInputMirrorButtonListener(mirrorState::performActions, screenUpdater.getKeyListener());

		var byValueState = stateFactory.create(view.asPanel(), actionHolder, paintContext, screenUpdater,
				StringID.BY_VALUE_ID);
		view.addLineInputByValueButtonListener(byValueState::performActions, screenUpdater.getKeyListener());

		view.addLengthButtonListener(
				setterFactory.create(new LengthMeasuringAction(valueSetting)));
		view.addAngleButtonListener(
				setterFactory.create(new AngleMeasuringAction(valueSetting)));
		view.addLengthTextFieldListener(
				new LengthValueInputListener(valueSetting));
		view.addAngleTextFieldListener(
				new AngleValueInputListener(valueSetting));

		var pbisecState = stateFactory.create(view.asPanel(), actionHolder, paintContext, screenUpdater,
				StringID.PERPENDICULAR_BISECTOR_ID);
		view.addLineInputPBisectorButtonListener(pbisecState::performActions, screenUpdater.getKeyListener());

		var angleSnapState = stateFactory.create(view.asPanel(), actionHolder, paintContext, screenUpdater,
				StringID.ANGLE_SNAP_ID);
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
		view.addBuildButtonListener(this::showFoldedModelWindows);

	}

	private void makeGridSizeHalf() {
		if (paintContext.getGridDivNum() < 65) {
			paintContext.setGridDivNum(paintContext.getGridDivNum() * 2);
			view.setGridDivNum(paintContext.getGridDivNum());

			screenUpdater.updateScreen();
		}
	}

	private void makeGridSizeTwiceLarge() {
		if (paintContext.getGridDivNum() > 3) {
			paintContext.setGridDivNum(paintContext.getGridDivNum() / 2);
			view.setGridDivNum(paintContext.getGridDivNum());

			screenUpdater.updateScreen();
		}
	}

	private void updateGridDivNum(final int gridDivNum) {
		paintContext.setGridDivNum(gridDivNum);

		screenUpdater.updateScreen();
	}

	/**
	 * display window with foldability checks
	 */
	private void showCheckerWindow() {
		var windowOpener = new CheckerWindowOpener(view.asPanel(), childFrameManager);
		windowOpener.showCheckerWindow(paintContext.getCreasePattern(), viewContext.isZeroLineWidth());
	}

	/**
	 * open window with folded model
	 */
	private void showFoldedModelWindows() {

		var frame = (JFrame) view.asPanel().getTopLevelAncestor();
		var parent = view.asPanel();

		// modal dialog while folding
		var dialogWhileFolding = new DialogWhileFolding(frame, resources);
		var modelComputation = new ModelComputationFacade(
				// ask if ORIPA should try to remove duplication.
				() -> dialogService.showCleaningUpDuplicationDialog(parent) == JOptionPane.YES_OPTION,
				// clean up the crease pattern
				() -> dialogService.showCleaningUpMessage(parent),
				// folding failed.
				() -> dialogService.showFoldFailureMessage(parent));

		var worker = new SwingWorker<ModelComputationFacade.ComputationResult, Void>() {
			@Override
			protected ModelComputationFacade.ComputationResult doInBackground() throws Exception {
				CreasePattern creasePattern = paintContext.getCreasePattern();

				// FIXME should not access swing component in
				// doInBackground().
				try {
					return modelComputation.computeModels(
							creasePattern,
							view.getFullEstimation());
				} catch (Exception e) {
					logger.error("error when folding", e);
					Dialogs.showErrorDialog(parent,
							resources.getString(ResourceKey.ERROR, StringID.Error.DEFAULT_TITLE_ID), e);
				}
				return null;
			}

			@Override
			protected void done() {

			}
		};

//		dialogWhileFolding.setWorker(worker);

		worker.addPropertyChangeListener(e -> {
			if ("state".equals(e.getPropertyName())
					&& SwingWorker.StateValue.DONE == e.getNewValue()) {
				dialogWhileFolding.setVisible(false);
				dialogWhileFolding.dispose();
			}
		});

		worker.execute();

		view.setBuildButtonEnabled(false);
		dialogWhileFolding.setVisible(true);

		try {
			var result = worker.get();

			// this action moves the main window to front.
			view.setBuildButtonEnabled(true);

			showFoldedModelWindows(parent, result);

		} catch (CancellationException | InterruptedException | ExecutionException e) {
			logger.info("folding failed or cancelled.", e);
			Dialogs.showErrorDialog(view.asPanel(),
					resources.getString(ResourceKey.ERROR, StringID.Error.DEFAULT_TITLE_ID), e);
		}
	}

	private void showFoldedModelWindows(final JComponent parent, final ComputationResult result) {
		var origamiModels = result.getOrigamiModels();
		var foldedModels = result.getFoldedModels();

		ModelViewFrameFactory modelViewFactory = new ModelViewFrameFactory(
				mainScreenSetting,
				childFrameManager);
		ModelViewFrame modelViewFrame = modelViewFactory.createFrame(parent, origamiModels,
				cutOutlinesHolder, screenUpdater::updateScreen, view.getPaperDomainOfModelChangeListener());

		modelViewFrame.repaint();

		EstimationResultFrame resultFrame = null;

		if (view.getFullEstimation()) {

			if (result.countFoldablePatterns() == 0) {
				// no answer is found.
				dialogService.showNoAnswerMessage(parent);
				return;
			} else {
				logger.info("foldable layer layout is found.");

				EstimationResultFrameFactory resultFrameFactory = new EstimationResultFrameFactory(
						childFrameManager);
				resultFrame = resultFrameFactory.createFrame(parent, foldedModels);

				resultFrame.setColors(
						view.getEstimationResultFrontColor(),
						view.getEstimationResultBackColor());
				resultFrame.setSaveColorsListener(view.getEstimationResultSaveColorsListener());
				resultFrame.repaint();

				resultFrame.setVisible(true);
			}
		}

		modelViewFrame.setVisible(true);

		putModelIndexChangeListener(modelViewFrame, resultFrame);
	}

	public void putModelIndexChangeListener(final ModelViewFrame modelViewFrame,
			final EstimationResultFrame resultFrame) {
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
