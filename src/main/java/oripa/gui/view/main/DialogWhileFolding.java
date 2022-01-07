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
package oripa.gui.view.main;

import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;

/**
 * @author OUCHI Koji
 *
 */
public class DialogWhileFolding extends JDialog {

	private SwingWorker<List<JFrame>, Void> worker;
	private final int WIDTH = 200;
	private final int HEIGHT = 100;

	public DialogWhileFolding(final JFrame parent, final ResourceHolder resources) {
		super(parent, true);
		setTitle(resources.getString(ResourceKey.INFO, StringID.Information.NOW_FOLDING_TITLE_ID));

		setLayout(new GridBagLayout());
		add(new JLabel(resources.getString(
				ResourceKey.INFO,
				StringID.Information.NOW_FOLDING_ID)));

		setSize(WIDTH, HEIGHT);
		setLocationRelativeTo(parent);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(final WindowEvent e) {
				worker.cancel(true); // doesn't work so far.
			}
		});
	}

	public void setWorker(final SwingWorker<List<JFrame>, Void> worker) {
		this.worker = worker;
	}
}
