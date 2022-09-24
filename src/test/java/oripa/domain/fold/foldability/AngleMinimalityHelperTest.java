package oripa.domain.fold.foldability;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import oripa.domain.fold.foldability.ring.RingArrayList;
import oripa.value.OriLine;

class AngleMinimalityHelperTest {

	private final double UNIT_ANGLE = Math.PI / 8;

	@Test
	void test_differentAngle_foldable() {
		var ring = new RingArrayList<>(List.of(
				new LineGap(2 * UNIT_ANGLE, OriLine.Type.MOUNTAIN.toInt()),
				new LineGap(1 * UNIT_ANGLE, OriLine.Type.VALLEY.toInt()),
				new LineGap(2 * UNIT_ANGLE, OriLine.Type.MOUNTAIN.toInt())));

		var helper = new AngleMinimalityHelper();

		assertTrue(helper.isMinimal(ring, 1));
	}

	@Test
	void test_sameAngle_foldable() {
		var ring = new RingArrayList<>(List.of(
				new LineGap(1 * UNIT_ANGLE, OriLine.Type.MOUNTAIN.toInt()),
				new LineGap(1 * UNIT_ANGLE, OriLine.Type.VALLEY.toInt()),
				new LineGap(1 * UNIT_ANGLE, OriLine.Type.MOUNTAIN.toInt())));

		var helper = new AngleMinimalityHelper();

		assertTrue(helper.isMinimal(ring, 1));
	}

	@Test
	void test_sameAngle_unfoldable() {
		var ring = new RingArrayList<>(List.of(
				new LineGap(1 * UNIT_ANGLE, OriLine.Type.MOUNTAIN.toInt()),
				new LineGap(1 * UNIT_ANGLE, OriLine.Type.VALLEY.toInt()),
				new LineGap(1 * UNIT_ANGLE, OriLine.Type.VALLEY.toInt())));

		var helper = new AngleMinimalityHelper();

		assertFalse(helper.isMinimal(ring, 1));
	}

	@Test
	void test_foldPartially() {
		var ring = new RingArrayList<>(List.of(
				new LineGap(5 * UNIT_ANGLE, OriLine.Type.MOUNTAIN.toInt()),
				new LineGap(2 * UNIT_ANGLE, OriLine.Type.MOUNTAIN.toInt()),
				new LineGap(1 * UNIT_ANGLE, OriLine.Type.VALLEY.toInt()),
				new LineGap(2 * UNIT_ANGLE, OriLine.Type.VALLEY.toInt())));

		var helper = new AngleMinimalityHelper();

		var mergedIndex = helper.foldPartially(ring, 2);

		assertEquals(1, mergedIndex);
		assertEquals(2, ring.size());

		assertEquals(3 * UNIT_ANGLE, ring.get(1).getAngleGap());

	}

}
