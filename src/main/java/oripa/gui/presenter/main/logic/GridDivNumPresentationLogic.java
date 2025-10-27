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
import oripa.domain.paint.PaintContext;
import oripa.gui.view.ViewScreenUpdater;
import oripa.gui.view.main.UIPanelView;

/**
 * @author OUCHI Koji
 *
 */
public class GridDivNumPresentationLogic {
	private final UIPanelView view;
	private final ViewScreenUpdater screenUpdater;

	private final PaintContext paintContext;

	@Inject
	public GridDivNumPresentationLogic(
			final UIPanelView view,
			final ViewScreenUpdater screenUpdater,
			final PaintContext paintContext) {

		this.view = view;
		this.screenUpdater = screenUpdater;
		this.paintContext = paintContext;
	}

	public void makeGridSizeHalf() {
		setGridDivNumIfValid(paintContext.getGridDivNum() * 2);
	}

	public void makeGridSizeTwiceLarge() {
		setGridDivNumIfValid(paintContext.getGridDivNum() / 2);
	}

	public void updateGridDivNum(final int gridDivNum) {
		setGridDivNumIfValid(gridDivNum);
	}

	private void setGridDivNumIfValid(final int gridDivNum) {
		if (!isValidGridDivNum(gridDivNum)) {
			return;
		}
		paintContext.setGridDivNum(gridDivNum);
		view.setGridDivNum(gridDivNum);

		screenUpdater.updateScreen();
	}

	private boolean isValidGridDivNum(final int gridDivNum) {
		return gridDivNum >= 2 && gridDivNum <= 256;
	}

	public void setTriangularGridMode(boolean enabled) {
		paintContext.setTriangularGridMode(enabled);
		screenUpdater.updateScreen();
	}
}
