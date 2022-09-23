package oripa.domain.suggestion;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import javax.vecmath.Vector2d;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.fold.halfedge.OriEdge;
import oripa.domain.fold.halfedge.OriVertex;
import oripa.value.OriLine;

class KawasakiTheoremSuggesterTest {
	private final double UNIT_ANGLE = Math.PI / 8;

	private static Logger logger = LoggerFactory.getLogger(KawasakiTheoremSuggesterTest.class);

	@Test
	void test_3InputLines() {
		var vertex = new OriVertex(0, 0);

		var edges = List.of(
				createEdge(vertex, 1, OriLine.Type.MOUNTAIN),
				createEdge(vertex, 8, OriLine.Type.MOUNTAIN),
				createEdge(vertex, 13, OriLine.Type.MOUNTAIN));

		edges.forEach(vertex::addEdge);

		var suggester = new KawasakiTheoremSuggester();

		var suggestions = suggester.suggest(vertex);

		var expectedLines = List.of(
				createLine(vertex, 4, OriLine.Type.VALLEY),
				createLine(vertex, 12, OriLine.Type.VALLEY),
				createLine(vertex, 14, OriLine.Type.VALLEY));

		assertEquals(3, expectedLines.size());

		expectedLines.forEach(expected -> assertTrue(
				suggestions.stream()
						.anyMatch(line -> angleEquals(line, expected, vertex))));
	}

	private boolean angleEquals(final double a1, final OriLine l2, final OriVertex center) {

		var angle1 = normalizeAngle(a1);
		var angle2 = normalizeAngle(getAngle(l2, center));

		logger.debug("angle1: {}, angle2: {}", angle1, angle2);

		return Math.abs(angle1 - angle2) < 1e-5;
	}

	private double normalizeAngle(final double angle) {
		return (angle + 2 * Math.PI) % (2 * Math.PI);
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

		return Math.atan2(dir.getY(), dir.getX());
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
