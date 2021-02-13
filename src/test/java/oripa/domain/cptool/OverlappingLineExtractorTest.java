/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package oripa.domain.cptool;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
class OverlappingLineExtractorTest {

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

		var extractor = new OverlappingLineExtractor();
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

		var extractor = new OverlappingLineExtractor();
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

		var extractor = new OverlappingLineExtractor();
		var overlaps = extractor.extract(lines, overlap1);

		assertEquals(1, overlaps.size());

		assertTrue(overlaps.contains(overlap2));
	}

}
