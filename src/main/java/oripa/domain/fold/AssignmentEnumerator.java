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
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.fold.foldability.VertexFoldability;
import oripa.domain.fold.halfedge.OriEdge;
import oripa.domain.fold.halfedge.OriVertex;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.domain.fold.origeom.OriGeomUtil;
import oripa.util.MathUtil;
import oripa.util.StopWatch;
import oripa.util.collection.CollectionUtil;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
class AssignmentEnumerator {
	private static final Logger logger = LoggerFactory.getLogger(AssignmentEnumerator.class);

	private final VertexFoldability foldability = new VertexFoldability();
	private final Consumer<OrigamiModel> answerConsumer;

	private Map<List<OriVertex>, OriEdge> edgeMap;
	private Set<OriVertex> originallyAssigned;

	private Map<Map<List<OriVertex>, OriLine.Type>, List<Map<List<OriVertex>, OriLine.Type>>> assignmentMemos;

	private int assignmentCallCount = 0;
	private int enumerationCallCount = 0;
	private int answerCount = 0;

	public AssignmentEnumerator(final Consumer<OrigamiModel> answerConsumer) {
		this.answerConsumer = answerConsumer;
	}

	/**
	 * Enumerates all locally-flat-foldable assignments of given origamiModel.
	 * This method calls {@code answerConsumer}, which is given at construction,
	 * every time the algorithm finds a locally-flat-foldable assignment.
	 *
	 * @param origamiModel
	 */
	public void enumerate(final OrigamiModel origamiModel) {
		var edges = origamiModel.getEdges();

		edgeMap = new HashMap<>();
		edges.forEach(edge -> edgeMap.put(edgeToKey(edge), edge));

		originallyAssigned = origamiModel.getVertices().stream()
				.filter(Predicate.not(OriVertex::hasUnassignedEdge))
				.collect(Collectors.toSet());

		assignmentMemos = new HashMap<>();

		enumerationCallCount = 0;
		assignmentCallCount = 0;
		answerCount = 0;

		var watch = new StopWatch(true);

		var sortedVertices = sort(origamiModel.getVertices());

		enumerateImpl(origamiModel, sortedVertices);

		logger.debug("time: {}[ms]", watch.getMilliSec());

		logger.debug("enumerationCallCount = {}, assignmentCallCount = {}, assignmentAnswerCount = {}",
				enumerationCallCount, assignmentCallCount, answerCount);
	}

	private void enumerateImpl(final OrigamiModel origamiModel,
			final List<OriVertex> candidateVertices) {

		enumerationCallCount++;

		if (candidateVertices.isEmpty()) {
//			if (origamiModel.isUnassigned()) {
//				logger.debug("wrong answer. {}", origamiModel);
//			}
			answerCount++;
			answerConsumer.accept(origamiModel);
			return;
		}

		var vertex = candidateVertices.get(0);

		if (originallyAssigned.contains(vertex)) {
			var nextCandidateVertices = CollectionUtil.partialCopy(
					candidateVertices, 1, candidateVertices.size());
			enumerateImpl(origamiModel, nextCandidateVertices);
			return;
		}

		if (!vertex.hasUnassignedEdge()) {
			// vertex can be fully assigned but sometimes not foldable if
			// connected other vertex is assigned previously.
			if (foldability.holds(vertex)) {
				var nextCandidateVertices = CollectionUtil.partialCopy(
						candidateVertices, 1, candidateVertices.size());
				enumerateImpl(origamiModel, nextCandidateVertices);
			}
			return;
		}

		var originalAssignment = toAssignmentMap(vertex);

		List<Map<List<OriVertex>, OriLine.Type>> assignments = null;

		if (assignmentMemos.keySet().contains(originalAssignment)) {
			assignments = assignmentMemos.get(originalAssignment);
		} else {
			var mountainCount = vertex.edgeStream().filter(OriEdge::isMountain).count();
			var valleyCount = vertex.edgeStream().filter(OriEdge::isValley).count();

			assignments = createAssignments(vertex, 0, mountainCount, valleyCount);
			assignmentMemos.put(originalAssignment, assignments);
		}

		var nextCandidateVertices = sort(candidateVertices.subList(1, candidateVertices.size()));

		for (var assignment : assignments) {
			logger.trace("go next. vertex@{} = {}, assignment = {}", vertex.getVertexID(),
					vertex.getPositionBeforeFolding(),
					assignment);
			apply(assignment);
			logger.trace("edges: {}", vertex.edgeStream().map(OriEdge::getType).toList());

			enumerateImpl(origamiModel, nextCandidateVertices);

			logger.trace("get back. vertex@{} = {}, assignment = {}", vertex.getVertexID(),
					vertex.getPositionBeforeFolding(),
					assignment);
			apply(originalAssignment);
			logger.trace("edges: {}", vertex.edgeStream().map(OriEdge::getType).toList());

		}

	}

