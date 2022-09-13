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
package oripa.gui.view.model;

import java.beans.PropertyChangeListener;

import oripa.domain.cutmodel.CutModelOutlinesHolder;
import oripa.gui.view.FrameView;
import oripa.gui.view.main.PainterScreenSetting;
import oripa.gui.view.util.CallbackOnUpdate;
import oripa.gui.view.util.ChildFrameManager;

/**
 * @author Koji
 *
 */
public class ModelViewSwingFrameFactory implements ModelViewFrameFactory {

	private final PainterScreenSetting mainScreenSetting;
	private final ChildFrameManager childFrameManager;

	public ModelViewSwingFrameFactory(final PainterScreenSetting mainScreenSetting,
			final ChildFrameManager childFrameManager) {
		this.mainScreenSetting = mainScreenSetting;
		this.childFrameManager = childFrameManager;
	}

	@Override
	public ModelViewFrameView createFrame(
			final FrameView parent,
			final CutModelOutlinesHolder lineHolder, final CallbackOnUpdate onUpdateLine,
			final PropertyChangeListener onChangePaperDomain) {

		ModelViewFrame frame = childFrameManager.find(parent,
				ModelViewFrame.class);

		if (frame != null) {
			removeFromChildFrameManager(frame);
			frame.dispose();
		}
		frame = new ModelViewFrame(400, 400, lineHolder, onUpdateLine, mainScreenSetting);

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
