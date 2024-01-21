package oripa.domain.paint.core;

import static org.junit.jupiter.api.Assertions.*;
import static oripa.test.util.AssertionUtil.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.geom.GeomUtil;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

class SnapPointFactoryTest {
	SnapPointFactory factory = new SnapPointFactory();

	static final double EPS = 1e-6;

	@Test
	void test() {
		var creases = List.of(
				// cross
				new OriLine(1, 1, 1, -1, OriLine.Type.VALLEY),
				// overlap
				new OriLine(2, 0, 3, 0, OriLine.Type.VALLEY));

		var creasePattern = new CreasePatternFactory().createCreasePattern(creases);

		var line = new OriLine(-1, 0, 10, 0, OriLine.Type.MOUNTAIN);

		var points = factory.createSnapPoints(creasePattern, line, EPS);

		var expectedPoints = List.of(new Vector2d(1, 0), new Vector2d(2, 0), new Vector2d(3, 0));

		assertEquals(expectedPoints.size(), points.size());

		expectedPoints.forEach(
				expected -> assertAnyMatch(expected, points, (p1, p2) -> GeomUtil.areEqual(p1, p2, EPS)));
	}

}
