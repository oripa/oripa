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

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.domain.fold.OrigamiModel;
import oripa.domain.fold.OrigamiModelFactory;

/**
 * @author OUCHI Koji
 *
 */
public class OrigamiModelInteractiveBuilder {
	private static final Logger logger = LoggerFactory
			.getLogger(OrigamiModelInteractiveBuilder.class);

	public OrigamiModel build(final CreasePatternInterface creasePattern,
			final Supplier<Boolean> cleaningUpDuplicationNeeded,
			final Runnable showCleaningUpMessage,
			final Runnable showFailureMessage) {
		OrigamiModelFactory modelFactory = new OrigamiModelFactory();
		OrigamiModel origamiModel = modelFactory.createOrigamiModel(
				creasePattern, creasePattern.getPaperSize());

		logger.debug("Building origami model.");

		if (origamiModel.isProbablyFoldable()) {
			logger.debug("No modification is needed.");
			return origamiModel;
		}

		// ask if ORIPA should try to remove duplication.
		if (!cleaningUpDuplicationNeeded.get()) {
			// the answer is "no."
			return origamiModel;
		}

		// clean up the crease pattern
		if (creasePattern.cleanDuplicatedLines()) {
			showCleaningUpMessage.run();
		}
		// re-create the model data for simplified crease pattern
		origamiModel = modelFactory
				.createOrigamiModel(
						creasePattern, creasePattern.getPaperSize());

		if (origamiModel.isProbablyFoldable()) {
			return origamiModel;
		}

		showFailureMessage.run();

		return origamiModel;

	}
}
