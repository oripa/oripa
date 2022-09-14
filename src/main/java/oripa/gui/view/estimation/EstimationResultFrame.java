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

package oripa.gui.view.estimation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.JLabel;

import oripa.domain.fold.FoldedModel;
import oripa.gui.view.FrameView;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;
import oripa.swing.view.util.ListItemSelectionPanel;

public class EstimationResultFrame extends JFrame implements EstimationResultFrameView, WindowListener {

	private static final long serialVersionUID = 1L;

	private final ResourceHolder resources = ResourceHolder.getInstance();

	private final ListItemSelectionPanel modelSelectionPanel = new ListItemSelectionPanel(
			resources.getString(ResourceKey.LABEL, StringID.EstimationResultUI.MODEL_ID));
	private final FoldedModelScreen screen = new FoldedModelScreen();
	private final EstimationResultUI ui = new EstimationResultUI();
	private final JLabel hintLabel = new JLabel(
			resources.getString(ResourceKey.LABEL, StringID.EstimationResultUI.HINT_LABEL_ID));;

	private final Map<Object, PropertyChangeListener> modelIndexChangeListenerMap = new HashMap<>();

	private Consumer<FrameView> onCloseListener;

	public EstimationResultFrame() {
		setTitle(resources.getString(ResourceKey.LABEL, StringID.EstimationResultUI.TITLE_ID));
		setBounds(0, 0, 800, 650);
		setLayout(new BorderLayout());

		ui.setScreen(screen);

		add(modelSelectionPanel, BorderLayout.NORTH);
		add(ui, BorderLayout.WEST);
		add(screen, BorderLayout.CENTER);
		add(hintLabel, BorderLayout.SOUTH);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(this);
	}

	@Override
	public EstimationResultUIView getUI() {
		return ui;
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
	public void setModel(final FoldedModel foldedModel) {
		if (foldedModel.getFoldablePatternCount() == 0) {
			screen.setModel(null);
			ui.setModel(null);
			return;
		}
		screen.setModel(foldedModel);
		ui.setModel(foldedModel);
	}

	@Override
	public void setColors(final Color front, final Color back) {
		ui.setColors(front, back);
	}

	@Override
	public void setSaveColorsListener(final BiConsumer<Color, Color> listener) {
		ui.setSaveColorsListener(listener);
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
	public void setOnCloseListener(final Consumer<FrameView> listener) {
		onCloseListener = listener;
	}

	@Override
	public void windowOpened(final WindowEvent e) {

	}

	@Override
	public void windowClosing(final WindowEvent e) {
	}

	@Override
	public void windowClosed(final WindowEvent e) {
		onCloseListener.accept(this);
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
}
