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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import oripa.test.util.AssertionUtil;
import oripa.value.OriLine;
import oripa.value.OriLine.Type;

/**
 * @author OUCHI Koji
 *
 */
class OverlappingLineMergerTest {

	@Test
	void test_2_Lines_Inclusion() {
		var lines = List.of(new OriLine(0, 0, 1, 0, Type.MOUNTAIN), new OriLine(0.5, 0, 0.7, 0, Type.MOUNTAIN));

		var merger = new OverlappingLineMerger();

		var result = merger.mergeIgnoringType(lines, 1e-8);

		assertEquals(1, result.size());

		AssertionUtil.assertSegmentEquals(
				new OriLine(0, 0, 1, 0, Type.MOUNTAIN),
				result.stream().findFirst().get(),
				(a, b) -> a.equals(b, 1e-8));
	}

	@Test
	void test_2_Lines_Equal() {
		var lines = List.of(new OriLine(0, 0, 1, 0, Type.MOUNTAIN), new OriLine(0, 0, 1, 0, Type.MOUNTAIN));

		var merger = new OverlappingLineMerger();

		var result = merger.mergeIgnoringType(lines, 1e-8);

		assertEquals(1, result.size());

		AssertionUtil.assertSegmentEquals(
				new OriLine(0, 0, 1, 0, Type.MOUNTAIN),
				result.stream().findFirst().get(),
				(a, b) -> a.equals(b, 1e-8));
	}

	@Test
	void test_2_Lines_PartialOverlap() {
		var lines = List.of(new OriLine(0, 0, 1, 0, Type.MOUNTAIN), new OriLine(0.5, 0, 2, 0, Type.MOUNTAIN));

		var merger = new OverlappingLineMerger();

		var result = merger.mergeIgnoringType(lines, 1e-8);

		assertEquals(1, result.size());

		AssertionUtil.assertSegmentEquals(
				new OriLine(0, 0, 2, 0, Type.MOUNTAIN),
				result.stream().findFirst().get(),
				(a, b) -> a.equals(b, 1e-8));
	}

	@Test
	void test_2_HorizontalLines_PartialOverlap() {
		var lines = List.of(new OriLine(0, 0, 0, 1, Type.MOUNTAIN), new OriLine(0, 0.5, 0, 2, Type.MOUNTAIN));

		var merger = new OverlappingLineMerger();

		var result = merger.mergeIgnoringType(lines, 1e-8);

		assertEquals(1, result.size());

		AssertionUtil.assertSegmentEquals(
				new OriLine(0, 0, 0, 2, Type.MOUNTAIN),
				result.stream().findFirst().get(),
				(a, b) -> a.equals(b, 1e-8));
	}

	@ParameterizedTest
	@CsvSource({
			"0,1, 0.5,1.5, 1.4,2, 0,2",
			"0,1, 0.5,2, 1.4,1.5, 0,2",
			// same start point, different end points
			"0,0.5, 0,1, 0,2, 0,2" })
	void test_3_HorizontalLines_2_Overlaps(
			final double l0X0, final double l0X1,
			final double l1X0, final double l1X1,
			final double l2X0, final double l2X1,
			final double expectedX0, final double expectedX1) {
		var lines = List.of(
				new OriLine(l0X0, 0, l0X1, 0, Type.MOUNTAIN),
				new OriLine(l1X0, 0, l1X1, 0, Type.MOUNTAIN),
				new OriLine(l2X0, 0, l2X1, 0, Type.MOUNTAIN));

		var merger = new OverlappingLineMerger();

		var result = merger.mergeIgnoringType(lines, 1e-8);

		assertEquals(1, result.size());

		AssertionUtil.assertSegmentEquals(
				new OriLine(expectedX0, 0, expectedX1, 0, Type.MOUNTAIN),
				result.stream().findFirst().get(),
				(a, b) -> a.equals(b, 1e-8));
	}

