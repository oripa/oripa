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
import oripa.domain.fold.EstimationResultRules;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.domain.fold.halfedge.OrigamiModelFactory;
import oripa.gui.presenter.foldability.FoldabilityCheckFramePresenter;
import oripa.gui.view.FrameView;
import oripa.gui.view.foldability.FoldabilityCheckFrameView;
import oripa.gui.view.main.SubFrameFactory;

/**
 * @author OUCHI Koji
 *
 */
public class CheckerWindowOpener {
	private final FrameView ownerView;
	private final SubFrameFactory frameFactory;

	public CheckerWindowOpener(
			final FrameView ownerView,
			final SubFrameFactory frameFactory) {
		this.ownerView = ownerView;
		this.frameFactory = frameFactory;
	}

	/**
	 * displays window with foldability checks.
	 *
	 * @param creasePattern
	 *            a crease pattern to be checked
	 * @param estimationRules
	 *            null if origami model check only.
	 * @param isZeroLineWidth
	 *            whether drawing lines thinner or not.
	 */
	public void showCheckerWindow(final CreasePattern creasePattern, final boolean isZeroLineWidth,
			final double pointEps) {
		OrigamiModel origamiModel;

		OrigamiModelFactory modelFactory = new OrigamiModelFactory();
		origamiModel = modelFactory.createOrigamiModel(
				creasePattern,
				pointEps);

		showCheckerWindow(creasePattern, origamiModel, new EstimationResultRules(), isZeroLineWidth, pointEps);
	}

	public void showCheckerWindow(final CreasePattern creasePattern, final OrigamiModel origamiModel,
			final EstimationResultRules estimationRules,
			final boolean isZeroLineWidth,
			final double pointEps) {

		FoldabilityCheckFrameView checker = frameFactory.createFoldabilityFrame(ownerView);

		var foldabilityPresenter = new FoldabilityCheckFramePresenter(
				checker,
				origamiModel,
				estimationRules,
				creasePattern,
				isZeroLineWidth,
				pointEps);

		foldabilityPresenter.setViewVisible(true);

	}
}
