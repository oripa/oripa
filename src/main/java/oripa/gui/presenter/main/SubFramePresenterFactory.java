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

import java.util.List;
import java.util.function.Consumer;

import oripa.application.FileAccessService;
import oripa.application.estimation.FoldedModelFileAccessServiceFactory;
import oripa.domain.creasepattern.CreasePattern;
import oripa.domain.cutmodel.CutModelOutlinesHolder;
import oripa.domain.fold.EstimationResultRules;
import oripa.domain.fold.FoldedModel;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.domain.fold.halfedge.OrigamiModelFactory;
import oripa.gui.presenter.estimation.EstimationResultFramePresenter;
import oripa.gui.presenter.estimation.FoldedModelFileSelectionPresenterFactory;
import oripa.gui.presenter.foldability.FoldabilityCheckFramePresenter;
import oripa.gui.presenter.model.ModelViewFramePresenter;
import oripa.gui.view.estimation.EstimationResultFrameView;
import oripa.gui.view.file.FileChooserFactory;
import oripa.gui.view.foldability.FoldabilityCheckFrameView;
import oripa.gui.view.main.PainterScreenSetting;
import oripa.gui.view.model.ModelViewFrameView;
import oripa.gui.view.util.CallbackOnUpdate;
import oripa.util.file.FileFactory;

/**
 * @author OUCHI Koji
 *
 */
public class SubFramePresenterFactory {
	private final FileChooserFactory fileChooserFactory;

	final FoldedModelFileSelectionPresenterFactory foldedModelFileSelectionPresenterFactory;

	private final PainterScreenSetting mainScreenSetting;
	private final CutModelOutlinesHolder cutModelOutlinesHolder;
	private final FileAccessService<OrigamiModel> origamiModelFileAccessService;
	private final FoldedModelFileAccessServiceFactory foldedModelFileAccessFactory;
	private final FileFactory fileFactory;

	public SubFramePresenterFactory(
			final FileChooserFactory fileChooserFactory,
			final FoldedModelFileSelectionPresenterFactory foldedModelFileSelectionPresenterFactory,
			final PainterScreenSetting mainScreenSetting,
			final CutModelOutlinesHolder cutModelOutlinesHolder,
			final FileAccessService<OrigamiModel> origamiModelFileAccessService,
			final FoldedModelFileAccessServiceFactory foldedModelFileAccessFactory,
			final FileFactory fileFactory) {
		this.fileChooserFactory = fileChooserFactory;
		this.foldedModelFileSelectionPresenterFactory = foldedModelFileSelectionPresenterFactory;
		this.mainScreenSetting = mainScreenSetting;
		this.cutModelOutlinesHolder = cutModelOutlinesHolder;
		this.origamiModelFileAccessService = origamiModelFileAccessService;
		this.foldedModelFileAccessFactory = foldedModelFileAccessFactory;
		this.fileFactory = fileFactory;
	}

	public ModelViewFramePresenter createModelViewFramePresenter(
			final ModelViewFrameView view,
			final List<OrigamiModel> origamiModels,
			final CallbackOnUpdate onUpdateScissorsLine,
			final double eps) {
		return new ModelViewFramePresenter(
				view,
				fileChooserFactory,
				mainScreenSetting,
				origamiModels,
				cutModelOutlinesHolder,
				onUpdateScissorsLine,
				origamiModelFileAccessService,
				fileFactory,
				eps);
	}

	public EstimationResultFramePresenter createEstimationResultFramePresenter(
			final EstimationResultFrameView view,
			final List<FoldedModel> foldedModels,
			final double eps,
			final String lastFilePath,
			final Consumer<String> lastFilePathChangeListener) {

		return new EstimationResultFramePresenter(
				view,
				fileChooserFactory,
				foldedModelFileSelectionPresenterFactory,
				foldedModelFileAccessFactory,
				fileFactory,
				foldedModels,
				eps,
				lastFilePath,
				lastFilePathChangeListener);
	}

	public FoldabilityCheckFramePresenter createFoldabilityCheckFrameView(
			final FoldabilityCheckFrameView view,
			final CreasePattern creasePattern, final boolean isZeroLineWidth,
			final double pointEps) {

		OrigamiModel origamiModel;

		OrigamiModelFactory modelFactory = new OrigamiModelFactory();
		origamiModel = modelFactory.createOrigamiModel(
				creasePattern,
				pointEps);

		return createFoldabilityCheckFrameView(view, creasePattern, origamiModel, new EstimationResultRules(),
				isZeroLineWidth,
				pointEps);

	}

	public FoldabilityCheckFramePresenter createFoldabilityCheckFrameView(
			final FoldabilityCheckFrameView view,
			final CreasePattern creasePattern, final OrigamiModel origamiModel,
			final EstimationResultRules estimationRules,
			final boolean isZeroLineWidth,
			final double pointEps) {

		return new FoldabilityCheckFramePresenter(
				view,
				origamiModel,
				estimationRules,
				creasePattern,
				isZeroLineWidth,
				pointEps);
	}

}
