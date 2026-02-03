/*
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package oripa.geom;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import oripa.vecmath.Vector2d;

class GeomUtilTest {

    @Test
    void test_getCrossPoint_Line_Line() {
        var l0 = new Line(new Vector2d(1, 0), new Vector2d(-1, 1));
        var l1 = new Line(new Vector2d(0, 0), new Vector2d(1, 1));

        var cp = GeomUtil.getCrossPoint(l0, l1).get();

        assertEquals(0.5, cp.getX(), 1e-8);
        assertEquals(0.5, cp.getY(), 1e-8);

    }

    @Test
    void test_getBisectorLine() {
        var v0 = new Vector2d(1, 0);
        var v1 = new Vector2d(0, 0);
        var v2 = new Vector2d(0, 1);

        var bisector = GeomUtil.getBisectorLine(v0, v1, v2);

        var point = bisector.getPoint();
        var direction = bisector.getDirection();

        assertEquals(v1.getX(), point.getX(), 1e-8);
        assertEquals(v1.getY(), point.getY(), 1e-8);

        assertEquals(Math.sqrt(2) / 2, direction.getX(), 1e-8);
        assertEquals(Math.sqrt(2) / 2, direction.getY(), 1e-8);
    }

    @ParameterizedTest
    @CsvSource({
            "1,1, 0,0, 2,0, 1,0",
    })
    void testGetNearestPointToSegment(final double px, final double py, final double sx, final double sy,
            final double ex, final double ey, final double nx, final double ny) {
        var p = new Vector2d(px, py);
        var segment = new Segment(sx, sy, ex, ey);
        var nearest = GeomUtil.getNearestPointToSegment(p, segment);

        assertEquals(nx, nearest.getX(), 1e-8);
        assertEquals(ny, nearest.getY(), 1e-8);
    }
}