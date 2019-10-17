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
package oripa.domain.paint;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class CreasePatternUndoManagerTest {

	Collection<OriLine> createOriLines(final double x0, final double y0, final double x1,
			final double y1) {
		Collection<OriLine> lines = new ArrayList<>();
		lines.add(new OriLine(x0, y0, x1, y1, OriLine.TYPE_RIDGE));
		return lines;
	}

	OriLine popLastLine(final CreasePatternUndoManager manager) {
		Collection<OriLine> lines = manager.pop().getInfo();

		return (OriLine) (lines.toArray()[0]);
	}

	@Test
	public void testExceedingDataShouldBeShifted() {
		CreasePatternUndoManager manager = new CreasePatternUndoManager(5);

		for (int i = 0; i < 5; i++) {
			manager.push(createOriLines(i, i, i, i));
		}
		assertEquals(5, manager.size());
		assertEquals(4, popLastLine(manager).p0.x, 1e-8);
		assertEquals(4, manager.size());

		for (int i = 4; i < 7; i++) {
			manager.push(createOriLines(i, i, i, i));
		}
		assertEquals(5, manager.size());
		assertEquals(6, popLastLine(manager).p0.x, 1e-8);
		assertEquals(4, manager.size());

	}

}
