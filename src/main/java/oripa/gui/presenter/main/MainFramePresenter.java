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

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.paint.PaintContext;
import oripa.gui.presenter.main.logic.MainFramePaintMenuListenerFactory;
import oripa.gui.presenter.main.logic.MainFramePresentationLogic;
import oripa.gui.presenter.plugin.GraphicMouseActionPlugin;
import oripa.gui.view.main.MainFrameView;
import oripa.persistence.dao.FileType;
import oripa.persistence.doc.Doc;
import oripa.persistence.doc.DocFileTypes;
import oripa.project.Project;

/**
 * @author OUCHI Koji
 *
 */
public class MainFramePresenter {
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final MainFrameView view;

	private final MainFramePresentationLogic presentationLogic;
	private final MainDialogPresenterFactory dialogPresenterFactory;

	private final Project project;

	private final PaintContext paintContext;
	private final MainFramePaintMenuListenerFactory paintMenuListenerFactory;

	public MainFramePresenter(
			final MainFrameView view,
			final MainFramePresentationLogic presentationLogic,
			final MainDialogPresenterFactory dialogPresenterFactory,
			final MainFramePaintMenuListenerFactory paintMenuListenerFactory,
			final Project project,
			final PaintContext paintContext,
			final List<GraphicMouseActionPlugin> plugins) {

		this.view = view;

		this.presentationLogic = presentationLogic;
		this.dialogPresenterFactory = dialogPresenterFactory;

		this.project = project;
		this.paintContext = paintContext;

		this.paintMenuListenerFactory = paintMenuListenerFactory;

		presentationLogic.loadIniFile();

		addListeners();

		presentationLogic.addPlugins(plugins);

		view.buildFileMenu();
		presentationLogic.updateTitleText();
	}

	public void setViewVisible(final boolean visible) {
		view.setVisible(visible);
	}

	private void addListeners() {
		view.addOpenButtonListener(() -> {
			loadFileUsingGUI();
		});

		addImportActionListener();

		view.addSaveButtonListener(() -> {
			project.getProjectFileType()
					.ifPresentOrElse(
							type -> saveFileToCurrentPath(type),
							() -> saveFileUsingGUI());

		});

		view.addSaveAsButtonListener(this::saveFileUsingGUI);

		view.addExportFOLDButtonListener(() -> {
			exportFileUsingGUI(DocFileTypes.fold());
		});

		view.addSaveAsImageButtonListener(() -> {
			exportFileUsingGUI(DocFileTypes.pictutre());
		});

		view.addExitButtonListener(this::exit);

		view.addUndoButtonListener(() -> {
			try {
				presentationLogic.undo();
			} catch (Exception ex) {
				logger.error("Wrong implementation.", ex);
			}
		});

		view.addRedoButtonListener(() -> {
			try {
				presentationLogic.redo();
			} catch (Exception ex) {
				logger.error("Wrong implementation.", ex);
			}
		});

		view.addClearButtonListener(presentationLogic::clear);

		view.addAboutButtonListener(view::showAboutAppMessage);

		// I wonder if the model check is really needed...
		view.addExportDXFButtonListener(() -> exportFileUsingGUIWithModelCheck(DocFileTypes.dxf()));
		view.addExportCPButtonListener(() -> exportFileUsingGUIWithModelCheck(DocFileTypes.cp()));
		view.addExportSVGButtonListener(() -> exportFileUsingGUIWithModelCheck(DocFileTypes.svg()));

		view.addPropertyButtonListener(this::showPropertyDialog);
		view.addRepeatCopyButtonListener(this::showArrayCopyDialog);
		view.addCircleCopyButtonListener(this::showCircleCopyDialog);

		addPaintMenuItemsListener();

		view.addMRUFileButtonListener(this::loadFile);
		view.addMRUFilesMenuItemUpdateListener(presentationLogic::updateMRUFilesMenuItem);

		view.setEstimationResultSaveColorsListener(presentationLogic::setEstimationResultSaveColors);

		view.setPaperDomainOfModelChangeListener(presentationLogic::setPaperDomainOfModel);

		view.addWindowClosingListener(this::windowClosing);
	}

	private void addImportActionListener() {
		var listener = paintMenuListenerFactory.createImportButtonListener(presentationLogic::importFileUsingGUI);
		view.addImportButtonListener(listener);
	}

