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
package oripa.persistent.foldformat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author OUCHI Koji
 *
 */
public class FaceMaker {
	private static final Logger logger = LoggerFactory.getLogger(FaceMaker.class);

	private final List<List<Integer>> verticesVertices;
	private final boolean[][] edgePassed;

	/**
	 * Constructor
	 */
	public FaceMaker(final List<List<Integer>> edgesVertices,
			final List<List<Integer>> verticesVertices) {

		this.verticesVertices = verticesVertices;

		edgePassed = new boolean[verticesVertices.size()][verticesVertices.size()];
		for (var arr : edgePassed) {
			Arrays.fill(arr, false);
		}
	}

	public List<Integer> makeFace(final List<Integer> edge) {
		var u = edge.get(0);
		var v = edge.get(1);

		if (edgePassed[edge.get(0)][edge.get(1)]) {
			return null;
		}

		var face = new ArrayList<Integer>();

		face.addAll(edge);
		edgePassed[u][v] = true;

		var w = getLeftSideNeighbor(u, v);

		if (!makeFace(face, List.of(v, w))) {
			return null;
		}

		return face;
	}

	/**
	 * To make a counter-clockwise loop, we consider edges incident to v of
	 * given [u, v]. The edge next to [u, v] in counter-clockwise direction
	 * (left side of u in the vertices list) is the edge to follow.
	 *
	 * @param u
	 * @param v
	 * @return the vertex of left side neighbor of u among vertices connecting
	 *         to v.
	 */
	private Integer getLeftSideNeighbor(final int u, final int v) {
		var vertices = verticesVertices.get(v);
		var uIndex = vertices.indexOf(u);
		return vertices.get((uIndex - 1 + vertices.size()) % vertices.size());
	}

	/**
	 * make a loop. The first call of this method is with face = (u,v) and edge
	 * = (v,w).
	 */
	private boolean makeFace(final List<Integer> face, final List<Integer> edge) {
		logger.info("called with face: " + face);

		var u = edge.get(0);
		var v = edge.get(1);
		edgePassed[u][v] = true;

		if (face.get(0) == v) {
			logger.info("succeeded to make a face: " + face);
			return true;
		}

		face.add(v);

		var w = getLeftSideNeighbor(u, v);

		if (edgePassed[v][w]) {
			logger.warn("failed to make a face. (The next path is already used)");

			return false;
		}

		var nextEdge = List.of(v, w);
		return makeFace(face, nextEdge);
	}

}
