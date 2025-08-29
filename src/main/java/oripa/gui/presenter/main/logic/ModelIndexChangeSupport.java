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
package oripa.gui.presenter.main.logic;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;

/**
 * @author OUCHI Koji
 *
 */
public class ModelIndexChangeSupport {
	private final PropertyChangeSupport support = new PropertyChangeSupport(this);

	private final HashMap<Object, PropertyChangeListener> map = new HashMap<>();

	private static final String INDEX = "INDEX";

	private int index = -1;

	public void putListener(final Object parentOfListener, final PropertyChangeListener listener) {
		removeListener(parentOfListener);

		support.addPropertyChangeListener(INDEX, listener);
	}

	public void removeListener(final Object parentOfListener) {
		if (!map.containsKey(parentOfListener)) {
			return;
		}

		support.removePropertyChangeListener(map.remove(parentOfListener));
	}

	public void removeListeners() {
		map.keySet().forEach(parent -> removeListener(parent));

		map.clear();

		index = -1;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(final int index) {
		var old = this.index;
		this.index = index;

		support.firePropertyChange(INDEX, old, index);
	}
}
