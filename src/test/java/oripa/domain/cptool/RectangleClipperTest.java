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

import oripa.geom.GeomUtil;
import oripa.geom.Segment;
import oripa.test.util.AssertionUtil;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
class RectangleClipperTest {
	static final double EPS = 1e-8;

	RectangleClipper clipper = new RectangleClipper(0, 0, 100, 100, EPS);

	@Test
	void testClip() {

		// entirely inside
		var segmentInRectangle = new Segment(1, 1, 99, 99);
		assertClip(segmentInRectangle, segmentInRectangle);

		// left to inside
		assertClip(new Segment(0, 50, 50, 50), new Segment(-1, 50, 50, 50));

		// right to inside
		assertClip(new Segment(100, 50, 50, 50), new Segment(50, 50, 101, 50));

		// top to inside
		assertClip(new Segment(50, 0, 50, 50), new Segment(50, -1, 50, 50));

		// bottom to inside
		assertClip(new Segment(50, 100, 50, 50), new Segment(50, 50, 50, 101));

		// outside to outside with intersection
		assertClip(new Segment(0, 0, 100, 100), new Segment(-1, -1, 101, 101));

		// outside to outside without intersection
		assertClipFails(new Segment(-100, -100, 100, -1));

	}

	void assertClip(final Segment expected, final Segment segment) {
		var clippedOpt = clipper.clip(new OriLine(segment, OriLine.Type.MOUNTAIN));
		AssertionUtil.assertSegmentEquals(
				expected, clippedOpt.orElseThrow(), (a, b) -> GeomUtil.areEqual(a, b, EPS));
	}

	void assertClipFails(final Segment segment) {
		var clippedOpt = clipper.clip(new OriLine(segment, OriLine.Type.MOUNTAIN));
		assertTrue(clippedOpt.isEmpty());

	}

	@Test
	void testIntersects_segmentTouchesRectangle() {
		// mid point touches the corner (100, 100)
		assertIntersects(new Segment(0, 200, 200, 0));

		// end point touches the corner (0, 0)
		assertIntersects(new Segment(-10, 0, 0, 0));

		// end point touches the left edge
		assertIntersects(new Segment(-10, 10, 0, 9));
	}

	void assertIntersects(final Segment segment) {
		assertTrue(clipper.intersects(new OriLine(segment, OriLine.Type.MOUNTAIN)));
	}

}
