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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.fold.halfedge.OrigamiModel;

public class Folder {
	private static Logger logger = LoggerFactory.getLogger(Folder.class);
	// helper object
	private final FaceDisplayModifier faceDisplayModifier = new FaceDisplayModifier();

	private final SimpleFolder simpleFolder;

	private final LayerOrderEnumerator enumerator;

	public Folder(final SimpleFolder simpleFolder,
			final LayerOrderEnumerator enumerator) {
		this.simpleFolder = simpleFolder;
		this.enumerator = enumerator;
	}

	/**
	 * Computes folded states.
	 *
	 * @param origamiModel
	 *            half-edge based data structure. It will be affected by this
	 *            method.
	 * @param fullEstimation
	 *            whether the algorithm should compute all possible folded
	 *            states or not.
	 * @return folded model whose {@link FoldedModel#getOrigamiModel()} returns
	 *         the given {@code origamiModel}.
	 */
	public FoldedModel fold(final OrigamiModel origamiModel, final boolean fullEstimation) {
		simpleFolder.simpleFoldWithoutZorder(origamiModel);
		faceDisplayModifier.setCurrentPositionsToDisplayPositions(origamiModel);

		if (!fullEstimation) {
			origamiModel.setFolded(true);
			return new FoldedModel(origamiModel, new OverlapRelationList());
		}

		OverlapRelationList overlapRelationList = new OverlapRelationList();
		try {
			overlapRelationList = enumerator.enumerate(origamiModel);
		} catch (Exception e) {
			logger.error("error on layer ordering", e);
		}
		var foldedModel = new FoldedModel(origamiModel, overlapRelationList);

		if (overlapRelationList.isEmpty()) {
			return foldedModel;
		}

		origamiModel.setFolded(true);
		return foldedModel;
	}

	/**
	 * Computes position of each face after fold.
	 *
	 * @param model
	 *            half-edge based data structure. It will be affected by this
	 *            method.
	 */
	public void foldWithoutLineType(
			final OrigamiModel model) {
		simpleFolder.foldWithoutLineType(model);
		faceDisplayModifier.setCurrentPositionsToDisplayPositions(model);
	}
}
