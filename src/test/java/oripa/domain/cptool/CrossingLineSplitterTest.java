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

import oripa.test.util.AssertionUtil;
import oripa.value.OriLine;
import oripa.value.OriLine.Type;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
public class CrossingLineSplitterTest {

	@Test
	void test_2_Lines_Cross() {
		var p00 = new Vector2d(0, 0);
		var p01 = new Vector2d(2, 2);

		var p10 = new Vector2d(0.5, 1.5);
		var p11 = new Vector2d(1.5, 0.5);

		var line0 = new OriLine(p00, p01, Type.MOUNTAIN);
		var line1 = new OriLine(p10, p11, Type.MOUNTAIN);

		var result = new CrossingLineSplitter().splitIgnoringType(List.of(line0, line1), 1e-8);

		assertEquals(4, result.size());

		var cross = new Vector2d(1, 1);
		AssertionUtil.assertAnyMatch(new OriLine(p00, cross, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p01, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(p10, cross, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p11, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
	}

	@Test
	void test_2_Lines_Cross_eps() {
		var p00 = new Vector2d(0, 0);
		var p01 = new Vector2d(2, 2);

		var p10 = new Vector2d(0.5, 1);
		var p11 = new Vector2d(1 - 1e-9, 1);

		var line0 = new OriLine(p00, p01, Type.MOUNTAIN);
		var line1 = new OriLine(p10, p11, Type.MOUNTAIN);

		var result = new CrossingLineSplitter().splitIgnoringType(List.of(line0, line1), 1e-8);

		assertEquals(3, result.size());

		var cross = new Vector2d(1, 1);
		AssertionUtil.assertAnyMatch(new OriLine(p00, cross, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p01, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p10, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
	}

	@Test
	void test_2_Lines_TouchLeft() {
		var p00 = new Vector2d(0, 0);
		var p01 = new Vector2d(2, 2);

		var p10 = new Vector2d(1, 1);
		var p11 = new Vector2d(1.5, 0.5);

		var line0 = new OriLine(p00, p01, Type.MOUNTAIN);
		var line1 = new OriLine(p10, p11, Type.MOUNTAIN);

		var result = new CrossingLineSplitter().splitIgnoringType(List.of(line0, line1), 1e-8);

		assertEquals(3, result.size());

		var cross = new Vector2d(1, 1);
		AssertionUtil.assertAnyMatch(new OriLine(p00, cross, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p01, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p11, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
	}

	@Test
	void test_2_Lines_TouchRight() {
		var p00 = new Vector2d(0, 0);
		var p01 = new Vector2d(2, 2);

		var p10 = new Vector2d(1, 0);
		var p11 = new Vector2d(1.5, 1.5);

		var line0 = new OriLine(p00, p01, Type.MOUNTAIN);
		var line1 = new OriLine(p10, p11, Type.MOUNTAIN);

		var result = new CrossingLineSplitter().splitIgnoringType(List.of(line0, line1), 1e-8);

		assertEquals(3, result.size());

		var cross = new Vector2d(1.5, 1.5);
		AssertionUtil.assertAnyMatch(new OriLine(p00, cross, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p01, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p10, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
	}

	@Test
	void test_2_Lines_Cross_sameX0() {
		var p00 = new Vector2d(0, 0);
		var p01 = new Vector2d(2, 2);

		var p10 = new Vector2d(0.0, 2);
		var p11 = new Vector2d(1.5, 0.5);

		var line0 = new OriLine(p00, p01, Type.MOUNTAIN);
		var line1 = new OriLine(p10, p11, Type.MOUNTAIN);

		var result = new CrossingLineSplitter().splitIgnoringType(List.of(line0, line1), 1e-8);

		assertEquals(4, result.size());

		var cross = new Vector2d(1, 1);
		AssertionUtil.assertAnyMatch(new OriLine(p00, cross, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p01, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(p10, cross, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p11, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
	}

	@Test
	void test_2_Lines_Vertical() {
		var p00 = new Vector2d(0, 0);
		var p01 = new Vector2d(2, 2);

		var p10 = new Vector2d(1, 2);
		var p11 = new Vector2d(1, 0.5);

		var line0 = new OriLine(p00, p01, Type.MOUNTAIN);
		var line1 = new OriLine(p10, p11, Type.MOUNTAIN);

		var result = new CrossingLineSplitter().splitIgnoringType(List.of(line0, line1), 1e-8);

		assertEquals(4, result.size());

		var cross = new Vector2d(1, 1);
		AssertionUtil.assertAnyMatch(new OriLine(p00, cross, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p01, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(p10, cross, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p11, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
	}

	@Test
	void test_2_Lines_Vertical_sameYs() {
		var p00 = new Vector2d(0, 0);
		var p01 = new Vector2d(2, 2);

		var p10 = new Vector2d(1, 2);
		var p11 = new Vector2d(1, 0);

		var line0 = new OriLine(p00, p01, Type.MOUNTAIN);
		var line1 = new OriLine(p10, p11, Type.MOUNTAIN);

		var result = new CrossingLineSplitter().splitIgnoringType(List.of(line0, line1), 1e-8);

		assertEquals(4, result.size());

		var cross = new Vector2d(1, 1);
		AssertionUtil.assertAnyMatch(new OriLine(p00, cross, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p01, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(p10, cross, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p11, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
	}

	@Test
	void test_3_Lines_2_Crosses() {
		var p00 = new Vector2d(0, 0);
		var p01 = new Vector2d(5, 0);

		var p10 = new Vector2d(0.5, 0.5);
		var p11 = new Vector2d(1.5, -0.5);

		var p20 = new Vector2d(2, 1);
		var p21 = new Vector2d(4, -1);

		var line0 = new OriLine(p00, p01, Type.MOUNTAIN);
		var line1 = new OriLine(p10, p11, Type.MOUNTAIN);
		var line2 = new OriLine(p20, p21, Type.MOUNTAIN);

		var result = new CrossingLineSplitter().splitIgnoringType(List.of(line0, line1, line2), 1e-8);

		assertEquals(7, result.size());

		var cross0 = new Vector2d(1, 0);
		AssertionUtil.assertAnyMatch(new OriLine(p00, cross0, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, p10, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, p11, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));

		var cross1 = new Vector2d(3, 0);

		AssertionUtil.assertAnyMatch(new OriLine(cross0, cross1, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));

		AssertionUtil.assertAnyMatch(new OriLine(cross1, p01, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(cross1, p20, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(cross1, p21, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
	}

	@Test
	void test_3_Lines_same_Cross() {
		var p00 = new Vector2d(0, 0);
		var p01 = new Vector2d(5, 0);

		var p10 = new Vector2d(0.5, 0.5);
		var p11 = new Vector2d(1.5, -0.5);

		var p20 = new Vector2d(2, 1);
		var p21 = new Vector2d(0, -1);

		var line0 = new OriLine(p00, p01, Type.MOUNTAIN);
		var line1 = new OriLine(p10, p11, Type.MOUNTAIN);
		var line2 = new OriLine(p20, p21, Type.MOUNTAIN);

		var result = new CrossingLineSplitter().splitIgnoringType(List.of(line0, line1, line2), 1e-8);

		assertEquals(6, result.size());

		var cross0 = new Vector2d(1, 0);
		AssertionUtil.assertAnyMatch(new OriLine(p00, cross0, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, p10, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, p11, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, p01, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, p20, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, p21, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
	}

	@Test
	void test_3_Lines_3_Crosses() {
		var p00 = new Vector2d(0, 1);
		var p01 = new Vector2d(6, 1);

		var p10 = new Vector2d(1, 0);
		var p11 = new Vector2d(4, 3);

		var p20 = new Vector2d(2, 3);
		var p21 = new Vector2d(5, 0);

		var line0 = new OriLine(p00, p01, Type.MOUNTAIN);
		var line1 = new OriLine(p10, p11, Type.MOUNTAIN);
		var line2 = new OriLine(p20, p21, Type.MOUNTAIN);

		var result = new CrossingLineSplitter().splitIgnoringType(List.of(line0, line1, line2), 1e-8);

		assertEquals(9, result.size());

		var cross0 = new Vector2d(2, 1);
		var cross1 = new Vector2d(3, 2);
		var cross2 = new Vector2d(4, 1);

		AssertionUtil.assertAnyMatch(new OriLine(p00, cross0, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, p10, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));

		AssertionUtil.assertAnyMatch(new OriLine(cross0, cross1, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, cross2, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));

		AssertionUtil.assertAnyMatch(new OriLine(cross1, p11, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(p20, cross1, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));

		AssertionUtil.assertAnyMatch(new OriLine(cross1, cross2, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));

		AssertionUtil.assertAnyMatch(new OriLine(cross2, p01, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(cross2, p21, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));

	}

	@Test
	void test_3_Lines_1_TouchEndPoints_2_Crosses() {
		var p00 = new Vector2d(2, 1);
		var p01 = new Vector2d(6, 1);

		var p10 = new Vector2d(2, 1);
		var p11 = new Vector2d(4, 3);

		var p20 = new Vector2d(2, 3);
		var p21 = new Vector2d(5, 0);

		var line0 = new OriLine(p00, p01, Type.MOUNTAIN);
		var line1 = new OriLine(p10, p11, Type.MOUNTAIN);
		var line2 = new OriLine(p20, p21, Type.MOUNTAIN);

		var result = new CrossingLineSplitter().splitIgnoringType(List.of(line0, line1, line2), 1e-8);

		assertEquals(7, result.size());

		var cross0 = new Vector2d(2, 1);
		var cross1 = new Vector2d(3, 2);
		var cross2 = new Vector2d(4, 1);

		AssertionUtil.assertAnyMatch(new OriLine(cross0, cross1, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, cross2, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));

		AssertionUtil.assertAnyMatch(new OriLine(cross1, p11, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(p20, cross1, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));

		AssertionUtil.assertAnyMatch(new OriLine(cross1, cross2, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));

		AssertionUtil.assertAnyMatch(new OriLine(cross2, p01, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(cross2, p21, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
	}

	@Test
	void test_4_Lines_sameCross_1_independent() {
		var p00 = new Vector2d(-359.992, 356.240);
		var p01 = new Vector2d(-354.956, 351.204);

		var p10 = new Vector2d(-361.835, 358.083);
		var p11 = new Vector2d(-354.956, 364.963);

		var p20 = new Vector2d(-373.751, 332.409);
		var p21 = new Vector2d(-354.956, 351.204);

		var p30 = new Vector2d(-354.956, 351.204);
		var p31 = new Vector2d(-336.160, 332.409);

		var line0 = new OriLine(p00, p01, Type.MOUNTAIN);
		var line1 = new OriLine(p10, p11, Type.MOUNTAIN);
		var line2 = new OriLine(p20, p21, Type.MOUNTAIN);
		var line3 = new OriLine(p30, p31, Type.MOUNTAIN);

		var result = new CrossingLineSplitter().splitIgnoringType(List.of(line0, line1, line2, line3), 1e-8);

		assertEquals(4, result.size());

		var cross = new Vector2d(-354.956, 351.204);

		AssertionUtil.assertAnyMatch(new OriLine(cross, p00, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p20, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p31, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));

		AssertionUtil.assertAnyMatch(new OriLine(p10, p11, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-5));

	}

}
