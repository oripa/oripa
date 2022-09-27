package oripa.domain.suggestion;

import static org.junit.jupiter.api.Assertions.*;
import static oripa.test.util.AssertionUtil.*;

import java.util.Collection;
import java.util.List;

import javax.vecmath.Vector2d;

import org.junit.jupiter.api.Test;

import oripa.domain.fold.halfedge.OriEdge;
import oripa.domain.fold.halfedge.OriVertex;
import oripa.util.MathUtil;
import oripa.value.OriLine;

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

		var angle1 = MathUtil.normalizeAngle(a1);
		var angle2 = MathUtil.normalizeAngle(a2);

		return Math.abs(angle1 - angle2) < 1e-5;
	}

	private OriEdge createEdge(final OriVertex start, final int angle, final OriLine.Type type) {
		return new OriEdge(
				start,
				new OriVertex(
						createPoint(start, angle)),
				type.toInt());
	}

	private Vector2d createPoint(final OriVertex start, final int angle) {
		return new Vector2d(
				start.getPosition().getX() + Math.cos(UNIT_ANGLE * angle),
				start.getPosition().getY() + Math.sin(UNIT_ANGLE * angle));
	}
}
