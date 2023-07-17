package oripa.domain.fold.foldability;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import oripa.domain.fold.foldability.ring.RingArrayList;
import oripa.value.OriLine;

class MinimalAngleFolderTest {
	private final double UNIT_ANGLE = Math.PI / 8;

	@Test
	void testFoldPartially() {
		var ring = new RingArrayList<>(List.of(
				new LineGap(5 * UNIT_ANGLE, OriLine.Type.MOUNTAIN.toInt()),
				new LineGap(2 * UNIT_ANGLE, OriLine.Type.MOUNTAIN.toInt()),
				new LineGap(1 * UNIT_ANGLE, OriLine.Type.VALLEY.toInt()),
				new LineGap(2 * UNIT_ANGLE, OriLine.Type.VALLEY.toInt())));

		MinimalAngleIndexManager indicesMock = mock(MinimalAngleIndexManager.class);

		var folder = new MinimalAngleFolder();

		var mergedIndex = folder.foldPartially(ring, 2, indicesMock);

		assertEquals(1, mergedIndex);
		assertEquals(2, ring.size());

		assertEquals(3 * UNIT_ANGLE, ring.get(1).getAngleGap());

		verify(indicesMock).remove(1);
		verify(indicesMock).remove(2);
		verify(indicesMock).remove(3);
	}

}
