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
package oripa.gui.view.util;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Koji
 *
 */
//TODO use View interface, not JComponent.
public class ChildFrameManager {
	private static final Logger logger = LoggerFactory.getLogger(ChildFrameManager.class);

	private final HashMap<JComponent, Collection<JFrame>> relationMap = new HashMap<>();

	public ChildFrameManager() {
	}

	public Collection<JFrame> getChildren(final JComponent parent) {
		var children = relationMap.get(parent);
		if (children == null) {
			children = new ArrayList<>();
			relationMap.put(parent, children);
		}

		return children;
	}

	public void putChild(final JComponent parent, final JFrame child) {
		getChildren(parent).add(child);
	}

	public <TFrame extends JFrame> TFrame find(final JComponent parent, final Class<TFrame> clazz) {
		var children = getChildren(parent);
		logger.info("children of " + parent + ": " + children);
		for (var child : children) {
			if (clazz.isInstance(child)) {
				logger.info("child(class = " + clazz.getName() + ") is found.");
				return clazz.cast(child);
			}
		}

		return null;
	}

	public void closeAll(final JComponent parent) {
		var children = getChildren(parent);
		for (JFrame frame : children) {
			// TODO make all frames short-life object
			// then change the following to dispose()
			frame.setVisible(false);
		}
		// children.clear(); // enable this if dispose() is used.
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
