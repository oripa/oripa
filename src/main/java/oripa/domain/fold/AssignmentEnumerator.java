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
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.fold.foldability.VertexFoldability;
import oripa.domain.fold.halfedge.OriEdge;
import oripa.domain.fold.halfedge.OriVertex;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class AssignmentEnumerator {
	private static final Logger logger = LoggerFactory.getLogger(AssignmentEnumerator.class);

	private final VertexFoldability foldability = new VertexFoldability();
	private final Consumer<OrigamiModel> answerConsumer;

	private List<OriEdge> edges;
	private Map<List<OriVertex>, OriEdge> edgeMap;

	private int answerCount = 0;

	public AssignmentEnumerator(final Consumer<OrigamiModel> answerConsumer) {
		this.answerConsumer = answerConsumer;
	}

	public void enumerate(final OrigamiModel origamiModel) {
		edges = origamiModel.getEdges();

		edgeMap = new HashMap<>();
		edges.forEach(edge -> edgeMap.put(edgeToKey(edge), edge));

		answerCount = 0;

		enumerateImpl(origamiModel, 0);

		logger.debug("answerCount = {}", answerCount);
	}

	private void enumerateImpl(final OrigamiModel origamiModel,
			final int vertexIndex) {
		var vertices = origamiModel.getVertices();

		if (vertexIndex == vertices.size()) {
//			if (origamiModel.isUnassigned()) {
//				logger.debug("wrong answer. {}", origamiModel);
//			}
			answerCount++;
			answerConsumer.accept(origamiModel);
			return;
		}

		var vertex = vertices.get(vertexIndex);

		if (!vertex.hasUnassignedEdge()) {
			enumerateImpl(origamiModel, vertexIndex + 1);
			return;
		}

		var mountainCount = vertex.edgeStream().filter(OriEdge::isMountain).count();
		var valleyCount = vertex.edgeStream().filter(OriEdge::isValley).count();

		var originalAssignment = toAssignmentMap(vertex);

		var assignments = createAssignments(vertex, 0,
				mountainCount, valleyCount);

		for (var assignment : assignments) {
			logger.trace("go next. vertex@{} = {}, assignment = {}", vertexIndex, vertex.getPositionBeforeFolding(),
					assignment);
			apply(assignment);
			logger.trace("edges: {}", vertex.edgeStream().map(OriEdge::getType).collect(Collectors.toList()));

			enumerateImpl(origamiModel, vertexIndex + 1);

			logger.trace("get back. vertex@{} = {}, assignment = {}", vertexIndex, vertex.getPositionBeforeFolding(),
					assignment);
			apply(originalAssignment);
			logger.trace("edges: {}", vertex.edgeStream().map(OriEdge::getType).collect(Collectors.toList()));

		}

	}

	private Map<List<OriVertex>, OriLine.Type> toAssignmentMap(final OriVertex vertex) {
		return vertex.edgeStream()
				.collect(Collectors.toMap(this::edgeToKey, edge -> OriLine.Type.fromInt(edge.getType())));
	}

	private List<OriVertex> edgeToKey(final OriEdge edge) {
		return Stream.of(edge.getStartVertex(), edge.getEndVertex()).sorted().collect(Collectors.toList());
	}

	private void apply(final Map<List<OriVertex>, OriLine.Type> assignment) {
		logger.trace("apply assignment");
		assignment.forEach((key, type) -> setType(key, type));
	}

	private void setType(final List<OriVertex> edgeKey, final OriLine.Type type) {
		var vertex = edgeKey.get(0);
		var opposite = edgeKey.get(1);
		logger.trace("set {} to ({},{})", type, vertex.getPositionBeforeFolding(), opposite.getPositionBeforeFolding());
		vertex.getEdge(opposite).setType(type.toInt());
		edgeMap.get(edgeKey).setType(type.toInt());
	}

	private List<Map<List<OriVertex>, OriLine.Type>> createAssignments(final OriVertex vertex,
			final int edgeIndex,
			final long mountainCount, final long valleyCount) {

		logger.trace("createAssignments(): vertex={}, edgeIndex={}", vertex, edgeIndex);

		var edgeCount = vertex.edgeCount();

		if (edgeIndex == edgeCount) {
			if (!foldability.holds(vertex)) {
				logger.trace("edges: {}", vertex.edgeStream().map(OriEdge::getType).collect(Collectors.toList()));
				logger.trace("return empty assignments. ({})", vertex);
				return List.of();
			}

			var assignment = List.of(toAssignmentMap(vertex));
			logger.trace("return {}", assignment);
			return assignment;
		}
		var edge = vertex.getEdge(edgeIndex);

		if (!edge.isUnassigned()) {
			return createAssignments(vertex, edgeIndex + 1, mountainCount, valleyCount);
		}

		var nextMountainCount = mountainCount;
		var nextValleyCount = valleyCount;

		var types = List.of(OriLine.Type.MOUNTAIN, OriLine.Type.VALLEY);

		var assignments = new ArrayList<Map<List<OriVertex>, OriLine.Type>>();

		for (var type : types) {
			if (vertex.isInsideOfPaper()) {
				if (type == OriLine.Type.MOUNTAIN) {
					if (nextMountainCount >= edgeCount / 2 + 1) {
						continue;
					}
					nextMountainCount++;
				}
				if (type == OriLine.Type.VALLEY) {
					if (nextValleyCount >= edgeCount / 2 + 1) {
						continue;
					}
					nextValleyCount++;
				}
			}
			logger.trace("make assignment");
			setType(edgeToKey(edge), type);
			assignments.addAll(createAssignments(vertex, edgeIndex + 1, nextMountainCount, nextValleyCount));
			logger.trace("make unassignment");
			setType(edgeToKey(edge), OriLine.Type.UNASSIGNED);

			if (vertex.isInsideOfPaper()) {
				if (type == OriLine.Type.MOUNTAIN) {
					nextMountainCount--;
				}
				if (type == OriLine.Type.VALLEY) {
					nextValleyCount--;
				}
			}
		}

		return assignments;
	}

}
