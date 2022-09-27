package oripa.domain.paint.core;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static oripa.test.util.AssertionUtil.*;

import java.util.List;

import javax.vecmath.Vector2d;

import org.junit.jupiter.api.Test;

import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.domain.paint.PaintContext;
import oripa.geom.GeomUtil;
import oripa.value.OriLine;

class MultipleRaySnapPointFactoryTest {

	MultipleRaySnapPointFactory factory = new MultipleRaySnapPointFactory();

	@Test
	void test() {
		var creases = List.of(
				new OriLine(-1, -1, -1, 1, OriLine.Type.CUT),
				new OriLine(-1, 1, 1, 1, OriLine.Type.CUT),
				new OriLine(1, 1, 1, -1, OriLine.Type.CUT),
				new OriLine(1, -1, -1, -1, OriLine.Type.CUT),
				new OriLine(0, 0, 0, 1, OriLine.Type.MOUNTAIN));

		var creasePattern = new CreasePatternFactory().createCreasePattern(creases);
		var contextMock = mock(PaintContext.class);

		when(contextMock.getCreasePattern()).thenReturn(creasePattern);

		var sourcePoint = new Vector2d(0, 0);

		var angles = List.of(0.0, Math.PI);

		var points = factory.createSnapPoints(contextMock, sourcePoint, angles);

		var expectedPoints = List.of(new Vector2d(1, 0), new Vector2d(-1, 0));

		assertEquals(expectedPoints.size(), points.size());
		expectedPoints
				.forEach(expected -> assertAnyMatch(expected, points, (p1, p2) -> GeomUtil.distance(p1, p2) < 1e-6));
	}

}
