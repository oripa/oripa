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

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;

/**
 * @author Koji
 * 
 */
public class FileMenu extends JMenu {
	private JMenuItem menuItemClear;
	private JMenuItem menuItemOpen;;

	private JMenuItem menuItemSave;
	private JMenuItem menuItemSaveAs;
	private JMenuItem menuItemSaveAsImage;
	private JMenuItem menuItemExportDXF;
	private JMenuItem menuItemExportOBJ;
	private JMenuItem menuItemExportCP;
	private JMenuItem menuItemExportSVG;
	private JMenuItem menuItemProperty;
	private JMenuItem menuItemExit;

	public FileMenu(ResourceHolder resourceHolder) {
		super();
		resourceHolder.getString(ResourceKey.LABEL, StringID.Main.FILE_ID);

		initializeElements(resourceHolder);
		buildFileMenu();
	}

	private void initializeElements(ResourceHolder resourceHolder) {
		menuItemClear = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL,
						StringID.Main.NEW_ID));

		menuItemOpen = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL,
						StringID.Main.OPEN_ID));

		menuItemSave = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL,
						StringID.Main.SAVE_ID));

		menuItemSaveAs = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL,
						StringID.Main.SAVE_AS_ID));

		menuItemSaveAsImage = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL,
						StringID.Main.SAVE_AS_IMAGE_ID));

		menuItemExportDXF = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL,
						StringID.Main.EXPORT_DXF_ID));

		menuItemExportOBJ = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL,
						StringID.Main.EXPORT_OBJ_ID));

		menuItemExportCP = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL,
						StringID.Main.EXPORT_CP_ID));

		menuItemExportSVG = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL,
						StringID.Main.EXPORT_SVG_ID));

		menuItemProperty = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL,
						StringID.Main.PROPERTY_ID));

		menuItemExit = new JMenuItem(
				resourceHolder.getString(ResourceKey.LABEL,
						StringID.Main.EXIT_ID));

	}

	private void buildFileMenu() {
		removeAll();

		add(menuItemClear);
		add(menuItemOpen);
		add(menuItemSave);
		add(menuItemSaveAs);
		add(menuItemSaveAsImage);
		add(menuItemExportDXF);
		add(menuItemExportOBJ);
		add(menuItemExportCP);
		add(menuItemExportSVG);
		addSeparator();
		add(menuItemProperty);
		addSeparator();

		addSeparator();
		add(menuItemExit);
	}

}
