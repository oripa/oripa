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
class LineTypeChangerTest {

	static final double EPS = 1e-5;

	/**
	 * Test method for
	 * {@link oripa.domain.cptool.LineTypeChanger#alterLineType(oripa.value.OriLine, java.util.Collection, oripa.domain.cptool.TypeForChange, oripa.domain.cptool.TypeForChange)}.
	 */
	@Test
	void testAlterLineType_changeables() {
		var changer = new LineTypeChanger();

		var lines = createChangeableLines();
		var line = findLine(lines, OriLine.Type.MOUNTAIN);
		testChangeable(changer, line, lines, TypeForChange.MOUNTAIN, TypeForChange.VALLEY);

		lines = createChangeableLines();
		line = findLine(lines, OriLine.Type.VALLEY);
		testChangeable(changer, line, lines, TypeForChange.VALLEY, TypeForChange.AUX);

		lines = createChangeableLines();
		line = findLine(lines, OriLine.Type.AUX);
		testChangeable(changer, line, lines, TypeForChange.AUX, TypeForChange.CUT);

		lines = createChangeableLines();
		line = findLine(lines, OriLine.Type.CUT);
		testChangeable(changer, line, lines, TypeForChange.CUT, TypeForChange.MOUNTAIN);
	}

	private Collection<OriLine> createChangeableLines() {
		var mountainLine = new OriLine(0, 0, 10, 0, OriLine.Type.MOUNTAIN);
		var valleyLine = new OriLine(10, 0, 20, 0, OriLine.Type.VALLEY);
		var auxLine = new OriLine(20, 0, 30, 0, OriLine.Type.AUX);
		var cutLine = new OriLine(30, 0, 40, 0, OriLine.Type.CUT);

		return new ArrayList<OriLine>(List.of(mountainLine, valleyLine, auxLine, cutLine));
	}

	private OriLine findLine(final Collection<OriLine> lines, final OriLine.Type type) {
		return lines.stream()
				.filter(l -> l.getType() == type)
				.findFirst()
				.get();
	}

	/**
	 * from mountain, valley, aux or cut to other.
	 *
	 * @param changer
	 * @param line
	 * @param lines
	 * @param from
	 * @param to
	 */
	private void testChangeable(final LineTypeChanger changer, final OriLine line,
			final Collection<OriLine> lines, final TypeForChange from, final TypeForChange to) {
		changer.alterLineType(line, lines, from, to, EPS);

		var afterLine = getLine(line, lines);

		assertEquals(to.getOriLineType(), afterLine.getType());
		assertNotNull(afterLine.getType());
	}

	private OriLine getLine(final OriLine line, final Collection<OriLine> lines) {
		return lines.stream()
				.filter(l -> l.getP0().equals(line.getP0()) && l.getP1().equals(line.getP1()))
				.findFirst()
				.get();
	}

	@Test
	void testAlterLineType_delete() {
		var changer = new LineTypeChanger();

		var lines = createChangeableLines();
		var lineCount = lines.size();
		var line = findLine(lines, OriLine.Type.MOUNTAIN);

		changer.alterLineType(line, lines, TypeForChange.EMPTY, TypeForChange.DELETE, EPS);
		assertEquals(lineCount - 1, lines.size());
		assertFalse(lines.contains(line));

	}

	@Test
	void testAlterLineType_flip() {
		var changer = new LineTypeChanger();

		var lines = createChangeableLines();
		var line = findLine(lines, OriLine.Type.MOUNTAIN);

		changer.alterLineType(line, lines, TypeForChange.EMPTY, TypeForChange.FLIP, EPS);
		assertEquals(OriLine.Type.VALLEY, getLine(line, lines).getType());

		lines = createChangeableLines();
		line = findLine(lines, OriLine.Type.VALLEY);

		changer.alterLineType(line, lines, TypeForChange.EMPTY, TypeForChange.FLIP, EPS);
		assertEquals(OriLine.Type.MOUNTAIN, getLine(line, lines).getType());

		// doesn't change if the type is aux or cut

		lines = createChangeableLines();
		line = findLine(lines, OriLine.Type.AUX);

		changer.alterLineType(line, lines, TypeForChange.EMPTY, TypeForChange.FLIP, EPS);
		assertEquals(OriLine.Type.AUX, getLine(line, lines).getType());

		lines = createChangeableLines();
		line = findLine(lines, OriLine.Type.CUT);

		changer.alterLineType(line, lines, TypeForChange.EMPTY, TypeForChange.FLIP, EPS);
		assertEquals(OriLine.Type.CUT, getLine(line, lines).getType());
	}

	/**
	 * Test method for
	 * {@link oripa.domain.cptool.LineTypeChanger#alterLineTypes(java.util.Collection, java.util.Collection, oripa.domain.cptool.TypeForChange, oripa.domain.cptool.TypeForChange)}.
	 */
	@Test
	void testAlterLineTypes_delete() {
		var changer = new LineTypeChanger();

		var lines = createChangeableLines();
		var lineCount = lines.size();
		var line1 = findLine(lines, OriLine.Type.MOUNTAIN);
		var line2 = findLine(lines, OriLine.Type.VALLEY);

		changer.alterLineTypes(List.of(line1, line2), lines, TypeForChange.EMPTY,
				TypeForChange.DELETE, EPS);
		assertEquals(lineCount - 2, lines.size());
		assertFalse(lines.contains(line1));
		assertFalse(lines.contains(line2));
	}

}