	private void addPaintMenuItemsListener() {
		/*
		 * For changing outline
		 */
		var changeOutlineListener = paintMenuListenerFactory.createChangeOutlineButtonListener();
		view.addChangeOutlineButtonListener(changeOutlineListener);

		/*
		 * For selecting all lines
		 */
		var selectAllListener = paintMenuListenerFactory.createSelectAllLineActionListener();
		view.addSelectAllButtonListener(selectAllListener);

		/*
		 * For starting copy-and-paste
		 */
		var copyPasteListener = paintMenuListenerFactory
				.createCopyAndPasteButtonListener(view::showCopyPasteErrorMessage);
		view.addCopyAndPasteButtonListener(copyPasteListener);

		/*
		 * For starting cut-and-paste
		 */
		var cutPasteListener = paintMenuListenerFactory
				.createCutAndPasteButtonListener(view::showCopyPasteErrorMessage);
		view.addCutAndPasteButtonListener(cutPasteListener);

		var unselectListener = paintMenuListenerFactory
				.createUnselectAllItemsActionListener(presentationLogic::updateScreen);
		view.addUnselectAllButtonListener(unselectListener);

		var deleteLinesListener = paintMenuListenerFactory
				.createDeleteSelectedLinesActionListener(presentationLogic::updateScreen);
		view.addDeleteSelectedLinesButtonListener(deleteLinesListener);

	}

	private void exit() {
		presentationLogic.exit(() -> System.exit(0));
	}

	private void showPropertyDialog() {

		var presenter = dialogPresenterFactory.createPropertyDialogPresenter(view, project);

		presenter.setViewVisible(true);
	}

	private void showArrayCopyDialog() {
		if (paintContext.countSelectedLines() == 0) {
			view.showNoSelectionMessageForArrayCopy();
			return;
		}

		var presenter = dialogPresenterFactory.createArrayCopyDialogPresenter(view);

		presenter.setViewVisible(true);
	}

	private void showCircleCopyDialog() {
		if (paintContext.countSelectedLines() == 0) {
			view.showNoSelectionMessageForCircleCopy();
			return;
		}

		var presenter = dialogPresenterFactory.createCircleCopyDialogPresenter(view);

		presenter.setViewVisible(true);
	}

	/**
	 * saves project without opening a dialog
	 */
	private void saveFileToCurrentPath(final FileType<Doc> type) {
		try {
			beforeSave();
			var filePath = presentationLogic.saveFileToCurrentPath(type);
			afterSaveFile(filePath);
		} catch (Exception e) {
			view.showSaveFailureErrorMessage(e);
		}
	}

	/**
	 * save file without origami model check
	 */
	@SafeVarargs
	private void saveFileUsingGUI(final FileType<Doc>... types) {
		try {
			beforeSave();
			var filePath = presentationLogic.saveFileUsingGUI(types);
			afterSaveFile(filePath);
		} catch (Exception e) {
			view.showSaveFailureErrorMessage(e);
		}
	}

	/**
	 * export file without origami model check. This does not update UI and
	 * CP-edit history.
	 */
	@SafeVarargs
	private void exportFileUsingGUI(final FileType<Doc>... types) {
		try {
			beforeSave();
			presentationLogic.saveFileUsingGUI(types);
		} catch (Exception e) {
			view.showSaveFailureErrorMessage(e);
		}
	}

	private void beforeSave() {
		presentationLogic.modifySavingActions();
	}

	/**
	 * Call this method when save is done.
	 *
	 * @param data
	 * @param path
	 */
	private void afterSaveFile(final String path) {

		if (Project.projectFileTypeMatch(path)) {
			paintContext.clearCreasePatternChanged();
			project.setDataFilePath(path);
		}

		presentationLogic.updateMenu();
		presentationLogic.updateTitleText();
	}

	/**
	 * Open Save File As Dialogue for specific file types {@code type}. Runs a
	 * model check before saving.
	 */
	private void exportFileUsingGUIWithModelCheck(final FileType<Doc> type) {
		presentationLogic.exportFileUsingGUIWithModelCheck(type);
	}

	/**
	 * This method tries to read data from the path.
	 *
	 * @param filePath
	 */
	private void loadFile(final String filePath) {
		try {
			presentationLogic.loadFile(filePath);
			afterLoadFile();
		} catch (Exception e) {
			view.showLoadFailureErrorMessage(e);
		}
	}

	/**
	 * This method opens the file dialog and load the selected file.
	 */
	private void loadFileUsingGUI() {
		try {
			presentationLogic.loadFileUsingGUI();
			afterLoadFile();
		} catch (Exception e) {
			view.showLoadFailureErrorMessage(e);
		}
	}

	/**
	 * Update UI.
	 *
	 * @param filePath
	 */
	private void afterLoadFile() {
		presentationLogic.updateScreen();
		presentationLogic.updateMenu();
		presentationLogic.updateTitleText();
		presentationLogic.updateValuePanelFractionDigits();
	}

	private void windowClosing() {

		if (paintContext.creasePatternChangeExists()) {
			// confirm saving edited opx
			if (view.showSaveOnCloseDialog()) {

				saveFileUsingGUI();
			}
		}

		presentationLogic.saveIniFile();
	}

}
