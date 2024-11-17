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

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.application.FileAccessService;
import oripa.application.main.IniFileAccess;
import oripa.application.main.PaintContextModification;
import oripa.appstate.StatePopperFactory;
import oripa.domain.cutmodel.CutModelOutlinesHolder;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.PaintDomainContext;
import oripa.file.FileHistory;
import oripa.gui.bind.state.BindingObjectFactoryFacade;
import oripa.gui.presenter.creasepattern.CreasePatternPresentationContext;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.presenter.creasepattern.DeleteSelectedLinesActionListener;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.creasepattern.MouseActionHolder;
import oripa.gui.presenter.creasepattern.SelectAllLineActionListener;
import oripa.gui.presenter.creasepattern.UnselectAllItemsActionListener;
import oripa.gui.presenter.file.UserAction;
import oripa.gui.presenter.plugin.GraphicMouseActionPlugin;
import oripa.gui.view.ViewScreenUpdater;
import oripa.gui.view.main.MainFrameDialogFactory;
import oripa.gui.view.main.MainFrameView;
import oripa.gui.view.main.MainViewSetting;
import oripa.gui.view.main.PainterScreenSetting;
import oripa.gui.view.main.SubFrameFactory;
import oripa.gui.view.main.ViewUpdateSupport;
import oripa.gui.view.util.ChildFrameManager;
import oripa.gui.view.util.ColorUtil;
import oripa.persistence.dao.DataAccessException;
import oripa.persistence.dao.FileType;
import oripa.persistence.doc.Doc;
import oripa.persistence.doc.DocFileTypes;
import oripa.persistence.doc.exporter.CreasePatternFOLDConfig;
import oripa.project.Project;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;
import oripa.util.file.FileFactory;

/**
 * @author OUCHI Koji
 *
 */
public class MainFramePresenter {
	private static final Logger logger = LoggerFactory.getLogger(MainFramePresenter.class);

	private final MainFrameView view;
	private final MainFrameDialogFactory dialogFactory;

	private final MainComponentPresenterFactory componentPresenterFactory;
	private final PainterScreenPresenter screenPresenter;
	private final UIPanelPresenter uiPanelPresenter;

	private final ResourceHolder resourceHolder;

	private final StatePopperFactory<EditMode> statePopperFactory;

	private final ViewScreenUpdater screenUpdater;
	private final PainterScreenSetting screenSetting;

	private final BindingObjectFactoryFacade bindingFactory;

	private final ChildFrameManager childFrameManager;

	private Project project;

	private final PaintContext paintContext;
	private final CreasePatternViewContext viewContext;
	private final MouseActionHolder actionHolder;
	private final CutModelOutlinesHolder cutModelOutlinesHolder;

	// data access
	private final IniFileAccess iniFileAccess;
	private final FileAccessService<Doc> dataFileAccess;
	private final FileHistory fileHistory;
	private final FileFactory fileFactory;

	private final Supplier<CreasePatternFOLDConfig> foldConfigFactory;

	// services
	private final PaintContextModification paintContextModification;

	public MainFramePresenter(
			final MainFrameView view,
			final MainViewSetting viewSetting,
			final ViewUpdateSupport viewUpdateSupport,
			final MainFrameDialogFactory dialogFactory,
			final SubFrameFactory subFrameFactory,
			final MainComponentPresenterFactory componentPresenterFactory,
			final CreasePatternPresentationContext presentationContext,
			final ChildFrameManager childFrameManager,
			final BindingObjectFactoryFacade bindingFactory,
			final StatePopperFactory<EditMode> statePopperFactory,
			final Project project,
			final PaintDomainContext domainContext,
			final PaintContextModification paintContextModification,
			final CutModelOutlinesHolder cutModelOutlinesHolder,
			final FileHistory fileHistory,
			final IniFileAccess iniFileAccess,
			final FileAccessService<Doc> dataFileAccess,
			final FileFactory fileFactory,
			final List<GraphicMouseActionPlugin> plugins,
			final Supplier<CreasePatternFOLDConfig> foldConfigFactory,
			final ResourceHolder resourceHolder) {
		this.resourceHolder = resourceHolder;

		this.view = view;
		this.dialogFactory = dialogFactory;

		this.childFrameManager = childFrameManager;

		this.bindingFactory = bindingFactory;

		this.componentPresenterFactory = componentPresenterFactory;

		this.project = project;
		this.paintContext = domainContext.getPaintContext();
		this.paintContextModification = paintContextModification;
		this.viewContext = presentationContext.getViewContext();
		this.cutModelOutlinesHolder = cutModelOutlinesHolder;

		this.actionHolder = presentationContext.getActionHolder();
		this.statePopperFactory = statePopperFactory;
		this.fileHistory = fileHistory;
		this.iniFileAccess = iniFileAccess;
		this.dataFileAccess = dataFileAccess;
		this.fileFactory = fileFactory;

		this.foldConfigFactory = foldConfigFactory;

		this.screenSetting = viewSetting.getPainterScreenSetting();

		var screen = view.getPainterScreenView();
		this.screenUpdater = viewUpdateSupport.getViewScreenUpdater();

		var uiPanel = view.getUIPanelView();

		screenPresenter = componentPresenterFactory.createPainterScreenPresenter(
				screen);

		uiPanelPresenter = componentPresenterFactory.createUIPanelPresenter(
				uiPanel);

		loadIniFile();

		modifySavingActions();

		addListeners();

		addPlugins(plugins);

		view.buildFileMenu();
		updateTitleText();
	}

