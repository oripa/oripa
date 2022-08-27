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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.gui.view.FrameView;

/**
 * @author Koji
 *
 */
public class ChildFrameManager {
	private static final Logger logger = LoggerFactory.getLogger(ChildFrameManager.class);

	private final HashMap<FrameView, Collection<FrameView>> relationMap = new HashMap<>();

	public ChildFrameManager() {
	}

	public Collection<FrameView> getChildren(final FrameView parentFrame) {
		var children = relationMap.get(parentFrame);
		if (children == null) {
			children = new ArrayList<>();
			relationMap.put(parentFrame, children);
		}

		return children;
	}

	public void putChild(final FrameView parentFrame, final FrameView childFrame) {
		getChildren(parentFrame).add(childFrame);
	}

	public <TFrame extends FrameView> TFrame find(final FrameView parentFrame, final Class<TFrame> clazz) {
		var children = getChildren(parentFrame);
		logger.info("children of " + parentFrame + ": " + children);
		for (var child : children) {
			if (clazz.isInstance(child)) {
				logger.info("child(class = " + clazz.getName() + ") is found.");
				return clazz.cast(child);
			}
		}

		return null;
	}

	public void closeAll(final FrameView parentFrame) {
		if (parentFrame == null) {
			return;
		}

		var children = getChildren(parentFrame);

		for (FrameView frame : children) {
			closeAll(frame);
			frame.setViewVisible(false);
		}
	}
}
