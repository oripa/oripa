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

import oripa.domain.fold.halfedge.OriFace;
import oripa.util.rule.AbstractRule;

/**
 * For visualization.
 *
 * @author OUCHI Koji
 *
 */
class LayerOrderRule extends AbstractRule<OriFace> {

    private final List<OriFace> violatingFaces;

    public LayerOrderRule(final String name, final List<OriFace> violatingFaces) {
        super(name);
        this.violatingFaces = violatingFaces;
    }

    @Override
    public boolean holds(final OriFace face) {
        return violatingFaces.stream().noneMatch(violation -> face.equals(violation));
    }

    @Override
    public String toString() {
        return getName() + ":[" +
                String.join(",",
                        violatingFaces.stream()
                                .mapToInt(OriFace::getFaceID)
                                .boxed()
                                .map(id -> id.toString())
                                .toList())
                + "]";
    }
}
