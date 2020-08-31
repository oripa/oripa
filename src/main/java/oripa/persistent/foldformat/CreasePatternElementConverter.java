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
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import oripa.value.OriLine;
import oripa.value.OriPoint;

/**
 * @author Koji
 *
 */
public class CreasePatternElementConverter {

	/**
	 * generates a list of coordinates of distinct vertices.
	 *
	 * @param lines
	 * @return
	 */
	public List<List<Double>> toVerticesCoords(final Collection<OriLine> lines) {
		return lines.stream()
				.flatMap(line -> Stream.of(line.p0, line.p1))
				.distinct()
				.map(point -> vertexToList(point))
				.collect(Collectors.toList());

	}

	private List<Double> vertexToList(final OriPoint p) {
		return List.of(p.x, p.y);
	}

	/**
	 * generates a list of index lists for each edge.
	 *
	 * @param lines
	 * @return
	 */
	public List<List<Integer>> toEdgesVertices(final Collection<OriLine> lines) {
		var coords = toVerticesCoords(lines);

		return lines.stream()
				.map(line -> List.of(
						coords.indexOf(vertexToList(line.p0)),
						coords.indexOf(vertexToList(line.p1))))
				.collect(Collectors.toList());
	}

	/**
	 * generates a list of assignment for each edge.
	 *
	 * @param lines
	 * @return
	 */
	public List<String> toEdgesAssignment(final Collection<OriLine> lines) {
		return lines.stream()
				.map(line -> {
					switch (line.getType()) {
					case NONE:
						return "F";
					case CUT:
						return "B";
					case RIDGE:
						return "M";
					case VALLEY:
						return "V";
					default:
						return null;
					}
				})
				.collect(Collectors.toList());
	}

	/**
	 * This method tries all possible combinations of [u, v] for all u and v,
	 * which implicitly defines half-edge structure.
	 *
	 * @param lines
	 * @return
	 */
	public List<List<Integer>> toVerticesVertices(final Collection<OriLine> lines) {
		var coords = toVerticesCoords(lines);
		var edgesVertices = toEdgesVertices(lines);

		var verticesVertices = new ArrayList<List<Integer>>();
		coords.forEach(p -> verticesVertices.add(new ArrayList<Integer>()));

		for (int u = 0; u < coords.size(); u++) {
			for (int v = u + 1; v < coords.size(); v++) {
				var edge = List.of(u, v);
				if (edgesVertices.contains(edge) || edgesVertices.contains(reverseEdge(edge))) {
					verticesVertices.get(u).add(v);
					verticesVertices.get(v).add(u);
				}
			}
		}

		for (int u = 0; u < coords.size(); u++) {
			verticesVertices.set(u, Geometry.sortByAngle(u, verticesVertices.get(u), coords));
		}

		return verticesVertices;
	}

	private List<Integer> reverseEdge(final List<Integer> edge) {
		return List.of(edge.get(1), edge.get(0));
	}

	public List<List<Integer>> toFacesVertices(final Collection<OriLine> lines) {
		var edgesVertices = toEdgesVertices(lines);
		var verticesVertices = toVerticesVertices(lines);
		var coords = toVerticesCoords(lines);
		var assignment = toEdgesAssignment(lines);

		var faceMaker = new FaceMaker(edgesVertices, verticesVertices, coords);

		var facesVertices = new ArrayList<List<Integer>>();

		for (int u = 0; u < verticesVertices.size(); u++) {
			var vertices = verticesVertices.get(u);
			for (var v : vertices) {
				var edge = List.of(u, v);

				var face = faceMaker.makeFace(edge);

				if (face != null) {
					facesVertices.add(face);
				}
			}
		}

		// remove outer face
		return facesVertices.stream().filter(face -> {
			for (int i = 0; i < face.size(); i++) {
				var u = face.get(i);
				var v = face.get((i + 1) % face.size());

				var edge = List.of(u, v);
//				if (!edgeExists(edge, edgesVertices)) {
//					continue;
//				}
				if (getAssignment(edge, edgesVertices, assignment) != "B") {
					return true;
				}
			}
			return false;
		}).collect(Collectors.toList());
	}

	private boolean edgeExists(final List<Integer> edge, final List<List<Integer>> edgesVertices) {
		return edgesVertices.indexOf(edge) != -1 || edgesVertices.indexOf(reverseEdge(edge)) != -1;
	}

	private String getAssignment(
			final List<Integer> edge,
			final List<List<Integer>> edgesVertices,
			final List<String> assignment) {

		var i = edgesVertices.indexOf(edge);
		if (i != -1) {
			return assignment.get(i);
		}

		return assignment.get(edgesVertices.indexOf(reverseEdge(edge)));
	}

	public Collection<OriLine> fromEdges(
			final List<List<Integer>> edgesVertices,
			final List<String> edgesAssignment,
			final List<List<Double>> verticesCoords) {

		var points = verticesCoords.stream()
				.map(coord -> new OriPoint(coord.get(0), coord.get(1)))
				.collect(Collectors.toList());

		var lines = edgesVertices.stream()
				.map(edge -> new OriLine(
						points.get(edge.get(0)), points.get(edge.get(1)),
						OriLine.Type.NONE))
				.collect(Collectors.toList());

		for (int i = 0; i < lines.size(); i++) {
			var line = lines.get(i);
			switch (edgesAssignment.get(i)) {
			case "B":
				line.setType(OriLine.Type.CUT);
				break;
			case "F":
				line.setType(OriLine.Type.NONE);
				break;
			case "M":
				line.setType(OriLine.Type.RIDGE);
				break;
			case "V":
				line.setType(OriLine.Type.VALLEY);
				break;
			default:
				break;
			}
		}

		return lines;
	}
}
