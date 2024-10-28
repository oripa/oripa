package oripa.vecmath;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;

class Matrix2dTest {

	@Test
	void testProduct() {
		// given
		Matrix2d m = new Matrix2d(1, 2, 3, 4);
		Vector2d v = new Vector2d(5, 6);

		// when
		Vector2d result = m.product(v);

		// then
		assertEquals(17, result.getX());
		assertEquals(39, result.getY());
	}

	@Test
	void testDeterminant() {
		// when
		Matrix2d m = new Matrix2d(1, 2, 3, 4);

		// then
		assertEquals(-2, m.determinant());
	}

	@Test
	void testRegular() {
		// when
		Matrix2d m = new Matrix2d(1, 2, 3, 4);

		// then
		assertTrue(m.isRegular());
		assertTrue(m.inverse().isPresent());
	}

	@Test
	void testIrregular() {
		// when
		Matrix2d m = new Matrix2d(1, 2, 1, 2);

		// then
		assertFalse(m.isRegular());
		assertFalse(m.inverse().isPresent());
	}

	@Test
	void testInverse() {
		// given
		Matrix2d m = new Matrix2d(1, 2, 3, 4);

		// when
		Optional<Matrix2d> result = m.inverse();

		// then
		assertTrue(result.isPresent());
		assertEquals(new Vector2d(-2, 1), result.get().rowVector(0));
		assertEquals(new Vector2d(1.5, -0.5), result.get().rowVector(1));
	}

}