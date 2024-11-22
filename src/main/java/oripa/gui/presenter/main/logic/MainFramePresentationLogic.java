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

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.application.main.DocFileAccess;
import oripa.appstate.ApplicationState;
import oripa.domain.cutmodel.CutModelOutlinesHolder;
import oripa.domain.paint.PaintContext;
import oripa.file.FileHistory;
import oripa.geom.RectangleDomain;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.creasepattern.MouseActionHolder;
import oripa.gui.presenter.file.FileSelectionResult;
import oripa.gui.presenter.file.UserAction;
import oripa.gui.presenter.main.MainComponentPresenterFactory;
import oripa.gui.presenter.main.PainterScreenPresenter;
import oripa.gui.presenter.main.UIPanelPresenter;
import oripa.gui.presenter.plugin.GraphicMouseActionPlugin;
import oripa.gui.view.ViewScreenUpdater;
import oripa.gui.view.main.MainFrameView;
import oripa.gui.view.util.ColorUtil;
import oripa.persistence.dao.DataAccessException;
import oripa.persistence.dao.FileType;
import oripa.persistence.doc.Doc;
import oripa.project.Project;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;
import oripa.util.file.FileFactory;

/**
 * @author OUCHI Koji
 *
 */
public class MainFramePresentationLogic {
	private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final MainFrameView view;

	private final PainterScreenPresenter screenPresenter;
	private final UIPanelPresenter uiPanelPresenter;
	private final MainComponentPresenterFactory componentPresenterFactory;

	private final ClearActionPresentationLogic clearActionPresentationLogic;
	private final FileAccessPresentationLogic fileAccessPresentationLogic;
	private final IniFileAccessPresentationLogic iniFileAccessPresentationLogic;

	private final ViewScreenUpdater screenUpdater;
	private final MouseActionHolder mouseActionHolder;

	private final Project project;

	private final PaintContext paintContext;

	private final DocFileAccess dataFileAccess;
	private final FileHistory fileHistory;
	private final FileFactory fileFactory;

	private final ResourceHolder resourceHolder;

	public MainFramePresentationLogic(
			final MainFrameView view,
			final ViewScreenUpdater screenUpdater,
			final MouseActionHolder mouseActionHolder,
			final PainterScreenPresenter screenPresenter,
			final UIPanelPresenter uiPanelPresenter,
			final MainComponentPresenterFactory componentPresenterFactory,
			final ClearActionPresentationLogic clearActionPresentationLogic,
			final FileAccessPresentationLogic fileAccessPresentationLogic,
			final IniFileAccessPresentationLogic iniFileAccessPresentationLogic,
			final Project project,
			final PaintContext paintContext,
			final CutModelOutlinesHolder cutModelOutlinesHolder,
			final FileHistory fileHistory,
			final DocFileAccess dataFileAccess,
			final FileFactory fileFactory,
			final ResourceHolder resourceHolder) {

		this.view = view;

		this.componentPresenterFactory = componentPresenterFactory;

		this.clearActionPresentationLogic = clearActionPresentationLogic;
		this.fileAccessPresentationLogic = fileAccessPresentationLogic;
		this.iniFileAccessPresentationLogic = iniFileAccessPresentationLogic;

		this.project = project;
		this.paintContext = paintContext;

		this.fileHistory = fileHistory;
		this.dataFileAccess = dataFileAccess;
		this.fileFactory = fileFactory;

		this.screenUpdater = screenUpdater;

		this.mouseActionHolder = mouseActionHolder;

		this.uiPanelPresenter = uiPanelPresenter;
		this.screenPresenter = screenPresenter;

		this.resourceHolder = resourceHolder;
	}

	public void setPaperDomainOfModel(final RectangleDomain domain) {
		screenPresenter.setPaperDomainOfModel(domain);
	}

	public void updateCameraCenter() {
		screenPresenter.updateCameraCenter();
	}

	public void updateScreen() {
		screenUpdater.updateScreen();
	}

	public void addPlugins(final List<GraphicMouseActionPlugin> plugins) {
		uiPanelPresenter.addPlugins(plugins);
	}

	public void updateValuePanelFractionDigits() {
		uiPanelPresenter.updateValuePanelFractionDigits();
	}

	public void updateMRUFilesMenuItem(final int index) {
		var histories = fileHistory.getHistory();
		if (index < histories.size()) {
			view.setMRUFilesMenuItem(index, histories.get(index));
		} else {
			view.setMRUFilesMenuItem(index, "");
		}
	}

	public void exit(final Runnable doExit) {
		saveIniFile();
		doExit.run();
	}

	public void saveIniFile() {
		try {
			iniFileAccessPresentationLogic.saveIniFile();
		} catch (Exception e) {
			logger.error("error when saving ini data", e);
			view.showSaveIniFileFailureErrorMessage(e);
		}
	}

	public void loadIniFile() {
		iniFileAccessPresentationLogic.loadIniFile();
	}

	public void clear() {
		clearActionPresentationLogic.clear();
		updateTitleText();
	}

