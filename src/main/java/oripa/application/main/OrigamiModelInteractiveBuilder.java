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
package oripa.application.main;

import java.util.List;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.creasepattern.CreasePattern;
import oripa.domain.fold.foldability.FoldabilityChecker;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.domain.fold.halfedge.OrigamiModelFactory;
import oripa.geom.GeomUtil;

/**
 * Creates {@link OrigamiModel} according to the interaction with the user.
 *
 * @author OUCHI Koji
 *
 */
public class OrigamiModelInteractiveBuilder {
	private static final Logger logger = LoggerFactory
			.getLogger(OrigamiModelInteractiveBuilder.class);

	/**
	 * Creates {@link OrigamiModel} from {@code creasePattern}.
	 *
	 * @param creasePattern
	 * @param needCleaningUpDuplication
	 *            a function that returns {@code true} if cleaning up line
	 *            duplication is needed.
	 * @param showCleaningUpMessage
	 *            an action that tells cleaning is done.
	 * @param showFailureMessage
	 *            an action that tells folding failed.
	 * @return origami model data.
	 */
	public List<OrigamiModel> build(final CreasePattern creasePattern,
			final Supplier<Boolean> needCleaningUpDuplication,
			final Runnable showCleaningUpMessage,
			final Runnable showFailureMessage) {
		var eps = GeomUtil.eps(creasePattern.getPaperSize());

		OrigamiModelFactory modelFactory = new OrigamiModelFactory();
		OrigamiModel wholeModel = modelFactory.createOrigamiModel(
				creasePattern, eps);

		List<OrigamiModel> origamiModels = modelFactory.createOrigamiModels(
				creasePattern, eps);

		var checker = new FoldabilityChecker();

		logger.debug("Building origami model.");

		if (checker.testLocalFlatFoldability(wholeModel)) {
			logger.debug("No modification is needed.");
			return origamiModels;
		}

		// ask if ORIPA should try to remove duplication.
		if (!needCleaningUpDuplication.get()) {
			// the answer is "no."
			return origamiModels;
		}

		// clean up the crease pattern
		if (creasePattern.cleanDuplicatedLines()) {
			showCleaningUpMessage.run();
		}

		// re-create the model data for simplified crease pattern
		wholeModel = modelFactory
				.createOrigamiModel(creasePattern, eps);

		if (checker.testLocalFlatFoldability(wholeModel)) {
			return modelFactory.createOrigamiModels(creasePattern, eps);
		}

		showFailureMessage.run();

		return origamiModels;

	}
}
