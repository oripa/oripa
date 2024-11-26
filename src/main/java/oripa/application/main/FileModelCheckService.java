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

import java.io.IOException;
import java.util.function.Supplier;

import jakarta.inject.Inject;
import oripa.domain.fold.TestedOrigamiModelFactory;
import oripa.domain.paint.PaintContext;

/**
 * @author OUCHI Koji
 *
 */
public class FileModelCheckService {

	private final PaintContext paintContext;
	private final TestedOrigamiModelFactory modelFactory;

	@Inject
	public FileModelCheckService(
			final PaintContext paintContext,
			final TestedOrigamiModelFactory modelFactory) {
		this.paintContext = paintContext;
		this.modelFactory = modelFactory;
	}

	public boolean checkFoldability(
			final Supplier<Boolean> acceptModelError)
			throws IOException {
		var creasePattern = paintContext.getCreasePattern();
		double pointEps = paintContext.getPointEps();

		var origamiModel = modelFactory.createOrigamiModel(
				creasePattern, pointEps);

		if (!origamiModel.isLocallyFlatFoldable()) {
			return acceptModelError.get();
		}

		return true;
	}

}
