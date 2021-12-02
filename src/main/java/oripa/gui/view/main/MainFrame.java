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
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.stream.IntStream;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.application.main.DataFileAccess;
import oripa.application.main.IniFileAccess;
import oripa.application.main.PaintContextModification;
import oripa.appstate.StatePopper;
import oripa.doc.Doc;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.PaintContextFactory;
import oripa.file.FileHistory;
import oripa.file.ImageResourceLoader;
import oripa.file.InitDataFileReader;
import oripa.file.InitDataFileWriter;
import oripa.gui.bind.ButtonFactory;
import oripa.gui.bind.PaintActionButtonFactory;
import oripa.gui.bind.state.EditModeStateManager;
import oripa.gui.bind.state.PaintBoundStateFactory;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.presenter.creasepattern.CreasePatternViewContextFactory;
import oripa.gui.presenter.creasepattern.DeleteSelectedLinesActionListener;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.creasepattern.MouseActionHolder;
import oripa.gui.presenter.creasepattern.SelectAllLineActionListener;
import oripa.gui.presenter.creasepattern.UnselectAllItemsActionListener;
import oripa.gui.presenter.main.CopyDialogOpener;
import oripa.gui.view.util.ChildFrameManager;
import oripa.gui.view.util.Dialogs;
import oripa.gui.view.util.KeyStrokes;
import oripa.gui.viewsetting.ViewScreenUpdater;
import oripa.gui.viewsetting.main.MainFrameSetting;
import oripa.gui.viewsetting.main.MainScreenSetting;
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

public class MainFrame extends JFrame implements ComponentListener, WindowListener {

	private static final Logger logger = LoggerFactory.getLogger(MainFrame.class);

	private static final long serialVersionUID = 272369294032419950L;

	// shared objects
	private final ResourceHolder resourceHolder = ResourceHolder.getInstance();

	private final EditModeStateManager stateManager = new EditModeStateManager();

	private final MainFrameSetting setting = new MainFrameSetting();
	private final MainScreenSetting screenSetting;

	private final ViewScreenUpdater screenUpdater;

	private final ChildFrameManager childFrameManager = new ChildFrameManager();

	private final FileHistory fileHistory = new FileHistory(Constants.MRUFILE_NUM);

	private final AbstractFilterSelector<Doc> filterSelector = new DocFilterSelector();

	private final Doc document = new Doc();

	// Create UI Factories
	private final PaintContextFactory contextFactory = new PaintContextFactory();
	private final PaintContext paintContext = contextFactory.createContext();
	private final CreasePatternViewContextFactory viewContextFactory = new CreasePatternViewContextFactory();
	private final CreasePatternViewContext viewContext = viewContextFactory.create(paintContext);
	private final MouseActionHolder actionHolder = new MouseActionHolder();

	private final ButtonFactory buttonFactory;

	private final MainDialogService dialogService = new MainDialogService(resourceHolder);
	private final CopyDialogOpener copyDialogOpener;
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

	// edit menu items
	/**
	 * For changing outline
	 */
	private JMenuItem menuItemChangeOutline;

	/**
	 * For selecting all lines
	 */
	private JMenuItem menuItemSelectAll;

	/**
	 * For starting copy-and-paste
	 */
	private JMenuItem menuItemCopyAndPaste;

	/**
	 * For starting cut-and-paste
	 */
	private JMenuItem menuItemCutAndPaste;

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

	// -----------------------------------------------------------------------------------------------------------
	// Create paint button

	private final IniFileAccess iniFileAccess = new IniFileAccess(
			new InitDataFileReader(), new InitDataFileWriter());
	private final DataFileAccess dataFileAccess = new DataFileAccess(new DocDAO(new DocFilterSelector()));
	private final PaintContextModification paintContextModification = new PaintContextModification();

