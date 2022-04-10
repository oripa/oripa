/*
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package oripa.domain.cptool;

import static org.junit.jupiter.api.Assertions.*;
import static oripa.domain.cptool.OverlappingLineSplitter.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.util.StopWatch;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
class OverlappingLineExtractorTest {
	private static final Logger logger = LoggerFactory.getLogger(OverlappingLineExtractorTest.class);

	private OverlappingLineExtractor extractor;

	@BeforeEach
	void setUp() {
		extractor = new OverlappingLineExtractor();
	}

	/**
	 * Test method for
	 * {@link oripa.domain.cptool.OverlappingLineExtractor#extract(java.util.Collection)}.
	 */
	@Test
	void testExtract_all() {
		var overlap1 = new OriLine(0, 0, 10, 10, OriLine.Type.MOUNTAIN);
		var overlap2 = new OriLine(0, 0, 50, 50, OriLine.Type.MOUNTAIN);
		var lines = List.of(overlap1, overlap2,
				new OriLine(10, 10, 20, 0, OriLine.Type.MOUNTAIN));

		var overlaps = extractor.extract(lines);

		assertEquals(2, overlaps.size());

		assertTrue(overlaps.contains(overlap1));
		assertTrue(overlaps.contains(overlap2));
	}

	@Test
	void testExtract_all_verticals() {
		var overlap1 = new OriLine(0, 0, 0, 10, OriLine.Type.MOUNTAIN);
		var overlap2 = new OriLine(0, 5, 0, 50, OriLine.Type.MOUNTAIN);
		var lines = List.of(overlap1, overlap2,
				new OriLine(0, 55, 0, 60, OriLine.Type.MOUNTAIN));

		var overlaps = extractor.extract(lines);

		assertEquals(2, overlaps.size());

		assertTrue(overlaps.contains(overlap1));
		assertTrue(overlaps.contains(overlap2));
	}

	/**
	 * Test method for
	 * {@link oripa.domain.cptool.OverlappingLineExtractor#extract(java.util.Collection, oripa.value.OriLine)}.
	 */
	@Test
	void testExtract_ofSpecifiedLine() {
		var overlap1 = new OriLine(0, 0, 10, 10, OriLine.Type.MOUNTAIN);
		var overlap2 = new OriLine(0, 0, 50, 50, OriLine.Type.MOUNTAIN);
		var lines = List.of(overlap1, overlap2,
				new OriLine(10, 10, 20, 0, OriLine.Type.MOUNTAIN));

		var overlaps = extractor.extract(lines, overlap1);

		assertEquals(2, overlaps.size());

		assertTrue(overlaps.contains(overlap1));
		assertTrue(overlaps.contains(overlap2));
	}

	@Test
	void should_detect_overlap_for_two_partially_overlapping_lines() {
		var overlap1 = new OriLine(0, 0, 200, 0, OriLine.Type.MOUNTAIN);
		var overlap2 = new OriLine(0, 0, 100, 0, OriLine.Type.MOUNTAIN);
		var lines = List.of(overlap1, overlap2);

		var overlaps = extractor.extract(lines);

		assertEquals(2, overlaps.size());

		assertTrue(overlaps.contains(overlap1));
		assertTrue(overlaps.contains(overlap2));
	}

	@Test
	void testExtractOverlapsGroupedBySupport() {
		var overlap1_1 = new OriLine(0, 0, 100, 0, OriLine.Type.MOUNTAIN);
		var overlap1_2 = new OriLine(0, 0, 100, 0, OriLine.Type.MOUNTAIN);
		var overlap1_3 = new OriLine(100, 0, 200, 0, OriLine.Type.MOUNTAIN);

		var overlap2_1 = new OriLine(0, 0, 0, 100, OriLine.Type.MOUNTAIN);
		var overlap2_2 = new OriLine(0, 0, 0, 100, OriLine.Type.MOUNTAIN);

		var overlapGroups = extractor
				.extractOverlapsGroupedBySupport(List.of(overlap1_1, overlap1_2, overlap1_3, overlap2_1, overlap2_2));

		assertEquals(2, overlapGroups.size());

		for (var overlaps : overlapGroups) {
			if (overlaps.contains(overlap1_1)) {
				assertEquals(2, overlaps.size());
				assertTrue(overlaps.contains(overlap1_2));
				assertFalse(overlaps.contains(overlap1_3));
			}
			if (overlaps.contains(overlap2_1)) {
				assertTrue(overlaps.contains(overlap2_2));
			}
		}
	}

	@Test
	void should_not_detect_overlap_for_two_disjoint_segments_on_the_same_line() {
		var overlap1 = new OriLine(-200, 0, 0, 0, OriLine.Type.MOUNTAIN);
		var overlap2 = new OriLine(0, 0, 100, 0, OriLine.Type.MOUNTAIN);
		var lines = List.of(overlap1, overlap2);

		var overlaps = extractor.extract(lines);

		assertEquals(0, overlaps.size());
	}

	@Disabled
	@Test
	void simple_overlap_detection_should_be_faster() {
		int nbIterations = 1000;
		List<OriLine> existingLines = generateRandomExistingLines(nbIterations);

		OriLine newLine = new OriLine(20, 20, 40, 40, OriLine.Type.MOUNTAIN);

		logger.info("starting new way");
		StopWatch stopWatchDetectOverlap = new StopWatch(true);
		existingLines.forEach(existingLine -> splitLinesIfOverlap(existingLine, newLine));
		long stopWatchDetectOverlapMilliSec = stopWatchDetectOverlap.getMilliSec();

		logger.info("starting old way");
		StopWatch stopWatchOverlappingLineExtractor = new StopWatch(true);
		existingLines.add(newLine);
		extractor.extract(existingLines);
		long stopWatchOverlappingLineExtractorMilliSec = stopWatchOverlappingLineExtractor.getMilliSec();

		assertTrue(stopWatchOverlappingLineExtractorMilliSec > stopWatchDetectOverlapMilliSec);
		logger.info("{} iterations in {}ms now vs {}ms before", nbIterations, stopWatchDetectOverlapMilliSec,
				stopWatchOverlappingLineExtractorMilliSec);
	}

	private List<OriLine> generateRandomExistingLines(final int nbIterations) {
		List<OriLine> existingLines = new ArrayList<>();
		double x0 = ThreadLocalRandom.current().nextInt(-200, 200 + 1);
		double y0 = ThreadLocalRandom.current().nextInt(-200, 200 + 1);
		double x1 = ThreadLocalRandom.current().nextInt(-200, 200 + 1);
		double y1 = ThreadLocalRandom.current().nextInt(-200, 200 + 1);

		for (int i = 0; i < nbIterations; i++) {
			existingLines.add(new OriLine(x0, y0, x1, y1, OriLine.Type.VALLEY));
		}
		return existingLines;
	}

}
