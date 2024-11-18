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

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.application.FileAccessService;
import oripa.application.main.PaintContextModification;
import oripa.appstate.StatePopperFactory;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.PaintDomainContext;
import oripa.file.FileHistory;
import oripa.gui.bind.state.BindingObjectFactoryFacade;
import oripa.gui.presenter.creasepattern.CreasePatternPresentationContext;
import oripa.gui.presenter.creasepattern.DeleteSelectedLinesActionListener;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.creasepattern.MouseActionHolder;
import oripa.gui.presenter.creasepattern.SelectAllLineActionListener;
import oripa.gui.presenter.creasepattern.UnselectAllItemsActionListener;
import oripa.gui.presenter.file.UserAction;
import oripa.gui.presenter.plugin.GraphicMouseActionPlugin;
import oripa.gui.view.main.MainFrameDialogFactory;
import oripa.gui.view.main.MainFrameView;
import oripa.gui.view.main.SubFrameFactory;
import oripa.gui.view.util.ColorUtil;
import oripa.persistence.dao.DataAccessException;
import oripa.persistence.dao.FileType;
import oripa.persistence.doc.Doc;
import oripa.persistence.doc.DocFileTypes;
import oripa.persistence.doc.exporter.CreasePatternFOLDConfig;
import oripa.project.Project;
import oripa.resource.StringID;

/**
 * @author OUCHI Koji
 *
 */
public class MainFramePresenter {
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final MainFrameView view;
	private final MainFrameDialogFactory dialogFactory;

	private final MainFramePresentationLogic presentationLogic;
	private final MainComponentPresenterFactory componentPresenterFactory;

	private final StatePopperFactory<EditMode> statePopperFactory;

	private final BindingObjectFactoryFacade bindingFactory;

	private final Project project;

	private final PaintContext paintContext;
	private final MouseActionHolder actionHolder;

	// data access
	private final FileAccessService<Doc> dataFileAccess;
	private final FileHistory fileHistory;

	private final Supplier<CreasePatternFOLDConfig> foldConfigFactory;

	// services
	private final PaintContextModification paintContextModification;

	public MainFramePresenter(
			final MainFrameView view,
			final MainFrameDialogFactory dialogFactory,
			final SubFrameFactory subFrameFactory,
			final MainFramePresentationLogic presentationLogic,
			final MainComponentPresenterFactory componentPresenterFactory,
			final CreasePatternPresentationContext presentationContext,
			final BindingObjectFactoryFacade bindingFactory,
			final StatePopperFactory<EditMode> statePopperFactory,
			final Project project,
			final PaintDomainContext domainContext,
			final PaintContextModification paintContextModification,
			final FileHistory fileHistory,
			final FileAccessService<Doc> dataFileAccess,
			final List<GraphicMouseActionPlugin> plugins,
			final Supplier<CreasePatternFOLDConfig> foldConfigFactory) {

		this.view = view;
		this.dialogFactory = dialogFactory;

		this.bindingFactory = bindingFactory;

		this.presentationLogic = presentationLogic;
		this.componentPresenterFactory = componentPresenterFactory;

		this.project = project;
		this.paintContext = domainContext.getPaintContext();
		this.paintContextModification = paintContextModification;

		this.actionHolder = presentationContext.getActionHolder();
		this.statePopperFactory = statePopperFactory;
		this.fileHistory = fileHistory;

		this.dataFileAccess = dataFileAccess;

		this.foldConfigFactory = foldConfigFactory;

		presentationLogic.loadIniFile();

		modifySavingActions();

		addListeners();

		addPlugins(plugins);

		view.buildFileMenu();
		presentationLogic.updateTitleText();
	}

	public void setViewVisible(final boolean visible) {
		view.setVisible(visible);
	}

	private void addPlugins(final List<GraphicMouseActionPlugin> plugins) {
		presentationLogic.addPlugins(plugins);
	}