	private List<OriVertex> sort(final List<OriVertex> vertices) {
		return vertices;

		// maybe this sort avoids the worst case.
//		return vertices.stream()
//				.sorted(Comparator.comparing(OriVertex::countUnassignedEdges))
//				.toList();
	}

	private Map<List<OriVertex>, OriLine.Type> toAssignmentMap(final OriVertex vertex) {
		return vertex.edgeStream()
				.collect(Collectors.toMap(this::edgeToKey, edge -> OriLine.Type.fromInt(edge.getType())));
	}

	private List<OriVertex> edgeToKey(final OriEdge edge) {
		return Stream.of(edge.getStartVertex(), edge.getEndVertex()).sorted().toList();
	}

	private void apply(final Map<List<OriVertex>, OriLine.Type> assignment) {
		logger.trace("apply assignment");
		assignment.forEach(this::setType);
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

		assignmentCallCount++;

		logger.trace("createAssignments(): vertex={}, edgeIndex={}", vertex, edgeIndex);

		var edgeCount = vertex.edgeCount();

		if (edgeIndex == edgeCount) {
			if (!foldability.holds(vertex)) {
				logger.trace("edges: {}", vertex.edgeStream().map(OriEdge::getType).toList());
				logger.trace("return empty assignments. ({})", vertex);
				return List.of();
			}

			var assignment = List.of(toAssignmentMap(vertex));
			logger.trace("return {}", assignment);
			return assignment;
		}
		var edge = vertex.getEdge(edgeIndex);

		if (!edge.isUnassigned()) {
			if (vertex.isInsideOfPaper() && vertex.edgeCount() >= 4) {
				// big-little-big lemma
				var prevAngle = OriGeomUtil.getAngleDifference(vertex, edgeIndex - 2);
				var angle = OriGeomUtil.getAngleDifference(vertex, edgeIndex - 1);
				var nextAngle = OriGeomUtil.getAngleDifference(vertex, edgeIndex);
				if (prevAngle > angle + MathUtil.angleRadianEps()
						&& nextAngle > angle + MathUtil.angleRadianEps()) {
					if (edge.getType() == vertex.getEdge(edgeIndex - 1).getType()) {
						return List.of();
					}
				}
			}
			return createAssignments(vertex, edgeIndex + 1, mountainCount, valleyCount);
		}

		var types = List.of(OriLine.Type.MOUNTAIN, OriLine.Type.VALLEY);

		var assignments = new ArrayList<Map<List<OriVertex>, OriLine.Type>>();

		for (var type : types) {
			if (vertex.isInsideOfPaper()) {
				// pruning by Maekawa's theorem
				if (type == OriLine.Type.MOUNTAIN) {
					if (mountainCount >= edgeCount / 2 + 1) {
						continue;
					}
				}
				if (type == OriLine.Type.VALLEY) {
					if (valleyCount >= edgeCount / 2 + 1) {
						continue;
					}
				}
			}

			var nextMountainCount = type == OriLine.Type.MOUNTAIN ? mountainCount + 1 : mountainCount;
			var nextValleyCount = type == OriLine.Type.VALLEY ? valleyCount + 1 : valleyCount;

			logger.trace("make assignment");
			setType(edgeToKey(edge), type);

			assignments.addAll(createAssignments(vertex, edgeIndex + 1, nextMountainCount, nextValleyCount));

			logger.trace("make unassignment");
			setType(edgeToKey(edge), OriLine.Type.UNASSIGNED);
		}

		return assignments;
	}

}
