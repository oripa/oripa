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
public class CrossingLineSplitterSweepLineAlgorithmTest {

	@Test
	void test_2_Lines_Cross() {
		var p00 = new Vector2d(0, 0);
		var p01 = new Vector2d(2, 2);

		var p10 = new Vector2d(0.5, 1.5);
		var p11 = new Vector2d(1.5, 0.5);

		var line0 = new OriLine(p00, p01, Type.MOUNTAIN);
		var line1 = new OriLine(p10, p11, Type.MOUNTAIN);

		var result = new CrossingLineSplitterSweepLineAlgorithm().splitIgnoringType(List.of(line0, line1), 1e-8);

		assertEquals(4, result.size());

		var cross = new Vector2d(1, 1);
		AssertionUtil.assertAnyMatch(new OriLine(p00, cross, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p01, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(p10, cross, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p11, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
	}

	@Test
	void test_2_Lines_Cross_eps() {
		var p00 = new Vector2d(0, 0);
		var p01 = new Vector2d(2, 2);

		var p10 = new Vector2d(0.5, 1);
		var p11 = new Vector2d(1 - 1e-9, 1);

		var line0 = new OriLine(p00, p01, Type.MOUNTAIN);
		var line1 = new OriLine(p10, p11, Type.MOUNTAIN);

		var result = new CrossingLineSplitterSweepLineAlgorithm().splitIgnoringType(List.of(line0, line1), 1e-8);

		assertEquals(3, result.size());

		var cross = new Vector2d(1, 1);
		AssertionUtil.assertAnyMatch(new OriLine(p00, cross, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p01, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p10, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
	}

	@Test
	void test_2_Lines_TouchLeft() {
		var p00 = new Vector2d(0, 0);
		var p01 = new Vector2d(2, 2);

		var p10 = new Vector2d(1, 1);
		var p11 = new Vector2d(1.5, 0.5);

		var line0 = new OriLine(p00, p01, Type.MOUNTAIN);
		var line1 = new OriLine(p10, p11, Type.MOUNTAIN);

		var result = new CrossingLineSplitterSweepLineAlgorithm().splitIgnoringType(List.of(line0, line1), 1e-8);

		assertEquals(3, result.size());

		var cross = new Vector2d(1, 1);
		AssertionUtil.assertAnyMatch(new OriLine(p00, cross, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p01, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p11, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
	}

	@Test
	void test_2_Lines_TouchRight() {
		var p00 = new Vector2d(0, 0);
		var p01 = new Vector2d(2, 2);

		var p10 = new Vector2d(1, 0);
		var p11 = new Vector2d(1.5 - 1e-9, 1.5 - 1e-9);

		var line0 = new OriLine(p00, p01, Type.MOUNTAIN);
		var line1 = new OriLine(p10, p11, Type.MOUNTAIN);

		var result = new CrossingLineSplitterSweepLineAlgorithm().splitIgnoringType(List.of(line0, line1), 1e-8);

		assertEquals(3, result.size());

		var cross = new Vector2d(1.5, 1.5);
		AssertionUtil.assertAnyMatch(new OriLine(p00, cross, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p01, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p10, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
	}

	@Test
	void test_2_Lines_Cross_sameX0() {
		var p00 = new Vector2d(0, 0);
		var p01 = new Vector2d(2, 2);

		var p10 = new Vector2d(0.0, 2);
		var p11 = new Vector2d(1.5, 0.5);

		var line0 = new OriLine(p00, p01, Type.MOUNTAIN);
		var line1 = new OriLine(p10, p11, Type.MOUNTAIN);

		var result = new CrossingLineSplitterSweepLineAlgorithm().splitIgnoringType(List.of(line0, line1), 1e-8);

		assertEquals(4, result.size());

		var cross = new Vector2d(1, 1);
		AssertionUtil.assertAnyMatch(new OriLine(p00, cross, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p01, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(p10, cross, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p11, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
	}

	@Test
	void test_2_Lines_Vertical() {
		var p00 = new Vector2d(0, 0);
		var p01 = new Vector2d(2, 2);

		var p10 = new Vector2d(1, 2);
		var p11 = new Vector2d(1, 0.5);

		var line0 = new OriLine(p00, p01, Type.MOUNTAIN);
		var line1 = new OriLine(p10, p11, Type.MOUNTAIN);

		var result = new CrossingLineSplitterSweepLineAlgorithm().splitIgnoringType(List.of(line0, line1), 1e-8);

		assertEquals(4, result.size());

		var cross = new Vector2d(1, 1);
		AssertionUtil.assertAnyMatch(new OriLine(p00, cross, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p01, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(p10, cross, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p11, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
	}

	@Test
	void test_2_Lines_orthogonalTouch() {
		var p00 = new Vector2d(2, 0);
		var p01 = new Vector2d(2, 2);

		var p10 = new Vector2d(1, 1);
		var p11 = new Vector2d(2, 1);

		var line0 = new OriLine(p00, p01, Type.MOUNTAIN);
		var line1 = new OriLine(p10, p11, Type.MOUNTAIN);

		var result = new CrossingLineSplitterSweepLineAlgorithm().splitIgnoringType(List.of(line0, line1), 1e-8);

		assertEquals(3, result.size());

		var cross0 = new Vector2d(2, 1);
		AssertionUtil.assertAnyMatch(new OriLine(p00, cross0, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, p01, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(p10, cross0, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));

	}

	@Test
	void test_3_Lines_orthogonalTouch() {
		var p00 = new Vector2d(2, 0);
		var p01 = new Vector2d(2, 2);

		var p10 = new Vector2d(1, 1);
		var p11 = new Vector2d(2, 1);

		var p20 = new Vector2d(1, 1);
		var p21 = new Vector2d(2, 0);

		var line0 = new OriLine(p00, p01, Type.MOUNTAIN);
		var line1 = new OriLine(p10, p11, Type.MOUNTAIN);
		var line2 = new OriLine(p20, p21, Type.MOUNTAIN);

		var result = new CrossingLineSplitterSweepLineAlgorithm().splitIgnoringType(List.of(line0, line1, line2), 1e-8);

		assertEquals(4, result.size());

		var cross0 = new Vector2d(2, 1);

		AssertionUtil.assertAnyMatch(new OriLine(p20, p21, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(p00, cross0, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, p01, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(p10, cross0, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));

	}

	@Test
	void test_2_Lines_Vertical_sameYs() {
		var p00 = new Vector2d(0, 0);
		var p01 = new Vector2d(2, 2);

		var p10 = new Vector2d(1, 2);
		var p11 = new Vector2d(1, 0);

		var line0 = new OriLine(p00, p01, Type.MOUNTAIN);
		var line1 = new OriLine(p10, p11, Type.MOUNTAIN);

		var result = new CrossingLineSplitterSweepLineAlgorithm().splitIgnoringType(List.of(line0, line1), 1e-8);

		assertEquals(4, result.size());

		var cross = new Vector2d(1, 1);
		AssertionUtil.assertAnyMatch(new OriLine(p00, cross, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p01, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(p10, cross, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p11, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
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

		var result = new CrossingLineSplitterSweepLineAlgorithm().splitIgnoringType(List.of(line0, line1, line2), 1e-8);

		assertEquals(7, result.size());

		var cross0 = new Vector2d(1, 0);
		AssertionUtil.assertAnyMatch(new OriLine(p00, cross0, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, p10, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, p11, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));

		var cross1 = new Vector2d(3, 0);

		AssertionUtil.assertAnyMatch(new OriLine(cross0, cross1, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));

		AssertionUtil.assertAnyMatch(new OriLine(cross1, p01, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross1, p20, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross1, p21, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
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

		var result = new CrossingLineSplitterSweepLineAlgorithm().splitIgnoringType(List.of(line0, line1, line2), 1e-8);

		assertEquals(6, result.size());

		var cross0 = new Vector2d(1, 0);
		AssertionUtil.assertAnyMatch(new OriLine(p00, cross0, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, p10, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, p11, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, p01, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, p20, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, p21, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
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

		var result = new CrossingLineSplitterSweepLineAlgorithm().splitIgnoringType(List.of(line0, line1, line2), 1e-8);

		assertEquals(9, result.size());

		var cross0 = new Vector2d(2, 1);
		var cross1 = new Vector2d(3, 2);
		var cross2 = new Vector2d(4, 1);

		AssertionUtil.assertAnyMatch(new OriLine(p00, cross0, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, p10, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));

		AssertionUtil.assertAnyMatch(new OriLine(cross0, cross1, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, cross2, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));

		AssertionUtil.assertAnyMatch(new OriLine(cross1, p11, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(p20, cross1, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));

		AssertionUtil.assertAnyMatch(new OriLine(cross1, cross2, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));

		AssertionUtil.assertAnyMatch(new OriLine(cross2, p01, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross2, p21, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));

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

		var result = new CrossingLineSplitterSweepLineAlgorithm().splitIgnoringType(List.of(line0, line1, line2), 1e-8);

		assertEquals(7, result.size());

		var cross0 = new Vector2d(2, 1);
		var cross1 = new Vector2d(3, 2);
		var cross2 = new Vector2d(4, 1);

		AssertionUtil.assertAnyMatch(new OriLine(cross0, cross1, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, cross2, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));

		AssertionUtil.assertAnyMatch(new OriLine(cross1, p11, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(p20, cross1, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));

		AssertionUtil.assertAnyMatch(new OriLine(cross1, cross2, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));

		AssertionUtil.assertAnyMatch(new OriLine(cross2, p01, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross2, p21, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
	}

	@Test
	void test_3_Lines_1_TouchEndPointAndLine_1_Cross() {
		var p00 = new Vector2d(0, 6);
		var p01 = new Vector2d(6, 0);

		var p10 = new Vector2d(1, 1);
		var p11 = new Vector2d(5, 5);

		var p20 = new Vector2d(2, -4);
		var p21 = new Vector2d(10, 4);

		var line0 = new OriLine(p00, p01, Type.MOUNTAIN);
		var line1 = new OriLine(p10, p11, Type.MOUNTAIN);
		var line2 = new OriLine(p20, p21, Type.MOUNTAIN);

		var result = new CrossingLineSplitterSweepLineAlgorithm().splitIgnoringType(List.of(line0, line1, line2), 1e-8);

		assertEquals(6, result.size());

		var cross0 = new Vector2d(3, 3);
		var cross1 = new Vector2d(6, 0);

		AssertionUtil.assertAnyMatch(new OriLine(cross0, p00, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, p10, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, p11, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, cross1, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));

		AssertionUtil.assertAnyMatch(new OriLine(cross1, p20, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross1, p21, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
	}

	@Test
	void test_3_Lines_RightTouchAndCrossAtSamePoint() {
		var p00 = new Vector2d(0, 0);
		var p01 = new Vector2d(6, 6);

		var p10 = new Vector2d(0, 2);
		var p11 = new Vector2d(2, 0);

		var p20 = new Vector2d(0, 1);
		var p21 = new Vector2d(1 - 1e-9, 1 - 1e-9);

		var line0 = new OriLine(p00, p01, Type.MOUNTAIN);
		var line1 = new OriLine(p10, p11, Type.MOUNTAIN);
		var line2 = new OriLine(p20, p21, Type.MOUNTAIN);

		var result = new CrossingLineSplitterSweepLineAlgorithm().splitIgnoringType(List.of(line0, line1, line2), 1e-8);

		assertEquals(5, result.size());

		var cross = new Vector2d(1, 1);

		AssertionUtil.assertAnyMatch(new OriLine(cross, p00, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p01, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p10, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p11, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p20, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
	}

	@Test
	void test_4_Lines_TouchEndPoints_2_VerticalCrosses() {
		var p00 = new Vector2d(0, 1);
		var p01 = new Vector2d(4, 5);

		var p10 = new Vector2d(0, 1);
		var p11 = new Vector2d(2, 5);

		var p20 = new Vector2d(-1, 2);
		var p21 = new Vector2d(0, 1);

		var p30 = new Vector2d(1 - 1e-10, 0);
		var p31 = new Vector2d(1 + 1e-10, 5);

		var line0 = new OriLine(p00, p01, Type.MOUNTAIN);
		var line1 = new OriLine(p10, p11, Type.MOUNTAIN);
		var line2 = new OriLine(p20, p21, Type.MOUNTAIN);
		var line3 = new OriLine(p30, p31, Type.MOUNTAIN);

		var result = new CrossingLineSplitterSweepLineAlgorithm().splitIgnoringType(List.of(line0, line1, line2, line3),
				1e-8);

		assertEquals(8, result.size());

		var cross0 = new Vector2d(1, 2);
		var cross1 = new Vector2d(1, 3);

		AssertionUtil.assertAnyMatch(new OriLine(cross0, p00, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, p01, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));

		AssertionUtil.assertAnyMatch(new OriLine(cross1, p10, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross1, p11, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));

		AssertionUtil.assertAnyMatch(new OriLine(p20, p21, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));

		AssertionUtil.assertAnyMatch(new OriLine(cross0, p30, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross1, p31, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, cross1, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
	}

	@Test
	void test_4_Lines_TouchEndPoints_3_Crosses() {
		var p00 = new Vector2d(0, 1);
		var p01 = new Vector2d(3, 4);

		var p10 = new Vector2d(0, 1);
		var p11 = new Vector2d(3, 7);

		var p20 = new Vector2d(0, 1);
		var p21 = new Vector2d(3, 10);

		var p30 = new Vector2d(2, 0);
		var p31 = new Vector2d(2, 10);

		var line0 = new OriLine(p00, p01, Type.MOUNTAIN);
		var line1 = new OriLine(p10, p11, Type.MOUNTAIN);
		var line2 = new OriLine(p20, p21, Type.MOUNTAIN);
		var line3 = new OriLine(p30, p31, Type.MOUNTAIN);

		var result = new CrossingLineSplitterSweepLineAlgorithm().splitIgnoringType(List.of(line0, line1, line2, line3),
				1e-8);

		assertEquals(10, result.size());

		var cross0 = new Vector2d(2, 3);
		var cross1 = new Vector2d(2, 5);
		var cross2 = new Vector2d(2, 7);

		AssertionUtil.assertAnyMatch(new OriLine(cross0, p00, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, p01, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, p30, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, cross1, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));

		AssertionUtil.assertAnyMatch(new OriLine(cross1, p10, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross1, p11, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross1, cross2, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));

		AssertionUtil.assertAnyMatch(new OriLine(cross2, p20, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross2, p21, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross2, p31, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
	}

	@Test
	void test_4_Lines_2_TouchAtMiddle_2_Crosses() {
		var p00 = new Vector2d(0, 6);
		var p01 = new Vector2d(4, 8);

		var p10 = new Vector2d(2, 7);
		var p11 = new Vector2d(5, 1);

		var p20 = new Vector2d(2, 7);
		var p21 = new Vector2d(7, 2);

		var p30 = new Vector2d(0, 4);
		var p31 = new Vector2d(6, 7);

		var line0 = new OriLine(p00, p01, Type.MOUNTAIN);
		var line1 = new OriLine(p10, p11, Type.MOUNTAIN);
		var line2 = new OriLine(p20, p21, Type.MOUNTAIN);
		var line3 = new OriLine(p30, p31, Type.MOUNTAIN);

		var result = new CrossingLineSplitterSweepLineAlgorithm().splitIgnoringType(List.of(line0, line1, line2, line3),
				1e-8);

		assertEquals(9, result.size());

		var cross0 = new Vector2d(14.0 / 5, 27.0 / 5);
		var cross1 = new Vector2d(10.0 / 3, 17.0 / 3);
		var touch0 = new Vector2d(2, 7);

		AssertionUtil.assertAnyMatch(new OriLine(touch0, p00, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(touch0, p01, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));

		AssertionUtil.assertAnyMatch(new OriLine(cross0, touch0, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, p30, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, cross1, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, p11, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));

		AssertionUtil.assertAnyMatch(new OriLine(cross1, touch0, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross1, p21, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross1, p31, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
	}

	@Test
	void test_FishBase() {
		var p00 = new Vector2d(-1 + 1e-9, 0 + 1e-9);
		var p01 = new Vector2d(6 + 1e-9, 7 + 1e-9);

		var p10 = new Vector2d(0 - 1e-9, 1 - 1e-9);
		var p11 = new Vector2d(4 + 1e-9, 1 + 1e-9);

		var p20 = new Vector2d(4 - 1e-9, 1 - 1e-9);
		var p21 = new Vector2d(4 + 1e-9, 5 - 1e-9);

		var p30 = new Vector2d(2 - 1e-9, 3 - 1e-9);
		var p31 = new Vector2d(4 - 1e-9, 1 - 1e-9);

		var p40 = new Vector2d(-1 - 1e-9, 0 - 1e-9);
		var p41 = new Vector2d(4 - 1e-9, 1 + 1e-9);

		var p50 = new Vector2d(4 + 1e-9, 1 + 1e-9);
		var p51 = new Vector2d(6 - 1e-9, 7 - 1e-9);

		var line0 = new OriLine(p00, p01, Type.MOUNTAIN);
		var line1 = new OriLine(p10, p11, Type.MOUNTAIN);
		var line2 = new OriLine(p20, p21, Type.MOUNTAIN);
		var line3 = new OriLine(p30, p31, Type.MOUNTAIN);
		var line4 = new OriLine(p40, p41, Type.MOUNTAIN);
		var line5 = new OriLine(p50, p51, Type.MOUNTAIN);

		var result = new CrossingLineSplitterSweepLineAlgorithm().splitIgnoringType(
				List.of(line0, line1, line2, line3, line4, line5),
				1e-8);

		assertEquals(9, result.size());

		AssertionUtil.assertAnyMatch(new OriLine(p00, p10, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(p00, p11, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));

		AssertionUtil.assertAnyMatch(new OriLine(p10, p30, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(p10, p11, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));

		AssertionUtil.assertAnyMatch(new OriLine(p30, p31, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(p30, p21, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));

		AssertionUtil.assertAnyMatch(new OriLine(p21, p11, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(p21, p01, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(p11, p01, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
	}

	@Test
	void test_Diamond() {
		var p00 = new Vector2d(1 + 1e-10, 1 + 1e-10);
		var p01 = new Vector2d(3 + 1e-10, 1 + 1e-10);

		var p10 = new Vector2d(1 - 1e-10, 1 - 1e-10);
		var p11 = new Vector2d(1 + 1e-10, 3 + 1e-10);

		var p20 = new Vector2d(1 - 1e-10, 3 - 1e-10);
		var p21 = new Vector2d(4 + 1e-10, 4 - 1e-10);

		var p30 = new Vector2d(3 - 1e-10, 1 - 1e-10);
		var p31 = new Vector2d(4 - 1e-10, 4 - 1e-10);

		var p40 = new Vector2d(1 - 1e-10, 1 - 1e-10);
		var p41 = new Vector2d(4 - 1e-10, 4 + 1e-10);

		var p50 = new Vector2d(1 + 1e-10, 3 + 1e-10);
		var p51 = new Vector2d(3 - 1e-10, 1 - 1e-10);

		var line0 = new OriLine(p00, p01, Type.MOUNTAIN);
		var line1 = new OriLine(p10, p11, Type.MOUNTAIN);
		var line2 = new OriLine(p20, p21, Type.MOUNTAIN);
		var line3 = new OriLine(p30, p31, Type.MOUNTAIN);
		var line4 = new OriLine(p40, p41, Type.MOUNTAIN);
		var line5 = new OriLine(p50, p51, Type.MOUNTAIN);

		var result = new CrossingLineSplitterSweepLineAlgorithm().splitIgnoringType(
				List.of(line0, line1, line2, line3, line4, line5),
				1e-8);

		assertEquals(8, result.size());
		var cross0 = new Vector2d(2, 2);

		AssertionUtil.assertAnyMatch(new OriLine(p00, p01, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));

		AssertionUtil.assertAnyMatch(new OriLine(p10, p11, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));

		AssertionUtil.assertAnyMatch(new OriLine(p20, p21, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));

		AssertionUtil.assertAnyMatch(new OriLine(p30, p31, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));

		AssertionUtil.assertAnyMatch(new OriLine(cross0, p00, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, p21, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, p11, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, p01, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
	}

	@Test
	void test_4_Lines_3_SameTouch_1_independent() {
		var p00 = new Vector2d(4, 4);
		var p01 = new Vector2d(2, 2);

		var p10 = new Vector2d(6, 6);
		var p11 = new Vector2d(4, 8);

		var p20 = new Vector2d(2, 2);
		var p21 = new Vector2d(5, 0);

		var p30 = new Vector2d(2, 2);
		var p31 = new Vector2d(0, 0);

		var line0 = new OriLine(p00, p01, Type.MOUNTAIN);
		var line1 = new OriLine(p10, p11, Type.MOUNTAIN);
		var line2 = new OriLine(p20, p21, Type.MOUNTAIN);
		var line3 = new OriLine(p30, p31, Type.MOUNTAIN);

		var result = new CrossingLineSplitterSweepLineAlgorithm().splitIgnoringType(List.of(line0, line1, line2, line3),
				1e-8);

		assertEquals(4, result.size());

		var cross = new Vector2d(2, 2);

		AssertionUtil.assertAnyMatch(new OriLine(cross, p00, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p21, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross, p31, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));

		AssertionUtil.assertAnyMatch(new OriLine(p10, p11, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));

	}

	@Test
	void test_6_Lines_1_Vertical_touchAtBothVerticalEnds_crossOnRight() {
		var p00 = new Vector2d(2, 0);
		var p01 = new Vector2d(2, 4);

		var p10 = new Vector2d(2, 0);
		var p11 = new Vector2d(8, 6);

		var p20 = new Vector2d(2, 4);
		var p21 = new Vector2d(7, -1);

		var p30 = new Vector2d(0, 2);
		var p31 = new Vector2d(2, 0);

		var p40 = new Vector2d(0, 2);
		var p41 = new Vector2d(3, 5);

		var p50 = new Vector2d(-2, 0);
		var p51 = new Vector2d(0, 2);

		var line0 = new OriLine(p00, p01, Type.MOUNTAIN);
		var line1 = new OriLine(p10, p11, Type.MOUNTAIN);
		var line2 = new OriLine(p20, p21, Type.MOUNTAIN);
		var line3 = new OriLine(p30, p31, Type.MOUNTAIN);
		var line4 = new OriLine(p40, p41, Type.MOUNTAIN);
		var line5 = new OriLine(p50, p51, Type.MOUNTAIN);

		var result = new CrossingLineSplitterSweepLineAlgorithm().splitIgnoringType(
				List.of(line0, line1, line2, line3, line4, line5), 1e-8);

		assertEquals(9, result.size());

		var cross0 = new Vector2d(4, 2);
		var cross1 = new Vector2d(2, 4);

		AssertionUtil.assertAnyMatch(new OriLine(p00, p01, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));

		AssertionUtil.assertAnyMatch(new OriLine(cross0, p10, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, p11, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, p20, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross0, p21, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));

		AssertionUtil.assertAnyMatch(new OriLine(p30, p31, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(p40, cross1, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));

		AssertionUtil.assertAnyMatch(new OriLine(cross1, p41, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(p50, p51, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));

	}

	@Test
	void test_5_Lines_TouchVerticalUpperEnd_3_Crosses() {
		var p00 = new Vector2d(2, 0);
		var p01 = new Vector2d(2, 3);

		var p10 = new Vector2d(0, 3);
		var p11 = new Vector2d(2, 3);

		var p20 = new Vector2d(1, 0);
		var p21 = new Vector2d(4, 6);

		var p30 = new Vector2d(0, 1);
		var p31 = new Vector2d(4, 5);

		var p40 = new Vector2d(0, 2);
		var p41 = new Vector2d(4, 4);

		var line0 = new OriLine(p00, p01, Type.MOUNTAIN);
		var line1 = new OriLine(p10, p11, Type.MOUNTAIN);
		var line2 = new OriLine(p20, p21, Type.MOUNTAIN);
		var line3 = new OriLine(p30, p31, Type.MOUNTAIN);
		var line4 = new OriLine(p40, p41, Type.MOUNTAIN);

		var result = new CrossingLineSplitterSweepLineAlgorithm().splitIgnoringType(
				List.of(line0, line1, line2, line3, line4), 1e-8);

		assertEquals(13, result.size());

		var cross0 = new Vector2d(2, 2);
		var cross1 = new Vector2d(8.0 / 3, 10.0 / 3);
		var cross2 = new Vector2d(3, 4);

		AssertionUtil.assertAnyMatch(new OriLine(cross0, cross1, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(cross1, cross2, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(p01, cross1, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));

	}

	@Test
	void test_5_Lines_TouchVerticalUpperEnd_2_Crosses_X() {
		var p00 = new Vector2d(2, 0);
		var p01 = new Vector2d(2, 3);

		var p10 = new Vector2d(0, 3);
		var p11 = new Vector2d(2, 3);

		var p20 = new Vector2d(1, 5);
		var p21 = new Vector2d(5, 1);

		var p30 = new Vector2d(0, 1);
		var p31 = new Vector2d(4, 5);

		var p40 = new Vector2d(0, 2);
		var p41 = new Vector2d(4, 4);

		var line0 = new OriLine(p00, p01, Type.MOUNTAIN);
		var line1 = new OriLine(p10, p11, Type.MOUNTAIN);
		var line2 = new OriLine(p20, p21, Type.MOUNTAIN);
		var line3 = new OriLine(p30, p31, Type.MOUNTAIN);
		var line4 = new OriLine(p40, p41, Type.MOUNTAIN);

		var result = new CrossingLineSplitterSweepLineAlgorithm().splitIgnoringType(
				List.of(line0, line1, line2, line3, line4), 1e-8);

		assertEquals(11, result.size());

	}

	@Test
	void test_5_Lines_2_crossesOnSweep() {
		var p00 = new Vector2d(0, 9);
		var p01 = new Vector2d(4, 5);

		var p10 = new Vector2d(0, 5);
		var p11 = new Vector2d(4, 9);

		var p20 = new Vector2d(0, 4);
		var p21 = new Vector2d(4, 0);

		var p30 = new Vector2d(0, 0);
		var p31 = new Vector2d(4, 4);

		var p40 = new Vector2d(0, 9);
		var p41 = new Vector2d(4, 7);

		var line0 = new OriLine(p00, p01, Type.MOUNTAIN);
		var line1 = new OriLine(p10, p11, Type.MOUNTAIN);
		var line2 = new OriLine(p20, p21, Type.MOUNTAIN);
		var line3 = new OriLine(p30, p31, Type.MOUNTAIN);
		var line4 = new OriLine(p40, p41, Type.MOUNTAIN);

		var result = new CrossingLineSplitterSweepLineAlgorithm().splitIgnoringType(
				List.of(line0, line1, line2, line3, line4), 1e-8);

		assertEquals(11, result.size());

	}
}
