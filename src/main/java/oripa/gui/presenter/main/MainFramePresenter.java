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
import java.awt.Rectangle;
import java.io.IOException;
import java.util.function.Supplier;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.application.main.DataFileAccess;
import oripa.application.main.IniFileAccess;
import oripa.application.main.PaintContextModification;
import oripa.appstate.StatePopper;
import oripa.doc.Doc;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.PaintContextFactory;
import oripa.domain.paint.copypaste.SelectionOriginHolder;
import oripa.file.FileHistory;
import oripa.file.InitDataFileReader;
import oripa.file.InitDataFileWriter;
import oripa.gui.bind.state.EditModeStateManager;
import oripa.gui.bind.state.PaintBoundStateFactory;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.presenter.creasepattern.CreasePatternViewContextFactory;
import oripa.gui.presenter.creasepattern.DeleteSelectedLinesActionListener;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.creasepattern.MouseActionHolder;
import oripa.gui.presenter.creasepattern.SelectAllLineActionListener;
import oripa.gui.presenter.creasepattern.UnselectAllItemsActionListener;
import oripa.gui.view.main.ArrayCopyDialogFactory;
import oripa.gui.view.main.CircleCopyDialogFactory;
import oripa.gui.view.main.MainDialogService;
import oripa.gui.view.main.MainFrameView;
import oripa.gui.view.main.PropertyDialog;
import oripa.gui.view.util.ChildFrameManager;
import oripa.gui.view.util.Dialogs;
import oripa.gui.viewsetting.main.MainScreenSetting;
import oripa.gui.viewsetting.main.MainScreenUpdater;
import oripa.persistence.dao.AbstractFilterSelector;
import oripa.persistence.doc.CreasePatternFileTypeKey;
import oripa.persistence.doc.DocDAO;
import oripa.persistence.doc.DocFilterSelector;
import oripa.persistence.filetool.AbstractSavingAction;
import oripa.persistence.filetool.FileAccessSupportFilter;
import oripa.persistence.filetool.FileVersionError;
import oripa.persistence.filetool.WrongDataFormatException;
import oripa.resource.Constants;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;

/**
 * @author OUCHI Koji
 *
 */
public class MainFramePresenter {
	private static final Logger logger = LoggerFactory.getLogger(MainFramePresenter.class);

	private final MainFrameView view;

	private final PainterScreenPresenter screenPresenter;
	private final UIPanelPresenter uiPanelPresenter;

	// shared objects
	private final ResourceHolder resourceHolder = ResourceHolder.getInstance();

	private final EditModeStateManager stateManager = new EditModeStateManager();
	private final SelectionOriginHolder selectionOriginHolder;

	private final MainScreenUpdater screenUpdater;
	private final MainScreenSetting screenSetting;

	private final PaintBoundStateFactory stateFactory;

	private final ChildFrameManager childFrameManager = new ChildFrameManager();

	private final MainDialogService dialogService = new MainDialogService(resourceHolder);
	private final CopyDialogOpener copyDialogOpener;

	private final FileHistory fileHistory = new FileHistory(Constants.MRUFILE_NUM);

	private final AbstractFilterSelector<Doc> filterSelector = new DocFilterSelector();

	private final Doc document = new Doc();

	// Create UI Factories
	private final PaintContextFactory contextFactory = new PaintContextFactory();
	private final PaintContext paintContext = contextFactory.createContext();
	private final CreasePatternViewContextFactory viewContextFactory = new CreasePatternViewContextFactory();
	private final CreasePatternViewContext viewContext = viewContextFactory.create(paintContext);
	private final MouseActionHolder actionHolder = new MouseActionHolder();

	private final IniFileAccess iniFileAccess = new IniFileAccess(
			new InitDataFileReader(), new InitDataFileWriter());
	private final DataFileAccess dataFileAccess = new DataFileAccess(new DocDAO(new DocFilterSelector()));
	private final PaintContextModification paintContextModification = new PaintContextModification();

