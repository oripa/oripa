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

import oripa.domain.paint.ArrayCopyParameter;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.arraycopy.ArrayCopyCommand;
import oripa.gui.view.ViewScreenUpdater;
import oripa.gui.view.main.ArrayCopyDialogView;
import oripa.util.Command;

/**
 * @author OUCHI Koji
 *
 */
public class ArrayCopyDialogPresenter {

	private final ArrayCopyDialogView view;
	private final PaintContext paintContext;
	private final ViewScreenUpdater screenUpdater;

	public ArrayCopyDialogPresenter(final ArrayCopyDialogView view,
			final PaintContext paintContext,
			final ViewScreenUpdater screenUpdater) {

		this.view = view;
		this.paintContext = paintContext;
		this.screenUpdater = screenUpdater;

		setParameterToView();

		view.setOKButtonListener(this::doArrayCopy);
	}

	private void setParameterToView() {
		var parameter = paintContext.getArrayCopyParameter();

		if (parameter == null) {
			return;
		}

		view.setFillUp(parameter.fillUp());
		view.setRowCount(parameter.rowCount());
		view.setColumnCount(parameter.columnCount());
		view.setIntervalX(parameter.intervalX());
		view.setIntervalY(parameter.intervalY());
	}

	private boolean doArrayCopy() {

		boolean shouldFillUp = view.shouldFillUp();
		int rowCount = view.getRowCount();
		int columnCount = view.getColumnCount();
		double intervalX = view.getIntervalX();
		double intervalY = view.getIntervalY();

		if (!shouldFillUp && (rowCount == 0 || columnCount == 0)) {
			view.showWrongInputMessage();
			return false;
		}

		paintContext.setArrayCopyParameter(
				new ArrayCopyParameter(shouldFillUp, rowCount, columnCount, intervalX, intervalY));

		Command command = new ArrayCopyCommand(paintContext);
		command.execute();

		screenUpdater.updateScreen();

		return true;
	}

	public void setViewVisible(final boolean visible) {
		view.setVisible(visible);
	}
}
