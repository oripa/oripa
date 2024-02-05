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
import oripa.value.OriLine.Type;
import oripa.vecmath.Vector2d;;

/**
 * @author OUCHI Koji
 *
 */
class SymmetricLineFactoryTest {
	SymmetricLineFactory factory = new SymmetricLineFactory();
	static final double EPS = 1e-8;

	@Test
	void testAutoWalk_XShape() throws PainterCommandFailedException {
		// X shape
		var lines = List.of(
				new OriLine(0, 0, 100, 100, Type.MOUNTAIN),
				new OriLine(0, 0, 100, -100, Type.MOUNTAIN),
				new OriLine(0, 0, -100, 100, Type.MOUNTAIN),
				new OriLine(0, 0, -100, -100, Type.MOUNTAIN));

		var symmetricLines = factory.createSymmetricLineAutoWalk(
				new Vector2d(-50, 50), new Vector2d(50, 50), new Vector2d(0, 0), lines, Type.VALLEY, EPS);

		assertEquals(3, symmetricLines.size());

		assertTrue(symmetricLines.contains(new OriLine(-50, -50, -50, 50, Type.VALLEY)));
		assertTrue(symmetricLines.contains(new OriLine(-50, -50, 50, -50, Type.VALLEY)));
		assertTrue(symmetricLines.contains(new OriLine(50, 50, 50, -50, Type.VALLEY)));

	}

	@Test
	void testAutoWalk_canGoThroughEndPoints() throws PainterCommandFailedException {
		var lines = List.of(
				new OriLine(0, 0, 0, 100, Type.MOUNTAIN),
				new OriLine(50, 0, 50, 100, Type.MOUNTAIN),
				new OriLine(150, 100, 150, 0, Type.MOUNTAIN),
				new OriLine(200, 100, 200, 0, Type.MOUNTAIN));

		var symmetricLines = factory.createSymmetricLineAutoWalk(
				new Vector2d(-50, 0), new Vector2d(0, 0), new Vector2d(0, 50), lines, Type.VALLEY, EPS);

		assertEquals(3, symmetricLines.size());

		assertTrue(symmetricLines.contains(new OriLine(0, 0, 50, 0, Type.VALLEY)));
		assertTrue(symmetricLines.contains(new OriLine(50, 0, 150, 0, Type.VALLEY)));
		assertTrue(symmetricLines.contains(new OriLine(150, 0, 200, 0, Type.VALLEY)));
	}
}
