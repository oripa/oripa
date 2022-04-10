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

import org.junit.jupiter.api.Test;
import oripa.value.OriLine;

import static org.junit.jupiter.api.Assertions.*;
import static oripa.geom.GeomUtil.detectOverlap;

class GeomUtilTest {

    @Test
    void should_detect_overlap_if_same_line_and_overlap() {
        OriLine l1 = new OriLine(0, 0, 30, 30, OriLine.Type.VALLEY);
        OriLine l2 = new OriLine(20, 20, 40, 40, OriLine.Type.MOUNTAIN);

        assertTrue(detectOverlap(l1, l2));

        OriLine l3 = new OriLine(0, 0, 0, 30, OriLine.Type.VALLEY);
        OriLine l4 = new OriLine(0, 0, 0, 40, OriLine.Type.MOUNTAIN);

        assertTrue(detectOverlap(l3, l4));
    }

    @Test
    void should_detect_overlap_if_same_line_and_included() {
        OriLine l1 = new OriLine(0, 0, 30, 30, OriLine.Type.VALLEY);
        OriLine l2 = new OriLine(20, 20, 25, 25, OriLine.Type.MOUNTAIN);

        assertTrue(detectOverlap(l1, l2));

        OriLine l3 = new OriLine(0, 0, 0, 30, OriLine.Type.VALLEY);
        OriLine l4 = new OriLine(0, 20, 0, 25, OriLine.Type.MOUNTAIN);

        assertTrue(detectOverlap(l3, l4));
    }

    @Test
    void should_not_detect_overlap_if_same_line_but_disjoint() {
        OriLine l1 = new OriLine(0, 0, 10, 10, OriLine.Type.VALLEY);
        OriLine l2 = new OriLine(20, 20, 30, 30, OriLine.Type.MOUNTAIN);

        assertFalse(detectOverlap(l1, l2));

        OriLine l3 = new OriLine(20, 20, 30, 30, OriLine.Type.VALLEY);
        OriLine l4 = new OriLine(0, 0, 10, 10, OriLine.Type.MOUNTAIN);

        assertFalse(detectOverlap(l3, l4));

        OriLine l5 = new OriLine(0, 0, 0, 30, OriLine.Type.VALLEY);
        OriLine l6 = new OriLine(0, 30, 0, 40, OriLine.Type.MOUNTAIN);

        assertFalse(detectOverlap(l5, l6));
    }

    @Test
    void should_not_detect_overlap_if_parallel() {
        OriLine l1 = new OriLine(0, 0, 10, 10, OriLine.Type.VALLEY);
        OriLine l2 = new OriLine(25, 20, 35, 30, OriLine.Type.MOUNTAIN);

        assertFalse(detectOverlap(l1, l2));

        OriLine l3 = new OriLine(0, 0, 0, 10, OriLine.Type.VALLEY);
        OriLine l4 = new OriLine(10, 0, 10, 10, OriLine.Type.MOUNTAIN);

        assertFalse(detectOverlap(l3, l4));
    }
}