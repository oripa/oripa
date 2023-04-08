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

import java.util.Collection;
import java.util.List;

import oripa.domain.fold.foldability.FoldabilityChecker;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.domain.fold.halfedge.OrigamiModelFactory;
import oripa.value.OriLine;

/**
 * Creates OrigamiModel and sets local flat foldability.
 *
 * @author OUCHI Koji
 *
 */
public class TestedOrigamiModelFactory {
	OrigamiModelFactory factory = new OrigamiModelFactory();
	FoldabilityChecker checker = new FoldabilityChecker();

	/**
	 * Constructs the half-edge based data structure which describes relation
	 * among faces and edges and store it into {@code OrigamiModel}. This is a
	 * preparation for estimating folded shape with layers: this method removes
	 * meaningless vertices.
	 *
	 * @param creasePattern
	 * @return A model data converted from crease pattern.
	 */
	public OrigamiModel createOrigamiModel(
			final Collection<OriLine> creasePattern, final double pointEps) {
		var origamiModel = factory.createOrigamiModel(creasePattern, pointEps);
		origamiModel.setLocallyFlatFoldable(checker.testLocalFlatFoldability(origamiModel));

		return origamiModel;
	}

	public List<OrigamiModel> createOrigamiModels(
			final Collection<OriLine> creasePattern, final double pointEps) {
		var origamiModels = factory.createOrigamiModels(creasePattern, pointEps);
		origamiModels.forEach(model -> model.setLocallyFlatFoldable(checker.testLocalFlatFoldability(model)));

		return origamiModels;
	}

}
