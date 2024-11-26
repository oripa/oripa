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
package oripa.swing.view.model;

import java.beans.PropertyChangeListener;

import jakarta.inject.Inject;
import oripa.gui.view.FrameView;
import oripa.gui.view.main.PainterScreenSetting;
import oripa.gui.view.model.ModelViewFrameFactory;
import oripa.gui.view.model.ModelViewFrameView;
import oripa.gui.view.util.ChildFrameManager;

/**
 * @author Koji
 *
 */
public class ModelViewSwingFrameFactory implements ModelViewFrameFactory {

	private final PainterScreenSetting mainScreenSetting;
	private final ChildFrameManager childFrameManager;

	@Inject
	public ModelViewSwingFrameFactory(final PainterScreenSetting mainScreenSetting,
			final ChildFrameManager childFrameManager) {
		this.mainScreenSetting = mainScreenSetting;
		this.childFrameManager = childFrameManager;
	}

	@Override
	public ModelViewFrameView createFrame(
			final FrameView parent,
			final PropertyChangeListener onChangePaperDomain) {

		var frameOpt = childFrameManager.find(parent,
				ModelViewFrame.class);

		frameOpt.ifPresent(frame -> {
			removeFromChildFrameManager(frame);
			frame.dispose();
		});

		var frame = new ModelViewFrame(400, 400, mainScreenSetting);

		frame.putPaperDomainChangeListener(parent, onChangePaperDomain);

		frame.setOnCloseListener(this::removeFromChildFrameManager);
		childFrameManager.putChild(parent, frame);

		return frame;
	}

	private void removeFromChildFrameManager(final FrameView frame) {
		childFrameManager.closeAll(frame);
		childFrameManager.removeFromChildren(frame);
	}

}
