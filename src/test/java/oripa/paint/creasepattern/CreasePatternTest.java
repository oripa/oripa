package oripa.paint.creasepattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import oripa.domain.creasepattern.impl.CreasePattern;
import oripa.value.OriLine;

public class CreasePatternTest {

	@Test
	public void testIteratorRemove() {
		final int paperSize = 400;
		CreasePattern cp = new CreasePattern(paperSize);

		cp.add(new OriLine(0.0, 0.0, 100.0, 100.0, OriLine.TYPE_RIDGE));
		cp.add(new OriLine(10.0, 10.0, 100.0, 100.0, OriLine.TYPE_RIDGE));
		cp.add(new OriLine(20.0, 20.0, 100.0, 100.0, OriLine.TYPE_RIDGE));

		for (Iterator<OriLine> iter = cp.iterator(); iter.hasNext();) {
			iter.remove();
		}
		assertTrue(cp.isEmpty());
	}

}
