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
class PointToLinePointToLineAxiomTest {

    @Test
    void testXCubicMinus2Eq0() {
        var p0 = new Vector2d(0, 1);
        var s0 = new Segment(new Vector2d(-10, -1), new Vector2d(10, -1));
        var p1 = new Vector2d(-2, 0);
        var s1 = new Segment(new Vector2d(2, -10), new Vector2d(2, 10));

        var axiom = new PointToLinePointToLineAxiom();

        var lines = axiom.createFoldLines(p0, s0, p1, s1, 10, 1e-6);

        assertEquals(1, lines.size());

        var dir = lines.get(0).getDirection();
        double slope = dir.getY() / dir.getX();
        assertEquals(Math.pow(2, 1.0 / 3), slope, 1e-6);

    }

}
