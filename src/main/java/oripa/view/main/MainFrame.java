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

package oripa.view.main;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.Config;
import oripa.ORIPA;
import oripa.appstate.StateManager;
import oripa.bind.ButtonFactory;
import oripa.bind.PaintActionButtonFactory;
import oripa.bind.state.PaintBoundStateFactory;
import oripa.controller.DeleteSelectedLinesActionListener;
import oripa.controller.SelectAllLineActionListener;
import oripa.controller.UnselectAllLinesActionListener;
import oripa.domain.cptool.Painter;
import oripa.domain.paint.MouseActionHolder;
import oripa.domain.paint.PaintContextFactory;
import oripa.domain.paint.PaintContextInterface;
import oripa.file.FileHistory;
import oripa.file.ImageResourceLoader;
import oripa.persistent.doc.DocDAO;
import oripa.persistent.doc.DocFilterSelector;
import oripa.persistent.doc.FileTypeKey;
import oripa.persistent.doc.doc.Doc;
import oripa.persistent.filetool.AbstractSavingAction;
import oripa.persistent.filetool.FileAccessSupportFilter;
import oripa.persistent.filetool.FileChooserCanceledException;
import oripa.persistent.filetool.FileVersionError;
import oripa.persistent.filetool.WrongDataFormatException;
import oripa.resource.Constants;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;
import oripa.util.gui.ChildFrameManager;
import oripa.viewsetting.ViewScreenUpdater;
import oripa.viewsetting.main.MainFrameSetting;
import oripa.viewsetting.main.MainScreenSetting;

public class MainFrame extends JFrame implements ComponentListener, WindowListener {

	private static final Logger logger = LoggerFactory.getLogger(MainFrame.class);

	/**
	 *
	 */
	private static final long serialVersionUID = 272369294032419950L;

	private final MainFrameSetting setting = new MainFrameSetting();
	private final MainScreenSetting screenSetting;

	private final ChildFrameManager childFrameManager = ChildFrameManager.getManager();

	private final JMenu menuFile = new JMenu(
			ORIPA.res.getString(StringID.Main.FILE_ID));
	private final JMenu menuEdit = new JMenu(ORIPA.res.getString("Edit"));
	private final JMenu menuHelp = new JMenu(ORIPA.res.getString("Help"));
	private final JMenuItem menuItemClear = new JMenuItem(
			ORIPA.res.getString("New"));
	private final JMenuItem menuItemOpen = new JMenuItem(
			ORIPA.res.getString("Open"));

	private final JMenuItem menuItemSave = new JMenuItem(
			ORIPA.res.getString("Save"));
	private final JMenuItem menuItemSaveAs = new JMenuItem(
			ORIPA.res.getString("SaveAs"));
	private final JMenuItem menuItemSaveAsImage = new JMenuItem(
			ORIPA.res.getString("SaveAsImage"));

	private final JMenuItem menuItemExportFOLD = new JMenuItem("Export FOLD");
	private final JMenuItem menuItemExportDXF = new JMenuItem("Export DXF");
	private final JMenuItem menuItemExportOBJ = new JMenuItem("Export OBJ");
	private final JMenuItem menuItemExportCP = new JMenuItem("Export CP");
	private final JMenuItem menuItemExportSVG = new JMenuItem("Export SVG");

	// -----------------------------------------------------------------------------------------------------------
	// Create paint button

	private final PaintContextFactory contextFactory = new PaintContextFactory();
	private final PaintContextInterface paintContext = contextFactory.createContext();
	private final MouseActionHolder actionHolder = new MouseActionHolder();

	private final ButtonFactory buttonFactory;
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

	// ---------------------------------------------------------------------------------------------

	private final ViewScreenUpdater screenUpdater;
	private final ResourceHolder resourceHolder = ResourceHolder.getInstance();
	private final JMenuItem menuItemProperty = new JMenuItem(
			resourceHolder.getString(ResourceKey.LABEL,
					StringID.Main.PROPERTY_ID));

