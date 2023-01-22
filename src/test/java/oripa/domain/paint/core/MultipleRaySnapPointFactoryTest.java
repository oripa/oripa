package oripa.domain.paint.core;

import static org.junit.jupiter.api.Assertions.*;
import static oripa.test.util.AssertionUtil.*;

import java.util.List;

import javax.vecmath.Vector2d;

import org.junit.jupiter.api.Test;

import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.geom.GeomUtil;
import oripa.value.OriLine;

class MultipleRaySnapPointFactoryTest {

	MultipleRaySnapPointFactory factory = new MultipleRaySnapPointFactory();

	static final double POINT_EPS = 1e-5;

	@Test
	void test_noOverlap() {
		var creases = List.of(
				new OriLine(-1, -1, -1, 1, OriLine.Type.CUT),
				new OriLine(-1, 1, 1, 1, OriLine.Type.CUT),
				new OriLine(1, 1, 1, -1, OriLine.Type.CUT),
				new OriLine(1, -1, -1, -1, OriLine.Type.CUT),
				new OriLine(0, 0, 0, 1, OriLine.Type.MOUNTAIN));

		var creasePattern = new CreasePatternFactory().createCreasePattern(creases);

		var sourcePoint = new Vector2d(0, 0);

		var angles = List.of(0.0, Math.PI);

		var points = factory.createSnapPoints(creasePattern, sourcePoint, angles, POINT_EPS);

		var expectedPoints = List.of(new Vector2d(1, 0), new Vector2d(-1, 0));

		assertEquals(expectedPoints.size(), points.size());
		expectedPoints
				.forEach(expected -> assertAnyMatch(expected, points, (p1, p2) -> GeomUtil.distance(p1, p2) < 1e-6));
	}

	@Test
	void test_overlap() {
		var creases = List.of(
				new OriLine(-1, -1, -1, 1, OriLine.Type.CUT),
				new OriLine(-1, 1, 1, 1, OriLine.Type.CUT),
				new OriLine(1, 1, 1, -1, OriLine.Type.CUT),
				new OriLine(1, -1, -1, -1, OriLine.Type.CUT),
				new OriLine(0, 0, -0.5, 0, OriLine.Type.MOUNTAIN),
				new OriLine(0.25, 0, 0.5, 0, OriLine.Type.MOUNTAIN));

		var creasePattern = new CreasePatternFactory().createCreasePattern(creases);

		var sourcePoint = new Vector2d(0, 0);

		var angles = List.of(0.0);

		var points = factory.createSnapPoints(creasePattern, sourcePoint, angles, POINT_EPS);

		var expectedPoints = List.of(new Vector2d(1, 0), new Vector2d(0.25, 0), new Vector2d(0.5, 0));

		assertEquals(expectedPoints.size(), points.size());
		expectedPoints
				.forEach(expected -> assertAnyMatch(expected, points, (p1, p2) -> GeomUtil.distance(p1, p2) < 1e-6));
	}

}
