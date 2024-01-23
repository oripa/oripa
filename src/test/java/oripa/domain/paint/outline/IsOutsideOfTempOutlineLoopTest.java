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
package oripa.domain.paint.outline;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
@ExtendWith(MockitoExtension.class)
class IsOutsideOfTempOutlineLoopTest {
	@InjectMocks
	private IsOutsideOfTempOutlineLoop isOutside;

	/**
	 * Test method for
	 * {@link oripa.domain.paint.outline.IsOutsideOfTempOutlineLoop#execute(java.util.Collection, oripa.vecmath.Vector2d)}.
	 */
	@Test
	void testExecute_outside() {
		var outlineVertices = List.of(new Vector2d(0, 0), new Vector2d(1, 0), new Vector2d(1, 1));
		var target = new Vector2d(0, 0.5);

		var result = isOutside.execute(outlineVertices, target);

		assertTrue(result);
	}

	@Test
	void testExecute_outside_fix_v145() {
		var outlineVertices = List.of(new Vector2d(-200, -200), new Vector2d(-200, 200), new Vector2d(200, 200));
		var target = new Vector2d(200, 0);

		var result = isOutside.execute(outlineVertices, target);

		assertTrue(result);
	}

	@Test
	void testExecute_inside() {
		var outlineVertices = List.of(new Vector2d(-200, -200), new Vector2d(-200, 200), new Vector2d(200, 200));
		var target = new Vector2d(-1, 0);

		var result = isOutside.execute(outlineVertices, target);

		assertFalse(result);
	}

}
