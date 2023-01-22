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

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
class OverlappingLineExtractorTest {
	private static final Logger logger = LoggerFactory.getLogger(OverlappingLineExtractorTest.class);

	private OverlappingLineExtractor extractor;

	static final double EPS = 1e-5;

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

		var overlaps = extractor.extract(lines, EPS);

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

		var overlaps = extractor.extract(lines, EPS);

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

		var overlaps = extractor.extract(lines, overlap1, EPS);

		assertEquals(2, overlaps.size());

		assertTrue(overlaps.contains(overlap1));
		assertTrue(overlaps.contains(overlap2));
	}

	@Test
	void should_detect_overlap_for_two_partially_overlapping_lines() {
		var overlap1 = new OriLine(0, 0, 200, 0, OriLine.Type.MOUNTAIN);
		var overlap2 = new OriLine(0, 0, 100, 0, OriLine.Type.MOUNTAIN);
		var lines = List.of(overlap1, overlap2);

		var overlaps = extractor.extract(lines, EPS);

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
				.extractOverlapsGroupedBySupport(List.of(overlap1_1, overlap1_2, overlap1_3, overlap2_1, overlap2_2),
						EPS);

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

		var overlaps = extractor.extract(lines, EPS);

		assertEquals(0, overlaps.size());
	}
}
