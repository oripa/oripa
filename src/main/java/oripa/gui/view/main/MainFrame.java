/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2005-2009 Jun Mitani http://mitani.cs.tsukuba.ac.jp/

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

package oripa.gui.view.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.appstate.StateManager;
import oripa.doc.Doc;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.PaintContextFactory;
import oripa.file.ImageResourceLoader;
import oripa.geom.RectangleDomain;
import oripa.gui.bind.state.EditModeStateManager;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.presenter.creasepattern.CreasePatternViewContextFactory;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.creasepattern.MouseActionHolder;
import oripa.gui.view.util.ChildFrameManager;
import oripa.gui.view.util.Dialogs;
import oripa.gui.view.util.KeyStrokes;
import oripa.gui.viewsetting.ViewScreenUpdater;
import oripa.gui.viewsetting.main.MainFrameSetting;
import oripa.gui.viewsetting.main.MainScreenSetting;
import oripa.gui.viewsetting.main.MainScreenUpdater;
import oripa.gui.viewsetting.main.uipanel.UIPanelSetting;
import oripa.resource.Constants;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;

public class MainFrame extends JFrame implements MainFrameView, ComponentListener, WindowListener {

	private static final Logger logger = LoggerFactory.getLogger(MainFrame.class);

	private static final long serialVersionUID = 272369294032419950L;

	// shared objects
	private final ResourceHolder resourceHolder = ResourceHolder.getInstance();

	private final EditModeStateManager stateManager = new EditModeStateManager();

	private final MainFrameSetting setting = new MainFrameSetting();
	private final MainScreenSetting screenSetting;

	private final ViewScreenUpdater screenUpdater;

	private final ChildFrameManager childFrameManager = new ChildFrameManager();

//	private final FileHistory fileHistory = new FileHistory(Constants.MRUFILE_NUM);

//	private final AbstractFilterSelector<Doc> filterSelector = new DocFilterSelector();

	private final Doc document = new Doc();

	// Create UI Factories
	private final PaintContextFactory contextFactory = new PaintContextFactory();
	private final PaintContext paintContext = contextFactory.createContext();
	private final CreasePatternViewContextFactory viewContextFactory = new CreasePatternViewContextFactory();
	private final CreasePatternViewContext viewContext = viewContextFactory.create(paintContext);
	private final MouseActionHolder actionHolder = new MouseActionHolder();

//	private final ButtonFactory buttonFactory;
//
//	private final MainDialogService dialogService = new MainDialogService(resourceHolder);
//	private final CopyDialogOpener copyDialogOpener;
	private final JLabel hintLabel = new JLabel();

	// setup Menu Bars
	private final JMenu menuFile = new JMenu(
			resourceHolder.getString(ResourceKey.LABEL, StringID.Main.FILE_ID));
	private final JMenu menuEdit = new JMenu(
			resourceHolder.getString(ResourceKey.LABEL, StringID.Main.EDIT_ID));
	private final JMenu menuHelp = new JMenu(
			resourceHolder.getString(ResourceKey.LABEL, StringID.Main.HELP_ID));

	// file menu items
	private final JMenuItem menuItemClear = new JMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.Main.NEW_ID));
	private final JMenuItem menuItemOpen = new JMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.Main.OPEN_ID));

	private final JMenuItem menuItemSave = new JMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.Main.SAVE_ID));
	private final JMenuItem menuItemSaveAs = new JMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.Main.SAVE_AS_ID));
	private final JMenuItem menuItemSaveAsImage = new JMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.Main.SAVE_AS_IMAGE_ID));

	private final JMenuItem menuItemExportFOLD = new JMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.Main.EXPORT_FOLD_ID));
	private final JMenuItem menuItemExportDXF = new JMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.Main.EXPORT_DXF_ID));
	private final JMenuItem menuItemExportCP = new JMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.Main.EXPORT_CP_ID));
	private final JMenuItem menuItemExportSVG = new JMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.Main.EXPORT_SVG_ID));
	private final JMenuItem menuItemExit = new JMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.Main.EXIT_ID));

	private final JMenuItem[] MRUFilesMenuItem = new JMenuItem[Constants.MRUFILE_NUM];

	private final JMenuItem menuItemProperty = new JMenuItem(
			resourceHolder.getString(ResourceKey.LABEL,
					StringID.Main.PROPERTY_ID));

	private final JMenuItem menuItemImport = new JMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.IMPORT_CP_ID));

	// edit menu items
	/**
	 * For changing outline
	 */
	private final JMenuItem menuItemChangeOutline = new JMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.EDIT_CONTOUR_ID));

	/**
	 * For selecting all lines
	 */
	private final JMenuItem menuItemSelectAll = new JMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.SELECT_ALL_LINE_ID));

	/**
	 * For starting copy-and-paste
	 */
	private final JMenuItem menuItemCopyAndPaste = new JMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.COPY_PASTE_ID));

	/**
	 * For starting cut-and-paste
	 */
	private final JMenuItem menuItemCutAndPaste = new JMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.CUT_PASTE_ID));

	private final JMenuItem menuItemUndo = new JMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.Main.UNDO_ID));
	private final JMenuItem menuItemRedo = new JMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.Main.REDO_ID));

	private final JMenuItem menuItemRepeatCopy = new JMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.Main.ARRAY_COPY_ID));
	private final JMenuItem menuItemCircleCopy = new JMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.Main.CIRCLE_COPY_ID));

	private final JMenuItem menuItemUnSelectAll = new JMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.Main.UNSELECT_ALL_ID));

	private final JMenuItem menuItemDeleteSelectedLines = new JMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.Main.DELETE_SELECTED_ID));

	// help menu items

	private final JMenuItem menuItemAbout = new JMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.Main.ABOUT_ID));

	private UIPanel uiPanel;
	private final PainterScreen mainScreen;