	public MainFramePresenter(final MainFrameView view) {
		this.view = view;

		document.setCreasePattern(paintContext.getCreasePattern());

		var screen = view.getPainterScreenView();
		screenUpdater = screen.getScreenUpdater();
		screenSetting = screen.getMainScreenSetting();
		selectionOriginHolder = screenSetting.getSelectionOriginHolder();

		var uiPanel = view.getUIPanelView();
		var uiPanelSetting = uiPanel.getUIPanelSetting();

		screenPresenter = new PainterScreenPresenter(
				screen,
				actionHolder,
				viewContext,
				paintContext,
				document);

		uiPanelPresenter = new UIPanelPresenter(
				uiPanel,
				stateManager,
				screenUpdater,
				actionHolder,
				viewContext,
				paintContext,
				document,
				view.getMainFrameSetting(),
				screenSetting);

		uiPanelPresenter.setChildFrameManager(childFrameManager);

		stateFactory = new PaintBoundStateFactory(stateManager, view.getMainFrameSetting(), uiPanelSetting,
				selectionOriginHolder);

		// Setup Dialog Windows
		copyDialogOpener = new CopyDialogOpener(
				new ArrayCopyDialogFactory(),
				new CircleCopyDialogFactory(),
				() -> dialogService.showNoSelectionMessageForArrayCopy(view.asFrame()),
				() -> dialogService.showNoSelectionMessageForCircleCopy(view.asFrame()));

		loadIniFile();

		modifySavingActions();

		addListeners();

		view.buildFileMenu();
		updateTitleText();
	}

	public void setViewVisible(final boolean visible) {
		view.setViewVisible(visible);
	}

	private void addListeners() {
		view.addOpenButtonListener(() -> {
			String path = loadFile(null);
			screenUpdater.updateScreen();
			updateMenu(path);
			updateTitleText();
		});

		addImportActionListener();

		view.addSaveButtonListener(() -> {
			var filePath = document.getDataFilePath();
			if (CreasePatternFileTypeKey.OPX.extensionsMatch(filePath)) {
				saveProjectFile(document, filePath, CreasePatternFileTypeKey.OPX);
			} else if (CreasePatternFileTypeKey.FOLD.extensionsMatch(filePath)) {
				saveProjectFile(document, filePath, CreasePatternFileTypeKey.FOLD);
			} else {
				saveAnyTypeUsingGUI();
			}
		});

		view.addSaveAsButtonListener(() -> saveAnyTypeUsingGUI());

		view.addExportFOLDButtonListener(() -> {
			String lastDirectory = fileHistory.getLastDirectory();
			saveFile(lastDirectory, document.getDataFileName(),
					filterSelector.getFilter(CreasePatternFileTypeKey.FOLD));
		});

		view.addSaveAsImageButtonListener(() -> {
			String lastDirectory = fileHistory.getLastDirectory();
			saveFile(lastDirectory, document.getDataFileName(),
					filterSelector.getFilter(CreasePatternFileTypeKey.PICT));
		});

		view.addExitButtonListener(() -> exit());

		view.addUndoButtonListener(() -> {
			try {
				actionHolder.getMouseAction().undo(paintContext);
			} catch (NullPointerException ex) {
				if (actionHolder.getMouseAction() == null) {
					logger.error("mouseAction should not be null.", ex);
				} else {
					logger.error("Wrong implementation.", ex);
				}
			}
			screenUpdater.updateScreen();
		});

		view.addRedoButtonListener(() -> {
			try {
				actionHolder.getMouseAction().redo(paintContext);
			} catch (NullPointerException ex) {
				if (actionHolder.getMouseAction() == null) {
					logger.error("mouseAction should not be null.", ex);
				} else {
					logger.error("Wrong implementation.", ex);
				}
			}
			screenUpdater.updateScreen();
		});

		view.addClearButtonListener(() -> clear());

		view.addAboutButtonListener(() -> dialogService.showAboutAppMessage(view.asFrame()));

		view.addExportDXFButtonListener(() -> saveFileWithModelCheck(CreasePatternFileTypeKey.DXF));
		view.addExportCPButtonListener(() -> saveFileWithModelCheck(CreasePatternFileTypeKey.CP));
		view.addExportSVGButtonListener(() -> saveFileWithModelCheck(CreasePatternFileTypeKey.SVG));

		view.addPropertyButtonListener(() -> showPropertyDialog());
		view.addRepeatCopyButtonListener(() -> showArrayCopyDialog());
		view.addCircleCopyButtonListener(() -> showCircleCopyDialog());

		addPaintMenuItemsListener();

		view.addMRUFileButtonListener(this::loadFileFromMRUFileMenuItem);
		view.addMRUFilesMenuItemUpdateListener(this::updateMRUFilesMenuItem);

		view.setEstimationResultSaveColorsListener((front, back) -> {
			var property = document.getProperty();
			property.putFrontColorCode(convertColorToCode(front));
			property.putBackColorCode(convertColorToCode(back));

		});

		view.setPaperDomainOfModelChangeListener(screenPresenter::setPaperDomainOfModel);

		view.addWindowClosingListener(this::windowClosing);
	}

