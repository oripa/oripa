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
package oripa.gui.presenter.foldability;

import oripa.domain.creasepattern.CreasePattern;
import oripa.domain.fold.EstimationResultRules;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.domain.fold.halfedge.OrigamiModelFactory;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.view.foldability.FoldabilityCheckFrameView;

/**
 * @author OUCHI Koji
 *
 */
public class FoldabilityCheckFramePresenterFactory {

	private final CreasePatternViewContext creasePatternViewContext;
	private final OrigamiModelFactory modelFactory;

	public FoldabilityCheckFramePresenterFactory(
			final CreasePatternViewContext creasePatternViewContext,
			final OrigamiModelFactory modelFactory

	) {
		this.creasePatternViewContext = creasePatternViewContext;
		this.modelFactory = modelFactory;
	}

	public FoldabilityCheckFramePresenter create(
			final FoldabilityCheckFrameView view,
			final CreasePattern creasePattern,
			final double pointEps) {

		OrigamiModel origamiModel;

		origamiModel = modelFactory.createOrigamiModel(
				creasePattern,
				pointEps);

		return create(
				view,
				creasePattern,
				origamiModel,
				new EstimationResultRules(),
				pointEps);

	}

	public FoldabilityCheckFramePresenter create(
			final FoldabilityCheckFrameView view,
			final CreasePattern creasePattern,
			final OrigamiModel origamiModel,
			final EstimationResultRules estimationRules,
			final double pointEps) {

		return new FoldabilityCheckFramePresenter(
				view,
				origamiModel,
				estimationRules,
				creasePattern,
				creasePatternViewContext.isZeroLineWidth(),
				pointEps);
	}
}
