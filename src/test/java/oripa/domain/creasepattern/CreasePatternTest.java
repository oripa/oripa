package oripa.domain.creasepattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

import oripa.geom.RectangleDomain;
import oripa.value.OriLine;

public class CreasePatternTest {

	@Test
	public void testIteratorRemove() {
		var lines = List.of(
				new OriLine(0.0, 0.0, 100.0, 100.0, OriLine.Type.MOUNTAIN),
				new OriLine(10.0, 10.0, 100.0, 100.0, OriLine.Type.MOUNTAIN),
				new OriLine(20.0, 20.0, 100.0, 100.0, OriLine.Type.MOUNTAIN));

		var domain = new RectangleDomain(lines);
		CreasePattern cp = new CreasePattern(domain);
		cp.addAll(lines);

		for (Iterator<OriLine> iter = cp.iterator(); iter.hasNext();) {
			OriLine oriLine = iter.next();
			iter.remove();
		}
		assertTrue(cp.isEmpty());
	}

}
