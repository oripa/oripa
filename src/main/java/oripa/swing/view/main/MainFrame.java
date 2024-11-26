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

package oripa.swing.view.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import oripa.geom.RectangleDomain;
import oripa.gui.view.ViewScreenUpdater;
import oripa.gui.view.main.MainFrameSetting;
import oripa.gui.view.main.MainFrameView;
import oripa.gui.view.main.PainterScreenSetting;
import oripa.gui.view.main.PainterScreenView;
import oripa.gui.view.main.UIPanelSetting;
import oripa.gui.view.main.UIPanelView;
import oripa.resource.Constants;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;
import oripa.swing.view.util.Dialogs;
import oripa.swing.view.util.ImageResourceLoader;
import oripa.swing.view.util.KeyStrokes;

@Singleton
public class MainFrame extends JFrame implements MainFrameView, ComponentListener, WindowListener {

	private static final Logger logger = LoggerFactory.getLogger(MainFrame.class);

	private static final long serialVersionUID = 272369294032419950L;

	// shared objects
	private final ResourceHolder resourceHolder;

	private final MainFrameSetting setting;

	private final JLabel hintLabel = new JLabel();

	// setup Menu Bars
	private final JMenu menuFile;
	private final JMenu menuEdit;
	private final JMenu menuHelp;

	// file menu items
	private final JMenuItem menuItemClear;
	private final JMenuItem menuItemOpen;

	private final JMenuItem menuItemSave;
	private final JMenuItem menuItemSaveAs;
	private final JMenuItem menuItemSaveAsImage;

	private final JMenuItem menuItemExportFOLD;
	private final JMenuItem menuItemExportDXF;
	private final JMenuItem menuItemExportCP;
	private final JMenuItem menuItemExportSVG;
	private final JMenuItem menuItemExit;

	private final JMenuItem[] MRUFilesMenuItem;

	private final JMenuItem menuItemProperty;

	private final JMenuItem menuItemImport;

	// edit menu items
	/**
	 * For changing outline
	 */
	private final JMenuItem menuItemChangeOutline;

	/**
	 * For selecting all lines
	 */
	private final JMenuItem menuItemSelectAll;

	/**
	 * For starting copy-and-paste
	 */
	private final JMenuItem menuItemCopyAndPaste;

	/**
	 * For starting cut-and-paste
	 */
	private final JMenuItem menuItemCutAndPaste;

	private final JMenuItem menuItemUndo;
	private final JMenuItem menuItemRedo;

	private final JMenuItem menuItemRepeatCopy;
	private final JMenuItem menuItemCircleCopy;

	private final JMenuItem menuItemUnSelectAll;

	private final JMenuItem menuItemDeleteSelectedLines;

	// help menu items

	private final JMenuItem menuItemAbout;

	private UIPanel uiPanel;
	private final PainterScreen mainScreen;
	private final MainDialogService dialogService;

	private Consumer<Integer> MRUFilesMenuItemUpdateListener;

	private Runnable windowClosingListener;

