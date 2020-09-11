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

/**
 * @author OUCHI Koji
 *
 */
public class DefaultEsitimationEntityHolder implements EstimationEntityHolder {
	/**
	 * Origami Model for Estimation
	 */
	private OrigamiModel origamiModel = null;

	/**
	 * Folded Model Information (Result of Estimation)
	 */
	private FoldedModelInfo foldedModelInfo = null;

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.fold.EstimationEntityHolder#getOrigamiModel()
	 */
	@Override
	public OrigamiModel getOrigamiModel() {
		return origamiModel;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see
	 * oripa.domain.fold.EstimationEntityHolder#setOrigamiModel(oripa.domain.
	 * fold.OrigamiModel)
	 */
	@Override
	public void setOrigamiModel(final OrigamiModel origamiModel) {
		this.origamiModel = origamiModel;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.fold.EstimationEntityHolder#getFoldedModelInfo()
	 */
	@Override
	public FoldedModelInfo getFoldedModelInfo() {
		return foldedModelInfo;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see
	 * oripa.domain.fold.EstimationEntityHolder#setFoldedModelInfo(oripa.domain.
	 * fold.FoldedModelInfo)
	 */
	@Override
	public void setFoldedModelInfo(final FoldedModelInfo foldedModelInfo) {
		this.foldedModelInfo = foldedModelInfo;
	}

}
