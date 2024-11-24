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

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.application.main.DocFileAccess;
import oripa.file.FileHistory;
import oripa.gui.presenter.file.FileSelectionResult;
import oripa.gui.presenter.file.UserAction;
import oripa.gui.presenter.main.MainDialogPresenterFactory;
import oripa.gui.view.main.MainFrameView;
import oripa.persistence.dao.DataAccessException;
import oripa.persistence.dao.FileType;
import oripa.persistence.doc.Doc;
import oripa.project.Project;
import oripa.util.file.FileFactory;

/**
 * @author OUCHI Koji
 *
 */
public class MainFrameFilePresentationLogic {
	private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final MainFrameView view;

	private final MainDialogPresenterFactory dialogPresenterFactory;
	private final FileAccessPresentationLogic fileAccessPresentationLogic;
	private final Project project;

	private final DocFileAccess dataFileAccess;
	private final FileHistory fileHistory;
	private final FileFactory fileFactory;

	public MainFrameFilePresentationLogic(
			final MainFrameView view,
			final MainDialogPresenterFactory dialogPresenterFactory,
			final FileAccessPresentationLogic fileAccessPresentationLogic,
			final Project project,
			final DocFileAccess dataFileAccess,
			final FileHistory fileHistory,
			final FileFactory fileFactory) {
		this.view = view;
		this.dialogPresenterFactory = dialogPresenterFactory;
		this.fileAccessPresentationLogic = fileAccessPresentationLogic;
		this.project = project;
		this.dataFileAccess = dataFileAccess;
		this.fileHistory = fileHistory;
		this.fileFactory = fileFactory;
	}

	public void modifySavingActions() {
		dataFileAccess.setupFOLDConfigForSaving();
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

		var presenter = dialogPresenterFactory.createDocFileSelectionPresenter(
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
		var presenter = dialogPresenterFactory.createDocFileSelectionPresenter(
				view,
				dataFileAccess.getFileSelectionService());

		FileSelectionResult<Doc> selection;
		try {
			selection = presenter.saveFileWithModelCheck(
					fileHistory.getLastDirectory(),
					type, view::showModelBuildFailureDialog);
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
		var selection = dialogPresenterFactory.createDocFileSelectionPresenter(
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

	public void importFileUsingGUI() {
		try {
			var selection = dialogPresenterFactory.createDocFileSelectionPresenter(
					view,
					dataFileAccess.getFileSelectionService())
					.loadUsingGUI(fileHistory.getLastPath());

			if (selection.action() == UserAction.CANCELED) {
				return;
			}

			fileAccessPresentationLogic.importFile(selection.path());

		} catch (DataAccessException | IllegalArgumentException e) {
			view.showLoadFailureErrorMessage(e);
		}

	}

}
