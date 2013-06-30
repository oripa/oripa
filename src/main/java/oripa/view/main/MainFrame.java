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
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import oripa.Config;
import oripa.FilterDB;
import oripa.ORIPA;
import oripa.bind.ButtonFactory;
import oripa.bind.PaintActionButtonFactory;
import oripa.doc.Doc;
import oripa.doc.exporter.ExporterXML;
import oripa.file.FileChooser;
import oripa.file.FileChooserFactory;
import oripa.file.FileFilterEx;
import oripa.file.FileHistory;
import oripa.file.FileVersionError;
import oripa.file.ImageResourceLoader;
import oripa.file.SavingAction;
import oripa.paint.DeleteSelectedLines;
import oripa.paint.Globals;
import oripa.paint.PaintContext;
import oripa.resource.Constants;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;
import oripa.view.PropertyDialog;
import oripa.view.uipanel.UIPanel;
import oripa.viewsetting.main.MainFrameSettingDB;
import oripa.viewsetting.main.MainScreenSettingDB;

public class MainFrame extends JFrame implements ActionListener,
		ComponentListener, WindowListener, Observer {


	/**
	 * 
	 */
	private static final long serialVersionUID = 272369294032419950L;

	private MainFrameSettingDB setting = MainFrameSettingDB.getInstance();
	private MainScreenSettingDB screenSetting = MainScreenSettingDB.getInstance();
	private PaintContext mouseContext = PaintContext.getInstance();

	MainScreen mainScreen;
	private JMenu menuFile = new JMenu(
			ORIPA.res.getString(StringID.Main.FILE_ID));
	private JMenu menuEdit = new JMenu(ORIPA.res.getString("Edit"));
	private JMenu menuHelp = new JMenu(ORIPA.res.getString("Help"));
	private JMenuItem menuItemClear = new JMenuItem(ORIPA.res.getString("New"));
	private JMenuItem menuItemOpen = new JMenuItem(ORIPA.res.getString("Open"));

	private JMenuItem menuItemSave = new JMenuItem(ORIPA.res.getString("Save"));
	private JMenuItem menuItemSaveAs = new JMenuItem(
			ORIPA.res.getString("SaveAs"));
	private JMenuItem menuItemSaveAsImage = new JMenuItem(
			ORIPA.res.getString("SaveAsImage"));

	private JMenuItem menuItemExportDXF = new JMenuItem("Export DXF");
	private JMenuItem menuItemExportOBJ = new JMenuItem("Export OBJ");
	private JMenuItem menuItemExportCP = new JMenuItem("Export CP");
	private JMenuItem menuItemExportSVG = new JMenuItem("Export SVG");

	// -----------------------------------------------------------------------------------------------------------
	// Create paint button

	ButtonFactory buttonFactory = new PaintActionButtonFactory();
	
	/**
	 * For changing outline
	 */
	private JMenuItem menuItemChangeOutline = (JMenuItem) buttonFactory.create(
			this, JMenuItem.class, StringID.EDIT_CONTOUR_ID);

	/**
	 * For selecting all lines
	 */
	private JMenuItem menuItemSelectAll = (JMenuItem) buttonFactory.create(
			this, JMenuItem.class, StringID.SELECT_ALL_LINE_ID);

	/**
	 * For starting copy-and-paste
	 */
	private JMenuItem menuItemCopyAndPaste = (JMenuItem) buttonFactory.create(
			this, JMenuItem.class, StringID.COPY_PASTE_ID);

	/**
	 * For starting cut-and-paste
	 */
	private JMenuItem menuItemCutAndPaste = (JMenuItem) buttonFactory.create(
			this, JMenuItem.class, StringID.CUT_PASTE_ID);

	// -----------------------------------------------------------------------------------------------------------


	private ResourceHolder resourceHolder = ResourceHolder.getInstance();
	private JMenuItem menuItemProperty = new JMenuItem(
			resourceHolder.getString(ResourceKey.LABEL,
					StringID.Main.PROPERTY_ID));

	private JMenuItem menuItemExit = new JMenuItem(resourceHolder.getString(
			ResourceKey.LABEL, StringID.Main.EXIT_ID));
	private JMenuItem menuItemUndo = new JMenuItem(ORIPA.res.getString("Undo"));
	private JMenuItem menuItemAbout = new JMenuItem(
			ORIPA.res.getString("About"));
	private JMenuItem menuItemRepeatCopy = new JMenuItem("Array Copy");
	private JMenuItem menuItemCircleCopy = new JMenuItem("Circle Copy");
	private JMenuItem menuItemUnSelectAll = new JMenuItem("UnSelect All");

	private JMenuItem menuItemDeleteSelectedLines = new JMenuItem(
			"Delete Selected Lines");
	private JMenuItem[] MRUFilesMenuItem = new JMenuItem[Config.MRUFILE_NUM];

	private RepeatCopyDialog arrayCopyDialog;
	private CircleCopyDialog circleCopyDialog;
	public static JLabel hintLabel = new JLabel();
	public UIPanel uiPanel;

	private FileHistory fileHistory = new FileHistory(Config.MRUFILE_NUM);

	private FilterDB filterDB = FilterDB.getInstance();
	private FileFilterEx[] fileFilters = new FileFilterEx[] {

	filterDB.getFilter("opx"), filterDB.getFilter("pict") };

	public MainFrame() {

		setting.addObserver(this);

		// addKeyListener(this);

		menuItemCopyAndPaste.setText(resourceHolder.getString(
				ResourceKey.LABEL, StringID.COPY_PASTE_ID));
		menuItemCutAndPaste.setText(resourceHolder.getString(
				ResourceKey.LABEL, StringID.CUT_PASTE_ID));
		// menuItemChangeOutline.setText(ORIPA.res.getString(StringID.Menu.CONTOUR_ID));

		mainScreen = new MainScreen();
		addWindowListener(this);
		uiPanel = new UIPanel(mainScreen);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(uiPanel, BorderLayout.WEST);
		getContentPane().add(mainScreen, BorderLayout.CENTER);
		getContentPane().add(hintLabel, BorderLayout.SOUTH);

		ImageResourceLoader imgLoader = new ImageResourceLoader();
		this.setIconImage(imgLoader.loadAsIcon("icon/oripa.gif", getClass())
				.getImage());

		menuItemOpen.addActionListener(this);
		menuItemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				ActionEvent.CTRL_MASK));

		menuItemSave.addActionListener(this);
		menuItemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				ActionEvent.CTRL_MASK));

		menuItemSaveAs.addActionListener(this);
		menuItemSaveAsImage.addActionListener(this);

		menuItemExit.addActionListener(this);
		// menuItemExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
		// ActionEvent.CTRL_MASK));

		menuItemUndo.addActionListener(this);
		menuItemUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
				ActionEvent.CTRL_MASK));

		menuItemClear.addActionListener(this);
		menuItemClear.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				ActionEvent.CTRL_MASK));

		menuItemAbout.addActionListener(this);
		menuItemExportDXF.addActionListener(this);
		menuItemExportOBJ.addActionListener(this);
		menuItemExportCP.addActionListener(this);
		menuItemExportSVG.addActionListener(this);
		menuItemProperty.addActionListener(this);
		menuItemChangeOutline.addActionListener(this);
		menuItemRepeatCopy.addActionListener(this);
		menuItemCircleCopy.addActionListener(this);
		menuItemSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
				ActionEvent.CTRL_MASK));

		menuItemUnSelectAll.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_ESCAPE, 0));

		menuItemUnSelectAll
				.addActionListener(new java.awt.event.ActionListener() {
					@Override
					public void actionPerformed(java.awt.event.ActionEvent e) {
						ORIPA.doc.resetSelectedOriLines();
						mouseContext.clear(false);
						mainScreen.repaint();
					}
				});

		menuItemDeleteSelectedLines
				.addActionListener(new DeleteSelectedLines());
		menuItemDeleteSelectedLines.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_DELETE, 0));

		menuItemCopyAndPaste.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		menuItemCutAndPaste.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_X, ActionEvent.CTRL_MASK));


		for (int i = 0; i < Config.MRUFILE_NUM; i++) {
			MRUFilesMenuItem[i] = new JMenuItem();
			MRUFilesMenuItem[i].addActionListener(this);
		}

		loadIniFile();

		// Building the menu bar
		JMenuBar menuBar = new JMenuBar();
		buildMenuFile();

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

		addSavingActions();
	}

	private void addSavingActions() {

		filterDB.getFilter("pict").setSavingAction(new SavingAction() {

			@Override
			public boolean save(String path) {
				try {
					savePictureFile(mainScreen.getCreasePatternImage(), path);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}

				return true;
			}
		});

		filterDB.getFilter("opx").setSavingAction(new SavingAction() {

			@Override
			public boolean save(String path) {
				try {
					saveOpxFile(path);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
				return true;

			}
		});
	}

	private void saveOpxFile(String filePath) {
		ExporterXML exporter = new ExporterXML();
		exporter.export(ORIPA.doc, filePath);
		ORIPA.doc.setDataFilePath(filePath);

		updateMenu(filePath);

		ORIPA.doc.clearChanged();
	}

	private void savePictureFile(Image cpImage, String filePath)
			throws IOException {
		BufferedImage image = new BufferedImage(cpImage.getWidth(this),
				cpImage.getHeight(this), BufferedImage.TYPE_INT_RGB);

		image.getGraphics().drawImage(cpImage, 0, 0, this);

		File file = new File(filePath);
		ImageIO.write(image, filePath.substring(filePath.lastIndexOf(".") + 1),
				file);
	}

	public void initialize() {
		arrayCopyDialog = new RepeatCopyDialog(this);
		circleCopyDialog = new CircleCopyDialog(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Check the last opened files
		for (int i = 0; i < Config.MRUFILE_NUM; i++) {
			if (e.getSource() == MRUFilesMenuItem[i]) {
				try {
					String filePath = MRUFilesMenuItem[i].getText();
					openFile(filePath);
					updateTitleText();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(this, e.toString(),
							ORIPA.res.getString("Error_FileLoadFailed"),
							JOptionPane.ERROR_MESSAGE);
				}
				mainScreen.repaint();
				return;
			}
		}

		// String lastPath = fileHistory.getLastPath();
		String lastDirectory = fileHistory.getLastDirectory();

		if (e.getSource() == menuItemOpen) {
			openFile(null);
			mainScreen.repaint();
			updateTitleText();
		} else if (e.getSource() == menuItemSave
				&& !ORIPA.doc.dataFilePath.equals("")) {
			saveOpxFile(ORIPA.doc.getDataFilePath());

		} else if (e.getSource() == menuItemSaveAs
				|| e.getSource() == menuItemSave) {

			String path = saveFile(lastDirectory, ORIPA.doc.getDataFileName(),
					fileFilters);

			updateMenu(path);
			updateTitleText();

		} else if (e.getSource() == menuItemSaveAsImage) {

			saveFile(lastDirectory, ORIPA.doc.getDataFileName(),
					new FileFilterEx[] { filterDB.getFilter("pict") });

		} else if (e.getSource() == menuItemExportDXF) {
			exportFile("dxf");
		} else if (e.getSource() == menuItemExportOBJ) {
			exportFile("obj");
		} else if (e.getSource() == menuItemExportCP) {
			exportFile("cp");
		} else if (e.getSource() == menuItemExportSVG) {
			exportFile("svg");
		} else if (e.getSource() == menuItemChangeOutline) {
			// Globals.preEditMode = Globals.editMode;
			// Globals.editMode = Constants.EditMode.EDIT_OUTLINE;

			// Globals.setMouseAction(new EditOutlineAction());

		} else if (e.getSource() == menuItemExit) {
			saveIniFile();
			System.exit(0);
		} else if (e.getSource() == menuItemUndo) {
			if (Globals.getMouseAction() != null) {
				Globals.getMouseAction().undo(mouseContext);
			} else {
				ORIPA.doc.loadUndoInfo();
			}
			mainScreen.repaint();
		} else if (e.getSource() == menuItemClear) {
			ORIPA.doc = new Doc(Constants.DEFAULT_PAPER_SIZE);
			ORIPA.modelFrame.repaint();

			ORIPA.modelFrame.setVisible(false);
			ORIPA.renderFrame.setVisible(false);

			screenSetting.setGridVisible(true);
			screenSetting.notifyObservers();

			// ORIPA.mainFrame.uiPanel.dispGridCheckBox.setSelected(true);
			updateTitleText();
		} else if (e.getSource() == menuItemAbout) {
			JOptionPane.showMessageDialog(this, ORIPA.infoString,
					ORIPA.res.getString("Title"),
					JOptionPane.INFORMATION_MESSAGE);
		} else if (e.getSource() == menuItemProperty) {
			PropertyDialog dialog = new PropertyDialog(this);
			dialog.setValue();
			Rectangle rec = getBounds();
			dialog.setLocation(
					(int) (rec.getCenterX() - dialog.getWidth() / 2),
					(int) (rec.getCenterY() - dialog.getHeight() / 2));
			dialog.setModal(true);
			dialog.setVisible(true);
		} else if (e.getSource() == menuItemRepeatCopy) {
			if (ORIPA.doc.getSelectedLineNum() == 0) {
				JOptionPane.showMessageDialog(this, "Select target lines",
						"ArrayCopy", JOptionPane.WARNING_MESSAGE);

			} else {
				arrayCopyDialog.setVisible(true);
			}
		} else if (e.getSource() == menuItemCircleCopy) {
			if (ORIPA.doc.getSelectedLineNum() == 0) {
				JOptionPane.showMessageDialog(this, "Select target lines",
						"ArrayCopy", JOptionPane.WARNING_MESSAGE);

			} else {
				circleCopyDialog.setVisible(true);
			}
		}

	}

	public void updateTitleText() {
		String fileName;
		if ((ORIPA.doc.dataFilePath).equals("")) {
			fileName = ORIPA.res.getString("DefaultFileName");
		} else {
			fileName = ORIPA.doc.getDataFileName();
		}

		setTitle(fileName + " - " + ORIPA.TITLE);
	}

	private String saveFile(String directory, String fileName,
			FileFilterEx[] filters) {

		File givenFile = new File(directory, fileName);

		return saveFile(givenFile.getPath(), filters);
	}

	private String saveFile(String homePath, FileFilterEx[] filters) {
		FileChooserFactory chooserFactory = new FileChooserFactory();
		FileChooser chooser = chooserFactory.createChooser(homePath, filters);

		String path = chooser.saveFile(this);
		if (path != null) {
			// if(path.endsWith(".opx")){
			// ORIPA.doc.setDataFilePath(path);
			// ORIPA.doc.clearChanged();
			//
			// updateMenu(path);
			// }
		} else {
			path = homePath;
		}

		return path;

	}

	public void exportFile(String ext) {
		if ("obj".equals(ext)) {
			if (!ORIPA.doc.hasModel) {
				if (!ORIPA.doc.buildOrigami(true)) {
					JOptionPane.showConfirmDialog(null,
							"Warning: Building a set of polygons from crease pattern "
									+ "was failed.", "Warning",
							JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE);
				}
			}
		}

		saveFile(null, new FileFilterEx[] { filterDB.getFilter(ext) });
	}

	private void buildMenuFile() {
		menuFile.removeAll();

		menuFile.add(menuItemClear);
		menuFile.add(menuItemOpen);
		menuFile.add(menuItemSave);
		menuFile.add(menuItemSaveAs);
		menuFile.add(menuItemSaveAsImage);
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

	public void updateMenu(String filePath) {

		if (filterDB.getLoadableFilterOf(filePath) == null) {
			return;
		}

		fileHistory.useFile(filePath);

		buildMenuFile();
	}

	/**
	 * if filePath is null, this method opens a dialog to select the target.
	 * otherwise, it tries to read data from the path.
	 * 
	 * @param filePath
	 */
	private void openFile(String filePath) {
		ORIPA.modelFrame.setVisible(false);
		ORIPA.renderFrame.setVisible(false);

		screenSetting.setGridVisible(false);
		screenSetting.notifyObservers();

		// ORIPA.mainFrame.uiPanel.dispGridCheckBox.setSelected(false);

		String path = null;

		if (filePath != null) {
			path = loadFile(filePath);
		} else {
			FileChooserFactory factory = new FileChooserFactory();
			FileChooser fileChooser = factory.createChooser(fileHistory
					.getLastPath(), FilterDB.getInstance().getLoadables());

			fileChooser.setFileFilter(FilterDB.getInstance().getFilter("opx"));

			path = fileChooser.loadFile(this);

		}

		if (path == null) {
			path = ORIPA.doc.getDataFilePath();
		} else {
			updateMenu(path);

		}

	}

	/**
	 * Do not call directly. Please use openFile().
	 * 
	 * @param filePath
	 * @return
	 */
	private String loadFile(String filePath) {

		FileFilterEx[] filters = FilterDB.getInstance().getLoadables();

		File file = new File(filePath);

		boolean loaded = false;
		for (FileFilterEx filter : filters) {
			if (filter.accept(file) && !file.isDirectory()) {
				try {
					loaded = filter.getLoadingAction().load(filePath);
				} catch (FileVersionError e) {
					JOptionPane
							.showMessageDialog(
									this,
									"This file is compatible with a new version. "
											+ "Please obtain the latest version of ORIPA",
									"Failed to load the file",
									JOptionPane.ERROR_MESSAGE);
				}
				break;
			}
		}

		if (!loaded) {
			return null;
		}

		return filePath;
	}

	private void saveIniFile() {
		fileHistory.saveToFile(ORIPA.iniFilePath);
	}

	private void loadIniFile() {
		fileHistory.loadFromFile(ORIPA.iniFilePath);
	}

	@Override
	public void componentResized(ComponentEvent arg0) {
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}

	@Override
	public void windowClosing(WindowEvent arg0) {

		if (ORIPA.doc.isChanged()) {
			// TODO: confirm saving edited opx
			int selected = JOptionPane
					.showConfirmDialog(
							this,
							"The crease pattern has been modified. Would you like to save?",
							"Comfirm to save", JOptionPane.YES_NO_OPTION);
			if (selected == JOptionPane.YES_OPTION) {
				String path = saveFile(fileHistory.getLastDirectory(),
						ORIPA.doc.getDataFileName(), fileFilters);
				if (path == null) {

				}
			}
		}

		saveIniFile();
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}

	// @Override
	// public void keyTyped(KeyEvent e) {
	// if(e.isControlDown()){
	// screenUpdater.updateScreen();
	// }
	// }
	//
	// @Override
	// public void keyPressed(KeyEvent e) {
	// if(e.isControlDown()){
	// screenUpdater.updateScreen();
	// }
	// }
	//
	// @Override
	// public void keyReleased(KeyEvent e) {
	// if(e.isControlDown()){
	// screenUpdater.updateScreen();
	// }
	//
	// }

	@Override
	public void update(Observable o, Object arg) {
		if (o.toString() == setting.getName()) {
			hintLabel.setText("    " + setting.getHint());
			hintLabel.repaint();
		}
	}
}
