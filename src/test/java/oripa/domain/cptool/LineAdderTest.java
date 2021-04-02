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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
class LineAdderTest {

	/**
	 * Test method for
	 * {@link oripa.domain.cptool.LineAdder#addLine(oripa.value.OriLine, java.util.Collection)}.
	 */
	@Test
	void testAddLine() {
		var creasePattern = new ArrayList<OriLine>(List.of(
				new OriLine(0, 0, 100, 0, OriLine.Type.MOUNTAIN),
				new OriLine(0, 50, 100, 50, OriLine.Type.AUX)));

		var line = new OriLine(20, 50, 20, -10, OriLine.Type.VALLEY);

		var adder = new LineAdder();

		adder.addLine(line, creasePattern);

		assertEquals(6, creasePattern.size());
	}

	/**
	 * Test method for
	 * {@link oripa.domain.cptool.LineAdder#addAll(java.util.Collection, java.util.Collection)}.
	 */
	@Test
	void testAddAll() {
		var creasePattern = new ArrayList<OriLine>(List.of(
				new OriLine(0, 0, 100, 0, OriLine.Type.MOUNTAIN),
				new OriLine(0, 50, 100, 50, OriLine.Type.AUX)));

		var line1 = new OriLine(20, 50, 20, -10, OriLine.Type.VALLEY);
		var line2 = new OriLine(40, 50, 40, 0, OriLine.Type.VALLEY);

		var adder = new LineAdder();

		adder.addAll(List.of(line1, line2), creasePattern);

		assertEquals(9, creasePattern.size());
		assertTypeCount(3, creasePattern, OriLine.Type.VALLEY);
		assertTypeCount(3, creasePattern, OriLine.Type.MOUNTAIN);
		assertTypeCount(3, creasePattern, OriLine.Type.AUX);
	}

	void assertTypeCount(final long expectedCount, final Collection<OriLine> creasePattern,
			final OriLine.Type type) {
		assertEquals(expectedCount,
				creasePattern.stream().filter(line -> line.getType() == type).count());
	}

}
