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

import java.util.Collections;
import java.util.List;

import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.domain.fold.origeom.OverlapRelation;
import oripa.domain.fold.subface.SubFace;

/**
 * An entity of folded model.
 *
 * @author OUCHI Koji
 *
 */
public record FoldedModel(
        OrigamiModel origamiModel,
        List<OverlapRelation> overlapRelations,
        List<SubFace> subfaces) {

    /**
     * Constructor
     */
    public FoldedModel(final OrigamiModel origamiModel, final List<OverlapRelation> overlapRelations,
            final List<SubFace> subfaces) {
        this.origamiModel = origamiModel;
        this.overlapRelations = Collections.unmodifiableList(overlapRelations);
        this.subfaces = Collections.unmodifiableList(subfaces);
    }

    /**
     *
     * @return the number of foldable patterns.
     */
    public int getFoldablePatternCount() {
        return overlapRelations.size();
    }

    /**
     *
     * @return whether the model is after fold or not.
     */
    public boolean isFolded() {
        return origamiModel.isFolded();
    }
}