	@Inject
	public MainFrame(
			final MainFrameSetting frameSetting,
			final PainterScreenSetting screenSetting,
			final UIPanelSetting uiPanelSetting,
			final ViewScreenUpdater viewScreenUpdater,
			final MainDialogService mainDialogService,
			final ResourceHolder resourceHolder) {
		this.resourceHolder = resourceHolder;

		// setup Menu Bars
		menuFile = new JMenu(
				resourceHolder.getString(ResourceKey.LABEL, StringID.Main.FILE_ID));
		menuEdit = new JMenu(
				resourceHolder.getString(ResourceKey.LABEL, StringID.Main.EDIT_ID));
		menuHelp = new JMenu(
				resourceHolder.getString(ResourceKey.LABEL, StringID.Main.HELP_ID));

		// file menu items
		menuItemClear = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL, StringID.Main.NEW_ID));
		menuItemOpen = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL, StringID.Main.OPEN_ID));

		menuItemSave = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL, StringID.Main.SAVE_ID));
		menuItemSaveAs = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL, StringID.Main.SAVE_AS_ID));
		menuItemSaveAsImage = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL, StringID.Main.SAVE_AS_IMAGE_ID));
		menuItemExportFOLD = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL, StringID.Main.EXPORT_FOLD_ID));
		menuItemExportDXF = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL, StringID.Main.EXPORT_DXF_ID));
		menuItemExportCP = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL, StringID.Main.EXPORT_CP_ID));
		menuItemExportSVG = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL, StringID.Main.EXPORT_SVG_ID));
		menuItemExit = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL, StringID.Main.EXIT_ID));
		MRUFilesMenuItem = new JMenuItem[Constants.MRUFILE_NUM];
		menuItemProperty = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL,
						StringID.Main.PROPERTY_ID));
		menuItemImport = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL, StringID.IMPORT_CP_ID));

		// edit menu items
		/**
		 * For changing outline
		 */
		menuItemChangeOutline = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL, StringID.EDIT_CONTOUR_ID));

		/**
		 * For selecting all lines
		 */
		menuItemSelectAll = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL, StringID.SELECT_ALL_LINE_ID));

		/**
		 * For starting copy-and-paste
		 */
		menuItemCopyAndPaste = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL, StringID.COPY_PASTE_ID));

		/**
		 * For starting cut-and-paste
		 */
		menuItemCutAndPaste = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL, StringID.CUT_PASTE_ID));
		menuItemUndo = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL, StringID.Main.UNDO_ID));
		menuItemRedo = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL, StringID.Main.REDO_ID));
		menuItemRepeatCopy = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL, StringID.Main.ARRAY_COPY_ID));
		menuItemCircleCopy = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL, StringID.Main.CIRCLE_COPY_ID));
		menuItemUnSelectAll = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL, StringID.Main.UNSELECT_ALL_ID));
		menuItemDeleteSelectedLines = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL, StringID.Main.DELETE_SELECTED_ID));

		// help menu items
		menuItemAbout = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL, StringID.Main.ABOUT_ID));

		this.dialogService = mainDialogService;

		logger.info("frame construction starts.");

		setting = frameSetting;

		mainScreen = new PainterScreen(screenSetting, viewScreenUpdater);

		// this has to be done before instantiation of UI panel.
		addHintPropertyChangeListenersToSetting();

		logger.info("start constructing UI panel.");
		try {
			uiPanel = new UIPanel(uiPanelSetting, screenSetting, dialogService, resourceHolder);
		} catch (RuntimeException ex) {
			logger.error("UI panel construction failed", ex);
			Dialogs.showErrorDialog(
					this, resourceHolder.getString(ResourceKey.ERROR, StringID.Error.DEFAULT_TITLE_ID), ex);
			System.exit(1);
		}
		logger.info("end constructing UI panel.");

		JScrollPane uiScroll = new JScrollPane(uiPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		uiScroll.setPreferredSize(new Dimension(255, 900));// setPreferredSize(new
															// Dimension(uiPanel.getPreferredSize().width
															// + 25,
		// uiPanel.getPreferredSize().height));
		uiScroll.setAlignmentX(JPanel.LEFT_ALIGNMENT);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(uiScroll, BorderLayout.WEST);
		getContentPane().add(mainScreen, BorderLayout.CENTER);
		getContentPane().add(hintLabel, BorderLayout.SOUTH);

		ImageResourceLoader imgLoader = new ImageResourceLoader();
		this.setIconImage(imgLoader.loadAsIcon("icon/oripa.gif").getImage());

		addWindowListener(this);

		IntStream.range(0, Constants.MRUFILE_NUM)
				.forEach(i -> MRUFilesMenuItem[i] = new JMenuItem());

		// Building the menu bar
		JMenuBar menuBar = new JMenuBar();

		setAccelerators();

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

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	private void setAccelerators() {

		menuItemOpen.setAccelerator(KeyStrokes.getWithControlDown(KeyEvent.VK_O));

		menuItemSave.setAccelerator(KeyStrokes.getWithControlDown(KeyEvent.VK_S));

		menuItemUndo.setAccelerator(KeyStrokes.getWithControlDown(KeyEvent.VK_Z));

		menuItemRedo.setAccelerator(KeyStrokes.getWithControlDown(KeyEvent.VK_Y));

		menuItemClear.setAccelerator(KeyStrokes.getWithControlDown(KeyEvent.VK_N));

		menuItemSelectAll.setAccelerator(KeyStrokes.getWithControlDown(KeyEvent.VK_A));

		menuItemUnSelectAll.setAccelerator(KeyStrokes.get(KeyEvent.VK_ESCAPE));

		menuItemDeleteSelectedLines.setAccelerator(KeyStrokes.get(KeyEvent.VK_DELETE));

		menuItemCopyAndPaste.setAccelerator(KeyStrokes.getWithControlDown(KeyEvent.VK_C));
		menuItemCutAndPaste.setAccelerator(KeyStrokes.getWithControlDown(KeyEvent.VK_X));
	}

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

		menuFile.addSeparator();
		menuFile.add(menuItemExit);
	}

	@Override
	public void addMRUFilesMenuItemUpdateListener(final Consumer<Integer> listener) {
		MRUFilesMenuItemUpdateListener = listener;
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
		windowClosingListener.run();
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
	public UIPanelView getUIPanelView() {
		return uiPanel;
	}

	@Override
	public PainterScreenView getPainterScreenView() {
		return mainScreen;
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
	public void addSaveButtonListener(final Runnable listener) {
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

	@Override
	public void showCopyPasteErrorMessage() {
		JOptionPane.showMessageDialog(this, "Select target lines",
				"Copy and Paste", JOptionPane.WARNING_MESSAGE);
	}

	@Override
	public void showNoSelectionMessageForArrayCopy() {
		dialogService.showNoSelectionMessageForArrayCopy(this);
	}

	@Override
	public void showNoSelectionMessageForCircleCopy() {
		dialogService.showNoSelectionMessageForCircleCopy(this);
	}

	@Override
	public void showAboutAppMessage() {
		dialogService.showAboutAppMessage(this);
	}

	@Override
	public boolean showModelBuildFailureDialog() {
		return dialogService.showModelBuildFailureDialog(this) == JOptionPane.OK_OPTION;
	}

	@Override
	public boolean showSaveOnCloseDialog() {
		return dialogService.showSaveOnCloseDialog(this) == JOptionPane.YES_OPTION;
	}

	@Override
	public void showLoadFailureErrorMessage(final Exception e) {
		Dialogs.showErrorDialog(this, resourceHolder.getString(
				ResourceKey.ERROR, StringID.Error.LOAD_FAILED_ID), e);
	}

	@Override
	public void showSaveFailureErrorMessage(final Exception e) {
		Dialogs.showErrorDialog(this, resourceHolder.getString(
				ResourceKey.ERROR, StringID.Error.SAVE_FAILED_ID), e);
	}

	@Override
	public void showSaveIniFileFailureErrorMessage(final Exception e) {
		Dialogs.showErrorDialog(this, resourceHolder.getString(
				ResourceKey.ERROR, StringID.Error.SAVE_INI_FAILED_ID), e);
	}

	@Override
	public void initializeFrameBounds() {
		// Configure position and size of the frame
		int uiPanelWidth = 0;// 150;

		int mainFrameWidth = 1000;
		int mainFrameHeight = 800;

		int appTotalWidth = mainFrameWidth + uiPanelWidth;
		int appTotalHeight = mainFrameHeight;

		Toolkit toolkit = getToolkit();
		Dimension dim = toolkit.getScreenSize();
		int originX = (int) (dim.getWidth() / 2 - appTotalWidth / 2);
		int originY = (int) (dim.getHeight() / 2 - appTotalHeight / 2);

		setBounds(originX + uiPanelWidth, originY, mainFrameWidth, mainFrameHeight);
	}
}
