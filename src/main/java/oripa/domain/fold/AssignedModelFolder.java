/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2005-2009 Jun Mitani http://mitani.cs.tsukuba.ac.jp/

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

package oripa.domain.fold;

import java.util.List;

import oripa.domain.fold.halfedge.OrigamiModel;

class AssignedModelFolder implements Folder {
	// helper object
	private final FaceDisplayModifier faceDisplayModifier = new FaceDisplayModifier();

	private final SimpleFolder simpleFolder;

	private final LayerOrderEnumerator enumerator;

	public AssignedModelFolder(final SimpleFolder simpleFolder,
			final LayerOrderEnumerator enumerator) {
		this.simpleFolder = simpleFolder;
		this.enumerator = enumerator;
	}

	@Override
	public FoldedModel fold(final OrigamiModel origamiModel, final double eps, final EstimationType estimationType) {
		simpleFolder.simpleFoldWithoutZorder(origamiModel);
		faceDisplayModifier.setCurrentPositionsToDisplayPositions(origamiModel);

		if (estimationType == EstimationType.X_RAY) {
			origamiModel.setFolded(true);
			return new FoldedModel(origamiModel, List.of(), List.of());
		}

		var enumerationResult = enumerator.enumerate(origamiModel, eps, estimationType == EstimationType.FIRST_ONLY);

		var foldedModel = new FoldedModel(origamiModel, enumerationResult.getOverlapRelations(),
				enumerationResult.getSubfaces());

		if (enumerationResult.isEmpty()) {
			return foldedModel;
		}

		origamiModel.setFolded(true);
		return foldedModel;
	}
}