	/**
	 * Ensure the execution order as loading file comes first.
	 */
	private void addImportActionListener() {
		var state = stateFactory.create(actionHolder, paintContext, screenUpdater, null,
				null,
				StringID.IMPORT_CP_ID);
		view.addImportButtonListener(() -> {
			try {
				dataFileAccess
						.loadFile(null, fileHistory.getLastPath(), view.asFrame(), filterSelector.getLoadables())
						.ifPresent(otherDoc -> {
							paintContext.getPainter().resetSelectedOriLines();
							var otherCreasePattern = otherDoc.getCreasePattern();
							otherCreasePattern.forEach(l -> l.selected = true);
							paintContext.getCreasePattern().addAll(otherCreasePattern);
						});
				state.performActions(null);
			} catch (IllegalArgumentException | FileVersionError | WrongDataFormatException | IOException ex) {
				logger.error("failed to load (import)", ex);
				Dialogs.showErrorDialog(view.asFrame(), resourceHolder.getString(
						ResourceKey.ERROR, StringID.Error.LOAD_FAILED_ID), ex);
			}
		});
	}

	private void addPaintMenuItemsListener() {
		/*
		 * For changing outline
		 */
		var changeOutlineState = stateFactory.create(actionHolder, paintContext, screenUpdater, null,
				null, StringID.EDIT_CONTOUR_ID);
		view.addChangeOutlineButtonListener(() -> changeOutlineState.performActions(null));

		/*
		 * For selecting all lines
		 */
		var selectAllState = stateFactory.create(actionHolder, paintContext, screenUpdater, null,
				null, StringID.SELECT_ALL_LINE_ID);
		view.addSelectAllButtonListener(() -> selectAllState.performActions(null));
		var selectAllListener = new SelectAllLineActionListener(paintContext);
		view.addSelectAllButtonListener(() -> selectAllListener.actionPerformed(null));

		/*
		 * For starting copy-and-paste
		 */
		Supplier<Boolean> detectCopyPasteError = () -> paintContext.getPainter().countSelectedLines() == 0;
		var copyPasteState = stateFactory.create(actionHolder, paintContext, screenUpdater,
				detectCopyPasteError, view::showCopyPasteErrorMessage, StringID.COPY_PASTE_ID);
		view.addCopyAndPasteButtonListener(() -> copyPasteState.performActions(null));

		/*
		 * For starting cut-and-paste
		 */
		var cutPasteState = stateFactory.create(actionHolder, paintContext, screenUpdater,
				detectCopyPasteError, view::showCopyPasteErrorMessage, StringID.CUT_PASTE_ID);
		view.addCutAndPasteButtonListener(() -> cutPasteState.performActions(null));

		var statePopper = new StatePopper<EditMode>(stateManager);
		var unselectListener = new UnselectAllItemsActionListener(actionHolder, paintContext, statePopper,
				screenUpdater);
		view.addUnselectAllButtonListener(
				() -> unselectListener.actionPerformed(null));

		var deleteLinesListener = new DeleteSelectedLinesActionListener(paintContext, screenUpdater);
		view.addDeleteSelectedLinesButtonListener(
				() -> deleteLinesListener.actionPerformed(null));

	}

