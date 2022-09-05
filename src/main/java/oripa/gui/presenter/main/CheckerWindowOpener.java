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

import oripa.domain.creasepattern.CreasePattern;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.domain.fold.halfedge.OrigamiModelFactory;
import oripa.gui.view.FrameView;
import oripa.gui.view.View;
import oripa.gui.view.foldability.FoldabilityCheckFrameFactory;
import oripa.gui.view.util.ChildFrameManager;

/**
 * @author OUCHI Koji
 *
 */
public class CheckerWindowOpener {
	private final FrameView ownerView;
	private final ChildFrameManager childFrameManager;

	public CheckerWindowOpener(
			final FrameView ownerView,
			final ChildFrameManager childFrameManager) {
		this.ownerView = ownerView;
		this.childFrameManager = childFrameManager;
	}

	/**
	 * displays window with foldability checks.
	 *
	 * @param creasePattern
	 *            a crease pattern to be checked
	 * @param isZeroLineWidth
	 *            whether drawing lines thinner or not.
	 */
	public void showCheckerWindow(final CreasePattern creasePattern, final boolean isZeroLineWidth) {
		OrigamiModel origamiModel;

		OrigamiModelFactory modelFactory = new OrigamiModelFactory();
		origamiModel = modelFactory.createOrigamiModel(
				creasePattern);

		FoldabilityCheckFrameFactory checkerFactory = new FoldabilityCheckFrameFactory(
				childFrameManager);
		View checker = checkerFactory.createFrame(
				ownerView, origamiModel, creasePattern, isZeroLineWidth);
		checker.repaint();
		checker.setVisible(true);
	}
}
