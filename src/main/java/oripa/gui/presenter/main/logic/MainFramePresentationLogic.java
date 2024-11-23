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
import java.lang.invoke.MethodHandles;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.appstate.ApplicationState;
import oripa.file.FileHistory;
import oripa.geom.RectangleDomain;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.main.PainterScreenPresenter;
import oripa.gui.presenter.main.UIPanelPresenter;
import oripa.gui.presenter.plugin.GraphicMouseActionPlugin;
import oripa.gui.view.main.MainFrameView;
import oripa.gui.view.util.ColorUtil;
import oripa.persistence.dao.FileType;
import oripa.persistence.doc.Doc;
import oripa.project.Project;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;

/**
 * @author OUCHI Koji
 *
 */
public class MainFramePresentationLogic {
	private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final MainFrameView view;

	private final PainterScreenPresenter screenPresenter;
	private final UIPanelPresenter uiPanelPresenter;

	private final MainFrameFilePresentationLogic mainFrameFilePresentationLogic;
	private final UndoRedoPresentationLogic undoRedoPresentationLogic;
	private final ClearActionPresentationLogic clearActionPresentationLogic;
	private final IniFileAccessPresentationLogic iniFileAccessPresentationLogic;

	private final Project project;

	private final FileHistory fileHistory;

	private final ResourceHolder resourceHolder;

	public MainFramePresentationLogic(
			final MainFrameView view,
			final PainterScreenPresenter screenPresenter,
			final UIPanelPresenter uiPanelPresenter,
			final MainFrameFilePresentationLogic mainFrameFilePresentationLogic,
			final ClearActionPresentationLogic clearActionPresentationLogic,
			final UndoRedoPresentationLogic undoRedoPresentationLogic,
			final IniFileAccessPresentationLogic iniFileAccessPresentationLogic,
			final Project project,
			final FileHistory fileHistory,
			final ResourceHolder resourceHolder) {

		this.view = view;

		this.mainFrameFilePresentationLogic = mainFrameFilePresentationLogic;
		this.clearActionPresentationLogic = clearActionPresentationLogic;
		this.iniFileAccessPresentationLogic = iniFileAccessPresentationLogic;

		this.project = project;

		this.fileHistory = fileHistory;

		this.undoRedoPresentationLogic = undoRedoPresentationLogic;

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
		screenPresenter.updateScreen();
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
		undoRedoPresentationLogic.undo();
	}

	public void redo() {
		undoRedoPresentationLogic.redo();
	}

	public void modifySavingActions() {
		mainFrameFilePresentationLogic.modifySavingActions();
	}

	/**
	 * saves project without opening a dialog
	 */
	public String saveFileToCurrentPath(final FileType<Doc> type) {
		return mainFrameFilePresentationLogic.saveFileToCurrentPath(type);

	}

	/**
	 * save file without origami model check
	 */
	public String saveFileUsingGUI(@SuppressWarnings("unchecked") final FileType<Doc>... types) {
		return mainFrameFilePresentationLogic.saveFileUsingGUI(types);
	}

	/**
	 * Open Save File As Dialogue for specific file types {@code type}. Runs a
	 * model check before saving.
	 */
	public void exportFileUsingGUIWithModelCheck(final FileType<Doc> type) {
		mainFrameFilePresentationLogic.exportFileUsingGUIWithModelCheck(type);
	}

	/**
	 * This method opens the file dialog and load the selected file.
	 */
	public void loadFileUsingGUI() {
		mainFrameFilePresentationLogic.loadFileUsingGUI();
	}

	/**
	 * This method tries to read data from the path.
	 *
	 * @param filePath
	 * @return file path for loaded file. {@code null} if loading is not done.
	 */
	public String loadFile(final String filePath) {
		return mainFrameFilePresentationLogic.loadFile(filePath);
	}

	public void importFileUsingGUI(final ApplicationState<EditMode> state) {
		mainFrameFilePresentationLogic.importFileUsingGUI(state);
	}

	public void setEstimationResultSaveColors(final Color front, final Color back) {
		var property = project.getProperty();
		property.putFrontColorCode(ColorUtil.convertColorToCode(front));
		property.putBackColorCode(ColorUtil.convertColorToCode(back));
	}

}
