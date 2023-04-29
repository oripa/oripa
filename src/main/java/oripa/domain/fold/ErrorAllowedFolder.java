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
package oripa.domain.fold;

import java.util.List;

import oripa.domain.fold.halfedge.OrigamiModel;

/**
 * @author OUCHI Koji
 *
 */
class ErrorAllowedFolder implements Folder {

	// helper object
	private final FaceDisplayModifier faceDisplayModifier = new FaceDisplayModifier();

	private final SimpleFolder simpleFolder;

	public ErrorAllowedFolder(final SimpleFolder simpleFolder) {
		this.simpleFolder = simpleFolder;
	}

	@Override
	public FoldedModel fold(final OrigamiModel origamiModel, final boolean fullEstimation) {
		simpleFolder.foldWithoutLineType(origamiModel);
		faceDisplayModifier.setCurrentPositionsToDisplayPositions(origamiModel);

		return new FoldedModel(origamiModel, List.of(), List.of());
	}

}
