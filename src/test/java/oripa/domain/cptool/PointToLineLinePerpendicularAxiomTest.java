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

import oripa.geom.Segment;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
class PointToLineLinePerpendicularAxiomTest {

	@Test
	void test45Degrees() {

		var p = new Vector2d(0, 5);
		var s = new Segment(new Vector2d(-10, 0), new Vector2d(10, 0));
		var perpendicular = new Segment(new Vector2d(0, 10), new Vector2d(10, 0));

		var lineOpt = new PointToLineLinePerpendicularAxiom().createFoldLine(p, s, perpendicular);

		assertTrue(lineOpt.isPresent());

		assertEquals(1, lineOpt.get().getDirection().getSlope(), 1e-8);
	}
}
