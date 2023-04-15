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

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.creasepattern.CreasePattern;
import oripa.domain.fold.FoldedModel;
import oripa.domain.fold.Folder;
import oripa.domain.fold.FolderFactory;
import oripa.domain.fold.TestedOrigamiModelFactory;
import oripa.domain.fold.UnassignedModelFolder;
import oripa.domain.fold.UnassignedModelFolderFactory;
import oripa.domain.fold.halfedge.OrigamiModel;

/**
 * @author OUCHI Koji
 *
 */
public class ModelComputationFacade {
	private static final Logger logger = LoggerFactory.getLogger(ModelComputationFacade.class);

	public class ComputationResult {
		private final List<OrigamiModel> origamiModels;
		private final List<FoldedModel> foldedModels;

		public ComputationResult(final List<OrigamiModel> origamiModels, final List<FoldedModel> foldedModels) {
			this.origamiModels = origamiModels;
			this.foldedModels = foldedModels;
		}

		public List<OrigamiModel> getOrigamiModels() {
			return Collections.unmodifiableList(origamiModels);
		}

		public List<FoldedModel> getFoldedModels() {
			if (foldedModels == null) {
				return List.of();
			}
			return Collections.unmodifiableList(foldedModels);
		}

		/**
		 *
		 * @return the sum of the number of the foldable patterns for each
		 *         crease pattern. -1 if some model is not foldable.
		 */
		public int countFoldablePatterns() {
			if (!allLocallyFlatFoldable()) {
				return -1;
			}
			return foldedModels.stream().mapToInt(m -> m.getFoldablePatternCount()).sum();
		}

		public boolean allLocallyFlatFoldable() {
			return origamiModels.stream().allMatch(OrigamiModel::isLocallyFlatFoldable);
		}
	}

	private final TestedOrigamiModelFactory modelFactory = new TestedOrigamiModelFactory();

	private final Supplier<Boolean> needCleaningUpDuplication;
	private final Runnable showCleaningUpMessage;
	private final Runnable showFailureMessage;

	public ModelComputationFacade(
			final Supplier<Boolean> needCleaningUpDuplication,
			final Runnable showCleaningUpMessage,
			final Runnable showFailureMessage) {
		this.needCleaningUpDuplication = needCleaningUpDuplication;
		this.showCleaningUpMessage = showCleaningUpMessage;
		this.showFailureMessage = showFailureMessage;
	}

	public ComputationResult computeModels(
			final List<OrigamiModel> origamiModels,
			final boolean fullEstimation) {

		var folderFactory = new FolderFactory();
		Folder folder = folderFactory.create();

		UnassignedModelFolder unassignedFolder = new UnassignedModelFolderFactory().create();

		var foldedModels = origamiModels.stream()
				.map(model -> model.isLocallyFlatFoldable()
						? (model.isUnassigned() ? unassignedFolder.fold(model) : folder.fold(model, fullEstimation))
						: folder.foldWithoutLineType(model))
				.collect(Collectors.toList());

		return new ComputationResult(origamiModels, foldedModels);
	}

	/**
	 * try building the crease pattern and ask for additional measures to help
	 * clean it
	 *
	 * @return Origami model before folding
	 */
	public List<OrigamiModel> buildOrigamiModels(final CreasePattern creasePattern, final double pointEps) {

		OrigamiModel wholeModel = modelFactory.createOrigamiModel(
				creasePattern, pointEps);

		logger.debug("Building origami model.");

		if (wholeModel.isLocallyFlatFoldable()) {
			logger.debug("No modification is needed.");
			return modelFactory.createOrigamiModels(creasePattern, pointEps);
		}

		// ask if ORIPA should try to remove duplication.
		if (!needCleaningUpDuplication.get()) {
			// the answer is "no."
			return modelFactory.createOrigamiModels(creasePattern, pointEps);
		}

		// clean up the crease pattern
		if (creasePattern.cleanDuplicatedLines(pointEps)) {
			showCleaningUpMessage.run();
		}
		// re-create the model data for simplified crease pattern
		wholeModel = modelFactory
				.createOrigamiModel(creasePattern, pointEps);

		if (wholeModel.isLocallyFlatFoldable()) {
			return modelFactory.createOrigamiModels(creasePattern, pointEps);
		}

		showFailureMessage.run();

		return modelFactory.createOrigamiModels(creasePattern, pointEps);
	}
}
