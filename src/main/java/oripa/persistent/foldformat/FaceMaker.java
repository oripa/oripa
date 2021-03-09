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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.util.collection.CollectionUtil;

/**
 * @author OUCHI Koji
 *
 */
public class FaceMaker {
	private static final Logger logger = LoggerFactory.getLogger(FaceMaker.class);

	private final List<List<Integer>> verticesVertices;
	private final List<Map<Integer, Integer>> reversedIndices;

	private final Set<List<Integer>> unusedDirectedEdges;

	/**
	 * Constructor
	 */
	public FaceMaker(final List<List<Integer>> edgesVertices,
			final List<List<Integer>> verticesVertices) {

		this.verticesVertices = verticesVertices;

		logger.debug("creating reversed indices.");
		reversedIndices = createReversedIndices(verticesVertices);

		logger.debug("creating unused directed edges.");
		unusedDirectedEdges = createUnusedDirectedEdges(edgesVertices);
		logger.debug("initialization of face maker is done.");
	}

	private List<Map<Integer, Integer>> createReversedIndices(final List<List<Integer>> verticesVertices) {
		List<Map<Integer, Integer>> reversed = new ArrayList<>();

		for (int u = 0; u < verticesVertices.size(); u++) {
			var map = new HashMap<Integer, Integer>();
			var vertices = verticesVertices.get(u);
			for (int vIndex = 0; vIndex < vertices.size(); vIndex++) {
				map.put(vertices.get(vIndex), vIndex);
			}
			reversed.add(map);
		}

		return reversed;
	}

	private Set<List<Integer>> createUnusedDirectedEdges(final List<List<Integer>> edgesVertices) {
		Set<List<Integer>> unused = new HashSet<>();

		for (var edge : edgesVertices) {
			unused.add(edge);
			unused.add(List.of(edge.get(1), edge.get(0)));
		}
		return unused;
	}

	public List<Integer> makeFace(final List<Integer> edge) {

		if (!unusedDirectedEdges.contains(edge)) {
			return null;
		}

		var face = new ArrayList<Integer>(edge);

		unusedDirectedEdges.remove(edge);

		var u = edge.get(0);
		var v = edge.get(1);

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
		var uIndex = reversedIndices.get(v).get(u);
		var vertices = verticesVertices.get(v);

		return CollectionUtil.getCircular(vertices, uIndex - 1);
	}

	/**
	 * make a loop. The first call of this method is with face = (u,v) and edge
	 * = (v,w).
	 */
	private boolean makeFace(final List<Integer> face, final List<Integer> edge) {
		logger.trace("called with face: " + face);

		var u = edge.get(0);
		var v = edge.get(1);
		unusedDirectedEdges.remove(edge);

		if (face.get(0).intValue() == v) {
			logger.debug("succeeded to make a face: " + face);
			return true;
		}

		face.add(v);

		var w = getLeftSideNeighbor(u, v);

		var nextEdge = List.of(v, w);

		if (!unusedDirectedEdges.contains(nextEdge)) {
			logger.warn("failed to make a face. (The next path is already used)");

			return false;
		}

		return makeFace(face, nextEdge);
	}

}