//	private final IniFileAccess iniFileAccess = new IniFileAccess(
//			new InitDataFileReader(), new InitDataFileWriter());
//	private final DataFileAccess dataFileAccess = new DataFileAccess(new DocDAO(new DocFilterSelector()));
//	private final PaintContextModification paintContextModification = new PaintContextModification();

	private Consumer<Integer> MRUFilesMenuItemUpdateListener;

	private Runnable windowClosingListener;

	private Runnable titleUpdateListener;

	public MainFrame() {
		logger.info("frame construction starts.");

		document.setCreasePattern(paintContext.getCreasePattern());

		mainScreen = new PainterScreen(actionHolder,
				viewContext, paintContext,
				document);
		screenUpdater = mainScreen.getScreenUpdater();
		screenSetting = mainScreen.getMainScreenSetting();

//		var originHolder = screenSetting.getSelectionOriginHolder();

		// this has to be done before instantiation of UI panel.
		addHintPropertyChangeListenersToSetting();

		logger.info("start constructing UI panel.");
		try {
			uiPanel = new UIPanel(
					stateManager, screenUpdater, actionHolder, viewContext, paintContext, document,
					setting, screenSetting);
			uiPanel.setChildFrameManager(childFrameManager);
		} catch (RuntimeException ex) {
			logger.error("UI panel construction failed", ex);
			Dialogs.showErrorDialog(
					this, resourceHolder.getString(ResourceKey.ERROR, StringID.Error.DEFAULT_TITLE_ID), ex);
			System.exit(1);
		}
		logger.info("end constructing UI panel.");

		JScrollPane uiScroll = new JScrollPane(uiPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		uiScroll.setPreferredSize(new Dimension(255, 800));// setPreferredSize(new
															// Dimension(uiPanel.getPreferredSize().width
															// + 25,
		// uiPanel.getPreferredSize().height));
		uiScroll.setAlignmentX(JPanel.LEFT_ALIGNMENT);

//		var stateFactory = new PaintBoundStateFactory(
//				stateManager, setting,
//				uiPanel.getUIPanelSetting(),
//				originHolder);
//		buttonFactory = new PaintActionButtonFactory(
//				stateFactory, paintContext, actionHolder, screenUpdater);

		// Setup Dialog Windows
//		copyDialogOpener = new CopyDialogOpener(
//				new ArrayCopyDialogFactory(),
//				new CircleCopyDialogFactory(),
//				() -> dialogService.showNoSelectionMessageForArrayCopy(this),
//				() -> dialogService.showNoSelectionMessageForCircleCopy(this));

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(uiScroll, BorderLayout.WEST);
		getContentPane().add(mainScreen, BorderLayout.CENTER);
		getContentPane().add(hintLabel, BorderLayout.SOUTH);

		ImageResourceLoader imgLoader = new ImageResourceLoader();
		this.setIconImage(imgLoader.loadAsIcon("icon/oripa.gif", getClass())
				.getImage());

		addWindowListener(this);

//		loadIniFile();

		createPaintMenuItems();
		IntStream.range(0, Constants.MRUFILE_NUM)
				.forEach(i -> MRUFilesMenuItem[i] = new JMenuItem());

		// Building the menu bar
		JMenuBar menuBar = new JMenuBar();
		// buildFileMenu();

		addActionListenersToComponents();

		menuEdit.add(menuItemCopyAndPaste);
		menuEdit.add(menuItemCutAndPaste);
		menuEdit.add(menuItemRepeatCopy);
		menuEdit.add(menuItemCircleCopy);
		menuEdit.add(menuItemSelectAll);
		menuEdit.add(menuItemUnSelectAll);
		menuEdit.add(menuItemDeleteSelectedLines);
		menuEdit.add(menuItemUndo);
		menuEdit.add(menuItemRedo);
		menuEdit.add(menuItemChangeOutline);

		menuHelp.add(menuItemAbout);

		menuBar.add(menuFile);
		menuBar.add(menuEdit);
		menuBar.add(menuHelp);
		setJMenuBar(menuBar);

//		modifySavingActions();

//		updateTitleText();
//		titleUpdateListener.run();

	}

	private void createPaintMenuItems() {
//		/*
//		 * For changing outline
//		 */
//		menuItemChangeOutline = buttonFactory
//				.create(this, JMenuItem.class,
//						StringID.EDIT_CONTOUR_ID, null);
//
//		/*
//		 * For selecting all lines
//		 */
//		menuItemSelectAll = buttonFactory
//				.create(this, JMenuItem.class,
//						StringID.SELECT_ALL_LINE_ID, null);
//
//		/*
//		 * For starting copy-and-paste
//		 */
//		menuItemCopyAndPaste = buttonFactory
//				.create(this, JMenuItem.class,
//						StringID.COPY_PASTE_ID, null);
//
//		/*
//		 * For starting cut-and-paste
//		 */
//		menuItemCutAndPaste = buttonFactory
//				.create(this, JMenuItem.class,
//						StringID.CUT_PASTE_ID, null);
	}

	private void addActionListenersToComponents() {
//		menuItemOpen.addActionListener(e -> {
//			String path = loadFile(null);
//			screenUpdater.updateScreen();
//			updateMenu(path);
//			updateTitleText();
//		});

		menuItemOpen.setAccelerator(KeyStrokes.getWithControlDown(KeyEvent.VK_O));

//		addImportActionListener();

//		menuItemSave.addActionListener(e -> {
//			var filePath = document.getDataFilePath();
//			if (CreasePatternFileTypeKey.OPX.extensionsMatch(filePath)) {
//				saveProjectFile(document, filePath, CreasePatternFileTypeKey.OPX);
//			} else if (CreasePatternFileTypeKey.FOLD.extensionsMatch(filePath)) {
//				saveProjectFile(document, filePath, CreasePatternFileTypeKey.FOLD);
//			} else {
//				saveAnyTypeUsingGUI();
//			}
//		});
		menuItemSave.setAccelerator(KeyStrokes.getWithControlDown(KeyEvent.VK_S));

//		menuItemSaveAs.addActionListener(e -> saveAnyTypeUsingGUI());

//		menuItemExportFOLD.addActionListener(e -> {
//			String lastDirectory = fileHistory.getLastDirectory();
//			saveFile(lastDirectory, document.getDataFileName(),
//					filterSelector.getFilter(CreasePatternFileTypeKey.FOLD));
//		});

//		menuItemSaveAsImage.addActionListener(e -> {
//			String lastDirectory = fileHistory.getLastDirectory();
//			saveFile(lastDirectory, document.getDataFileName(),
//					filterSelector.getFilter(CreasePatternFileTypeKey.PICT));
//		});

//		menuItemExit.addActionListener(e -> exit());

//		menuItemUndo.addActionListener(e -> {
//			try {
//				actionHolder.getMouseAction().undo(paintContext);
//			} catch (NullPointerException ex) {
//				if (actionHolder.getMouseAction() == null) {
//					logger.error("mouseAction should not be null.", ex);
//				} else {
//					logger.error("Wrong implementation.", ex);
//				}
//			}
//			screenUpdater.updateScreen();
//		});
		menuItemUndo.setAccelerator(KeyStrokes.getWithControlDown(KeyEvent.VK_Z));

//		menuItemRedo.addActionListener(e -> {
//			try {
//				actionHolder.getMouseAction().redo(paintContext);
//			} catch (NullPointerException ex) {
//				if (actionHolder.getMouseAction() == null) {
//					logger.error("mouseAction should not be null.", ex);
//				} else {
//					logger.error("Wrong implementation.", ex);
//				}
//			}
//			screenUpdater.updateScreen();
//		});
		menuItemRedo.setAccelerator(KeyStrokes.getWithControlDown(KeyEvent.VK_Y));

//		menuItemClear.addActionListener(e -> clear());
		menuItemClear.setAccelerator(KeyStrokes.getWithControlDown(KeyEvent.VK_N));

//		menuItemAbout.addActionListener(e -> dialogService.showAboutAppMessage(this));

//		menuItemExportDXF
//				.addActionListener(e -> saveFileWithModelCheck(CreasePatternFileTypeKey.DXF));
//		menuItemExportCP
//				.addActionListener(e -> saveFileWithModelCheck(CreasePatternFileTypeKey.CP));
//		menuItemExportSVG
//				.addActionListener(e -> saveFileWithModelCheck(CreasePatternFileTypeKey.SVG));

//		menuItemProperty.addActionListener(e -> showPropertyDialog());
//		menuItemRepeatCopy.addActionListener(e -> showArrayCopyDialog());
//		menuItemCircleCopy.addActionListener(e -> showCircleCopyDialog());

//		menuItemSelectAll.addActionListener(
//				new SelectAllLineActionListener(paintContext));
		menuItemSelectAll.setAccelerator(KeyStrokes.getWithControlDown(KeyEvent.VK_A));

//		var statePopper = new StatePopper<EditMode>(stateManager);
//		menuItemUnSelectAll.addActionListener(
//				new UnselectAllItemsActionListener(actionHolder, paintContext, statePopper,
//						screenUpdater));
		menuItemUnSelectAll.setAccelerator(KeyStrokes.get(KeyEvent.VK_ESCAPE));

//		menuItemDeleteSelectedLines
//				.addActionListener(
//						new DeleteSelectedLinesActionListener(paintContext, screenUpdater));
		menuItemDeleteSelectedLines.setAccelerator(KeyStrokes.get(KeyEvent.VK_DELETE));

		menuItemCopyAndPaste.setAccelerator(KeyStrokes.getWithControlDown(KeyEvent.VK_C));
		menuItemCutAndPaste.setAccelerator(KeyStrokes.getWithControlDown(KeyEvent.VK_X));

//		for (int i = 0; i < Constants.MRUFILE_NUM; i++) {
//			MRUFilesMenuItem[i].addActionListener(this::loadFileFromMRUFileMenuItem);
//		}

//		uiPanel.setEstimationResultSaveColorsListener((front, back) -> {
//			var property = document.getProperty();
//			property.putFrontColorCode(convertColorToCode(front));
//			property.putBackColorCode(convertColorToCode(back));
//
//			menuItemSave.doClick();
//		});
//
//		uiPanel.setPaperDomainOfModelChangeListener(
//				e -> mainScreen.setPaperDomainOfModel((RectangleDomain) e.getNewValue()));
	}

//	/**
//	 * Ensure the execution order as loading file comes first.
//	 */
//	private void addImportActionListener() {
//		var original = Arrays.asList(menuItemImport.getActionListeners());
//		original.forEach(menuItemImport::removeActionListener);
//
//		menuItemImport.addActionListener(e -> {
//			try {
//				dataFileAccess
//						.loadFile(null, fileHistory.getLastPath(), this, filterSelector.getLoadables())
//						.ifPresent(otherDoc -> {
//							paintContext.getPainter().resetSelectedOriLines();
//							var otherCreasePattern = otherDoc.getCreasePattern();
//							otherCreasePattern.forEach(l -> l.selected = true);
//							paintContext.getCreasePattern().addAll(otherCreasePattern);
//						});
//				original.forEach(listener -> listener.actionPerformed(e));
//			} catch (IllegalArgumentException | FileVersionError | WrongDataFormatException | IOException ex) {
//				logger.error("failed to load (import)", ex);
//				Dialogs.showErrorDialog(this, resourceHolder.getString(
//						ResourceKey.ERROR, StringID.Error.LOAD_FAILED_ID), ex);
//			}
//		});
//	}

//	private void modifySavingActions() {
//		// overwrite the action to update GUI after saving.
//		setProjectSavingAction(CreasePatternFileTypeKey.OPX);
//		setProjectSavingAction(CreasePatternFileTypeKey.FOLD);
//	}

//	private void setProjectSavingAction(final CreasePatternFileTypeKey fileType) {
//		filterSelector.getFilter(fileType).setSavingAction(
//				new AbstractSavingAction<Doc>() {
//
//					@Override
//					public boolean save(final Doc data) {
//						try {
//							saveProjectFile(data, getPath(), fileType);
//						} catch (Exception e) {
//							logger.error("Failed to save file " + getPath(), e);
//							return false;
//						}
//						return true;
//					}
//				});
//
//	}

//	private void loadFileFromMRUFileMenuItem(final ActionEvent e) {
//
//		var menuItem = (JMenuItem) (e.getSource());
//		try {
//			String filePath = menuItem.getText();
//			loadFile(filePath);
//			updateTitleText();
//		} catch (Exception ex) {
//			logger.error("error when loading: ", ex);
//			Dialogs.showErrorDialog(this, resourceHolder.getString(
//					ResourceKey.ERROR, StringID.Error.LOAD_FAILED_ID), ex);
//		}
//		screenUpdater.updateScreen();
//	}

//	private void saveAnyTypeUsingGUI() {
//		String lastDirectory = fileHistory.getLastDirectory();
//
//		String path = saveFile(lastDirectory, document.getDataFileName(),
//				filterSelector.getSavables());
//
//		updateMenu(path);
//		updateTitleText();
//	}

//	private void exit() {
//		saveIniFile();
//		System.exit(0);
//	}

//	private void clear() {
//		document.set(new Doc(Constants.DEFAULT_PAPER_SIZE));
//
//		paintContextModification
//				.setCreasePatternToPaintContext(document.getCreasePattern(), paintContext);
//
//		screenSetting.setGridVisible(true);
//
//		childFrameManager.closeAllChildrenRecursively(this);
//
//		screenUpdater.updateScreen();
////		updateTitleText();
//		titleUpdateListener.run();
//	}

//	private void showPropertyDialog() {
//		PropertyDialog dialog = new PropertyDialog(this, document);
//
//		Rectangle rec = getBounds();
//		dialog.setLocation(
//				(int) (rec.getCenterX() - dialog.getWidth() / 2),
//				(int) (rec.getCenterY() - dialog.getHeight() / 2));
//		dialog.setModal(true);
//		dialog.setVisible(true);
//	}

//	private void showArrayCopyDialog() {
//		copyDialogOpener.showArrayCopyDialog(this, paintContext, screenUpdater);
//	}
//
//	private void showCircleCopyDialog() {
//		copyDialogOpener.showCircleCopyDialog(this, paintContext, screenUpdater);
//	}

//	private void updateTitleText() {
//		String fileName;
//		if (document.getDataFilePath().isEmpty()) {
//			fileName = resourceHolder.getString(ResourceKey.DEFAULT, StringID.Default.FILE_NAME_ID);
//		} else {
//			fileName = document.getDataFileName();
//		}
//
//		setTitle(fileName + " - "
//				+ resourceHolder.getString(ResourceKey.LABEL, StringID.Main.TITLE_ID));
//	}

//	/**
//	 * saves project without opening a dialog
//	 */
//	private void saveProjectFile(final Doc doc, final String filePath,
//			final CreasePatternFileTypeKey fileType) {
//		try {
//			dataFileAccess.saveProjectFile(doc, filePath, fileType);
//		} catch (IOException | IllegalArgumentException e) {
//			logger.error("Failed to save", e);
//			Dialogs.showErrorDialog(this, resourceHolder.getString(
//					ResourceKey.ERROR, StringID.Error.SAVE_FAILED_ID), e);
//		}
//
//		paintContext.creasePatternUndo().clearChanged();
//
//		updateMenu(filePath);
//		updateTitleText();
//	}

//	/**
//	 * save file without origami model check
//	 */
//	@SafeVarargs
//	private String saveFile(final String directory, final String fileName,
//			final FileAccessSupportFilter<Doc>... filters) {
//
//		try {
//			return dataFileAccess.saveFile(
//					document, directory, fileName, this, filters)
//					.map(path -> {
//						paintContext.creasePatternUndo().clearChanged();
//						return path;
//					})
//					.orElse(document.getDataFilePath());
//		} catch (IOException | IllegalArgumentException e) {
//			logger.error("failed to save", e);
//			Dialogs.showErrorDialog(this, resourceHolder.getString(
//					ResourceKey.ERROR, StringID.Error.SAVE_FAILED_ID), e);
//			return document.getDataFilePath();
//		}
//	}

//	/**
//	 * Open Save File As Dialogue for specific file types {@code type}. Runs a
//	 * model check before saving.
//	 */
//	private void saveFileWithModelCheck(final CreasePatternFileTypeKey type) {
//
//		try {
//			dataFileAccess.saveFileWithModelCheck(document, fileHistory.getLastDirectory(),
//					filterSelector.getFilter(type), this,
//					() -> dialogService.showModelBuildFailureDialog(this) == JOptionPane.OK_OPTION);
//		} catch (IOException e) {
//			logger.error("IO trouble", e);
//			Dialogs.showErrorDialog(this, resourceHolder.getString(
//					ResourceKey.ERROR, StringID.Error.SAVE_FAILED_ID), e);
//		} catch (IllegalArgumentException e) {
//			logger.error("Maybe data is not appropriate.", e);
//			Dialogs.showErrorDialog(this, resourceHolder.getString(
//					ResourceKey.ERROR, StringID.Error.SAVE_FAILED_ID), e);
//		}
//	}

	@Override
	public void buildFileMenu() {
		menuFile.removeAll();

		menuFile.add(menuItemClear);
		menuFile.add(menuItemOpen);
		menuFile.add(menuItemImport);
		menuFile.add(menuItemSave);
		menuFile.add(menuItemSaveAs);
		menuFile.add(menuItemSaveAsImage);
		menuFile.add(menuItemExportFOLD);
		menuFile.add(menuItemExportDXF);
		menuFile.add(menuItemExportCP);
		menuFile.add(menuItemExportSVG);
		menuFile.addSeparator();
		menuFile.add(menuItemProperty);
		menuFile.addSeparator();

		for (int i = 0; i < MRUFilesMenuItem.length; i++) {
			MRUFilesMenuItemUpdateListener.accept(i);
			menuFile.add(MRUFilesMenuItem[i]);
		}

//		for (String path : fileHistory.getHistory()) {
//			MRUFilesMenuItemUpdateListener.accept(i);
//			menuFile.add(MRUFilesMenuItem[i]);
//
//			i++;
//		}
//		while (i < MRUFilesMenuItem.length) {
//			MRUFilesMenuItem[i].setText("");
//			i++;
//		}

		menuFile.addSeparator();
		menuFile.add(menuItemExit);
	}

	@Override
	public void addMRUFilesMenuItemUpdateListener(final Consumer<Integer> listener) {
		MRUFilesMenuItemUpdateListener = listener;
	}

//	/**
//	 * Update file menu. Do nothing if the given {@code filePath} is null or
//	 * wrong.
//	 *
//	 * @param filePath
//	 */
//	private void updateMenu(final String filePath) {
//		if (filePath == null) {
//			return;
//		}
//		try {
//			filterSelector.getLoadableFilterOf(filePath);
//		} catch (IllegalArgumentException e) {
//			logger.debug("updating menu is canceled.", e);
//			return;
//		}
//		fileHistory.useFile(filePath);
//
//		buildFileMenu();
//	}

//	/**
//	 * if filePath is null, this method opens a dialog to select the target.
//	 * otherwise, it tries to read data from the path.
//	 *
//	 * @param filePath
//	 * @return file path for loaded file. {@code null} if loading is not done.
//	 */
//	private String loadFile(final String filePath) {
//		childFrameManager.closeAllChildrenRecursively(this);
//
//		try {
//			return dataFileAccess.loadFile(
//					filePath, fileHistory.getLastPath(), this, filterSelector.getLoadables())
//					.map(doc -> {
//						// we can't substitute a loaded object because
//						// the document object is referred by screen and UI
//						// panel as a Holder.
//						document.set(doc);
//
//						var property = document.getProperty();
//						uiPanel.setEstimationResultColors(
//								convertCodeToColor(property.extractFrontColorCode()),
//								convertCodeToColor(property.extractBackColorCode()));
//
//						screenSetting.setGridVisible(false);
//						paintContextModification
//								.setCreasePatternToPaintContext(
//										document.getCreasePattern(), paintContext);
//						return document.getDataFilePath();
//					}).orElse(null);
//		} catch (FileVersionError | IllegalArgumentException | WrongDataFormatException
//				| IOException e) {
//			logger.error("failed to load", e);
//			Dialogs.showErrorDialog(this, resourceHolder.getString(
//					ResourceKey.ERROR, StringID.Error.LOAD_FAILED_ID), e);
//			return document.getDataFilePath();
//		}
//	}

//	private String convertColorToCode(final Color color) {
//		return String.format("#%06X", color.getRGB() & 0x00FFFFFF);
//	}

//	private Color convertCodeToColor(final String code) {
//		if (code == null) {
//			return null;
//		}
//
//		try {
//			return new Color(Integer.decode(code));
//		} catch (NumberFormatException e) {
//			return null;
//		}
//	}
//
//	private void saveIniFile() {
//		try {
//			iniFileAccess.save(fileHistory, viewContext);
//		} catch (IllegalStateException e) {
//			logger.error("error when building ini file data", e);
//			Dialogs.showErrorDialog(this, resourceHolder.getString(
//					ResourceKey.ERROR, StringID.Error.SAVE_INI_FAILED_ID), e);
//		}
//	}
//
//	private void loadIniFile() {
//		var ini = iniFileAccess.load();
//
//		fileHistory.loadFromInitData(ini);
//		screenSetting.setZeroLineWidth(ini.isZeroLineWidth());
//
//		logger.debug("loaded ini.mvLineVisible: " + ini.isMvLineVisible());
//		screenSetting.setMVLineVisible(ini.isMvLineVisible());
//
//		logger.debug("loaded ini.auxLineVisible: " + ini.isAuxLineVisible());
//		screenSetting.setAuxLineVisible(ini.isAuxLineVisible());
//
//		logger.debug("loaded ini.vertexVisible: " + ini.isVertexVisible());
//		screenSetting.setVertexVisible(ini.isVertexVisible());
//	}

	@Override
	public void componentResized(final ComponentEvent arg0) {
	}

	@Override
	public void componentMoved(final ComponentEvent arg0) {
	}

	@Override
	public void componentShown(final ComponentEvent arg0) {
	}

	@Override
	public void componentHidden(final ComponentEvent arg0) {
	}

	@Override
	public void windowOpened(final WindowEvent arg0) {
	}

	@Override
	public void windowClosing(final WindowEvent arg0) {
		windowClosingListener.run();
//		if (paintContext.creasePatternUndo().changeExists()) {
//			// confirm saving edited opx
//			int selected = dialogService.showSaveOnCloseDialog(this);
//			if (selected == JOptionPane.YES_OPTION) {
//
//				document.setCreasePattern(paintContext.getCreasePattern());
//
//				String path = saveFile(fileHistory.getLastDirectory(),
//						document.getDataFileName(), filterSelector.getSavables());
//				if (path == null) {
//
//				}
//			}
//		}
//
//		saveIniFile();
	}

	@Override
	public void windowClosed(final WindowEvent arg0) {
	}

	@Override
	public void windowIconified(final WindowEvent arg0) {
	}

	@Override
	public void windowDeiconified(final WindowEvent arg0) {
	}

	@Override
	public void windowActivated(final WindowEvent arg0) {
	}

	@Override
	public void windowDeactivated(final WindowEvent arg0) {
	}

	private void addHintPropertyChangeListenersToSetting() {
		setting.addPropertyChangeListener(MainFrameSetting.HINT, e -> {
			hintLabel.setText("    " + (String) e.getNewValue());
			hintLabel.repaint();
		});

	}

	@Override
	public void setViewVisible(final boolean visible) {
		setVisible(visible);
	}

	@Override
	public MainFrameSetting getMainFrameSetting() {
		return setting;
	}

	@Override
	public UIPanelView getUIPanelView() {
		return uiPanel;
	}

	@Override
	public UIPanelSetting getUIPanelSetting() {
		return uiPanel.getUIPanelSetting();
	}

	@Override
	public PainterScreenView getPainterScreenView() {
		return mainScreen;
	}

	@Override
	public MainScreenSetting getMainScreenSetting() {
		return mainScreen.getMainScreenSetting();
	}

	@Override
	public MainScreenUpdater getScreenUpdater() {
		return mainScreen.getScreenUpdater();
	}

	@Override
	public void addClearButtonListener(final Runnable listener) {
		menuItemClear.addActionListener(e -> listener.run());
	}

	@Override
	public void addOpenButtonListener(final Runnable listener) {
		menuItemOpen.addActionListener(e -> listener.run());
	}

	@Override
	public void addImportButtonListener(final Runnable listener) {
		menuItemImport.addActionListener(e -> listener.run());
	}

	@Override
	public void addSaveButtonLisetener(final Runnable listener) {
		menuItemSave.addActionListener(e -> listener.run());

	}

	@Override
	public void addSaveAsButtonListener(final Runnable listener) {
		menuItemSaveAs.addActionListener(e -> listener.run());

	}

	@Override
	public void addSaveAsImageButtonListener(final Runnable listener) {
		menuItemSaveAsImage.addActionListener(e -> listener.run());
	}

	@Override
	public void addExportFOLDButtonListener(final Runnable listener) {
		menuItemExportFOLD.addActionListener(e -> listener.run());
	}

	@Override
	public void addExportDXFButtonListener(final Runnable listener) {
		menuItemExportDXF.addActionListener(e -> listener.run());
	}

	@Override
	public void addExportCPButtonListener(final Runnable listener) {
		menuItemExportCP.addActionListener(e -> listener.run());
	}

	@Override
	public void addExportSVGButtonListener(final Runnable listener) {
		menuItemExportSVG.addActionListener(e -> listener.run());
	}

	@Override
	public void addPropertyButtonListener(final Runnable listener) {
		menuItemProperty.addActionListener(e -> listener.run());
	}

	@Override
	public void addChangeOutlineButtonListener(final Runnable listener) {
		menuItemChangeOutline.addActionListener(e -> listener.run());
	}

	@Override
	public void addSelectAllButtonListener(final Runnable listener) {
		menuItemSelectAll.addActionListener(e -> listener.run());
	}

	@Override
	public void addCopyAndPasteButtonListener(final Runnable listener) {
		menuItemCopyAndPaste.addActionListener(e -> listener.run());
	}

	@Override
	public void addCutAndPasteButtonListener(final Runnable listener) {
		menuItemCutAndPaste.addActionListener(e -> listener.run());
	}

	@Override
	public void addUndoButtonListener(final Runnable listener) {
		menuItemUndo.addActionListener(e -> listener.run());
	}

	@Override
	public void addRedoButtonListener(final Runnable listener) {
		menuItemRedo.addActionListener(e -> listener.run());
	}

	@Override
	public void addRepeatCopyButtonListener(final Runnable listener) {
		menuItemRepeatCopy.addActionListener(e -> listener.run());
	}

	@Override
	public void addCircleCopyButtonListener(final Runnable listener) {
		menuItemCircleCopy.addActionListener(e -> listener.run());
	}

	@Override
	public void addUnselectAllButtonListener(final Runnable listener) {
		menuItemUnSelectAll.addActionListener(e -> listener.run());
	}

	@Override
	public void addDeleteSelectedLinesButtonListener(final Runnable listener) {
		menuItemDeleteSelectedLines.addActionListener(e -> listener.run());
	}

	@Override
	public void addAboutButtonListener(final Runnable listener) {
		menuItemAbout.addActionListener(e -> listener.run());
	}

	@Override
	public void addExitButtonListener(final Runnable listener) {
		menuItemExit.addActionListener(e -> listener.run());
	}

	@Override
	public void setEstimationResultSaveColorsListener(final BiConsumer<Color, Color> listener) {
		uiPanel.setEstimationResultSaveColorsListener((front, back) -> {
			listener.accept(front, back);
			menuItemSave.doClick();
		});
	}

	@Override
	public void addMRUFileButtonListener(final Consumer<String> listener) {
		for (var item : MRUFilesMenuItem) {
			item.addActionListener(e -> listener.accept(item.getText()));
		}
	}

	@Override
	public void setTitleUpdateListener(final Runnable listener) {
		titleUpdateListener = listener;
	}

	@Override
	public void addWindowClosingListener(final Runnable listener) {
		windowClosingListener = listener;
	}

	@Override
	public void setMRUFilesMenuItem(final int index, final String path) {
		MRUFilesMenuItem[index].setText(path);
	}

	@Override
	public void setFileNameToTitle(final String fileName) {
		setTitle(fileName + " - "
				+ resourceHolder.getString(ResourceKey.LABEL, StringID.Main.TITLE_ID));
	}

	@Override
	public void setPaperDomainOfModelChangeListener(final Consumer<RectangleDomain> listener) {
		uiPanel.setPaperDomainOfModelChangeListener(e -> listener.accept((RectangleDomain) e.getNewValue()));
	}

//	@Override
//	public void setPaperDomainOfModel(final RectangleDomain domain) {
//		mainScreen.setPaperDomainOfModel(domain);
//	}

	@Override
	public PaintContext getPaintContext() {
		return paintContext;
	}

	@Override
	public CreasePatternViewContext getCreasePattenViewContext() {
		return viewContext;
	}

	@Override
	public MouseActionHolder getActionHolder() {
		return actionHolder;
	}

	@Override
	public Doc getDocument() {
		return document;
	}

	@Override
	public StateManager<EditMode> getStateManager() {
		return stateManager;
	}
}
