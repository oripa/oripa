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
package oripa.domain.fold.halfedge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import oripa.util.Pair;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class OriEdgesFactory {
    /**
     * Creates new OriEdges and sets the relation to half-edges of faces.
     *
     * @param faces
     *            are assumed to have correct half-edges, i.e., each half-edge
     *            should be assigned at least a start vertex, next half-edge and
     *            the line type value. every half-edge of the faces will be
     *            affected.
     * @return a list of edges
     */
    public List<OriEdge> createOriEdges(final Collection<OriFace> faces) {
        var edges = new ArrayList<OriEdge>();

        var halfedges = new HashMap<Pair<OriVertex, OriVertex>, OriHalfedge>();

        for (OriFace face : faces) {
            face.halfedgeStream().forEach(he -> {
                he.setPair(null);

                halfedges.put(new Pair<>(he.getVertex(), he.getNext().getVertex()), he);
            });
        }

        for (var entry : halfedges.entrySet()) {
            var vertexPair = entry.getKey();
            var halfedge = entry.getValue();

            if (halfedge.getPair().isEmpty()) {
                var reversed = halfedges.get(new Pair<>(vertexPair.v2(), vertexPair.v1()));
                var edge = (reversed == null) ? makeBoundary(halfedge) : makePair(halfedge, reversed);

                edges.add(edge);
            }
        }

        return edges;
    }

    /**
     * creates new OriEdge which consists of given half-edges.
     *
     * @param he0
     *            .pair and .type will be affected.
     * @param he1
     *            .pair and .type will be affected.
     * @return an edge for he0 and he1.
     */
    private OriEdge makePair(final OriHalfedge he0, final OriHalfedge he1) {
        he0.setPair(he1);
        he1.setPair(he0);

        if (he0.getType() != he1.getType()) {
            throw new IllegalArgumentException("line types of paired half-edges should be equal.");
        }

        var type = he0.getType();
        OriEdge edge = new OriEdge(he0.getVertex(), he1.getVertex(), type);
        edge.setHalfedgeV0V1(he0);
        edge.setHalfedgeV1V0(he1);
        return edge;
    }

    /**
     * creates new OriEdge which consists of given half-edge.
     *
     * @param he
     *            type will be affected.
     * @return an edge with left halfedge of CUT type.
     */
    private OriEdge makeBoundary(final OriHalfedge he) {
        OriEdge edge = new OriEdge(he.getVertex(), he.getNext().getVertex(), OriLine.Type.CUT.toInt());
        he.setType(edge.getType());
        edge.setHalfedgeV0V1(he);

        return edge;
    }

}
