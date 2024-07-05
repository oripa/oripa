package oripa.domain.cptool;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import oripa.geom.GeomUtil;
import oripa.geom.Segment;
import oripa.test.util.AssertionUtil;
import oripa.vecmath.Vector2d;

class PointToLineThroughPointAxiomTest {

	@Test
	void test() {
		double sqrt2 = Math.sqrt(2);
		var p = new Vector2d(sqrt2, 1);
		var c = new Vector2d(0, 1);
		var s = new Segment(-10, 0, 10, 0);

		var axiom = new PointToLineThroughPointAxiom();

		var lines = axiom.createFoldLine(p, c, s, 1e-6);

		assertEquals(2, lines.size());

		var foldedPoints = lines.stream()
				.map(line -> GeomUtil.getSymmetricPoint(
						p, line.getPoint(), line.getPoint().add(line.getDirection())))
				.toList();

		AssertionUtil.assertAnyMatch(new Vector2d(1, 0), foldedPoints, (a, b) -> a.distance(b) < 1e-6);
		AssertionUtil.assertAnyMatch(new Vector2d(-1, 0), foldedPoints, (a, b) -> a.distance(b) < 1e-6);
	}

}
