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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.creasepattern.CreasePattern;
import oripa.domain.fold.FoldedModel;
import oripa.domain.fold.Folder.EstimationType;
import oripa.domain.fold.FolderFactory;
import oripa.domain.fold.TestedOrigamiModelFactory;
import oripa.domain.fold.halfedge.OrigamiModel;

/**
 * @author OUCHI Koji
 *
 */
public class ModelComputationFacade {
	private static final Logger logger = LoggerFactory.getLogger(ModelComputationFacade.class);

	public enum ComputationType {
		FULL(EstimationType.FULL, "All folded states", true),
		FIRST_ONLY(EstimationType.FIRST_ONLY, "First folded state", true),
		X_RAY(EstimationType.X_RAY, "X-Ray", false);

		private final EstimationType estimationType;
		private final String name;
		private final boolean isLayerOrdering;

		private ComputationType(final EstimationType estimationType, final String name, final boolean isLayerOrdering) {
			this.estimationType = estimationType;
			this.name = name;
			this.isLayerOrdering = isLayerOrdering;
		}

		public boolean isLayerOrdering() {
			return isLayerOrdering;
		}

		EstimationType toEstimationType() {
			return estimationType;
		}

		@Override
		public String toString() {
			return name;
		}

		public static Optional<ComputationType> fromString(final String s) {
			return Arrays.stream(values()).filter(v -> v.toString().equals(s)).findFirst();
		}
	}

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

	private final double eps;

	public ModelComputationFacade(
			final Supplier<Boolean> needCleaningUpDuplication,
			final Runnable showCleaningUpMessage,
			final Runnable showFailureMessage,
			final double eps) {
		this.needCleaningUpDuplication = needCleaningUpDuplication;
		this.showCleaningUpMessage = showCleaningUpMessage;
		this.showFailureMessage = showFailureMessage;
		this.eps = eps;
	}

	public ComputationResult computeModels(
			final List<OrigamiModel> origamiModels,
			final ComputationType type) {

		var folderFactory = new FolderFactory();

		var foldedModels = origamiModels.stream()
				.map(model -> folderFactory.create(model.getModelType()).fold(model, eps, type.toEstimationType()))
				.toList();

		return new ComputationResult(origamiModels, foldedModels);
	}

	/**
	 * try building the crease pattern and ask for additional measures to help
	 * clean it
	 *
	 * @return Origami model before folding
	 */
	public List<OrigamiModel> buildOrigamiModels(final CreasePattern creasePattern) {

		OrigamiModel wholeModel = modelFactory.createOrigamiModel(
				creasePattern, eps);

		logger.debug("Building origami model.");

		if (wholeModel.isLocallyFlatFoldable()) {
			logger.debug("No modification is needed.");
			return modelFactory.createOrigamiModels(creasePattern, eps);
		}

		// ask if ORIPA should try to remove duplication.
		if (!needCleaningUpDuplication.get()) {
			// the answer is "no."
			return modelFactory.createOrigamiModels(creasePattern, eps);
		}

		// clean up the crease pattern
		if (creasePattern.cleanDuplicatedLines(eps)) {
			showCleaningUpMessage.run();
		}
		// re-create the model data for simplified crease pattern
		wholeModel = modelFactory
				.createOrigamiModel(creasePattern, eps);

		if (wholeModel.isLocallyFlatFoldable()) {
			return modelFactory.createOrigamiModels(creasePattern, eps);
		}

		showFailureMessage.run();

		return modelFactory.createOrigamiModels(creasePattern, eps);
	}
}
