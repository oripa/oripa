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

class SnapPointFactoryTest {
	SnapPointFactory factory = new SnapPointFactory();

	@Test
	void test() {
		var creases = List.of(
				// cross
				new OriLine(1, 1, 1, -1, OriLine.Type.VALLEY),
				// overlap
				new OriLine(2, 0, 3, 0, OriLine.Type.VALLEY));

		var creasePattern = new CreasePatternFactory().createCreasePattern(creases);

		var line = new OriLine(-1, 0, 10, 0, OriLine.Type.MOUNTAIN);

		var contextMock = mock(PaintContext.class);
		when(contextMock.getCreasePattern()).thenReturn(creasePattern);

		var points = factory.createSnapPoints(contextMock, line);

		var expectedPoints = List.of(new Vector2d(1, 0), new Vector2d(2, 0), new Vector2d(3, 0));

		assertEquals(expectedPoints.size(), points.size());

		expectedPoints.forEach(
				expected -> assertAnyMatch(expected, points, (p1, p2) -> GeomUtil.distance(p1, p2) < 1e-6));
	}

}