	public void updateTitleText() {

		view.setFileNameToTitle(getTitleText());
	}

	private String getTitleText() {
		var defaultFileName = resourceHolder.getString(ResourceKey.DEFAULT, StringID.Default.FILE_NAME_ID);
		var fileName = project.getDataFileName().orElse(defaultFileName);

		return fileName.isEmpty() ? defaultFileName : fileName;
	}

	/**
	 * Update file menu. Do nothing if the given {@code filePath} is null or
	 * wrong.
	 *
	 * @param filePath
	 */
	public void updateMenu() {
		var filePath = project.getDataFilePath();

		if (!project.isProjectFile()) {
			logger.debug("updating menu is canceled: {}", filePath);
			return;
		}

		fileHistory.useFile(filePath);

		view.buildFileMenu();
	}

	public void undo() {
		mouseActionHolder.getMouseAction().orElseThrow().undo(paintContext);
		screenUpdater.updateScreen();
	}

	public void redo() {
		mouseActionHolder.getMouseAction().orElseThrow().redo(paintContext);
		screenUpdater.updateScreen();
	}

	public void modifySavingActions() {
		dataFileAccess.setupFOLDConfigForSaving(paintContext.getPointEps());
	}

	/**
	 * saves project without opening a dialog
	 */
	public String saveFileToCurrentPath(final FileType<Doc> type) {
		var filePath = project.getDataFilePath();

		try {
			return fileAccessPresentationLogic.saveFile(filePath, type);
		} catch (DataAccessException | IllegalArgumentException e) {
			return filePath;
		}

	}

	/**
	 * save file without origami model check
	 */
	public String saveFileUsingGUI(@SuppressWarnings("unchecked") final FileType<Doc>... types) {
		var directory = fileHistory.getLastDirectory();
		var fileName = project.getDataFileName().orElse("newFile.opx");

		logger.debug("saveFilelUsingGUI at {}, {}", directory, fileName);

		File defaultFile = fileFactory.create(
				directory,
				fileName);

		var filePath = defaultFile.getPath();

		var presenter = componentPresenterFactory.createDocFileSelectionPresenter(
				view, dataFileAccess.getFileSelectionService());

		var selection = (types == null || types.length == 0) ? presenter.saveUsingGUI(filePath)
				: presenter.saveUsingGUI(filePath, List.of(types));

		if (selection.action() == UserAction.CANCELED) {
			return project.getDataFilePath();
		}

		try {
			return fileAccessPresentationLogic.saveFile(selection.path(), selection.type());
		} catch (DataAccessException | IllegalArgumentException e) {
			return project.getDataFilePath();
		}

	}

	/**
	 * Open Save File As Dialogue for specific file types {@code type}. Runs a
	 * model check before saving.
	 */
	public void exportFileUsingGUIWithModelCheck(final FileType<Doc> type) {
		var presenter = componentPresenterFactory.createDocFileSelectionPresenter(
				view,
				dataFileAccess.getFileSelectionService());

		FileSelectionResult<Doc> selection;
		try {
			selection = presenter.saveFileWithModelCheck(
					paintContext.getCreasePattern(),
					fileHistory.getLastDirectory(),
					type, view, view::showModelBuildFailureDialog, paintContext.getPointEps());
		} catch (IOException e) {
			logger.error("error", e);
			view.showSaveFailureErrorMessage(e);
			return;
		}

		if (selection.action() == UserAction.CANCELED) {
			return;
		}

		fileAccessPresentationLogic.saveFile(selection.path(), type);

	}

	/**
	 * This method opens the file dialog and load the selected file.
	 */
	public void loadFileUsingGUI() {
		var selection = componentPresenterFactory.createDocFileSelectionPresenter(
				view,
				dataFileAccess.getFileSelectionService())
				.loadUsingGUI(fileHistory.getLastPath());

		if (selection.action() == UserAction.CANCELED) {
			return;
		}

		fileAccessPresentationLogic.loadFile(selection.path());
	}

	/**
	 * This method tries to read data from the path.
	 *
	 * @param filePath
	 * @return file path for loaded file. {@code null} if loading is not done.
	 */
	public String loadFile(final String filePath) {
		return fileAccessPresentationLogic.loadFile(filePath);
	}

	public void importFileUsingGUI(final ApplicationState<EditMode> state) {
		try {
			var selection = componentPresenterFactory.createDocFileSelectionPresenter(
					view,
					dataFileAccess.getFileSelectionService())
					.loadUsingGUI(fileHistory.getLastPath());

			if (selection.action() == UserAction.CANCELED) {
				return;
			}

			fileAccessPresentationLogic.importFile(selection.path());

			state.performActions();
		} catch (DataAccessException | IllegalArgumentException e) {
			view.showLoadFailureErrorMessage(e);
		}

	}

	public void setEstimationResultSaveColors(final Color front, final Color back) {
		var property = project.getProperty();
		property.putFrontColorCode(ColorUtil.convertColorToCode(front));
		property.putBackColorCode(ColorUtil.convertColorToCode(back));
	}

}
