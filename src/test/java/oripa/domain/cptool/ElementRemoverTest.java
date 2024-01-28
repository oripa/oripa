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

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import oripa.value.OriLine;
import oripa.value.OriPoint;

/**
 * @author OUCHI Koji
 *
 */
class ElementRemoverTest {

	static final double EPS = 1e-5;

	/**
	 * Test method for
	 * {@link oripa.domain.cptool.ElementRemover#removeLine(oripa.value.OriLine, java.util.Collection)}.
	 */
	@Test
	void testRemoveLine() {
		var toBeRemoved = new OriLine(50, 50, 50, 0, OriLine.Type.VALLEY);

		var creasePattern = new ArrayList<OriLine>(List.of(
				new OriLine(0, 0, 50, 0, OriLine.Type.MOUNTAIN),
				new OriLine(50, 0, 100, 0, OriLine.Type.MOUNTAIN),

				new OriLine(0, 50, 50, 50, OriLine.Type.AUX),
				new OriLine(50, 50, 100, 50, OriLine.Type.VALLEY),
				toBeRemoved));

		var remover = new ElementRemover();

		remover.removeLine(toBeRemoved, creasePattern, EPS);

		// merge will happen
		assertEquals(3, creasePattern.size());
		assertFalse(creasePattern.contains(toBeRemoved));
	}

	/**
	 * Test method for
	 * {@link oripa.domain.cptool.ElementRemover#removeVertex(javax.vecmath.Vector2d, java.util.Collection)}.
	 */
	@Test
	void testRemoveVertex_shouldBeRemoved() {
		var toBeRemoved = new OriPoint(50, 0);
		var creasePattern = new ArrayList<OriLine>(List.of(
				new OriLine(0, 0, 50, 0, OriLine.Type.MOUNTAIN),
				new OriLine(50, 0, 100, 0, OriLine.Type.MOUNTAIN),

				new OriLine(0, 50, 50, 50, OriLine.Type.AUX),
				new OriLine(50, 50, 100, 50, OriLine.Type.VALLEY)));

		var remover = new ElementRemover();

		remover.removeVertex(toBeRemoved, creasePattern, EPS);
		assertEquals(3, creasePattern.size());
	}

	@Test
	void testRemoveVertex_shouldNotBeRemoved_notParallel() {
		var shouldNotBeRemoved = new OriPoint(50, 0);
		var creasePattern = new ArrayList<OriLine>(List.of(
				new OriLine(0, 0, 50, 0, OriLine.Type.MOUNTAIN),
				new OriLine(50, 0, 100, 1, OriLine.Type.MOUNTAIN),

				new OriLine(0, 50, 50, 50, OriLine.Type.AUX),
				new OriLine(50, 50, 100, 50, OriLine.Type.VALLEY)));

		var remover = new ElementRemover();

		remover.removeVertex(shouldNotBeRemoved, creasePattern, EPS);
		assertEquals(4, creasePattern.size());
	}

	@Test
	void testRemoveVertex_shouldNotBeRemoved_differentType() {
		var shouldNotBeRemoved = new OriPoint(50, 50);
		var creasePattern = new ArrayList<OriLine>(List.of(
				new OriLine(0, 0, 50, 0, OriLine.Type.MOUNTAIN),
				new OriLine(50, 0, 100, 0, OriLine.Type.MOUNTAIN),

				new OriLine(0, 50, 50, 50, OriLine.Type.AUX),
				new OriLine(50, 50, 100, 50, OriLine.Type.VALLEY)));

		var remover = new ElementRemover();

		remover.removeVertex(shouldNotBeRemoved, creasePattern, EPS);
		assertEquals(4, creasePattern.size());
	}

	/**
	 * Test method for
	 * {@link oripa.domain.cptool.ElementRemover#removeMeaninglessVertices(java.util.Collection)}.
	 */
	@Test
	void testRemoveMeaninglessVertices() {
		var creasePattern = new ArrayList<OriLine>(List.of(
				new OriLine(0, 0, 50, 0, OriLine.Type.MOUNTAIN),
				new OriLine(50, 0, 100, 0, OriLine.Type.MOUNTAIN),

				new OriLine(0, 50, 50, 50, OriLine.Type.VALLEY),
				new OriLine(50, 50, 100, 50, OriLine.Type.VALLEY)));

		var remover = new ElementRemover();

		remover.removeMeaninglessVertices(creasePattern, EPS);
		assertEquals(2, creasePattern.size());
	}

	/**
	 * Test method for
	 * {@link oripa.domain.cptool.ElementRemover#removeLines(java.util.Collection, java.util.Collection)}.
	 */
	@Test
	void testRemoveLines() {
		var toBeRemoved1 = new OriLine(50, 50, 50, 0, OriLine.Type.VALLEY);
		var toBeRemoved2 = new OriLine(50, 50, 100, 0, OriLine.Type.MOUNTAIN);

		var creasePattern = new ArrayList<OriLine>(List.of(
				new OriLine(0, 0, 50, 0, OriLine.Type.MOUNTAIN),
				new OriLine(50, 0, 100, 0, OriLine.Type.MOUNTAIN),

				new OriLine(0, 50, 50, 50, OriLine.Type.AUX),
				new OriLine(50, 50, 100, 50, OriLine.Type.VALLEY),

				toBeRemoved1, toBeRemoved2));

		var remover = new ElementRemover();

		remover.removeLines(List.of(toBeRemoved1, toBeRemoved2), creasePattern, EPS);
		assertEquals(3, creasePattern.size());
	}

	/**
	 * Test method for
	 * {@link oripa.domain.cptool.ElementRemover#removeSelectedLines(java.util.Collection)}.
	 */
	@Test
	void testRemoveSelectedLines() {
		var toBeRemoved1 = new OriLine(0, 50, 50, 50, OriLine.Type.VALLEY);
		toBeRemoved1.setSelected(true);
		var toBeRemoved2 = new OriLine(50, 50, 100, 100, OriLine.Type.MOUNTAIN);
		toBeRemoved2.setSelected(true);

		var creasePattern = new ArrayList<OriLine>(List.of(
				new OriLine(0, 0, 0, 50, OriLine.Type.MOUNTAIN),
				new OriLine(0, 50, 0, 100, OriLine.Type.MOUNTAIN),

				new OriLine(50, 0, 50, 50, OriLine.Type.AUX),
				new OriLine(50, 50, 50, 100, OriLine.Type.VALLEY),

				toBeRemoved1, toBeRemoved2));

		var remover = new ElementRemover();

		remover.removeSelectedLines(creasePattern, EPS);
		assertEquals(3, creasePattern.size());
	}
}
