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

import oripa.application.main.PaintContextModification;
import oripa.domain.cutmodel.CutModelOutlinesHolder;
import oripa.domain.paint.PaintContext;
import oripa.gui.view.ViewScreenUpdater;
import oripa.gui.view.main.MainFrameView;
import oripa.gui.view.main.PainterScreenSetting;
import oripa.gui.view.util.ChildFrameManager;
import oripa.project.Project;

/**
 * @author OUCHI Koji
 *
 */
public class ClearActionPresentationLogic {

	private final MainFrameView view;

	private final ViewScreenUpdater screenUpdater;
	private final PainterScreenSetting screenSetting;

	private final ChildFrameManager childFrameManager;

	private final PaintContext paintContext;
	private final CutModelOutlinesHolder cutModelOutlinesHolder;

	private final Project project;

	private final PaintContextModification paintContextModification;

	public ClearActionPresentationLogic(
			final MainFrameView view,
			final ViewScreenUpdater screenUpdater,
			final PainterScreenSetting screenSetting,
			final ChildFrameManager childFrameManager,
			final PaintContext paintContext,
			final CutModelOutlinesHolder cutModelOutlinesHolder,
			final Project project,
			final PaintContextModification paintContextModification

	) {
		this.view = view;
		this.screenUpdater = screenUpdater;
		this.screenSetting = screenSetting;
		this.childFrameManager = childFrameManager;
		this.paintContext = paintContext;
		this.cutModelOutlinesHolder = cutModelOutlinesHolder;
		this.project = project;
		this.paintContextModification = paintContextModification;
	}

	public void clear() {
		paintContextModification.clear(paintContext, cutModelOutlinesHolder);
		project.clear();

		screenSetting.setGridVisible(true);

		childFrameManager.closeAll(view);

		screenUpdater.updateScreen();
	}

}