	private void addListeners() {
		view.addOpenButtonListener(() -> {
			loadFileUsingGUI();
		});

		addImportActionListener();

		view.addSaveButtonListener(() -> {
			project.getProjectFileType()
					.ifPresentOrElse(
							type -> saveFile(type),
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
				actionHolder.getMouseAction().orElseThrow().undo(paintContext);
			} catch (NoSuchElementException ex) {
				logger.error("mouseAction should not be null.", ex);
			} catch (Exception ex) {
				logger.error("Wrong implementation.", ex);
			}
			presentationLogic.updateScreen();
		});

		view.addRedoButtonListener(() -> {
			try {
				actionHolder.getMouseAction().orElseThrow().redo(paintContext);
			} catch (NoSuchElementException ex) {
				logger.error("mouseAction should not be null.", ex);
			} catch (Exception ex) {
				logger.error("Wrong implementation.", ex);
			}
			presentationLogic.updateScreen();
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

		view.setEstimationResultSaveColorsListener((front, back) -> {
			var property = project.getProperty();
			property.putFrontColorCode(ColorUtil.convertColorToCode(front));
			property.putBackColorCode(ColorUtil.convertColorToCode(back));

		});

		view.setPaperDomainOfModelChangeListener(presentationLogic::setPaperDomainOfModel);

		view.addWindowClosingListener(this::windowClosing);
	}

	/**
	 * Ensure the execution order as loading file comes first.
	 */
	private void addImportActionListener() {
		var state = bindingFactory.createState(StringID.IMPORT_CP_ID);

		view.addImportButtonListener(() -> {
			try {
				var presenter = componentPresenterFactory.createDocFileSelectionPresenter(
						view,
						dataFileAccess.getFileSelectionService());

				var selection = presenter.loadUsingGUI(fileHistory.getLastPath());
				if (selection.action() == UserAction.CANCELED) {
					return;
				}

				var docOpt = dataFileAccess.loadFile(selection.path());
				docOpt.ifPresent(
						doc -> paintContextModification.setToImportedLines(doc.getCreasePattern(), paintContext));

				state.performActions();
			} catch (DataAccessException | IllegalArgumentException e) {
				view.showLoadFailureErrorMessage(e);
			}
		});

	}

	private void addPaintMenuItemsListener() {
		/*
		 * For changing outline
		 */
		var changeOutlineState = bindingFactory.createState(StringID.EDIT_CONTOUR_ID);
		view.addChangeOutlineButtonListener(changeOutlineState::performActions);

		/*
		 * For selecting all lines
		 */
		var selectAllState = bindingFactory.createState(StringID.SELECT_ALL_LINE_ID);
		view.addSelectAllButtonListener(selectAllState::performActions);
		var selectAllListener = new SelectAllLineActionListener(paintContext);
		view.addSelectAllButtonListener(selectAllListener);

		/*
		 * For starting copy-and-paste
		 */
		Supplier<Boolean> detectCopyPasteError = () -> paintContext.getPainter().countSelectedLines() == 0;
		var copyPasteState = bindingFactory.createState(StringID.COPY_PASTE_ID,
				detectCopyPasteError, view::showCopyPasteErrorMessage);
		view.addCopyAndPasteButtonListener(copyPasteState::performActions);

		/*
		 * For starting cut-and-paste
		 */
		var cutPasteState = bindingFactory.createState(StringID.CUT_PASTE_ID,
				detectCopyPasteError, view::showCopyPasteErrorMessage);
		view.addCutAndPasteButtonListener(cutPasteState::performActions);

		var statePopper = statePopperFactory.createForState();
		var unselectListener = new UnselectAllItemsActionListener(actionHolder, paintContext, statePopper,
				presentationLogic::updateScreen);
		view.addUnselectAllButtonListener(unselectListener);

		var deleteLinesListener = new DeleteSelectedLinesActionListener(paintContext, presentationLogic::updateScreen);
		view.addDeleteSelectedLinesButtonListener(deleteLinesListener);

	}

	private void modifySavingActions() {
		dataFileAccess.setConfigToSavingAction(DocFileTypes.fold(), this::createFOLDConfig);
	}

	private void exit() {
		presentationLogic.exit(() -> System.exit(0));
	}

	private void showPropertyDialog() {
		var dialog = dialogFactory.createPropertyDialog(view);

		var presenter = componentPresenterFactory.createPropertyDialogPresenter(dialog, project);

		presenter.setViewVisible(true);
	}

	private void showArrayCopyDialog() {
		if (paintContext.getPainter().countSelectedLines() == 0) {
			view.showNoSelectionMessageForArrayCopy();
			return;
		}

		var dialog = dialogFactory.createArrayCopyDialog(view);

		var presenter = componentPresenterFactory.createArrayCopyDialogPresenter(dialog);

		presenter.setViewVisible(true);
	}

	private void showCircleCopyDialog() {
		if (paintContext.getPainter().countSelectedLines() == 0) {
			view.showNoSelectionMessageForCircleCopy();
			return;
		}

		var dialog = dialogFactory.createCircleCopyDialog(view);

		var presenter = componentPresenterFactory.createCircleCopyDialogPresenter(dialog);

		presenter.setViewVisible(true);
	}

	/**
	 * saves project without opening a dialog
	 */
	private void saveFile(final FileType<Doc> type) {
		var filePath = presentationLogic.saveFileImpl(type);

		afterSaveFile(filePath);
	}

	/**
	 * save file without origami model check
	 */
	@SafeVarargs
	private void saveFileUsingGUI(final FileType<Doc>... types) {
		var filePath = presentationLogic.saveFileUsingGUIImpl(types);

		afterSaveFile(filePath);
	}

	/**
	 * export file without origami model check. This does not update UI and
	 * CP-edit history.
	 */
	@SafeVarargs
	private void exportFileUsingGUI(final FileType<Doc>... types) {
		presentationLogic.saveFileUsingGUIImpl(types);
	}

	/**
	 * Call this method when save is done.
	 *
	 * @param data
	 * @param path
	 */
	private void afterSaveFile(final String path) {

		if (Project.projectFileTypeMatch(path)) {
			paintContext.creasePatternUndo().clearChanged();
			project.setDataFilePath(path);
		}

		presentationLogic.updateMenu();
		presentationLogic.updateTitleText();
	}

	private CreasePatternFOLDConfig createFOLDConfig() {
		var config = foldConfigFactory.get();
		config.setEps(paintContext.getPointEps());

		return config;
	}

	/**
	 * Open Save File As Dialogue for specific file types {@code type}. Runs a
	 * model check before saving.
	 */
	private void exportFileUsingGUIWithModelCheck(final FileType<Doc> type) {
		try {
			var presenter = componentPresenterFactory.createDocFileSelectionPresenter(
					view,
					dataFileAccess.getFileSelectionService());

			var selection = presenter.saveFileWithModelCheck(
					Doc.forSaving(paintContext.getCreasePattern(), project.getProperty()),
					fileHistory.getLastDirectory(),
					type, view, view::showModelBuildFailureDialog, paintContext.getPointEps());

			if (selection.action() == UserAction.CANCELED) {
				return;
			}

			var doc = Doc.forSaving(paintContext.getCreasePattern(), project.getProperty());
			dataFileAccess.saveFile(doc, selection.path(), type);

		} catch (IOException e) {
			logger.error("IO trouble", e);
			view.showSaveFailureErrorMessage(e);
		}
	}

	/**
	 * This method tries to read data from the path.
	 *
	 * @param filePath
	 */
	private void loadFile(final String filePath) {
		presentationLogic.loadFileImpl(filePath);
		afterLoadFile();
	}

	/**
	 * This method opens the file dialog and load the selected file.
	 */
	private void loadFileUsingGUI() {
		var selection = componentPresenterFactory.createDocFileSelectionPresenter(
				view,
				dataFileAccess.getFileSelectionService())
				.loadUsingGUI(fileHistory.getLastPath());

		if (selection.action() == UserAction.CANCELED) {
			return;
		}

		presentationLogic.loadFileImpl(selection.path());
		afterLoadFile();
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

		if (paintContext.creasePatternUndo().changeExists()) {
			// confirm saving edited opx
			if (view.showSaveOnCloseDialog()) {

				saveFileUsingGUI();
			}
		}

		presentationLogic.saveIniFile();
	}

}
