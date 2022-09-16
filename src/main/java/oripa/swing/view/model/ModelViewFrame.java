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

package oripa.swing.view.model;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.*;

import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.geom.RectangleDomain;
import oripa.gui.view.FrameView;
import oripa.gui.view.main.PainterScreenSetting;
import oripa.gui.view.model.ModelDisplayMode;
import oripa.gui.view.model.ModelViewFrameView;
import oripa.gui.view.model.ModelViewScreenView;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;
import oripa.swing.view.util.Dialogs;
import oripa.swing.view.util.ListItemSelectionPanel;

/**
 * A frame to show a transparent folded model.
 *
 * @author Koji
 *
 */
public class ModelViewFrame extends JFrame
		implements ModelViewFrameView, AdjustmentListener, WindowListener, ComponentListener {

	private final ResourceHolder resourceHolder = ResourceHolder.getInstance();

	private ModelViewScreen screen;
	private final JMenu menuDisp = new JMenu(
			resourceHolder.getString(ResourceKey.LABEL, StringID.ModelUI.DISPLAY_ID));
	private final JMenu menuFile = new JMenu(resourceHolder.getString(ResourceKey.LABEL, StringID.ModelUI.FILE_ID));
	private final JMenuItem menuItemExportDXF = new JMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.ModelUI.EXPORT_DXF_ID));
	private final JMenuItem menuItemExportOBJ = new JMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.ModelUI.EXPORT_OBJ_ID));
	private final JMenuItem menuItemExportSVG = new JMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.ModelUI.EXPORT_SVG_ID));
	private final JMenuItem menuItemFlip = new JMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.ModelUI.INVERT_ID));
	private final JCheckBoxMenuItem menuItemCrossLine = new JCheckBoxMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.ModelUI.SHOW_CROSS_LINE_ID), false);
	private final JLabel hintLabel = new JLabel(
			resourceHolder.getString(ResourceKey.LABEL, StringID.ModelUI.DIRECTION_BASIC_ID));
	private final JMenu dispSubMenu = new JMenu(
			resourceHolder.getString(ResourceKey.LABEL, StringID.ModelUI.DISPLAY_TYPE_ID));
	private final JRadioButtonMenuItem menuItemFillAlpha = new JRadioButtonMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.ModelUI.FILL_ALPHA_ID));
	private final JRadioButtonMenuItem menuItemFillNone = new JRadioButtonMenuItem(
			resourceHolder.getString(ResourceKey.LABEL, StringID.ModelUI.DRAW_LINES_ID));
	private final JScrollBar scrollBarAngle = new JScrollBar(
			Adjustable.HORIZONTAL, 90, 5, 0, 185);
	private final JScrollBar scrollBarPosition = new JScrollBar(
			Adjustable.VERTICAL, 0, 5, -150, 150);

	private final PainterScreenSetting mainScreenSetting;

	private final ListItemSelectionPanel modelSelectionPanel = new ListItemSelectionPanel(
			resourceHolder.getString(ResourceKey.LABEL, StringID.ModelUI.MODEL_ID));

	private final Map<Object, PropertyChangeListener> modelIndexChangeListenerMap = new HashMap<>();

	private final PropertyChangeSupport support = new PropertyChangeSupport(this);
	private final String PAPER_DOMAIN = "PAPER_DOMAIN";
	private RectangleDomain domainBeforeFolding;
	private final Map<Object, PropertyChangeListener> paperDomainChangeListenerMap = new HashMap<>();

	private Consumer<FrameView> onCloseListener;

	public ModelViewFrame(
			final int width, final int height,
			final PainterScreenSetting mainScreenSetting) {

		this.mainScreenSetting = mainScreenSetting;

		initialize();
		this.setBounds(0, 0, width, height);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	private void initialize() {

		setTitle(resourceHolder.getString(ResourceKey.LABEL, StringID.ModelUI.TITLE_ID));
		screen = new ModelViewScreen(mainScreenSetting);

		setLayout(new BorderLayout());
		add(screen, BorderLayout.CENTER);

		var southPanel = new JPanel();
		southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
		southPanel.add(modelSelectionPanel);
		southPanel.add(hintLabel);

		add(southPanel, BorderLayout.SOUTH);

		add(scrollBarAngle, BorderLayout.NORTH);
		add(scrollBarPosition, BorderLayout.WEST);

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

		menuBar.add(menuFile);
		menuBar.add(menuDisp);

		setJMenuBar(menuBar);

		scrollBarAngle.addAdjustmentListener(this);
		scrollBarPosition.addAdjustmentListener(this);

		addWindowListener(this);
		addComponentListener(this);
	}

	@Override
	public ModelViewScreenView getModelScreenView() {
		return screen;
	}

	@Override
	public void addModelSwitchListener(final Consumer<Integer> listener) {
		modelSelectionPanel.addPropertyChangeListener(
				ListItemSelectionPanel.INDEX,
				e -> listener.accept((Integer) e.getNewValue()));
	}

	@Override
	public void setModelCount(final int count) {
		modelSelectionPanel.setItemCount(count);
	}

	@Override
	public void setModel(final OrigamiModel origamiModel) {
		int boundSize = Math.min(getWidth(), getHeight()
				- getJMenuBar().getHeight() - 100);
		screen.setModel(origamiModel, boundSize);

		setDomainBeforeFolding(origamiModel.createPaperDomain());
	}

	private void setDomainBeforeFolding(final RectangleDomain domain) {
		var old = domainBeforeFolding;
		domainBeforeFolding = domain;
		support.firePropertyChange(PAPER_DOMAIN, old, domainBeforeFolding);
	}

	@Override
	public void putPaperDomainChangeListener(final Object parentOfListener, final PropertyChangeListener listener) {
		if (paperDomainChangeListenerMap.get(parentOfListener) == null) {
			paperDomainChangeListenerMap.put(parentOfListener, listener);
			support.addPropertyChangeListener(PAPER_DOMAIN, listener);
		}
	}

	@Override
	public void putModelIndexChangeListener(final Object parentOfListener, final PropertyChangeListener listener) {
		if (modelIndexChangeListenerMap.get(parentOfListener) == null) {
			modelIndexChangeListenerMap.put(parentOfListener, listener);
			modelSelectionPanel.addPropertyChangeListener(ListItemSelectionPanel.INDEX, listener);
		}
	}

	@Override
	public void selectModel(final int index) {
		modelSelectionPanel.selectItem(index);

	}

	@Override
	public void adjustmentValueChanged(final AdjustmentEvent e) {
		if (e.getSource() == scrollBarAngle) {
			screen.setScissorsLineAngle(e.getValue());
		} else if (e.getSource() == scrollBarPosition) {
			screen.setScissorsLinePosition(e.getValue());
		}

	}

	@Override
	public void setOnCloseListener(final Consumer<FrameView> listener) {
		onCloseListener = listener;
	}

	@Override
	public void windowOpened(final WindowEvent e) {

	}

	@Override
	public void windowClosing(final WindowEvent e) {
		setDomainBeforeFolding(null);
	}

	@Override
	public void windowClosed(final WindowEvent e) {
		onCloseListener.accept(this);
		mainScreenSetting.setCrossLineVisible(false);
	}

	@Override
	public void windowIconified(final WindowEvent e) {

	}

	@Override
	public void windowDeiconified(final WindowEvent e) {

	}

	@Override
	public void windowActivated(final WindowEvent e) {

	}

	@Override
	public void windowDeactivated(final WindowEvent e) {

	}

	@Override
	public void componentResized(final ComponentEvent e) {

	}

	@Override
	public void componentMoved(final ComponentEvent e) {

	}

	@Override
	public void componentShown(final ComponentEvent e) {

	}

	@Override
	public void componentHidden(final ComponentEvent e) {
		setDomainBeforeFolding(null);
	}

	@Override
	public void addFlipModelButtonListener(final Runnable listener) {
		addButtonListener(menuItemFlip, listener);
	}

	@Override
	public void addCrossLineButtonListener(final Runnable listener) {
		addButtonListener(menuItemCrossLine, listener);
	}

	@Override
	public void addExportDXFButtonListener(final Runnable listener) {
		addButtonListener(menuItemExportDXF, listener);
	}

	@Override
	public void addExportOBJButtonListener(final Runnable listener) {
		addButtonListener(menuItemExportOBJ, listener);
	}

	@Override
	public void addExportSVGButtonListener(final Runnable listener) {
		addButtonListener(menuItemExportSVG, listener);
	}

	@Override
	public void addFillAlphaButtonListener(final Runnable listener) {
		addButtonListener(menuItemFillAlpha, listener);
	}

	@Override
	public void addFillNoneButtonListener(final Runnable listener) {
		addButtonListener(menuItemFillNone, listener);
	}

	private void addButtonListener(final AbstractButton button, final Runnable listener) {
		button.addActionListener(e -> listener.run());
	}

	@Override
	public boolean isCrossLineVisible() {
		return menuItemCrossLine.isSelected();
	}

	@Override
	public void setModelDisplayMode(final ModelDisplayMode mode) {
		screen.setModelDisplayMode(mode);
	}

	@Override
	public void showExportErrorMessage(final Exception e) {
		Dialogs.showErrorDialog(this, resourceHolder.getString(ResourceKey.ERROR, StringID.Error.DEFAULT_TITLE_ID),
				e);

	}
}
