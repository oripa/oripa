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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class OriEdgesFactory {
	/**
	 * Creates new OriEdges and sets the relation between half-edges of faces.
	 * Half-edge data structure in strict way should share reference of edges
	 * among vertices and half-edges but ORIPA doesn't use the relation.
	 * Therefore we can 'create' new edges and set the references to half-edges
	 * of faces.
	 *
	 * @param faces
	 * @return a list of edges
	 */
	public List<OriEdge> createOriEdges(final List<OriFace> faces) {
		var edges = new ArrayList<OriEdge>();
		var halfedges = new HashMap<OriVertex, List<OriHalfedge>>();

		// Clear all the Halfedges
		for (OriFace face : faces) {
			for (OriHalfedge he : face.halfedges) {
				he.pair = null;
				he.edge = null;

				allocateAndPut(he.vertex, he, halfedges);
			}
		}

		// find half-edge pairs whose
		// directions are opposite (that's the definition of edge).
		halfedges.values().forEach(hes -> {
			for (var he0 : hes) {
				if (he0.pair != null) {
					continue;
				}

				var oppositeHes = halfedges.get(he0.next.vertex);
				for (var he1 : oppositeHes) {
					if (isOppositeDirection(he0, he1)) {
						edges.add(makePair(he0, he1));
						break;
					}
				}
			}
		});

		// If the pair wasn't found it should be boundary of paper
		halfedges.values().forEach(hes -> {
			for (var he : hes) {
				if (he.pair == null) {
					edges.add(makeBoundary(he));
				}
			}
		});

		return edges;
	}

	private void allocateAndPut(final OriVertex vertex, final OriHalfedge he,
			final Map<OriVertex, List<OriHalfedge>> halfedges) {
		if (halfedges.get(vertex) == null) {
			halfedges.put(vertex, new ArrayList<>());
		}
		halfedges.get(vertex).add(he);
	}

	private boolean isOppositeDirection(final OriHalfedge he0, final OriHalfedge he1) {
		return he0.vertex == he1.next.vertex && he0.next.vertex == he1.vertex;
	}

	/**
	 * creates new OriEdge which consist of given halfedges.
	 *
	 * @param he0
	 *            .pair and .edge will be affected.
	 * @param he1
	 *            .pair and .edge will be affected.
	 * @return an edge with AUX type for he0 and he1.
	 */
	private OriEdge makePair(final OriHalfedge he0, final OriHalfedge he1) {
		OriEdge edge = new OriEdge();
		he0.pair = he1;
		he1.pair = he0;
		he0.edge = edge;
		he1.edge = edge;
		edge.sv = he0.vertex;
		edge.ev = he1.vertex;
		edge.left = he0;
		edge.right = he1;
		edge.type = OriLine.Type.AUX.toInt();
		return edge;
	}

	/**
	 * creates new OriEdge which consist of given halfedge.
	 *
	 * @param he
	 *            .edge will be affected.
	 * @return an edge with CUT type for he0 and he1.
	 */
	private OriEdge makeBoundary(final OriHalfedge he) {
		OriEdge edge = new OriEdge();
		he.edge = edge;
		edge.sv = he.vertex;
		edge.ev = he.next.vertex;
		edge.left = he;
		edge.type = OriLine.Type.CUT.toInt();

		return edge;
	}

}
