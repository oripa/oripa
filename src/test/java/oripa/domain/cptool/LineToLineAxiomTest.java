package oripa.domain.cptool;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import oripa.geom.GeomUtil;
import oripa.geom.Segment;
import oripa.test.util.AssertionUtil;
import oripa.util.MathUtil;
import oripa.vecmath.Vector2d;

class LineToLineAxiomTest {
	double EPS = 1e-5;

	@Test
	void testCreateFoldLinesParallel() {
		var s0 = new Segment(new Vector2d(-100, 100), new Vector2d(100, 100));
		var s1 = new Segment(new Vector2d(-50, 0), new Vector2d(0, 0));

		var line = new LineToLineAxiom().createFoldLines(s0, s1, EPS).get(0);

		var dir = line.dir;

		assertEquals(1.0, dir.getX(), EPS);
		assertEquals(0, dir.getY(), EPS);

		assertEquals(0, GeomUtil.distancePointToLine(new Vector2d(0, 50), line), EPS);
	}

	@Test
	void testCreateFoldLinesIndependent() {
		var s0 = new Segment(new Vector2d(50, 50), new Vector2d(100, 100));
		var s1 = new Segment(new Vector2d(0, 0), new Vector2d(100, 0));

		var line = new LineToLineAxiom().createFoldLines(s0, s1, EPS).get(0);

		var dir = line.dir;

		assertEquals(Math.cos(Math.PI / 8), dir.getX(), EPS);
		assertEquals(Math.sin(Math.PI / 8), dir.getY(), EPS);

		assertEquals(0, GeomUtil.distancePointToLine(new Vector2d(0, 0), line), EPS);
	}

	@Test
	void testCreateFoldLinesShareEndPoint() {
		var s0 = new Segment(new Vector2d(0, 0), new Vector2d(100, 100));
		var s1 = new Segment(new Vector2d(0, 0), new Vector2d(100, 0));

		var line = new LineToLineAxiom().createFoldLines(s0, s1, EPS).get(0);

		var dir = line.dir;

		assertEquals(Math.cos(Math.PI / 8), dir.getX(), EPS);
		assertEquals(Math.sin(Math.PI / 8), dir.getY(), EPS);

		assertEquals(0, GeomUtil.distancePointToLine(new Vector2d(0, 0), line), EPS);
	}

	@Test
	void testCreateFoldLinesCross() {
		var s0 = new Segment(new Vector2d(-100, -100), new Vector2d(100, 100));
		var s1 = new Segment(new Vector2d(-100, 0), new Vector2d(100, 0));

		var lines = new LineToLineAxiom().createFoldLines(s0, s1, EPS);

		assertEquals(2, lines.size());

		var angles = lines.stream()
				.map(line -> line.dir.angle(new Vector2d(1, 0)))
				.collect(Collectors.toList());

		AssertionUtil.assertAnyMatch(Math.PI / 8, angles,
				(expected, actual) -> MathUtil.areEqual(expected, actual, EPS));

		AssertionUtil.assertAnyMatch(5 * Math.PI / 8, angles,
				(expected, actual) -> MathUtil.areEqual(expected, actual, EPS));
	}

}
