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
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

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
import oripa.gui.view.main.DialogWhileFolding;
import oripa.gui.view.main.MainDialogService;
import oripa.gui.view.main.UIPanelView;
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
			final MainScreenSetting mainScreenSetting,
			final CutModelOutlinesHolder cutOutlinesHolder) {
		this.view = view;

		setting = view.getUIPanelSetting();
		valueSetting = setting.getValueSetting();
		screenUpdater = view.getScreenUpdater();
		paintContext = view.getPaintContext();
		viewContext = view.getViewContext();

		stateManager = view.getStateManager();

		actionHolder = view.getMouseActionHolder();
		mainFrameSetting = view.getMainFrameSetting();

		this.mainScreenSetting = mainScreenSetting;
		this.cutOutlinesHolder = cutOutlinesHolder;

		originHolder = mainScreenSetting.getSelectionOriginHolder();

		addListeners();
	}

	public void setChildFrameManager(final ChildFrameManager manager) {
		childFrameManager = manager;
	}

	private void addListeners() {
		view.addGridSmallButtonListener(this::makeGridSizeHalf);
		view.addGridLargeButtonListener(this::makeGridSizeTwiceLarge);
		view.addGridChangeButtonListener(this::updateGridDivNum);

		view.addBuildButtonListener(this::showFoldedModelWindows);

		PaintActionSetterFactory setterFactory = new PaintActionSetterFactory(
				actionHolder, screenUpdater, paintContext);

		var stateFactory = new PaintBoundStateFactory(stateManager, mainFrameSetting, setting, originHolder);

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

		view.addLengthButtonListener(
				setterFactory.create(new LengthMeasuringAction(valueSetting)));
		view.addAngleButtonListener(
				setterFactory.create(new AngleMeasuringAction(valueSetting)));
		view.addLengthTextFieldListener(
				new LengthValueInputListener(valueSetting));
		view.addAngleTextFieldListener(
				new AngleValueInputListener(valueSetting));

		var addVertexState = stateFactory.create(view.asPanel(), actionHolder, paintContext, screenUpdater,
				StringID.ADD_VERTEX_ID);
		view.addEditModeAddVertexButtonListener(addVertexState::performActions, screenUpdater.getKeyListener());

		var deleteVertexState = stateFactory.create(view.asPanel(), actionHolder, paintContext, screenUpdater,
				StringID.DELETE_VERTEX_ID);
		view.addEditModeDeleteVertexButtonListener(deleteVertexState::performActions, screenUpdater.getKeyListener());

		view.addAngleStepComboListener(step -> paintContext.setAngleStep(step));
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
	 * open window with folded model
	 */
	private void showFoldedModelWindows() {

		var frame = (JFrame) view.asPanel().getTopLevelAncestor();

		// modal dialog while folding
		var dialogWhileFolding = new DialogWhileFolding(frame, resources);

		var worker = new SwingWorker<List<JFrame>, Void>() {
			@Override
			protected List<JFrame> doInBackground() throws Exception {
				CreasePattern creasePattern = paintContext.getCreasePattern();

				var parent = view.asPanel();

				var windowOpener = new FoldedModelWindowOpener(parent, childFrameManager,
						// ask if ORIPA should try to remove duplication.
						() -> dialogService.showCleaningUpDuplicationDialog(parent) == JOptionPane.YES_OPTION,
						// clean up the crease pattern
						() -> dialogService.showCleaningUpMessage(parent),
						// folding failed.
						() -> dialogService.showFoldFailureMessage(parent),
						// no answer is found.
						() -> dialogService.showNoAnswerMessage(parent));

				try {
					return windowOpener.showFoldedModelWindows(
							creasePattern,
							cutOutlinesHolder,
							mainScreenSetting,
							view.getFullEstimation(),
							view.getEstimationResultFrontColor(),
							view.getEstimationResultBackColor(),
							view.getEstimationResultSaveColorsListener(),
							view.getPaperDomainOfModelChangeListener(),
							screenUpdater);
				} catch (Exception e) {
					logger.error("error when folding", e);
					Dialogs.showErrorDialog(parent,
							resources.getString(ResourceKey.ERROR, StringID.Error.DEFAULT_TITLE_ID), e);
				}
				return List.of();
			}

			@Override
			protected void done() {
				dialogWhileFolding.setVisible(false);
				dialogWhileFolding.dispose();

				// this action moves the main window to front.
				view.setBuildButtonEnabled(true);
			}
		};

		dialogWhileFolding.setWorker(worker);

		worker.execute();

		view.setBuildButtonEnabled(false);
		dialogWhileFolding.setVisible(true);

		try {
			var openedWindows = worker.get();
			// bring new windows to front.
			openedWindows.forEach(w -> w.setVisible(true));

		} catch (CancellationException | InterruptedException | ExecutionException e) {
			logger.info("folding failed or cancelled.", e);
			Dialogs.showErrorDialog(view.asPanel(),
					resources.getString(ResourceKey.ERROR, StringID.Error.DEFAULT_TITLE_ID), e);
		}
	}

}
