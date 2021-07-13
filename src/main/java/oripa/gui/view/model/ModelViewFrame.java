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

package oripa.gui.view.model;

import java.awt.Adjustable;
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

import oripa.application.model.OrigamiModelFileAccess;
import oripa.domain.cutmodel.CutModelOutlinesHolder;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.gui.view.util.CallbackOnUpdate;
import oripa.gui.view.util.Dialogs;
import oripa.gui.viewsetting.main.MainScreenSetting;
import oripa.persistence.entity.OrigamiModelDAO;
import oripa.persistence.entity.OrigamiModelFileTypeKey;
import oripa.persistence.entity.OrigamiModelFilterSelector;
import oripa.persistence.filetool.FileChooserCanceledException;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;

/**
 * A frame to show a transparent folded model.
 *
 * @author Koji
 *
 */
public class ModelViewFrame extends JFrame
		implements AdjustmentListener {
	private final ResourceHolder resourceHolder = ResourceHolder.getInstance();

	private ModelViewScreen screen;
	private final JMenu menuDisp = new JMenu(
			resourceHolder.getString(ResourceKey.LABEL, StringID.ModelMenu.DISPLAY_ID));
	private final JMenu menuFile = new JMenu(resourceHolder.getString(ResourceKey.LABEL, StringID.ModelMenu.FILE_ID));
	private final JMenuItem menuItemExportDXF = new JMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.ModelMenu.EXPORT_DXF_ID));
	private final JMenuItem menuItemExportOBJ = new JMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.ModelMenu.EXPORT_OBJ_ID));
	private final JMenuItem menuItemExportSVG = new JMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.ModelMenu.EXPORT_SVG_ID));
	private final JMenuItem menuItemFlip = new JMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.ModelMenu.INVERT_ID));
	private final JCheckBoxMenuItem menuItemCrossLine = new JCheckBoxMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.ModelMenu.SHOW_CROSS_LINE_ID), false);
	private final JLabel hintLabel = new JLabel(
			resourceHolder.getString(ResourceKey.LABEL, StringID.ModelMenu.DIRECTION_BASIC_ID));
	private final JMenu dispSubMenu = new JMenu(
			resourceHolder.getString(ResourceKey.LABEL, StringID.ModelMenu.DISPLAY_TYPE_ID));
	private final JRadioButtonMenuItem menuItemFillAlpha = new JRadioButtonMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.ModelMenu.FILL_ALPHA_ID));
	private final JRadioButtonMenuItem menuItemFillNone = new JRadioButtonMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.ModelMenu.DRAW_LINES_ID));
	private final JScrollBar scrollBarAngle = new JScrollBar(
			Adjustable.HORIZONTAL, 90, 5, 0, 185);
	private final JScrollBar scrollBarPosition = new JScrollBar(
			Adjustable.VERTICAL, 0, 5, -150, 150);

	private OrigamiModel origamiModel = null;
	private final MainScreenSetting mainScreenSetting;

	private final OrigamiModelFilterSelector filterSelector = new OrigamiModelFilterSelector();
	private final OrigamiModelFileAccess fileAccess = new OrigamiModelFileAccess(new OrigamiModelDAO(filterSelector));

	public ModelViewFrame(
			final int width, final int height,
			final CutModelOutlinesHolder lineHolder, final CallbackOnUpdate onUpdateCrossLine,
			final MainScreenSetting mainScreenSetting) {

		this.mainScreenSetting = mainScreenSetting;

		initialize(lineHolder, onUpdateCrossLine);
		this.setBounds(0, 0, width, height);

	}

	private void initialize(final CutModelOutlinesHolder lineHolder,
			final CallbackOnUpdate onUpdateCrossLine) {

		setTitle(resourceHolder.getString(ResourceKey.LABEL, StringID.ModelMenu.TITLE_ID));
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
		menuFile.add(menuItemExportSVG);
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

	public void setModel(final OrigamiModel origamiModel) {
		int boundSize = Math.min(getWidth(), getHeight()
				- getJMenuBar().getHeight() - 50);
		screen.setModel(origamiModel, boundSize);
		this.origamiModel = origamiModel;
	}

	private void addActionListenersToComponents() {
		menuItemFlip.addActionListener(e -> flipOrigamiModel());

		menuItemCrossLine.addActionListener(e -> mainScreenSetting.setCrossLineVisible(menuItemCrossLine.isSelected()));

		menuItemExportDXF.addActionListener(e -> exportFile(OrigamiModelFileTypeKey.DXF_MODEL));

		menuItemExportOBJ.addActionListener(e -> exportFile(OrigamiModelFileTypeKey.OBJ_MODEL));

		menuItemExportSVG.addActionListener(e -> exportFile(OrigamiModelFileTypeKey.SVG_MODEL));

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
		origamiModel.flipXCoordinates();
		screen.repaint();
	}

	@Override
	public void adjustmentValueChanged(final AdjustmentEvent e) {
		if (e.getSource() == scrollBarAngle) {
			screen.setScissorsLineAngle(e.getValue());
		} else if (e.getSource() == scrollBarPosition) {
			screen.setScissorsLinePosition(e.getValue());
		}

	}

	private void exportFile(final OrigamiModelFileTypeKey type) {

		try {
			fileAccess.saveFile(origamiModel, this, filterSelector.getFilter(type));
		} catch (FileChooserCanceledException ignored) {
			// ignored
		} catch (Exception e) {
			Dialogs.showErrorDialog(this, resourceHolder.getString(ResourceKey.ERROR, StringID.Error.DEFAULT_TITLE_ID),
					e);
		}
	}

}
