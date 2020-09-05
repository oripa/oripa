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
package oripa.util.gui;

import java.awt.Component;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * @author Koji
 *
 */
public class ChildFrameManager {
	private final HashMap<JComponent, ChildFrameList> relationMap = new HashMap<>();

	private static ChildFrameManager manager;

	public static ChildFrameManager getManager() {
		if (manager == null) {
			manager = new ChildFrameManager();
		}

		return manager;
	}

	private ChildFrameManager() {
	}

	public ChildFrameList getChildren(final JComponent parent) {
		ChildFrameList children = relationMap.get(parent);
		if (children == null) {
			children = new ChildFrameList();
			relationMap.put(parent, children);
		}

		return children;
	}

	public void putChild(final JComponent parent, final JFrame child) {
		getChildren(parent).addChild(child);
	}

	public void closeAll(final JComponent parent) {
		ChildFrameList children = getChildren(parent);
		children.clear();
	}

	/**
	 * Close all child frames of the given component and do the same for
	 * descendants of the given component.
	 *
	 * @param frame
	 */
	public void closeAllChildrenRecursively(final JFrame frame) {
		for (Component component : frame.getComponents()) {
			if (!(component instanceof JComponent)) {
				continue;
			}
			JComponent casted = (JComponent) component;
			closeAllRecursively(casted);
		}
	}

	/**
	 * Close all child frames of the given component and do the same for
	 * descendants of the given component.
	 *
	 * @param parent
	 */
	private void closeAllRecursively(final JComponent parent) {
		closeAll(parent);

		for (JComponent key : relationMap.keySet()) {
			if (parent.isAncestorOf(key)) {
				closeAll(key);
			}
		}
	}

}
