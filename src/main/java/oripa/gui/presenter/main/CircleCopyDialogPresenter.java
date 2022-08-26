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

import oripa.domain.paint.CircleCopyParameter;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.circlecopy.CircleCopyCommand;
import oripa.gui.presenter.creasepattern.ScreenUpdater;
import oripa.gui.view.main.CircleCopyDialogView;
import oripa.util.Command;

/**
 * @author OUCHI Koji
 *
 */
public class CircleCopyDialogPresenter {

	private final CircleCopyDialogView view;
	private final PaintContext paintContext;
	private final ScreenUpdater screenUpdater;

	public CircleCopyDialogPresenter(
			final CircleCopyDialogView view,
			final PaintContext paintContext,
			final ScreenUpdater screenUpdater) {

		this.view = view;
		this.paintContext = paintContext;
		this.screenUpdater = screenUpdater;

		setParameterToView();

		view.setOKButtonListener(this::doCircleCopy);
	}

	private void setParameterToView() {
		var parameter = paintContext.getCircleCopyParameter();

		if (parameter == null) {
			return;
		}

		view.setCenterX(parameter.getCenterX());
		view.setCenterY(parameter.getCenterY());
		view.setAngleDegree(parameter.getAngleDegree());
		view.setCopyCount(parameter.getCopyCount());

	}

	private boolean doCircleCopy() {
		var parameter = new CircleCopyParameter(
				view.getCenterX(), view.getCenterY(), view.getAngleDegree(), view.getCopyCount());

		if (parameter.getCopyCount() <= 0) {
			view.showWrongCopyCountMessage();
			return false;
		}

		paintContext.setCircleCopyParameter(parameter);

		Command command = new CircleCopyCommand(paintContext);
		command.execute();

		screenUpdater.updateScreen();

		return true;
	}

	public void setViewVisible(final boolean visible) {
		view.setViewVisible(visible);
	}
}