	private void modifySavingActions() {
		// overwrite the action to update GUI after saving.
		setProjectSavingAction(CreasePatternFileTypeKey.OPX);
		setProjectSavingAction(CreasePatternFileTypeKey.FOLD);
	}

	private void setProjectSavingAction(final CreasePatternFileTypeKey fileType) {
		filterSelector.getFilter(fileType).setSavingAction(
				new AbstractSavingAction<Doc>() {

					@Override
					public boolean save(final Doc data) {
						try {
							saveProjectFile(data, getPath(), fileType);
						} catch (Exception e) {
							logger.error("Failed to save file " + getPath(), e);
							return false;
						}
						return true;
					}
				});

	}

	private void updateMRUFilesMenuItem(final int index) {
		var histories = fileHistory.getHistory();
		if (index < histories.size()) {
			view.setMRUFilesMenuItem(index, histories.get(index));
		} else {
			view.setMRUFilesMenuItem(index, "");
		}
	}

	private void loadFileFromMRUFileMenuItem(final String filePath) {

		try {
			loadFile(filePath);
			updateTitleText();
		} catch (Exception ex) {
			logger.error("error when loading: ", ex);
			Dialogs.showErrorDialog(view.asFrame(), resourceHolder.getString(
					ResourceKey.ERROR, StringID.Error.LOAD_FAILED_ID), ex);
		}
		screenUpdater.updateScreen();
	}

	private void saveAnyTypeUsingGUI() {
		String lastDirectory = fileHistory.getLastDirectory();

		String path = saveFile(lastDirectory, document.getDataFileName(),
				filterSelector.getSavables());

		updateMenu(path);
		updateTitleText();
	}

	private void exit() {
		saveIniFile();
		System.exit(0);
	}

	private void clear() {
		document.set(new Doc(Constants.DEFAULT_PAPER_SIZE));

		paintContextModification
				.setCreasePatternToPaintContext(document.getCreasePattern(), paintContext);

		screenSetting.setGridVisible(true);

		childFrameManager.closeAllChildrenRecursively(view.asFrame());

		screenUpdater.updateScreen();
		updateTitleText();
	}

	private void showPropertyDialog() {
		var frame = view.asFrame();
		PropertyDialog dialog = new PropertyDialog(frame, document);

		Rectangle rec = frame.getBounds();
		dialog.setLocation(
				(int) (rec.getCenterX() - dialog.getWidth() / 2),
				(int) (rec.getCenterY() - dialog.getHeight() / 2));
		dialog.setModal(true);
		dialog.setVisible(true);
	}

	private void showArrayCopyDialog() {
		copyDialogOpener.showArrayCopyDialog(view.asFrame(), paintContext, screenUpdater);
	}

	private void showCircleCopyDialog() {
		copyDialogOpener.showCircleCopyDialog(view.asFrame(), paintContext, screenUpdater);
	}

	private void updateTitleText() {
		String fileName;
		if (document.getDataFilePath().isEmpty()) {
			fileName = resourceHolder.getString(ResourceKey.DEFAULT, StringID.Default.FILE_NAME_ID);
		} else {
			fileName = document.getDataFileName();
		}

		view.setFileNameToTitle(fileName);
	}

	/**
	 * saves project without opening a dialog
	 */
	private void saveProjectFile(final Doc doc, final String filePath,
			final CreasePatternFileTypeKey fileType) {
		try {
			dataFileAccess.saveProjectFile(doc, filePath, fileType);
		} catch (IOException | IllegalArgumentException e) {
			logger.error("Failed to save", e);
			Dialogs.showErrorDialog(view.asFrame(), resourceHolder.getString(
					ResourceKey.ERROR, StringID.Error.SAVE_FAILED_ID), e);
		}

		paintContext.creasePatternUndo().clearChanged();

		updateMenu(filePath);
		updateTitleText();
	}

