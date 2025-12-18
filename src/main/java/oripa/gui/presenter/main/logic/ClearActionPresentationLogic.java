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

import jakarta.inject.Inject;
import oripa.application.main.PaintContextService;
import oripa.gui.presenter.main.PainterScreenPresenter;
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

	private final PainterScreenPresenter screenPresenter;
	private final PainterScreenSetting screenSetting;

	private final ChildFrameManager childFrameManager;

	private final Project project;

	private final PaintContextService paintContextService;

	@Inject
	public ClearActionPresentationLogic(
			final MainFrameView view,
			final PainterScreenPresenter screenPresenter,
			final PainterScreenSetting screenSetting,
			final ChildFrameManager childFrameManager,
			final Project project,
			final PaintContextService paintContextService) {
		this.view = view;
		this.screenPresenter = screenPresenter;
		this.screenSetting = screenSetting;
		this.childFrameManager = childFrameManager;
		this.project = project;
		this.paintContextService = paintContextService;
	}

	public void clear(boolean keepContour) {
		if (keepContour) {
			paintContextService.clearLines();
		} else {
			paintContextService.clearAll();
		}
		project.clear();

		screenSetting.setGridVisible(true);

		childFrameManager.closeAll(view);

		screenPresenter.clearPaperDomainOfModel();
		screenPresenter.updateScreen();
	}

}
