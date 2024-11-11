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
package oripa.persistence.foldformat;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
class CreasePatternElementConverterTest {

	private final Collection<OriLine> lines = List.of(
			new OriLine(0.0, 0.0, 1.0, 0.0, OriLine.Type.CUT),
			new OriLine(1.0, 0.0, 1.0, 1.0, OriLine.Type.CUT),
			new OriLine(1.0, 1.0, 0.0, 1.0, OriLine.Type.CUT),
			new OriLine(0.0, 1.0, 0.0, 0.0, OriLine.Type.CUT),

			new OriLine(0.0, 0.0, 1.0, 1.0, OriLine.Type.MOUNTAIN)

	);

	private List<Double> createCoord(final double x, final double y) {
		return List.of(x, y);
	}

	private List<Integer> createEdge(final int index1, final int index2) {
		return List.of(index1, index2);
	}

	/**
	 * Test method for
	 * {@link oripa.persistence.foldformat.CreasePatternElementConverter#toVerticesCoords(java.util.Collection)}.
	 */
	@Test
	void testToVerticesCoords() {
		var converter = new CreasePatternElementConverter();

		var coords = converter.toVerticesCoords(lines);

		assertEquals(4, coords.size());

		assertTrue(coords.contains(createCoord(0.0, 0.0)));
		assertTrue(coords.contains(createCoord(1.0, 0.0)));
		assertTrue(coords.contains(createCoord(1.0, 1.0)));
		assertTrue(coords.contains(createCoord(0.0, 1.0)));
	}

	/**
	 * Test method for
	 * {@link oripa.persistence.foldformat.CreasePatternElementConverter#toEdgesVertices(java.util.Collection)}.
	 */
	@Test
	void testToEdgesVertices() {
		var converter = new CreasePatternElementConverter();

		var edges = converter.toEdgesVertices(lines);

		assertEquals(createEdge(0, 1), edges.get(0));
		assertEquals(createEdge(0, 2), edges.get(4));
	}

	/**
	 * Test method for
	 * {@link oripa.persistence.foldformat.CreasePatternElementConverter#toEdgesAssignment(java.util.Collection)}.
	 */
	@Test
	void testToEdgesAssignment() {
		var converter = new CreasePatternElementConverter();

		var assignment = converter.toEdgesAssignment(lines);

		assertEquals("B", assignment.get(3));
		assertEquals("M", assignment.get(4));
	}

	@Test
	void testToFacesVertices() {
		var converter = new CreasePatternElementConverter();

		var facesVertices = converter.toFacesVertices(lines);

		assertEquals(2, facesVertices.size());

		assertEquals(3, facesVertices.get(0).size());
		assertEquals(3, facesVertices.get(1).size());

	}

}
