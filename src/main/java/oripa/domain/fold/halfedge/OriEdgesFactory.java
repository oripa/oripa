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
import java.util.Map;

import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class OriEdgesFactory {
	/**
	 * Creates new OriEdges and sets the relation to half-edges of faces.
	 * Half-edge data structure in strict way should share reference of edges
	 * among vertices and half-edges but ORIPA doesn't use the relation.
	 * Therefore we can 'create' new edges and set the references to half-edges
	 * of faces.
	 *
	 * @param faces
	 *            are assumed to have correct half-edges, i.e., each half-edge
	 *            should be assigned at least a start vertex, next half-edge and
	 *            the line type value.
	 * @return a list of edges
	 */
	public List<OriEdge> createOriEdges(final Collection<OriFace> faces) {
		var edges = new ArrayList<OriEdge>();
		var halfedges = new HashMap<OriVertex, List<OriHalfedge>>();

		// Clear all the Halfedges
		for (OriFace face : faces) {
			for (OriHalfedge he : face.halfedges) {
				he.setPair(null);
				he.setEdge(null);

				allocateAndPut(he.getVertex(), he, halfedges);
			}
		}

		// find half-edge pairs whose
		// directions are opposite (that's the definition of edge).
		halfedges.values().forEach(hes -> {
			for (var he0 : hes) {
				if (he0.getPair() != null) {
					continue;
				}

				var oppositeHes = halfedges.get(he0.next.getVertex());
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
				if (he.getPair() == null) {
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
		return he0.getVertex() == he1.next.getVertex() && he0.next.getVertex() == he1.getVertex();
	}

	/**
	 * creates new OriEdge which consist of given half-edges.
	 *
	 * @param he0
	 *            .pair and .edge will be affected.
	 * @param he1
	 *            .pair and .edge will be affected.
	 * @return an edge for he0 and he1.
	 */
	private OriEdge makePair(final OriHalfedge he0, final OriHalfedge he1) {
		OriEdge edge = new OriEdge();
		he0.setPair(he1);
		he1.setPair(he0);
		he0.setEdge(edge);
		he1.setEdge(edge);
		edge.sv = he0.getVertex();
		edge.ev = he1.getVertex();
		edge.left = he0;
		edge.setRight(he1);
		edge.setType(he0.getTemporaryType());
		return edge;
	}

	/**
	 * creates new OriEdge which consist of given half-edge.
	 *
	 * @param he
	 *            .edge will be affected.
	 * @return an edge with CUT type for he0 and he1.
	 */
	private OriEdge makeBoundary(final OriHalfedge he) {
		OriEdge edge = new OriEdge();
		he.setEdge(edge);
		edge.sv = he.getVertex();
		edge.ev = he.next.getVertex();
		edge.left = he;
		edge.setType(OriLine.Type.CUT.toInt());

		return edge;
	}

}
