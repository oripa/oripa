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
package oripa.swing.view.util;

import javax.swing.SwingWorker;

/**
 * @author OUCHI Koji
 *
 */
public class SimpleModalWorker extends SwingWorker<Void, Void> {
	private final SimpleModalDialog dialog;
	private final Runnable action;

	public SimpleModalWorker(final SimpleModalDialog dialog, final Runnable action) {
		this.dialog = dialog;
		this.action = action;

		addPropertyChangeListener(e -> {
			if ("state".equals(e.getPropertyName())
					&& SwingWorker.StateValue.DONE == e.getNewValue()) {
				dialog.setVisible(false);
				dialog.dispose();
			}
		});

	}

	@Override
	protected Void doInBackground() throws Exception {
		action.run();
		return null;
	}

	public void executeModal() {
		execute();
		dialog.setVisible(true);
	}
}
