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

import java.util.List;
import java.util.function.Consumer;

import oripa.domain.creasepattern.CreasePattern;
import oripa.domain.fold.EstimationResultRules;
import oripa.domain.fold.FoldedModel;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.gui.presenter.estimation.EstimationResultFramePresenter;
import oripa.gui.presenter.estimation.EstimationResultFramePresenterFactory;
import oripa.gui.presenter.foldability.FoldabilityCheckFramePresenter;
import oripa.gui.presenter.foldability.FoldabilityCheckFramePresenterFactory;
import oripa.gui.presenter.model.ModelViewFramePresenter;
import oripa.gui.presenter.model.ModelViewFramePresenterFactory;
import oripa.gui.view.estimation.EstimationResultFrameView;
import oripa.gui.view.foldability.FoldabilityCheckFrameView;
import oripa.gui.view.model.ModelViewFrameView;

/**
 * @author OUCHI Koji
 *
 */
public class SubFramePresenterFactory {
	private final FoldabilityCheckFramePresenterFactory foldabilityCheckFramePresenterFactory;
	private final ModelViewFramePresenterFactory modelViewFramePresenterFactory;

	private final EstimationResultFramePresenterFactory estimationResultFramePresenterFactory;

	public SubFramePresenterFactory(
			final FoldabilityCheckFramePresenterFactory foldabilityCheckFramePresenterFactory,
			final ModelViewFramePresenterFactory modelViewFramePresenterFactory,
			final EstimationResultFramePresenterFactory estimationResultFramePresenterFactory) {

		this.foldabilityCheckFramePresenterFactory = foldabilityCheckFramePresenterFactory;
		this.modelViewFramePresenterFactory = modelViewFramePresenterFactory;
		this.estimationResultFramePresenterFactory = estimationResultFramePresenterFactory;
	}

	public ModelViewFramePresenter createModelViewFramePresenter(
			final ModelViewFrameView view,
			final List<OrigamiModel> origamiModels,
			final double eps) {
		return modelViewFramePresenterFactory.create(
				view,
				origamiModels,
				eps);
	}

	public EstimationResultFramePresenter createEstimationResultFramePresenter(
			final EstimationResultFrameView view,
			final List<FoldedModel> foldedModels,
			final double eps,
			final String lastFilePath,
			final Consumer<String> lastFilePathChangeListener) {

		return estimationResultFramePresenterFactory.create(
				view,
				foldedModels,
				eps,
				lastFilePath,
				lastFilePathChangeListener);
	}

	public FoldabilityCheckFramePresenter createFoldabilityCheckFrameViewPresenter(
			final FoldabilityCheckFrameView view,
			final CreasePattern creasePattern,
			final double pointEps) {

		return foldabilityCheckFramePresenterFactory.create(
				view,
				creasePattern,
				pointEps);

	}

	public FoldabilityCheckFramePresenter createFoldabilityCheckFrameViewPresenter(
			final FoldabilityCheckFrameView view,
			final CreasePattern creasePattern,
			final OrigamiModel origamiModel,
			final EstimationResultRules estimationRules,
			final double pointEps) {

		return foldabilityCheckFramePresenterFactory.create(
				view,
				creasePattern,
				origamiModel,
				estimationRules,
				pointEps);
	}

}
