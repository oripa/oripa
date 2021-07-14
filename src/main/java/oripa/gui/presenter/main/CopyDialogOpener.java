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

import javax.swing.JFrame;

import oripa.domain.cptool.Painter;
import oripa.domain.paint.PaintContext;
import oripa.gui.view.main.CircleCopyDialogFactory;
import oripa.gui.view.main.RepeatCopyDialogFactory;

/**
 * @author OUCHI Koji
 *
 */
public class CopyDialogOpener {
	private final Runnable showNoSelectionMessageForArrayCopy;
	private final Runnable showNoSelectionMessageForCircleCopy;

	private final RepeatCopyDialogFactory arrayCopyDialogFactory;
	private final CircleCopyDialogFactory circleCopyDialogFactory;

	public CopyDialogOpener(
			final RepeatCopyDialogFactory arrayCopyDialogFactory,
			final CircleCopyDialogFactory circleCopyDialogFactory,
			final Runnable showNoSelectionMessageForArrayCopy,
			final Runnable showNoSelectionMessageForCircleCopy) {
		this.arrayCopyDialogFactory = arrayCopyDialogFactory;
		this.circleCopyDialogFactory = circleCopyDialogFactory;

		this.showNoSelectionMessageForArrayCopy = showNoSelectionMessageForArrayCopy;
		this.showNoSelectionMessageForCircleCopy = showNoSelectionMessageForCircleCopy;
	}

	public void showArrayCopyDialog(final JFrame ownerView, final PaintContext paintContext) {
		Painter painter = paintContext.getPainter();
		if (painter.countSelectedLines() == 0) {
			showNoSelectionMessageForArrayCopy.run();
			return;
		}

		var arrayCopyDialog = arrayCopyDialogFactory.create(ownerView, paintContext);
		arrayCopyDialog.setVisible(true);
	}

	public void showCircleCopyDialog(final JFrame ownerView, final PaintContext paintContext) {
		Painter painter = paintContext.getPainter();
		if (painter.countSelectedLines() == 0) {
			showNoSelectionMessageForCircleCopy.run();
			return;
		}

		var circleCopyDialog = circleCopyDialogFactory.create(ownerView, paintContext);
		circleCopyDialog.setVisible(true);
	}

}
