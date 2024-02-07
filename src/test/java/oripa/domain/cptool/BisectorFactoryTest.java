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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import oripa.geom.Line;
import oripa.test.util.AssertionUtil;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
@ExtendWith(MockitoExtension.class)
class BisectorFactoryTest {
	@InjectMocks
	BisectorFactory factory;

	final double EPS = 1e-6;

	@Test
	void testPerpendicularBisector() {
		var v0 = new Vector2d(-1, 0);
		var v1 = new Vector2d(1, 0);

		var bisector = factory.createPerpendicularBisector(v0, v1);

		var expected = new Line(new Vector2d(0, 0), new Vector2d(0, 1));
		assertTrue(expected.equals(bisector, EPS));

	}

	@Test
	void testAngleBisectorOf90Degrees() {
		var v0 = new Vector2d(1, 0);
		var v1 = new Vector2d(0, 0);
		var v2 = new Vector2d(0, 1);

		var line = new OriLine(1, 0, 0, 1, OriLine.Type.VALLEY);

		var bisector = factory.createAngleBisectorLine(v0, v1, v2, line, OriLine.Type.MOUNTAIN);

		var expected = new OriLine(0, 0, 0.5, 0.5, OriLine.Type.MOUNTAIN);

		AssertionUtil.assertSegmentEquals(expected, bisector, (a, b) -> a.equals(b, EPS));
	}

	@Test
	void testAngleBisectorOf120Degrees() {
		var v0 = new Vector2d(1, 0);
		var v1 = new Vector2d(0, 0);
		var v2 = new Vector2d(-1, Math.sqrt(3));

		var line = new OriLine(1, 0, 0.5, Math.sqrt(3) / 2, OriLine.Type.MOUNTAIN);

		var bisector = factory.createAngleBisectorLine(v0, v1, v2, line, OriLine.Type.MOUNTAIN);

		var expected = new OriLine(0, 0, 0.5, Math.sqrt(3) / 2, OriLine.Type.MOUNTAIN);

		AssertionUtil.assertSegmentEquals(expected, bisector, (a, b) -> a.equals(b, EPS));
	}

	@Test
	void testAngleBisectorOf180Degrees() {
		var v0 = new Vector2d(1, 0);
		var v1 = new Vector2d(0, 0);
		var v2 = new Vector2d(-1, 0);

		var line = new OriLine(1, 1, -1, 1, OriLine.Type.AUX);

		var bisector = factory.createAngleBisectorLine(v0, v1, v2, line, OriLine.Type.MOUNTAIN);

		var expected = new OriLine(0, 0, 0, 1, OriLine.Type.MOUNTAIN);

		AssertionUtil.assertSegmentEquals(expected, bisector, (a, b) -> a.equals(b, EPS));
	}

}
