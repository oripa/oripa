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
import java.util.List;
import java.util.function.BiConsumer;

import javax.swing.JFrame;
import javax.swing.JLabel;

import oripa.domain.fold.FoldedModel;
import oripa.gui.view.util.ListItemSelectionPanel;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;

public class EstimationResultFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private final ResourceHolder resources = ResourceHolder.getInstance();

	private final ListItemSelectionPanel<FoldedModel> modelSelectionPanel = new ListItemSelectionPanel<>("Model");
	private final FoldedModelScreen screen = new FoldedModelScreen();
	private final EstimationResultUI ui = new EstimationResultUI();
	private final JLabel hintLabel = new JLabel(
			resources.getString(ResourceKey.LABEL, StringID.EstimationResultUI.HINT_LABEL_ID));;

	public EstimationResultFrame() {
		setTitle(resources.getString(ResourceKey.LABEL, StringID.EstimationResultUI.TITLE_ID));
		setBounds(0, 0, 800, 600);
		setLayout(new BorderLayout());

		ui.setScreen(screen);

		add(modelSelectionPanel, BorderLayout.NORTH);
		add(ui, BorderLayout.WEST);
		add(screen, BorderLayout.CENTER);
		add(hintLabel, BorderLayout.SOUTH);

		addPropertyChangeListenerToComponents();
	}

	private void addPropertyChangeListenerToComponents() {
		modelSelectionPanel.addPropertyChangeListener(ListItemSelectionPanel.ITEM,
				e -> setModel((FoldedModel) e.getNewValue()));
	}

	public void setModels(final List<FoldedModel> models) {
		modelSelectionPanel.setItems(models);
	}

	private void setModel(final FoldedModel foldedModel) {
		if (foldedModel.getFoldablePatternCount() == 0) {
			screen.setModel(null);
			ui.setModel(null);
			return;
		}
		screen.setModel(foldedModel);
		ui.setModel(foldedModel);
	}

	public void setColors(final Color front, final Color back) {
		ui.setColors(front, back);
	}

	public void setSaveColorsListener(final BiConsumer<Color, Color> listener) {
		ui.setSaveColorsListener(listener);
	}
}