	public MainFrame() {
		logger.info("frame construction starts.");

		document.setCreasePattern(paintContext.getCreasePattern());

		var mainScreen = new PainterScreen(actionHolder,
				viewContext, paintContext,
				document);
		screenUpdater = mainScreen.getScreenUpdater();
		screenSetting = mainScreen.getMainScreenSetting();

		var originHolder = screenSetting.getSelectionOriginHolder();

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

		var stateFactory = new PaintBoundStateFactory(
				stateManager, setting,
				uiPanel.getUIPanelSetting(),
				originHolder);
		buttonFactory = new PaintActionButtonFactory(
				stateFactory, paintContext, actionHolder, screenUpdater);

		// Setup Dialog Windows
		copyDialogOpener = new CopyDialogOpener(
				new ArrayCopyDialogFactory(),
				new CircleCopyDialogFactory(),
				() -> dialogService.showNoSelectionMessageForArrayCopy(this),
				() -> dialogService.showNoSelectionMessageForCircleCopy(this));

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(uiScroll, BorderLayout.WEST);
		getContentPane().add(mainScreen, BorderLayout.CENTER);
		getContentPane().add(hintLabel, BorderLayout.SOUTH);

		ImageResourceLoader imgLoader = new ImageResourceLoader();
		this.setIconImage(imgLoader.loadAsIcon("icon/oripa.gif", getClass())
				.getImage());

		addWindowListener(this);

		loadIniFile();

		createPaintMenuItems();
		IntStream.range(0, Constants.MRUFILE_NUM)
				.forEach(i -> MRUFilesMenuItem[i] = new JMenuItem());
		addActionListenersToComponents();

		// Building the menu bar
		JMenuBar menuBar = new JMenuBar();
		buildFileMenu();

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

		modifySavingActions();

		updateTitleText();
	}

	private void createPaintMenuItems() {
		/*
		 * For changing outline
		 */
		menuItemChangeOutline = (JMenuItem) buttonFactory
				.create(this, JMenuItem.class,
						StringID.EDIT_CONTOUR_ID, null);

		/*
		 * For selecting all lines
		 */
		menuItemSelectAll = (JMenuItem) buttonFactory
				.create(this, JMenuItem.class,
						StringID.SELECT_ALL_LINE_ID, null);

		/*
		 * For starting copy-and-paste
		 */
		menuItemCopyAndPaste = (JMenuItem) buttonFactory
				.create(this, JMenuItem.class,
						StringID.COPY_PASTE_ID, null);

		/*
		 * For starting cut-and-paste
		 */
		menuItemCutAndPaste = (JMenuItem) buttonFactory
				.create(this, JMenuItem.class,
						StringID.CUT_PASTE_ID, null);
	}