	private final JMenuItem menuItemExit = new JMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.Main.EXIT_ID));
	private final JMenuItem menuItemUndo = new JMenuItem(
			ORIPA.res.getString("Undo"));
	private final JMenuItem menuItemAbout = new JMenuItem(
			ORIPA.res.getString("About"));
	private final JMenuItem menuItemRepeatCopy = new JMenuItem("Array Copy");
	private final JMenuItem menuItemCircleCopy = new JMenuItem("Circle Copy");

	private final JMenuItem menuItemUnSelectAll = new JMenuItem("UnSelect All");

	private final JMenuItem menuItemDeleteSelectedLines = new JMenuItem(
			"Delete Selected Lines");
	private final JMenuItem[] MRUFilesMenuItem = new JMenuItem[Config.MRUFILE_NUM];

	private RepeatCopyDialog arrayCopyDialog;
	private CircleCopyDialog circleCopyDialog;
	public static JLabel hintLabel = new JLabel();

	private final FileHistory fileHistory = new FileHistory(Config.MRUFILE_NUM);

	private final DocFilterSelector filterSelector = new DocFilterSelector();

	private final Doc document = new Doc();

	public MainFrame() {
		logger.info("frame construction starts.");

		document.setCreasePattern(paintContext.getCreasePattern());

		addPropertyChangeListenersToSetting();

		var mainScreen = new PainterScreen(actionHolder, paintContext, document);
		screenUpdater = mainScreen.getScreenUpdater();
		screenSetting = mainScreen.getMainScreenSetting();

		var originHolder = screenSetting.getSelectionOriginHolder();

		var stateManager = new StateManager();

		UIPanel uiPanel = null;
		logger.info("start constructing UI panel.");
		try {
			uiPanel = new UIPanel(
					stateManager, screenUpdater, actionHolder, paintContext, document,
					document, setting, screenSetting);
			uiPanel.setChildFrameManager(childFrameManager);
		} catch (RuntimeException ex) {
			logger.error("UI panel construction failed", ex);
		}
		logger.info("end constructing UI panel.");

		var stateFactory = new PaintBoundStateFactory(
				stateManager, setting,
				uiPanel.getUIPanelSetting(),
				originHolder);
		buttonFactory = new PaintActionButtonFactory(stateFactory, paintContext);

		createPaintMenuItems();

		menuItemCopyAndPaste.setText(resourceHolder.getString(
				ResourceKey.LABEL, StringID.COPY_PASTE_ID));
		menuItemCutAndPaste.setText(resourceHolder.getString(ResourceKey.LABEL,
				StringID.CUT_PASTE_ID));
		// menuItemChangeOutline.setText(ORIPA.res.getString(StringID.Menu.CONTOUR_ID));

		addWindowListener(this);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(uiPanel, BorderLayout.WEST);
		getContentPane().add(mainScreen, BorderLayout.CENTER);
		getContentPane().add(hintLabel, BorderLayout.SOUTH);

		ImageResourceLoader imgLoader = new ImageResourceLoader();
		this.setIconImage(imgLoader.loadAsIcon("icon/oripa.gif", getClass())
				.getImage());

		IntStream.range(0, Config.MRUFILE_NUM)
				.forEach(i -> MRUFilesMenuItem[i] = new JMenuItem());

		addActionListenersToComponents();

		loadIniFile();

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
		menuEdit.add(menuItemChangeOutline);

		menuHelp.add(menuItemAbout);

		menuBar.add(menuFile);
		menuBar.add(menuEdit);
		menuBar.add(menuHelp);
		setJMenuBar(menuBar);

		modifySavingActions();
	}

	public void initialize() {
		arrayCopyDialog = new RepeatCopyDialog(this, paintContext);
		circleCopyDialog = new CircleCopyDialog(this, paintContext);
	}

	private void createPaintMenuItems() {
		/*
		 * For changing outline
		 */
		menuItemChangeOutline = (JMenuItem) buttonFactory
				.create(this, JMenuItem.class, actionHolder, screenUpdater,
						StringID.EDIT_CONTOUR_ID, null);

		/*
		 * For selecting all lines
		 */
		menuItemSelectAll = (JMenuItem) buttonFactory
				.create(this, JMenuItem.class, actionHolder, screenUpdater,
						StringID.SELECT_ALL_LINE_ID, null);

		/*
		 * For starting copy-and-paste
		 */
		menuItemCopyAndPaste = (JMenuItem) buttonFactory
				.create(this, JMenuItem.class, actionHolder, screenUpdater, StringID.COPY_PASTE_ID,
						null);

		/*
		 * For starting cut-and-paste
		 */
		menuItemCutAndPaste = (JMenuItem) buttonFactory
				.create(this, JMenuItem.class, actionHolder, screenUpdater, StringID.CUT_PASTE_ID,
						null);
	}

	private void addActionListenersToComponents() {
		menuItemOpen.addActionListener(e -> {
			String path = openFile(null);
			screenUpdater.updateScreen();
			updateMenu(path);
			updateTitleText();
		});

		menuItemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				InputEvent.CTRL_DOWN_MASK));

		menuItemSave.addActionListener(e -> {
			var filePath = document.getDataFilePath();
			if (FileTypeKey.OPX.extensionsMatch(filePath)) {
				saveProjectFile(document, filePath, FileTypeKey.OPX);
			} else if (FileTypeKey.FOLD.extensionsMatch(filePath)) {
				saveProjectFile(document, filePath, FileTypeKey.FOLD);
			} else {
				saveAnyTypeUsingGUI();
			}
		});
		menuItemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_DOWN_MASK));

		menuItemSaveAs.addActionListener(e -> saveAnyTypeUsingGUI());

		menuItemExportFOLD.addActionListener(e -> {
			String lastDirectory = fileHistory.getLastDirectory();
			saveFile(lastDirectory, document.getDataFileName(),
					filterSelector.getFilter(FileTypeKey.FOLD));
		});

		menuItemSaveAsImage.addActionListener(e -> {
			String lastDirectory = fileHistory.getLastDirectory();
			saveFile(lastDirectory, document.getDataFileName(),
					filterSelector.getFilter(FileTypeKey.PICT));
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
		menuItemUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
				InputEvent.CTRL_DOWN_MASK));

		menuItemClear.addActionListener(e -> clear());
		menuItemClear.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				InputEvent.CTRL_DOWN_MASK));

		menuItemAbout.addActionListener(e -> JOptionPane.showMessageDialog(this,
				resourceHolder.getString(ResourceKey.APP_INFO, StringID.AppInfo.ABOUT_THIS_ID),
				resourceHolder.getString(ResourceKey.LABEL, StringID.Main.TITLE_ID),
				JOptionPane.INFORMATION_MESSAGE));

		menuItemExportDXF.addActionListener(e -> saveFileWithModelCheck(FileTypeKey.DXF_MODEL));
		menuItemExportOBJ.addActionListener(e -> saveFileWithModelCheck(FileTypeKey.OBJ_MODEL));
		menuItemExportCP.addActionListener(e -> saveFileWithModelCheck(FileTypeKey.CP));
		menuItemExportSVG.addActionListener(e -> saveFileWithModelCheck(FileTypeKey.SVG));

		menuItemProperty.addActionListener(e -> showPropertyDialog());
		menuItemRepeatCopy.addActionListener(e -> showArrayCopyDialog());
		menuItemCircleCopy.addActionListener(e -> showCircleCopyDialog());

		menuItemSelectAll.addActionListener(
				new SelectAllLineActionListener(paintContext));
		menuItemSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
				InputEvent.CTRL_DOWN_MASK));

		menuItemUnSelectAll.addActionListener(
				new UnselectAllLinesActionListener(paintContext, screenUpdater));
		menuItemUnSelectAll.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_ESCAPE, 0));

		menuItemDeleteSelectedLines
				.addActionListener(
						new DeleteSelectedLinesActionListener(paintContext, screenUpdater));
		menuItemDeleteSelectedLines.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_DELETE, 0));

		menuItemCopyAndPaste.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
		menuItemCutAndPaste.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));

		for (int i = 0; i < Config.MRUFILE_NUM; i++) {
			MRUFilesMenuItem[i].addActionListener(this::openFileFromMRUFileMenuItem);
		}

	}

	private void modifySavingActions() {

		// overwrite the action to update GUI after saving.
		setProjectSavingAction(FileTypeKey.OPX);
		setProjectSavingAction(FileTypeKey.FOLD);
	}

	private void setProjectSavingAction(final FileTypeKey fileType) {
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

	private void openFileFromMRUFileMenuItem(final ActionEvent e) {

		var menuItem = (JMenuItem) (e.getSource());
		try {
			String filePath = menuItem.getText();
			openFile(filePath);
			updateTitleText();
		} catch (Exception ex) {
			showErrorDialog(ORIPA.res.getString("Error_FileLoadFailed"), ex);
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
		paintContext.setCreasePattern(document.getCreasePattern());
		paintContext.creasePatternUndo().clear();

		childFrameManager.closeAllChildrenRecursively(this);

		screenSetting.setGridVisible(true);

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
		Painter painter = paintContext.getPainter();
		if (painter.countSelectedLines() == 0) {
			JOptionPane.showMessageDialog(this, "Select target lines",
					"ArrayCopy", JOptionPane.WARNING_MESSAGE);

		} else {
			arrayCopyDialog.setVisible(true);
		}
	}

	private void showCircleCopyDialog() {
		Painter painter = paintContext.getPainter();
		if (painter.countSelectedLines() == 0) {
			JOptionPane.showMessageDialog(this, "Select target lines",
					"CircleCopy", JOptionPane.WARNING_MESSAGE);

		} else {
			circleCopyDialog.setVisible(true);
		}
	}

	public void updateTitleText() {
		String fileName;
		if ((document.getDataFilePath()).equals("")) {
			fileName = ORIPA.res.getString("DefaultFileName");
		} else {
			fileName = document.getDataFileName();
		}

		setTitle(fileName + " - " + ORIPA.TITLE);
	}

	private void saveProjectFile(final Doc doc, final String filePath, final FileTypeKey fileType) {
		final DocDAO dao = new DocDAO();
		try {
			dao.save(doc, filePath, fileType);
		} catch (IOException | IllegalArgumentException e) {
			logger.error("Failed to save", e);
			showErrorDialog("Failed to save.", e);
		}

		doc.setDataFilePath(filePath);

		paintContext.creasePatternUndo().clearChanged();

		updateMenu(filePath);
		updateTitleText();
	}

	@SafeVarargs
	private String saveFile(final String directory, String fileName,
			final FileAccessSupportFilter<Doc>... filters) {

		if (fileName.isEmpty()) {
			fileName = "newFile.opx";
		}
		File givenFile = new File(directory, fileName);

		var filePath = givenFile.getPath();

		try {
			final DocDAO dao = new DocDAO();
			String savedPath = dao.saveUsingGUI(document, filePath, this, filters);
			paintContext.creasePatternUndo().clearChanged();
			return savedPath;
		} catch (FileChooserCanceledException e) {
			logger.info("File selection is canceled.");
			return document.getDataFilePath();
		} catch (IOException | IllegalArgumentException e) {
			logger.error("failed to save", e);
			showErrorDialog("Failed to save", e);
			return document.getDataFilePath();
		}
	}

	private void saveFileWithModelCheck(final FileTypeKey type) {

		try {
			final DocDAO dao = new DocDAO();
			dao.saveUsingGUIWithModelCheck(document, this, filterSelector.getFilter(type));

		} catch (FileChooserCanceledException e) {
			logger.info("File selection is canceled.");
		} catch (IOException e) {
			logger.error("IO trouble", e);
			showErrorDialog("Failed to save", e);
		} catch (IllegalArgumentException e) {
			logger.error("Maybe data is not appropriate.", e);
			showErrorDialog("Failed to save", e);
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
		menuFile.add(menuItemExportOBJ);
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

	public void updateMenu(final String filePath) {

		if (filterSelector.getLoadableFilterOf(filePath) == null) {
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
	 */
	private String openFile(final String filePath) {
		childFrameManager.closeAllChildrenRecursively(this);

		screenSetting.setGridVisible(false);

		DocDAO dao = new DocDAO();

		try {
			// we can't substitute a loaded object because
			// the document object is referred by screen and UI panel as a
			// Holder.
			if (filePath != null) {
				document.set(dao.load(filePath));
			} else {
				document.set(dao.loadUsingGUI(
						fileHistory.getLastPath(), filterSelector.getLoadables(),
						this));
			}
		} catch (FileVersionError | WrongDataFormatException | IOException e) {
			logger.error("failed to load", e);
			showErrorDialog("Failed to load the file", e);
		} catch (FileChooserCanceledException cancel) {
			return null;
		}

		paintContext.clear(true);
		paintContext.setCreasePattern(document.getCreasePattern());

		paintContext.creasePatternUndo().clear();

		paintContext.updateGrids();

		return document.getDataFilePath();

	}

	private void showErrorDialog(final String title, final Exception ex) {
		JOptionPane.showMessageDialog(this,
				ex.getMessage(), title, JOptionPane.ERROR_MESSAGE);

	}

	void saveIniFile() {
		fileHistory.saveToFile(ORIPA.iniFilePath);
	}

	void loadIniFile() {
		fileHistory.loadFromFile(ORIPA.iniFilePath);
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
			// TODO: confirm saving edited opx
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

	private void addPropertyChangeListenersToSetting() {
		setting.addPropertyChangeListener(MainFrameSetting.HINT, e -> {
			hintLabel.setText("    " + (String) e.getNewValue());
			hintLabel.repaint();
		});

	}
}
