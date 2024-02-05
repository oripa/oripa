package oripa.domain.cptool;

import static org.junit.jupiter.api.Assertions.*;

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

		var lines = new LineToLineAxiom().createFoldLines(s0, s1, EPS);

		assertEquals(1, lines.size());

		var line = lines.get(0);

		var dir = line.getDirection();

		assertEquals(1.0, dir.getX(), EPS);
		assertEquals(0, dir.getY(), EPS);

		assertEquals(0, GeomUtil.distancePointToLine(new Vector2d(0, 50), line), EPS);
	}

	@Test
	void testCreateFoldLinesIndependent() {
		var s0 = new Segment(new Vector2d(50, 50), new Vector2d(100, 100));
		var s1 = new Segment(new Vector2d(50, 0), new Vector2d(100, 0));

		var lines = new LineToLineAxiom().createFoldLines(s0, s1, EPS);

		assertEquals(1, lines.size());

		var line = lines.get(0);

		var dir = line.getDirection();

		assertEquals(Math.cos(Math.PI / 8), dir.getX(), EPS);
		assertEquals(Math.sin(Math.PI / 8), dir.getY(), EPS);

		assertEquals(0, GeomUtil.distancePointToLine(new Vector2d(0, 0), line), EPS);
	}

	@Test
	void testCreateFoldLinesShareEndPoint() {
		var s0 = new Segment(new Vector2d(0, 0), new Vector2d(100, 100));
		var s1 = new Segment(new Vector2d(0, 0), new Vector2d(100, 0));

		var lines = new LineToLineAxiom().createFoldLines(s0, s1, EPS);

		assertEquals(1, lines.size());

		var line = lines.get(0);

		var dir = line.getDirection();

		assertEquals(Math.cos(Math.PI / 8), dir.getX(), EPS);
		assertEquals(Math.sin(Math.PI / 8), dir.getY(), EPS);

		assertEquals(0, GeomUtil.distancePointToLine(new Vector2d(0, 0), line), EPS);
	}

	@Test
	void testCreateFoldLinesPotentiallyCross() {
		var s0 = new Segment(new Vector2d(-200, -200), new Vector2d(-100, -100));
		var s1 = new Segment(new Vector2d(0, -200), new Vector2d(0, 200));

		var lines = new LineToLineAxiom().createFoldLines(s0, s1, EPS);

		assertEquals(2, lines.size());

		var angles = lines.stream()
				.map(line -> MathUtil.angleOf(line.getDirection()))
				.toList();

		AssertionUtil.assertAnyMatch(7 * Math.PI / 8, angles,
				(expected, actual) -> MathUtil.areEqual(expected, actual, EPS));

		AssertionUtil.assertAnyMatch(11 * Math.PI / 8, angles,
				(expected, actual) -> MathUtil.areEqual(expected, actual, EPS));
	}

	@Test
	void testCreateFoldLinesCross() {
		var s0 = new Segment(new Vector2d(-100, -100), new Vector2d(100, 100));
		var s1 = new Segment(new Vector2d(-100, 0), new Vector2d(100, 0));

		var lines = new LineToLineAxiom().createFoldLines(s0, s1, EPS);

		assertEquals(2, lines.size());

		var angles = lines.stream()
				.map(line -> MathUtil.angleOf(line.getDirection()))
				.toList();

		AssertionUtil.assertAnyMatch(Math.PI / 8, angles,
				(expected, actual) -> MathUtil.areEqual(expected, actual, EPS));

		AssertionUtil.assertAnyMatch(5 * Math.PI / 8, angles,
				(expected, actual) -> MathUtil.areEqual(expected, actual, EPS));
	}

}