	@Test
	void test_3_HorizontalLines_1_Overlaps() {
		var lines = List.of(
				new OriLine(0, 0, 1, 0, Type.MOUNTAIN),
				new OriLine(0.5, 0, 1.5, 0, Type.MOUNTAIN),
				new OriLine(2, 0, 3, 0, Type.MOUNTAIN));

		var merger = new OverlappingLineMerger();

		var result = merger.mergeIgnoringType(lines, 1e-8);

		assertEquals(2, result.size());

		AssertionUtil.assertAnyMatch(new OriLine(0, 0, 1.5, 0, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(2, 0, 3, 0, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
	}

	@Test
	void test_2_HorizontalLines_2_45degreeLines() {
		var lines = List.of(
				new OriLine(0, 0, 1, 0, Type.MOUNTAIN),
				new OriLine(0.5, 0, 1.5, 0, Type.MOUNTAIN),
				new OriLine(0, 0, 1, 1, Type.MOUNTAIN),
				new OriLine(0.3, 0.3, 1.7, 1.7, Type.MOUNTAIN));

		var merger = new OverlappingLineMerger();

		var result = merger.mergeIgnoringType(lines, 1e-8);

		assertEquals(2, result.size());

		AssertionUtil.assertAnyMatch(new OriLine(0, 0, 1.5, 0, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(0, 0, 1.7, 1.7, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
	}

	@Test
	void test_multipleOverlaps_connectionAtBottom() {
		var lines = List.of(
				new OriLine(-1e-9, -1e-9, 1e-9, 1 + 1e-9, Type.MOUNTAIN),
				new OriLine(1e-9, 1e-9, 1e-9, 1 + 1e-9, Type.MOUNTAIN),

				new OriLine(0, 0, 2, -2, Type.MOUNTAIN),
				new OriLine(0, 0, 2, -2, Type.MOUNTAIN),
				new OriLine(0, 0, 1, -1, Type.MOUNTAIN),
				new OriLine(-0.5, 0.5, 1, -1, Type.MOUNTAIN));

		var merger = new OverlappingLineMerger();

		var result = merger.mergeIgnoringType(lines, 1e-8);

		assertEquals(2, result.size());

		AssertionUtil.assertAnyMatch(new OriLine(0, 0, 0, 1, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(-0.5, 0.5, 2, -2, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
	}

	@Test
	void test_multipleOverlaps_connectionAtTop() {
		var lines = List.of(
				new OriLine(-1e-9, -1e-9, 1e-9, 1 + 1e-9, Type.MOUNTAIN),
				new OriLine(1e-9, 1e-9, 1e-9, 1 + 1e-9, Type.MOUNTAIN),

				new OriLine(0, 0, 2, 2, Type.MOUNTAIN),
				new OriLine(0, 0, 2, 2, Type.MOUNTAIN),
				new OriLine(0, 0, 1, 1, Type.MOUNTAIN),
				new OriLine(-0.5, -0.5, 1, 1, Type.MOUNTAIN));

		var merger = new OverlappingLineMerger();

		var result = merger.mergeIgnoringType(lines, 1e-8);

		assertEquals(2, result.size());

		AssertionUtil.assertAnyMatch(new OriLine(0, 0, 0, 1, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(-0.5, -0.5, 2, 2, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
	}

	@Test
	void test_multipleOverlaps_connectionAtEndPoints() {
		var lines = List.of(
				new OriLine(0, 0, 0, 1, Type.MOUNTAIN),
				new OriLine(0, 0, 0, 1, Type.MOUNTAIN),

				new OriLine(-0.5, 0.5, 0, 1, Type.MOUNTAIN),
				new OriLine(-0.5, 0.5, 0, 1, Type.MOUNTAIN),
				new OriLine(-0.5, 0.5, 1, 2, Type.MOUNTAIN),

				new OriLine(-0.5, 0.5, 0, 0, Type.MOUNTAIN),
				new OriLine(-0.5, 0.5, 0, 0, Type.MOUNTAIN),
				new OriLine(-0.5, 0.5, 1, -1, Type.MOUNTAIN),
				new OriLine(-0.5, 0.5, 1, -1, Type.MOUNTAIN)

		);

		var merger = new OverlappingLineMerger();

		var result = merger.mergeIgnoringType(lines, 1e-8);

		assertEquals(3, result.size());

		AssertionUtil.assertAnyMatch(new OriLine(0, 0, 0, 1, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(-0.5, 0.5, 1, 2, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(-0.5, 0.5, 1, -1, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
	}

	@Test
	void test_multipleOverlaps_connectionAtEndPoints_cross() {
		var lines = List.of(
				new OriLine(0, 0, 0, 1, Type.MOUNTAIN),
				new OriLine(0, 0, 0, 1, Type.MOUNTAIN),
				new OriLine(0, 0, 0, 1, Type.MOUNTAIN),

				new OriLine(-1, 0, 0, 1, Type.MOUNTAIN),
				new OriLine(-1, 0, 0, 1, Type.MOUNTAIN),
				new OriLine(-1, 0, 1, 2, Type.MOUNTAIN),

				new OriLine(-2, 2, 0, 0, Type.MOUNTAIN),
				new OriLine(-2, 2, 0, 0, Type.MOUNTAIN),
				new OriLine(-2, 2, 1, -1, Type.MOUNTAIN),
				new OriLine(-2, 2, 1, -1, Type.MOUNTAIN)

		);

		var merger = new OverlappingLineMerger();

		var result = merger.mergeIgnoringType(lines, 1e-8);

		assertEquals(3, result.size());

		AssertionUtil.assertAnyMatch(new OriLine(0, 0, 0, 1, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(-1, 0, 1, 2, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
		AssertionUtil.assertAnyMatch(new OriLine(-2, 2, 1, -1, Type.MOUNTAIN), result, (a, b) -> a.equals(b, 1e-8));
	}

}
