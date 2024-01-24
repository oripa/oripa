package oripa.domain.creasepattern;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

import oripa.geom.RectangleDomain;
import oripa.value.OriLine;

public class CreasePatternTest {

	@Test
	public void testIteratorRemove_LinesShareP1() {
		var lines = List.of(
				new OriLine(0.0, 0.0, 100.0, 100.0, OriLine.Type.MOUNTAIN),
				new OriLine(10.0, 10.0, 100.0, 100.0, OriLine.Type.MOUNTAIN),
				new OriLine(20.0, 20.0, 100.0, 100.0, OriLine.Type.MOUNTAIN));

		var domain = RectangleDomain.createFromSegments(lines);
		CreasePatternImpl cp = new CreasePatternImpl(domain);
		cp.addAll(lines);

		int lineCount = lines.size();
		for (Iterator<OriLine> iter = cp.iterator(); iter.hasNext();) {
			OriLine oriLine = iter.next();
			assertEquals(1, cp.getVerticesAround(oriLine.getP0()).size());
			assertEquals(1, cp.getVerticesAround(oriLine.getP1()).size());

			iter.remove();
			lineCount--;
			assertEquals(0, cp.getVerticesAround(oriLine.getP0()).size());
			if (lineCount == 0) {
				assertEquals(0, cp.getVerticesAround(oriLine.getP1()).size());
			} else {
				assertEquals(1, cp.getVerticesAround(oriLine.getP1()).size());
			}
		}
		assertTrue(cp.isEmpty());
	}

}
