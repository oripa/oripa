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
package oripa.view.main;

import java.awt.Component;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import oripa.Config;
import oripa.ORIPA;
import oripa.bind.ButtonFactory;
import oripa.bind.PaintActionButtonFactory;
import oripa.file.FileHistory;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;

/**
 * @author Koji
 * 
 */
public class MainMenuBar extends JMenuBar {

	private final JMenu fileMenu = new JMenu(
			ORIPA.res.getString(StringID.Main.FILE_ID));

	private final JMenu editMenu = new JMenu(ORIPA.res.getString("Edit"));
	private final JMenu helpMenu = new JMenu(ORIPA.res.getString("Help"));

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

	private JMenuItem menuItemExportDXF;
	private JMenuItem menuItemExportOBJ;
	private JMenuItem menuItemExportCP;
	private JMenuItem menuItemExportSVG;

	// -----------------------------------------------------------------------------------------------------------
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
	// -----------------------------------------------------------------------------------------------------------

	private final ResourceHolder resourceHolder = ResourceHolder.getInstance();
	private JMenuItem menuItemProperty;

	private JMenuItem menuItemExit;
	private JMenuItem menuItemUndo;
	private JMenuItem menuItemAbout;
	private JMenuItem menuItemRepeatCopy;
	private JMenuItem menuItemCircleCopy;
	private JMenuItem menuItemUnSelectAll;

	private JMenuItem menuItemDeleteSelectedLines;
	private JMenuItem[] MRUFilesMenuItem = new JMenuItem[Config.MRUFILE_NUM];

	private final ButtonFactory buttonFactory = new PaintActionButtonFactory();

	/**
	 * Constructor
	 */
	public MainMenuBar(Component owner) {

		build(owner);

		addMenus();
	}

	private void build(Component owner) {
		buildMenuItems(owner);

		buildFileMenu();
		buildEditMenu();
		buildHelpMenu();

	}

	private void buildMenuItems(Component owner) {
		menuItemExportDXF = new JMenuItem("Export DXF");
		menuItemExportOBJ = new JMenuItem("Export OBJ");
		menuItemExportCP = new JMenuItem("Export CP");
		menuItemExportSVG = new JMenuItem("Export SVG");
		/**
		 * For changing outline
		 */
		menuItemChangeOutline = (JMenuItem) buttonFactory
				.create(owner, JMenuItem.class, StringID.EDIT_CONTOUR_ID);

		/**
		 * For selecting all lines
		 */
		menuItemSelectAll = (JMenuItem) buttonFactory
				.create(owner, JMenuItem.class, StringID.SELECT_ALL_LINE_ID);

		/**
		 * For starting copy-and-paste
		 */
		menuItemCopyAndPaste = (JMenuItem) buttonFactory
				.create(owner, JMenuItem.class, StringID.COPY_PASTE_ID);

		/**
		 * For starting cut-and-paste
		 */
		menuItemCutAndPaste = (JMenuItem) buttonFactory
				.create(owner, JMenuItem.class, StringID.CUT_PASTE_ID);

		// -----------------------------------------------------------------------------------------------------------

		ResourceHolder resourceHolder = ResourceHolder
				.getInstance();
		menuItemProperty = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL,
						StringID.Main.PROPERTY_ID));

		menuItemExit = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL,
						StringID.Main.EXIT_ID));
		menuItemUndo = new JMenuItem(
				ORIPA.res.getString("Undo"));
		menuItemAbout = new JMenuItem(
				ORIPA.res.getString("About"));
		menuItemRepeatCopy = new JMenuItem("Array Copy");
		menuItemCircleCopy = new JMenuItem("Circle Copy");
		menuItemUnSelectAll = new JMenuItem("UnSelect All");

		menuItemDeleteSelectedLines = new JMenuItem(
				"Delete Selected Lines");
		MRUFilesMenuItem = new JMenuItem[Config.MRUFILE_NUM];

	}

	private void buildFileMenu() {
		fileMenu.removeAll();

		fileMenu.add(menuItemClear);
		fileMenu.add(menuItemOpen);
		fileMenu.add(menuItemSave);
		fileMenu.add(menuItemSaveAs);
		fileMenu.add(menuItemSaveAsImage);
		fileMenu.add(menuItemExportDXF);
		fileMenu.add(menuItemExportOBJ);
		fileMenu.add(menuItemExportCP);
		fileMenu.add(menuItemExportSVG);
		fileMenu.addSeparator();
		fileMenu.add(menuItemProperty);
		fileMenu.addSeparator();

		fileMenu.addSeparator();
		fileMenu.add(menuItemExit);
	}

	private void buildEditMenu() {
		editMenu.add(menuItemCopyAndPaste);
		editMenu.add(menuItemCutAndPaste);
		editMenu.add(menuItemRepeatCopy);
		editMenu.add(menuItemCircleCopy);
		editMenu.add(menuItemSelectAll);
		editMenu.add(menuItemUnSelectAll);
		editMenu.add(menuItemDeleteSelectedLines);
		editMenu.add(menuItemUndo);
		editMenu.add(menuItemChangeOutline);

	}

	private void buildHelpMenu() {
		helpMenu.add(menuItemAbout);

	}

	private void addMenus() {

		add(fileMenu);
		add(editMenu);
		add(helpMenu);

	}

	public void setFileHistoryPaths(FileHistory history) {
		int i = 0;
		for (String path : history.getHistory()) {
			MRUFilesMenuItem[i].setText(path);
			fileMenu.add(MRUFilesMenuItem[i]);

			i++;
		}
		while (i < MRUFilesMenuItem.length) {
			MRUFilesMenuItem[i].setText("");
			i++;
		}

	}

}
