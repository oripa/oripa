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
package oripa.domain.creasepattern;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import oripa.geom.RectangleDomain;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
class OriLineClipTest {

	@Test
	void testPositveSlope_2() {
		var clip = new OriLineClip(new RectangleDomain(0, 0, 16, 16));

		clip.add(new OriLine(1.2, 1.2, 3.2, 5.2, OriLine.Type.MOUNTAIN));

		var clipped = clip.clip(new RectangleDomain(0, 0, 1, 1), 0);
		assertTrue(clipped.isEmpty());

		clipped = clip.clip(new RectangleDomain(1, 1, 2, 2), 0);
		assertTrue(!clipped.isEmpty());

		clipped = clip.clip(new RectangleDomain(1, 2, 2, 3), 0);
		assertTrue(!clipped.isEmpty());
		clipped = clip.clip(new RectangleDomain(2, 2, 3, 3), 0);
		assertTrue(!clipped.isEmpty());
		clipped = clip.clip(new RectangleDomain(2, 3, 3, 4), 0);
		assertTrue(!clipped.isEmpty());
		clipped = clip.clip(new RectangleDomain(2, 4, 3, 5), 0);
		assertTrue(!clipped.isEmpty());
		clipped = clip.clip(new RectangleDomain(3, 4, 4, 5), 0);
		assertTrue(!clipped.isEmpty());

		clipped = clip.clip(new RectangleDomain(3, 5, 4, 6), 0);
		assertTrue(!clipped.isEmpty());
	}

	@Test
	void testNegativeSlope_2() {
		var clip = new OriLineClip(new RectangleDomain(0, 0, 16, 16));

		clip.add(new OriLine(1.2, 5.2, 3.2, 1.2, OriLine.Type.MOUNTAIN));

		var clipped = clip.clip(new RectangleDomain(0, 5, 1, 6), 0);
		assertTrue(clipped.isEmpty());

		clipped = clip.clip(new RectangleDomain(1, 5, 2, 6), 0);
		assertTrue(!clipped.isEmpty());

		clipped = clip.clip(new RectangleDomain(1, 4, 2, 5), 0);
		assertTrue(!clipped.isEmpty());
		clipped = clip.clip(new RectangleDomain(1, 3, 2, 4), 0);
		assertTrue(!clipped.isEmpty());
		clipped = clip.clip(new RectangleDomain(2, 3, 3, 4), 0);
		assertTrue(!clipped.isEmpty());
		clipped = clip.clip(new RectangleDomain(2, 2, 3, 3), 0);
		assertTrue(!clipped.isEmpty());
		clipped = clip.clip(new RectangleDomain(2, 1, 3, 2), 0);
		assertTrue(!clipped.isEmpty());

		clipped = clip.clip(new RectangleDomain(3, 1, 4, 2), 0);
		assertTrue(!clipped.isEmpty());
	}

	@Test
	void testVertical() {
		var clip = new OriLineClip(new RectangleDomain(0, 0, 16, 16));

		clip.add(new OriLine(2.9, 4.5, 2.9, 8.9, OriLine.Type.MOUNTAIN));

		var clipped = clip.clip(new RectangleDomain(2, 3, 3, 4), 0);
		assertTrue(clipped.isEmpty());

		clipped = clip.clip(new RectangleDomain(2, 4, 3, 5), 0);
		assertTrue(!clipped.isEmpty());
		clipped = clip.clip(new RectangleDomain(2, 5, 3, 6), 0);
		assertTrue(!clipped.isEmpty());
		clipped = clip.clip(new RectangleDomain(2, 6, 3, 7), 0);
		assertTrue(!clipped.isEmpty());
		clipped = clip.clip(new RectangleDomain(2, 7, 3, 8), 0);
		assertTrue(!clipped.isEmpty());
		clipped = clip.clip(new RectangleDomain(2, 8, 3, 9), 0);
		assertTrue(!clipped.isEmpty());

		clipped = clip.clip(new RectangleDomain(2, 9, 3, 10), 0);
		assertTrue(clipped.isEmpty());
	}

	@Test
	void testHorizontal() {
		var clip = new OriLineClip(new RectangleDomain(0, 0, 16, 16));

		clip.add(new OriLine(2.2, 3.4, 5.6, 3.4, OriLine.Type.MOUNTAIN));

		var clipped = clip.clip(new RectangleDomain(1, 3, 2, 4), 0);
		assertTrue(clipped.isEmpty());

		clipped = clip.clip(new RectangleDomain(2, 3, 3, 4), 0);
		assertTrue(!clipped.isEmpty());
		clipped = clip.clip(new RectangleDomain(4, 3, 5, 4), 0);
		assertTrue(!clipped.isEmpty());
		clipped = clip.clip(new RectangleDomain(5, 3, 6, 4), 0);
		assertTrue(!clipped.isEmpty());

		clipped = clip.clip(new RectangleDomain(6, 3, 7, 4), 0);
		assertTrue(clipped.isEmpty());
	}

}
