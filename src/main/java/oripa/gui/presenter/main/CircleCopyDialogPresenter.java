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

		view.setOKButtonListener(this::doCircleCopy);
	}

	private boolean doCircleCopy() {
		var copyCount = view.getCopyCount();

		if (copyCount <= 0) {
			view.showWrongCopyCountMessage();
			return false;
		}

		double centerX = view.getCenterX();
		double centerY = view.getCenterY();
		double angleDegree = view.getAngleDegree();

		Command command = new CircleCopyCommand(
				centerX, centerY, angleDegree, copyCount, paintContext);
		command.execute();

		screenUpdater.updateScreen();

		return true;
	}

	public void setViewVisible(final boolean visible) {
		view.setViewVisible(visible);
	}
}
