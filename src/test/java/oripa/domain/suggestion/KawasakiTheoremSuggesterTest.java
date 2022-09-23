package oripa.domain.suggestion;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.List;

import javax.vecmath.Vector2d;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.fold.halfedge.OriEdge;
import oripa.domain.fold.halfedge.OriVertex;
import oripa.util.MathUtil;
import oripa.value.OriLine;

class KawasakiTheoremSuggesterTest {
	private final double UNIT_ANGLE = Math.PI / 8;

	private static Logger logger = LoggerFactory.getLogger(KawasakiTheoremSuggesterTest.class);

	@Test
	void test_3InputLines() {
		logger.debug("test 3 lines");

		var vertex = new OriVertex(0, 0);

		var edges = List.of(
				createEdge(vertex, 1, OriLine.Type.MOUNTAIN),
				createEdge(vertex, 8, OriLine.Type.MOUNTAIN),
				createEdge(vertex, 13, OriLine.Type.MOUNTAIN));

		var expectedLines = List.of(
				createLine(vertex, 4, OriLine.Type.VALLEY),
				createLine(vertex, 12, OriLine.Type.VALLEY),
				createLine(vertex, 14, OriLine.Type.VALLEY));

		doTest(expectedLines, vertex, edges);

	}

	@Test
	void test_3InputLines_symmetric() {
		logger.debug("test 3 input lines symmetric");
		var vertex = new OriVertex(0, 0);

		var edges = List.of(
				createEdge(vertex, 2, OriLine.Type.MOUNTAIN),
				createEdge(vertex, 8, OriLine.Type.MOUNTAIN),
				createEdge(vertex, 14, OriLine.Type.MOUNTAIN));

		var expectedLines = List.of(
				createLine(vertex, 0, OriLine.Type.VALLEY),
				createLine(vertex, 4, OriLine.Type.VALLEY),
				createLine(vertex, 12, OriLine.Type.VALLEY));

		doTest(expectedLines, vertex, edges);

	}

	@Test
	void test_3CloseInputLine() {
		logger.debug("test 3 close lines");

		var vertex = new OriVertex(0, 0);

		var edges = List.of(
				createEdge(vertex, 0, OriLine.Type.MOUNTAIN),
				createEdge(vertex, 12, OriLine.Type.VALLEY),
				createEdge(vertex, 14, OriLine.Type.MOUNTAIN));

		var expectedLines = List.of(
				createLine(vertex, 6, OriLine.Type.VALLEY));

		doTest(expectedLines, vertex, edges);

	}

	private void doTest(final Collection<OriLine> expectedLines, final OriVertex vertex,
			final Collection<OriEdge> edges) {
		edges.forEach(vertex::addEdge);

		var suggester = new KawasakiTheoremSuggester();

		var suggestions = suggester.suggest(vertex);

		assertEquals(expectedLines.size(), suggestions.size());

		expectedLines.forEach(expected -> assertTrue(
				suggestions.stream()
						.anyMatch(lineAngle -> angleEquals(lineAngle, expected, vertex))));

	}

	private boolean angleEquals(final double a1, final OriLine l2, final OriVertex center) {

		var angle1 = MathUtil.normalizeAngle(a1);
		var angle2 = MathUtil.normalizeAngle(getAngle(l2, center));

		logger.debug("angle1: {}, angle2: {}", angle1, angle2);

		return Math.abs(angle1 - angle2) < 1e-5;
	}

	private double getAngle(final OriLine line, final OriVertex center) {
		Vector2d dir;
		if (line.getP0().epsilonEquals(center.getPosition(), 1e-5)) {
			dir = line.getP1();
			dir.sub(line.getP0());
		} else {
			dir = line.getP0();
			dir.sub(line.getP1());
		}

		return MathUtil.normalizeAngle(Math.atan2(dir.getY(), dir.getX()));
	}

	private OriEdge createEdge(final OriVertex start, final int angle, final OriLine.Type type) {
		return new OriEdge(
				start,
				new OriVertex(
						createPoint(start, angle)),
				type.toInt());
	}

	private OriLine createLine(final OriVertex start, final int angle, final OriLine.Type type) {
		return new OriLine(start.getPosition(), createPoint(start, angle), type);
	}

	private Vector2d createPoint(final OriVertex start, final int angle) {
		return new Vector2d(
				start.getPosition().getX() + Math.cos(UNIT_ANGLE * angle),
				start.getPosition().getY() + Math.sin(UNIT_ANGLE * angle));
	}
}
