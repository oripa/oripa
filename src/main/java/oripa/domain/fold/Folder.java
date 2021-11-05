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

import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OrigamiModel;

public class Folder {
	// helper object
	private final FolderTool folderTool = new FolderTool();

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
		List<OriFace> faces = origamiModel.getFaces();

		simpleFolder.simpleFoldWithoutZorder(origamiModel);
		folderTool.setFacesOutline(faces);

		if (!fullEstimation) {
			origamiModel.setFolded(true);
			return new FoldedModel(origamiModel, new OverlapRelationList());
		}

		var overlapRelationList = enumerator.enumerate(origamiModel);

		var foldedModel = new FoldedModel(origamiModel, overlapRelationList);

		overlapRelationList.setCurrentIndex(0);
		if (overlapRelationList.isEmpty()) {
			return foldedModel;
		}

//		folderTool.setFacesOutline(faces);

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

		List<OriFace> faces = model.getFaces();

		folderTool.setFacesOutline(faces);
	}
}