	/**
	 * save file without origami model check
	 */
	@SafeVarargs
	private String saveFile(final String directory, final String fileName,
			final FileAccessSupportFilter<Doc>... filters) {

		try {
			return dataFileAccess.saveFile(
					document, directory, fileName, view.asFrame(), filters)
					.map(path -> {
						paintContext.creasePatternUndo().clearChanged();
						return path;
					})
					.orElse(document.getDataFilePath());
		} catch (IOException | IllegalArgumentException e) {
			logger.error("failed to save", e);
			Dialogs.showErrorDialog(view.asFrame(), resourceHolder.getString(
					ResourceKey.ERROR, StringID.Error.SAVE_FAILED_ID), e);
			return document.getDataFilePath();
		}
	}

	/**
	 * Open Save File As Dialogue for specific file types {@code type}. Runs a
	 * model check before saving.
	 */
	private void saveFileWithModelCheck(final CreasePatternFileTypeKey type) {
		var frame = view.asFrame();
		try {
			dataFileAccess.saveFileWithModelCheck(document, fileHistory.getLastDirectory(),
					filterSelector.getFilter(type), frame,
					() -> dialogService.showModelBuildFailureDialog(frame) == JOptionPane.OK_OPTION);
		} catch (IOException e) {
			logger.error("IO trouble", e);
			Dialogs.showErrorDialog(frame, resourceHolder.getString(
					ResourceKey.ERROR, StringID.Error.SAVE_FAILED_ID), e);
		} catch (IllegalArgumentException e) {
			logger.error("Maybe data is not appropriate.", e);
			Dialogs.showErrorDialog(frame, resourceHolder.getString(
					ResourceKey.ERROR, StringID.Error.SAVE_FAILED_ID), e);
		}
	}

	/**
	 * Update file menu. Do nothing if the given {@code filePath} is null or
	 * wrong.
	 *
	 * @param filePath
	 */
	private void updateMenu(final String filePath) {
		if (filePath == null) {
			return;
		}
		try {
			filterSelector.getLoadableFilterOf(filePath);
		} catch (IllegalArgumentException e) {
			logger.debug("updating menu is canceled.", e);
			return;
		}
		fileHistory.useFile(filePath);

		view.buildFileMenu();
	}

	/**
	 * if filePath is null, this method opens a dialog to select the target.
	 * otherwise, it tries to read data from the path.
	 *
	 * @param filePath
	 * @return file path for loaded file. {@code null} if loading is not done.
	 */
	private String loadFile(final String filePath) {
		var frame = view.asFrame();

		childFrameManager.closeAllChildrenRecursively(frame);

		try {
			return dataFileAccess.loadFile(
					filePath, fileHistory.getLastPath(), frame, filterSelector.getLoadables())
					.map(doc -> {
						// we can't substitute a loaded object because
						// the document object is referred by screen and UI
						// panel as a Holder.
						document.set(doc);

						var property = document.getProperty();
						view.getUIPanelView().setEstimationResultColors(
								convertCodeToColor(property.extractFrontColorCode()),
								convertCodeToColor(property.extractBackColorCode()));

						screenSetting.setGridVisible(false);
						paintContextModification
								.setCreasePatternToPaintContext(
										document.getCreasePattern(), paintContext);
						return document.getDataFilePath();
					}).orElse(null);
		} catch (FileVersionError | IllegalArgumentException | WrongDataFormatException
				| IOException e) {
			logger.error("failed to load", e);
			Dialogs.showErrorDialog(frame, resourceHolder.getString(
					ResourceKey.ERROR, StringID.Error.LOAD_FAILED_ID), e);
			return document.getDataFilePath();
		}
	}

	private String convertColorToCode(final Color color) {
		return String.format("#%06X", color.getRGB() & 0x00FFFFFF);
	}

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
			Dialogs.showErrorDialog(view.asFrame(), resourceHolder.getString(
					ResourceKey.ERROR, StringID.Error.SAVE_INI_FAILED_ID), e);
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
			int selected = dialogService.showSaveOnCloseDialog(view.asFrame());
			if (selected == JOptionPane.YES_OPTION) {

				document.setCreasePattern(paintContext.getCreasePattern());

				String path = saveFile(fileHistory.getLastDirectory(),
						document.getDataFileName(), filterSelector.getSavables());
				if (path == null) {

				}
			}
		}

		saveIniFile();
	}

}
