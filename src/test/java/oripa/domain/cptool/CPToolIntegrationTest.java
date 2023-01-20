package oripa.domain.cptool;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.value.OriLine;
import oripa.value.OriLine.Type;
import oripa.value.OriPoint;

class CPToolIntegrationTest {
	private static final Logger logger = LoggerFactory.getLogger(CPToolIntegrationTest.class);

	@Test
	void addedLineCanBeDeletedAfterTypeChange() {
		var creasePattern = new CreasePatternFactory().createCreasePattern(400);
		var line = new OriLine(new OriPoint(0, 0), new OriPoint(100, 0), Type.MOUNTAIN);
		var changedLine = new OriLine(new OriPoint(0, 0), new OriPoint(100, 0), Type.VALLEY);

		final double eps = 1e-5;

		var painter = new Painter(creasePattern, eps);

		painter.addLine(line);
		logger.debug("CP = {}", creasePattern);
		assertTrue(creasePattern.contains(line));

		painter.alterLineType(line, TypeForChange.EMPTY, TypeForChange.FLIP);
		logger.debug("CP = {}", creasePattern);
		assertTrue(creasePattern.contains(changedLine));

		painter.removeLine(changedLine);

		assertEquals(4, creasePattern.size());

	}

}