	public void setViewVisible(final boolean visible) {
		view.setVisible(visible);
	}

	private void addPlugins(final List<GraphicMouseActionPlugin> plugins) {
		uiPanelPresenter.addPlugins(plugins);
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
			screenUpdater.updateScreen();
		});

		view.addRedoButtonListener(() -> {
			try {
				actionHolder.getMouseAction().orElseThrow().redo(paintContext);
			} catch (NoSuchElementException ex) {
				logger.error("mouseAction should not be null.", ex);
			} catch (Exception ex) {
				logger.error("Wrong implementation.", ex);
			}
			screenUpdater.updateScreen();
		});

		view.addClearButtonListener(this::clear);

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
		view.addMRUFilesMenuItemUpdateListener(this::updateMRUFilesMenuItem);

		view.setEstimationResultSaveColorsListener((front, back) -> {
			var property = project.getProperty();
			property.putFrontColorCode(ColorUtil.convertColorToCode(front));
			property.putBackColorCode(ColorUtil.convertColorToCode(back));

		});

		view.setPaperDomainOfModelChangeListener(screenPresenter::setPaperDomainOfModel);

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
				screenUpdater::updateScreen);
		view.addUnselectAllButtonListener(unselectListener);

		var deleteLinesListener = new DeleteSelectedLinesActionListener(paintContext, screenUpdater::updateScreen);
		view.addDeleteSelectedLinesButtonListener(deleteLinesListener);

	}

	private void modifySavingActions() {
		dataFileAccess.setConfigToSavingAction(DocFileTypes.fold(), this::createFOLDConfig);
	}

	private void updateMRUFilesMenuItem(final int index) {
		var histories = fileHistory.getHistory();
		if (index < histories.size()) {
			view.setMRUFilesMenuItem(index, histories.get(index));
		} else {
			view.setMRUFilesMenuItem(index, "");
		}
	}

	private void exit() {
		saveIniFile();
		System.exit(0);
	}

	private void clear() {
		paintContextModification.clear(paintContext, cutModelOutlinesHolder);
		project = new Project();

		screenSetting.setGridVisible(true);

		childFrameManager.closeAll(view);

		screenUpdater.updateScreen();
		updateTitleText();
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

	private void updateTitleText() {

		view.setFileNameToTitle(getTitleText());
	}

	private String getTitleText() {
		var defaultFileName = resourceHolder.getString(ResourceKey.DEFAULT, StringID.Default.FILE_NAME_ID);
		var fileName = project.getDataFileName().orElse(defaultFileName);

		return fileName.isEmpty() ? defaultFileName : fileName;

	}

	/**
	 * saves project without opening a dialog
	 */
	private void saveFile(final FileType<Doc> type) {
		var doc = Doc.forSaving(paintContext.getCreasePattern(), project.getProperty());

		try {
			var filePath = project.getDataFilePath();
			dataFileAccess.saveFile(doc, filePath, type);

			afterSaveFile(filePath);
		} catch (DataAccessException | IllegalArgumentException e) {
			logger.error("Failed to save", e);
			view.showSaveFailureErrorMessage(e);
		}
	}

	/**
	 * save file without origami model check
	 */
	@SafeVarargs
	private void saveFileUsingGUI(final FileType<Doc>... types) {
		var filePath = saveFileUsingGUIImpl(types);

		afterSaveFile(filePath);
	}

	/**
	 * export file without origami model check. This does not update UI and
	 * CP-edit history.
	 */
	@SafeVarargs
	private void exportFileUsingGUI(final FileType<Doc>... types) {
		saveFileUsingGUIImpl(types);
	}

	/**
	 * save file without origami model check
	 */
	@SafeVarargs
	private String saveFileUsingGUIImpl(final FileType<Doc>... types) {
		var directory = fileHistory.getLastDirectory();
		var fileName = project.getDataFileName().get();

		try {

			var presenter = componentPresenterFactory.createDocFileSelectionPresenter(
					view, dataFileAccess.getFileSelectionService());

			File givenFile = fileFactory.create(
					directory,
					(fileName.isEmpty()) ? "newFile.opx" : fileName);

			var filePath = givenFile.getPath();

			var selection = (types == null || types.length == 0) ? presenter.saveUsingGUI(filePath)
					: presenter.saveUsingGUI(filePath, List.of(types));

			if (selection.action() == UserAction.CANCELED) {
				return project.getDataFilePath();
			}

			var path = selection.path();

			var doc = Doc.forSaving(paintContext.getCreasePattern(), project.getProperty());
			dataFileAccess.saveFile(doc, selection.path(), selection.type());

			return path;

		} catch (IllegalArgumentException | DataAccessException e) {
			// ignore
			return project.getDataFilePath();
		}
	}

	/**
	 * Call this method when save is done.
	 *
	 * @param data
	 * @param path
	 */
	private void afterSaveFile(final String path) {
		paintContext.creasePatternUndo().clearChanged();

		if (Project.projectFileTypeMatch(path)) {
			project = new Project(project.getProperty(), path);
		}

		updateMenu();
		updateTitleText();
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
	 * Update file menu. Do nothing if the given {@code filePath} is null or
	 * wrong.
	 *
	 * @param filePath
	 */
	private void updateMenu() {
		var filePath = project.getDataFilePath();

		if (!project.isProjectFile()) {
			logger.debug("updating menu is canceled: {}", filePath);
			return;
		}

		fileHistory.useFile(filePath);

		view.buildFileMenu();
	}

	/**
	 * This method tries to read data from the path.
	 *
	 * @param filePath
	 */
	private void loadFile(final String filePath) {
		loadFileImpl(filePath);
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

		loadFileImpl(selection.path());
		afterLoadFile();
	}

	/**
	 * This method tries to read data from the path.
	 *
	 * @param filePath
	 * @return file path for loaded file. {@code null} if loading is not done.
	 */
	private String loadFileImpl(final String filePath) {

		childFrameManager.closeAll(view);

		try {

			var DocOpt = dataFileAccess.loadFile(filePath);
			return DocOpt
					.map(doc -> {
						project = new Project(doc.getProperty(), filePath);

						var property = project.getProperty();
						view.setEstimationResultColors(
								convertCodeToColor(property.extractFrontColorCode()),
								convertCodeToColor(property.extractBackColorCode()));

						screenSetting.setGridVisible(false);
						paintContextModification
								.setCreasePatternToPaintContext(
										doc.getCreasePattern(), paintContext, cutModelOutlinesHolder);
						screenPresenter.updateCameraCenter();
						return project.getDataFilePath();
					}).orElse(null);
		} catch (DataAccessException | IllegalArgumentException e) {
			logger.error("failed to load", e);
			view.showLoadFailureErrorMessage(e);
			return project.getDataFilePath();
		}
	}

	/**
	 * Update UI.
	 *
	 * @param filePath
	 */
	private void afterLoadFile() {
		screenUpdater.updateScreen();
		updateMenu();
		updateTitleText();
		uiPanelPresenter.updateValuePanelFractionDigits();
	}

	/**
	 * Can return null because the returned value will be passed to other
	 * method.
	 *
	 * @param code
	 * @return
	 */
	private Color convertCodeToColor(final String code) {
		if (code == null) {
			return null;
		}

		try {
			return new Color(Integer.decode(code));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private void saveIniFile() {
		try {
			iniFileAccess.save(fileHistory, viewContext);
		} catch (IllegalStateException e) {
			logger.error("error when building ini file data", e);
			view.showSaveIniFileFailureErrorMessage(e);
		}
	}

	private void loadIniFile() {
		var ini = iniFileAccess.load();

		fileHistory.loadFromInitData(ini);
		screenSetting.setZeroLineWidth(ini.isZeroLineWidth());

		logger.debug("loaded ini.mvLineVisible: " + ini.isMvLineVisible());
		screenSetting.setMVLineVisible(ini.isMvLineVisible());

		logger.debug("loaded ini.auxLineVisible: " + ini.isAuxLineVisible());
		screenSetting.setAuxLineVisible(ini.isAuxLineVisible());

		logger.debug("loaded ini.vertexVisible: " + ini.isVertexVisible());
		screenSetting.setVertexVisible(ini.isVertexVisible());
	}

	private void windowClosing() {

		if (paintContext.creasePatternUndo().changeExists()) {
			// confirm saving edited opx
			if (view.showSaveOnCloseDialog()) {

				saveFileUsingGUI();
			}
		}

		saveIniFile();
	}

}
