package oripa.domain.suggestion;

import static org.junit.jupiter.api.Assertions.*;
import static oripa.test.util.AssertionUtil.*;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import oripa.domain.fold.halfedge.OriEdge;
import oripa.domain.fold.halfedge.OriVertex;
import oripa.util.MathUtil;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

class KawasakiTheoremSuggesterTest {
	private final double UNIT_ANGLE = Math.PI / 8;

	@Test
	void test_3InputLines() {
		var vertex = new OriVertex(0, 0);

		var edges = List.of(
				createEdge(vertex, 1, OriLine.Type.MOUNTAIN),
				createEdge(vertex, 8, OriLine.Type.MOUNTAIN),
				createEdge(vertex, 13, OriLine.Type.MOUNTAIN));

		var expectedLineAngles = List.of(
				4 * UNIT_ANGLE,
				12 * UNIT_ANGLE,
				14 * UNIT_ANGLE);

		doTest(expectedLineAngles, vertex, edges);

	}

	@Test
	void test_3InputLines_symmetric() {
		var vertex = new OriVertex(0, 0);

		var edges = List.of(
				createEdge(vertex, 2, OriLine.Type.MOUNTAIN),
				createEdge(vertex, 8, OriLine.Type.MOUNTAIN),
				createEdge(vertex, 14, OriLine.Type.MOUNTAIN));

		var expectedLineAngles = List.of(
				0 * UNIT_ANGLE,
				4 * UNIT_ANGLE,
				12 * UNIT_ANGLE);

		doTest(expectedLineAngles, vertex, edges);

	}

	@Test
	void test_3CloseInputLine() {
		var vertex = new OriVertex(0, 0);

		var edges = List.of(
				createEdge(vertex, 0, OriLine.Type.MOUNTAIN),
				createEdge(vertex, 12, OriLine.Type.VALLEY),
				createEdge(vertex, 14, OriLine.Type.MOUNTAIN));

		var expectedLines = List.of(
				6 * UNIT_ANGLE);

		doTest(expectedLines, vertex, edges);

	}

	private void doTest(final Collection<Double> expectedLineAngles, final OriVertex vertex,
			final Collection<OriEdge> edges) {
		edges.forEach(vertex::addEdge);

		var suggester = new KawasakiTheoremSuggester();

		var suggestions = suggester.suggest(vertex);

		assertEquals(expectedLineAngles.size(), suggestions.size());

		expectedLineAngles.forEach(expected -> assertAnyMatch(
				expected, suggestions, this::angleEquals));
	}

	private boolean angleEquals(final double a1, final double a2) {
		double diff = Math.abs(a1 - a2);

		return diff < MathUtil.angleRadianEps() || MathUtil.areRadianEqual(diff, 2 * Math.PI);
	}

	private OriEdge createEdge(final OriVertex start, final int angle, final OriLine.Type type) {
		return new OriEdge(
				start,
				new OriVertex(
						createPoint(start, angle)),
				type.toInt());
	}

	private Vector2d createPoint(final OriVertex start, final int angle) {
		return start.getPosition().add(Vector2d.unitVector(UNIT_ANGLE * angle));
	}
}