	private void addActionListenersToComponents() {
		menuItemOpen.addActionListener(e -> {
			String path = loadFile(null);
			screenUpdater.updateScreen();
			updateMenu(path);
			updateTitleText();
		});

		menuItemOpen.setAccelerator(KeyStrokes.getWithControlDown(KeyEvent.VK_O));

		menuItemSave.addActionListener(e -> {
			var filePath = document.getDataFilePath();
			if (CreasePatternFileTypeKey.OPX.extensionsMatch(filePath)) {
				saveProjectFile(document, filePath, CreasePatternFileTypeKey.OPX);
			} else if (CreasePatternFileTypeKey.FOLD.extensionsMatch(filePath)) {
				saveProjectFile(document, filePath, CreasePatternFileTypeKey.FOLD);
			} else {
				saveAnyTypeUsingGUI();
			}
		});
		menuItemSave.setAccelerator(KeyStrokes.getWithControlDown(KeyEvent.VK_S));

		menuItemSaveAs.addActionListener(e -> saveAnyTypeUsingGUI());

		menuItemExportFOLD.addActionListener(e -> {
			String lastDirectory = fileHistory.getLastDirectory();
			saveFile(lastDirectory, document.getDataFileName(),
					filterSelector.getFilter(CreasePatternFileTypeKey.FOLD));
		});

		menuItemSaveAsImage.addActionListener(e -> {
			String lastDirectory = fileHistory.getLastDirectory();
			saveFile(lastDirectory, document.getDataFileName(),
					filterSelector.getFilter(CreasePatternFileTypeKey.PICT));
		});

		menuItemExit.addActionListener(e -> exit());

		menuItemUndo.addActionListener(e -> {
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
		menuItemUndo.setAccelerator(KeyStrokes.getWithControlDown(KeyEvent.VK_Z));

		menuItemRedo.addActionListener(e -> {
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
		menuItemRedo.setAccelerator(KeyStrokes.getWithControlDown(KeyEvent.VK_Y));

		menuItemClear.addActionListener(e -> clear());
		menuItemClear.setAccelerator(KeyStrokes.getWithControlDown(KeyEvent.VK_N));

		menuItemAbout.addActionListener(e -> JOptionPane.showMessageDialog(this,
				resourceHolder.getString(ResourceKey.APP_INFO, StringID.AppInfo.ABOUT_THIS_ID),
				resourceHolder.getString(ResourceKey.LABEL, StringID.Main.TITLE_ID),
				JOptionPane.INFORMATION_MESSAGE));

		menuItemExportDXF
				.addActionListener(e -> saveFileWithModelCheck(CreasePatternFileTypeKey.DXF));
		menuItemExportCP
				.addActionListener(e -> saveFileWithModelCheck(CreasePatternFileTypeKey.CP));
		menuItemExportSVG
				.addActionListener(e -> saveFileWithModelCheck(CreasePatternFileTypeKey.SVG));

		menuItemProperty.addActionListener(e -> showPropertyDialog());
		menuItemRepeatCopy.addActionListener(e -> showArrayCopyDialog());
		menuItemCircleCopy.addActionListener(e -> showCircleCopyDialog());

		menuItemSelectAll.addActionListener(
				new SelectAllLineActionListener(paintContext));
		menuItemSelectAll.setAccelerator(KeyStrokes.getWithControlDown(KeyEvent.VK_A));

		var statePopper = new StatePopper<EditMode>(stateManager);
		menuItemUnSelectAll.addActionListener(
				new UnselectAllItemsActionListener(actionHolder, paintContext, statePopper,
						screenUpdater));
		menuItemUnSelectAll.setAccelerator(KeyStrokes.get(KeyEvent.VK_ESCAPE));

		menuItemDeleteSelectedLines
				.addActionListener(
						new DeleteSelectedLinesActionListener(paintContext, screenUpdater));
		menuItemDeleteSelectedLines.setAccelerator(KeyStrokes.get(KeyEvent.VK_DELETE));

		menuItemCopyAndPaste.setAccelerator(KeyStrokes.getWithControlDown(KeyEvent.VK_C));
		menuItemCutAndPaste.setAccelerator(KeyStrokes.getWithControlDown(KeyEvent.VK_X));

		for (int i = 0; i < Constants.MRUFILE_NUM; i++) {
			MRUFilesMenuItem[i].addActionListener(this::loadFileFromMRUFileMenuItem);
		}

		uiPanel.setEstimationResultSaveColorsListener((front, back) -> {
			var property = document.getProperty();
			property.putFrontColorCode(convertColorToCode(front));
			property.putBackColorCode(convertColorToCode(back));

			uiPanel.setEstimationResultColors(front, back);

			menuItemSave.doClick();
		});
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

	private void loadFileFromMRUFileMenuItem(final ActionEvent e) {

		var menuItem = (JMenuItem) (e.getSource());
		try {
			String filePath = menuItem.getText();
			loadFile(filePath);
			updateTitleText();
		} catch (Exception ex) {
			logger.error("error when loading: ", ex);
			Dialogs.showErrorDialog(this, resourceHolder.getString(
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

		childFrameManager.closeAllChildrenRecursively(this);

		screenUpdater.updateScreen();
		updateTitleText();
	}

	private void showPropertyDialog() {
		AbstractPropertyDialog dialog = new PropertyDialog(this, document);

		dialog.setValue();
		Rectangle rec = getBounds();
		dialog.setLocation(
				(int) (rec.getCenterX() - dialog.getWidth() / 2),
				(int) (rec.getCenterY() - dialog.getHeight() / 2));
		dialog.setModal(true);
		dialog.setVisible(true);
	}

	private void showArrayCopyDialog() {
		copyDialogOpener.showArrayCopyDialog(this, paintContext, screenUpdater);
	}

	private void showCircleCopyDialog() {
		copyDialogOpener.showCircleCopyDialog(this, paintContext, screenUpdater);
	}

	private void updateTitleText() {
		String fileName;
		if (document.getDataFilePath().isEmpty()) {
			fileName = resourceHolder.getString(ResourceKey.DEFAULT, StringID.Default.FILE_NAME_ID);
		} else {
			fileName = document.getDataFileName();
		}

		setTitle(fileName + " - "
				+ resourceHolder.getString(ResourceKey.LABEL, StringID.Main.TITLE_ID));
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
			Dialogs.showErrorDialog(this, resourceHolder.getString(
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
					document, directory, fileName, this, filters)
					.map(path -> {
						paintContext.creasePatternUndo().clearChanged();
						return path;
					})
					.orElse(document.getDataFilePath());
		} catch (IOException | IllegalArgumentException e) {
			logger.error("failed to save", e);
			Dialogs.showErrorDialog(this, resourceHolder.getString(
					ResourceKey.ERROR, StringID.Error.SAVE_FAILED_ID), e);
			return document.getDataFilePath();
		}
	}

	/**
	 * Open Save File As Dialogue for specific file types {@code type}. Runs a
	 * model check before saving.
	 */
	private void saveFileWithModelCheck(final CreasePatternFileTypeKey type) {

		try {
			dataFileAccess.saveFileWithModelCheck(document, fileHistory.getLastDirectory(),
					filterSelector.getFilter(type), this,
					() -> dialogService.showModelBuildFailureDialog(this) == JOptionPane.OK_OPTION);
		} catch (IOException e) {
			logger.error("IO trouble", e);
			Dialogs.showErrorDialog(this, resourceHolder.getString(
					ResourceKey.ERROR, StringID.Error.SAVE_FAILED_ID), e);
		} catch (IllegalArgumentException e) {
			logger.error("Maybe data is not appropriate.", e);
			Dialogs.showErrorDialog(this, resourceHolder.getString(
					ResourceKey.ERROR, StringID.Error.SAVE_FAILED_ID), e);
		}
	}

	private void buildFileMenu() {
		menuFile.removeAll();

		menuFile.add(menuItemClear);
		menuFile.add(menuItemOpen);
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

		int i = 0;
		for (String path : fileHistory.getHistory()) {
			MRUFilesMenuItem[i].setText(path);
			menuFile.add(MRUFilesMenuItem[i]);

			i++;
		}
		while (i < MRUFilesMenuItem.length) {
			MRUFilesMenuItem[i].setText("");
			i++;
		}

		menuFile.addSeparator();
		menuFile.add(menuItemExit);
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

		buildFileMenu();
	}

	/**
	 * if filePath is null, this method opens a dialog to select the target.
	 * otherwise, it tries to read data from the path.
	 *
	 * @param filePath
	 * @return file path for loaded file. {@code null} if loading is not done.
	 */
	private String loadFile(final String filePath) {
		childFrameManager.closeAllChildrenRecursively(this);

		try {
			return dataFileAccess.loadFile(
					filePath, fileHistory.getLastPath(), this, filterSelector.getLoadables())
					.map(doc -> {
						// we can't substitute a loaded object because
						// the document object is referred by screen and UI
						// panel as a Holder.
						document.set(doc);

						var property = document.getProperty();
						uiPanel.setEstimationResultColors(
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
			Dialogs.showErrorDialog(this, resourceHolder.getString(
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
		return new Color(Integer.decode(code));
	}

	private void saveIniFile() {
		try {
			iniFileAccess.save(fileHistory, viewContext);
		} catch (IllegalStateException e) {
			logger.error("error when building ini file data", e);
			Dialogs.showErrorDialog(this, resourceHolder.getString(
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

		if (paintContext.creasePatternUndo().changeExists()) {
			// TODO: Use string resource.
			// confirm saving edited opx
			int selected = JOptionPane
					.showConfirmDialog(
							this,
							"The crease pattern has been modified. Would you like to save?",
							"Comfirm to save", JOptionPane.YES_NO_OPTION);
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
}
