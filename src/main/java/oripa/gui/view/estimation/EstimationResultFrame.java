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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;

import oripa.domain.fold.FoldedModel;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;

public class EstimationResultFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	private final ResourceHolder resources = ResourceHolder.getInstance();

	private final FoldedModelScreen screen = new FoldedModelScreen();
	private final EstimationResultUI ui = new EstimationResultUI();
	private final JLabel hintLabel = new JLabel(
			resources.getString(ResourceKey.LABEL, StringID.EstimationResultUI.HINT_LABEL_ID));;

	public EstimationResultFrame() {
		setTitle(resources.getString(ResourceKey.LABEL, StringID.EstimationResultUI.TITLE_ID));
		setBounds(0, 0, 800, 600);
		setLayout(new BorderLayout());

		ui.setScreen(screen);

		add(ui, BorderLayout.WEST);
		add(screen, BorderLayout.CENTER);
		add(hintLabel, BorderLayout.SOUTH);
	}

	public void setModel(final FoldedModel foldedModel) {
		screen.setModel(foldedModel);
		ui.setModel(foldedModel);
	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {

	}
}
