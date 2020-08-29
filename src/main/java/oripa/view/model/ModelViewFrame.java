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

package oripa.view.model;

import java.awt.BorderLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollBar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.ORIPA;
import oripa.domain.fold.FolderTool;
import oripa.domain.fold.OrigamiModel;
import oripa.persistent.doc.FileTypeKey;
import oripa.persistent.doc.SheetCutOutlinesHolder;
import oripa.persistent.entity.exporter.OrigamiModelExporterDXF;
import oripa.persistent.entity.exporter.OrigamiModelExporterOBJ;
import oripa.persistent.filetool.FileAccessActionProvider;
import oripa.persistent.filetool.FileAccessSupportFilter;
import oripa.persistent.filetool.FileChooserFactory;
import oripa.persistent.filetool.SavingActionTemplate;
import oripa.resource.Constants.ModelDisplayMode;
import oripa.util.gui.CallbackOnUpdate;
import oripa.viewsetting.main.MainScreenSettingDB;

/**
 * A frame to show a transparent folded model.
 *
 * @author Koji
 *
 */
public class ModelViewFrame extends JFrame
		implements AdjustmentListener {
	private final static Logger logger = LoggerFactory.getLogger(ModelViewFrame.class);

	private ModelViewScreen screen;
	private final JMenu menuDisp = new JMenu(ORIPA.res.getString("MENU_Disp"));
	private final JMenu menuFile = new JMenu(ORIPA.res.getString("File"));
	private final JMenuItem menuItemExportDXF = new JMenuItem(
			ORIPA.res.getString("MENU_ExportModelLine_DXF"));
	private final JMenuItem menuItemExportOBJ = new JMenuItem(
			"Export to OBJ file");
	private final JMenuItem menuItemFlip = new JMenuItem(
			ORIPA.res.getString("MENU_Invert"));
	private final JCheckBoxMenuItem menuItemCrossLine = new JCheckBoxMenuItem(
			"Show Cross-Line", false);
	public JCheckBoxMenuItem menuItemSlideFaces = new JCheckBoxMenuItem(
			ORIPA.res.getString("MENU_SlideFaces"), false);
	public JLabel hintLabel = new JLabel(ORIPA.res.getString("Direction_Basic"));
	private final JMenu dispSubMenu = new JMenu(
			ORIPA.res.getString("MENU_DispType"));
	private final JRadioButtonMenuItem menuItemFillAlpha = new JRadioButtonMenuItem(
			ORIPA.res.getString("MENU_FillAlpha"));
	private final JRadioButtonMenuItem menuItemFillNone = new JRadioButtonMenuItem(
			ORIPA.res.getString("MENU_DrawLines"));
	private final JScrollBar scrollBarAngle = new JScrollBar(
			JScrollBar.HORIZONTAL, 90, 5, 0, 185);
	private final JScrollBar scrollBarPosition = new JScrollBar(
			JScrollBar.VERTICAL, 0, 5, -150, 150);

	private final MainScreenSettingDB mainScreenSetting;

	public ModelViewFrame(final int width, final int height,
			final SheetCutOutlinesHolder lineHolder, final CallbackOnUpdate onUpdateCrossLine,
			final MainScreenSettingDB mainScreenSetting) {

		this.mainScreenSetting = mainScreenSetting;

		initialize(lineHolder, onUpdateCrossLine);
		this.setBounds(0, 0, width, height);

	}

	private void initialize(final SheetCutOutlinesHolder lineHolder,
			final CallbackOnUpdate onUpdateCrossLine) {

		setTitle(ORIPA.res.getString("ExpectedFoldedOrigami"));
		screen = new ModelViewScreen(lineHolder, onUpdateCrossLine, mainScreenSetting);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(screen, BorderLayout.CENTER);
		getContentPane().add(hintLabel, BorderLayout.SOUTH);
		getContentPane().add(scrollBarAngle, BorderLayout.NORTH);
		getContentPane().add(scrollBarPosition, BorderLayout.WEST);

		// Construct menu bar
		JMenuBar menuBar = new JMenuBar();

		menuFile.add(menuItemExportDXF);
		menuFile.add(menuItemExportOBJ);
		menuDisp.add(menuItemFlip);

		menuDisp.add(dispSubMenu);
		menuDisp.add(menuItemCrossLine);
		ButtonGroup dispGroup = new ButtonGroup();
		dispGroup.add(menuItemFillAlpha);
		dispSubMenu.add(menuItemFillAlpha);
		dispGroup.add(menuItemFillNone);
		dispSubMenu.add(menuItemFillNone);

		menuItemFillAlpha.setSelected(true);

		addActionListenersToComponents();

		menuBar.add(menuFile);
		menuBar.add(menuDisp);

		setJMenuBar(menuBar);

		scrollBarAngle.addAdjustmentListener(this);
		scrollBarPosition.addAdjustmentListener(this);
	}

	private OrigamiModel origamiModel = null;

	public void setModel(final OrigamiModel origamiModel) {
		int boundSize = Math.min(getWidth(), getHeight()
				- getJMenuBar().getHeight() - 50);
		screen.setModel(origamiModel, boundSize);
		this.origamiModel = origamiModel;
	}

	private void addActionListenersToComponents() {
		menuItemFlip.addActionListener(e -> flipOrigamiModel());

		menuItemSlideFaces.addActionListener(e -> slideOrigamiModel());

		menuItemCrossLine.addActionListener(e -> {
			mainScreenSetting.setCrossLineVisible(menuItemCrossLine.isSelected());
		});

		menuItemExportDXF.addActionListener(e -> exportFile(FileTypeKey.DXF_MODEL));

		menuItemExportOBJ.addActionListener(e -> exportFile(FileTypeKey.OBJ_MODEL));

		menuItemFillAlpha.addActionListener(e -> {
			screen.setModelDisplayMode(ModelDisplayMode.FILL_ALPHA);
			screen.repaint();
		});

		menuItemFillNone.addActionListener(e -> {
			screen.setModelDisplayMode(ModelDisplayMode.FILL_NONE);
			screen.repaint();
		});
	}

	private void flipOrigamiModel() {
		FolderTool folderTool = new FolderTool();
		folderTool.filpAll(origamiModel);
		screen.repaint();
	}

	private void slideOrigamiModel() {
		FolderTool folderTool = new FolderTool();
		folderTool.setFacesOutline(
				origamiModel.getVertices(), origamiModel.getFaces(),
				menuItemSlideFaces.isSelected());
		screen.repaint();
	}

	@Override
	public void adjustmentValueChanged(final AdjustmentEvent e) {
		if (e.getSource() == scrollBarAngle) {
			screen.setCrossLineAngle(e.getValue());
		} else if (e.getSource() == scrollBarPosition) {
			screen.setCrossLinePosition(e.getValue());
		}

	}

	private FileAccessSupportFilter<OrigamiModel> createFilter(final FileTypeKey type) {
		FileAccessSupportFilter<OrigamiModel> filter = new FileAccessSupportFilter<OrigamiModel>(
				type,
				FileAccessSupportFilter.createDefaultDescription(
						type, ORIPA.res.getString("File")));

		switch (type) {
		case DXF_MODEL:
			filter.setSavingAction(
					new SavingActionTemplate<OrigamiModel>(new OrigamiModelExporterDXF()));
			break;
		case OBJ_MODEL:
			filter.setSavingAction(
					new SavingActionTemplate<OrigamiModel>(new OrigamiModelExporterOBJ()));
			break;
		default:
			throw new RuntimeException("Wrong implementation");
		}

		return filter;
	}

	private void exportFile(final FileTypeKey type) {

		FileChooserFactory<OrigamiModel> chooserFactory = new FileChooserFactory<>();
		FileAccessActionProvider<OrigamiModel> chooser = chooserFactory.createChooser(null,
				createFilter(type));

		try {
			chooser.getActionForSavingFile(this).save(origamiModel);
		} catch (Exception e) {

		}

	}

}
