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
package oripa.gui.presenter.main;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.byvalue.ValueSetting;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.view.main.MainDialogService;
import oripa.gui.view.main.UIPanelView;
import oripa.gui.view.util.ChildFrameManager;
import oripa.gui.viewsetting.ViewScreenUpdater;
import oripa.gui.viewsetting.main.uipanel.UIPanelSetting;
import oripa.resource.ResourceHolder;

/**
 * @author OUCHI Koji
 *
 */
public class UIPanelPresenter {
	UIPanelView view;

	private final ResourceHolder resources = ResourceHolder.getInstance();

	private final MainDialogService dialogService = new MainDialogService(resources);

	private final UIPanelSetting setting;
	private final ValueSetting valueSetting;
	private ChildFrameManager childFrameManager;

	private final ViewScreenUpdater screenUpdater;
	private final PaintContext paintContext;
	private final CreasePatternViewContext viewContext;

	public UIPanelPresenter(final UIPanelView view) {
		this.view = view;

		setting = view.getUIPanelSetting();
		valueSetting = setting.getValueSetting();
		screenUpdater = view.getScreenUpdater();
		paintContext = view.getPaintContext();
		viewContext = view.getViewContext();

		addListeners();
	}

	private void addListeners() {
		view.addGridSmallButtonListener(this::makeGridSizeHalf);
		view.addGridLargeButtonListener(this::makeGridSizeTwiceLarge);
		view.addGridChangeButtonListener(this::updateGridDivNum);
	}

	private void makeGridSizeHalf() {
		if (paintContext.getGridDivNum() < 65) {
			paintContext.setGridDivNum(paintContext.getGridDivNum() * 2);
			view.setGridDivNum(paintContext.getGridDivNum());

			screenUpdater.updateScreen();
		}
	}

	private void makeGridSizeTwiceLarge() {
		if (paintContext.getGridDivNum() > 3) {
			paintContext.setGridDivNum(paintContext.getGridDivNum() / 2);
			view.setGridDivNum(paintContext.getGridDivNum());

			screenUpdater.updateScreen();
		}
	}

	private void updateGridDivNum(final int gridDivNum) {
		paintContext.setGridDivNum(gridDivNum);

		screenUpdater.updateScreen();
	}

}
