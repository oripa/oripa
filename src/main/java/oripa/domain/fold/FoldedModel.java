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

import oripa.domain.fold.halfedge.OrigamiModel;

/**
 * An entity of folded model.
 *
 * @author OUCHI Koji
 *
 */
public class FoldedModel {
	private OrigamiModel origamiModel;
	private OverlapRelationList foldedModelInfo;

	/**
	 * Constructor
	 */
	public FoldedModel(final OrigamiModel origamiModel, final OverlapRelationList foldedModelInfo) {
		this.origamiModel = origamiModel;
		this.foldedModelInfo = foldedModelInfo;
	}

	/**
	 * @return origamiModel
	 */
	public OrigamiModel getOrigamiModel() {
		return origamiModel;
	}

	/**
	 * @param origamiModel
	 *            Sets origamiModel
	 */
	public void setOrigamiModel(final OrigamiModel origamiModel) {
		this.origamiModel = origamiModel;
	}

	/**
	 * @return foldedModelinfo
	 */
	public OverlapRelationList getFoldedModelInfo() {
		return foldedModelInfo;
	}

	/**
	 * @param foldedModelInfo
	 *            Sets foldedModelinfo
	 */
	public void setFoldedModelInfo(final OverlapRelationList foldedModelInfo) {
		this.foldedModelInfo = foldedModelInfo;
	}
}
