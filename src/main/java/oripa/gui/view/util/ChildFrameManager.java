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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Singleton;
import oripa.gui.view.FrameView;

/**
 * @author Koji
 *
 */
@Singleton
public class ChildFrameManager {
	private static final Logger logger = LoggerFactory.getLogger(ChildFrameManager.class);

	private final HashMap<FrameView, Collection<FrameView>> relationMap = new HashMap<>();

	public ChildFrameManager() {
	}

	public Collection<FrameView> getChildren(final FrameView parentFrame) {
		relationMap.putIfAbsent(parentFrame, new HashSet<>());
		var children = relationMap.get(parentFrame);

		return children;
	}

	public void putChild(final FrameView parentFrame, final FrameView childFrame) {
		var children = getChildren(parentFrame);
		children.add(childFrame);
		logger.info("{} is put.", childFrame);
		logger.info("There are {} children.", children.size());
	}

	public void removeChild(final FrameView parentFrame, final FrameView childFrame) {
		var children = getChildren(parentFrame);
		if (children.remove(childFrame)) {
			logger.info("{} is removed.", childFrame);
			logger.info("There are {} children.", children.size());
		}
	}

	public void removeFromChildren(final FrameView childFrame) {
		relationMap.keySet().forEach(parentFrame -> removeChild(parentFrame, childFrame));
	}

	public <TFrame extends FrameView> Optional<TFrame> find(final FrameView parentFrame, final Class<TFrame> clazz) {
		var children = getChildren(parentFrame);
		logger.info("{} children of  {}: {}", children.size(), parentFrame, children);
		for (var child : children) {
			if (clazz.isInstance(child)) {
				logger.info("child(class = " + clazz.getName() + ") is found.");
				return Optional.of(clazz.cast(child));
			}
		}

		return Optional.empty();
	}

	/**
	 * Disposes all descendants of {@code parentFrame}.
	 *
	 * @param parentFrame
	 */
	public void closeAll(final FrameView parentFrame) {
		if (parentFrame == null) {
			return;
		}

		var children = getChildren(parentFrame);

		var iterator = children.iterator();
		while (iterator.hasNext()) {
			var child = iterator.next();
			closeAll(child);
			iterator.remove();
			child.dispose();
		}
	}

}
