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
package oripa.renderer.estimation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OriVertex;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.domain.fold.origeom.OverlapRelation;
import oripa.util.IntPair;
import oripa.util.collection.CollectionUtil;
import oripa.value.OriPoint;

/**
 * @author OUCHI Koji
 *
 */
public class VertexDepthMapFactory {
	public Map<OriVertex, Integer> create(final OrigamiModel origamiModel, final OverlapRelation overlapRelation,
			final double eps) {

		var vertexToFaces = createVertexToFaces(origamiModel, overlapRelation);

		var samePositionVertices = createSamePositionVertices(origamiModel.getVertices(), eps);

		var depthMap = new HashMap<OriVertex, Integer>();

		// sort by depth
		samePositionVertices.forEach((position, vertices) -> {

			var sorted = new ArrayList<OriVertex>();

			for (var vertex : vertices) {
				// can happen by numerical error
				if (!vertexToFaces.containsKey(vertex)) {
					sorted.add(vertex);
					continue;
				}

				for (int k = 0; k <= sorted.size(); k++) {
					if (k == sorted.size()) {
						sorted.add(vertex);
						break;
					}

					var faceIndices = findTopFaceIndices(vertex, sorted.get(k), vertexToFaces, overlapRelation);
					int i = faceIndices.getV1();
					int j = faceIndices.getV2();

					if (overlapRelation.isUpper(i, j)) {
						sorted.add(k, vertex);
						break;
					}
				}
			}
			for (var vertex : vertices) {
				depthMap.put(vertex, sorted.indexOf(vertex));
			}
		});

		return depthMap;
	}

	private TreeMap<OriPoint, List<OriVertex>> createSamePositionVertices(final List<OriVertex> vertices,
			final double eps) {

		var samePositionVertices = new TreeMap<OriPoint, List<OriVertex>>();

		// build samePositionVertices
		for (var vertex : vertices) {
			double x = vertex.getPosition().getX();
			double y = vertex.getPosition().getY();

			var boundMap = CollectionUtil.rangeMap(
					samePositionVertices,
					new OriPoint(x - eps, y - eps),
					new OriPoint(x + eps, y + eps));

			var v = new OriPoint(x, y);
			var posOpt = boundMap.keySet().stream()
					.filter(p -> p.equals(v, eps))
					.findFirst();

			posOpt.ifPresentOrElse(pos -> {
				var list = samePositionVertices.get(pos);
				list.add(vertex);
			}, () -> {
				var list = new ArrayList<OriVertex>();
				list.add(vertex);
				samePositionVertices.put(v, list);
			});
		}

		return samePositionVertices;
	}

	/**
	 * Finds the smallest indices of faces with intersection.
	 *
	 * @param v0
	 * @param v1
	 * @param vertexToFaces
	 * @param overlapRelation
	 * @return
	 */
	private IntPair findTopFaceIndices(final OriVertex v0, final OriVertex v1,
			final Map<OriVertex, List<OriFace>> vertexToFaces,
			final OverlapRelation overlapRelation) {

		var faces0 = vertexToFaces.get(v0);
		var faces1 = vertexToFaces.get(v1);

		if (faces0 == null) {
			throw new IllegalArgumentException("no faces for vertex " + v0.toString());
		}
		if (faces1 == null) {
			throw new IllegalArgumentException("no faces for vertex " + v1.toString());
		}

		for (int f0Index = 0; f0Index < faces0.size(); f0Index++) {
			int i = faces0.get(f0Index).getFaceID();

			var jOpt = faces1.stream()
					.map(OriFace::getFaceID)
					.filter(j -> !overlapRelation.isNoOverlap(i, j))
					.findFirst();

			if (jOpt.isPresent()) {
				return new IntPair(i, jOpt.get());
			}

		}
		return new IntPair(faces0.get(0).getFaceID(), faces1.get(0).getFaceID());
	}

	private Map<OriVertex, List<OriFace>> createVertexToFaces(final OrigamiModel origamiModel,
			final OverlapRelation overlapRelation) {
		var vertexToFaces = new HashMap<OriVertex, List<OriFace>>();
		var sortedVertexToFaces = new HashMap<OriVertex, List<OriFace>>();

		for (var face : origamiModel.getFaces()) {
			for (var halfedge : face.halfedgeIterable()) {
				var vertex = halfedge.getVertex();
				vertexToFaces.putIfAbsent(vertex, new ArrayList<>());
				vertexToFaces.get(vertex).add(face);
			}
		}

		vertexToFaces.forEach((vertex, faces) -> {
			var sorted = new ArrayList<OriFace>();

			for (int i = 0; i < faces.size(); i++) {
				var face_i = faces.get(i);
				for (int j = 0; j <= sorted.size(); j++) {
					if (j == sorted.size()) {
						sorted.add(face_i);
						break;
					}
					var face_j = sorted.get(j);

					if (overlapRelation.isUpper(face_i.getFaceID(), face_j.getFaceID())) {
						sorted.add(j, face_i);
						break;
					}
				}
			}

			sortedVertexToFaces.put(vertex, sorted);
		});

		return sortedVertexToFaces;
	}

}
