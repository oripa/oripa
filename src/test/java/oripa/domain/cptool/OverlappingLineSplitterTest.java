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

package oripa.domain.cptool;

import org.junit.jupiter.api.Test;
import oripa.value.OriLine;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static oripa.domain.cptool.OverlappingLineSplitter.splitLinesIfOverlap;

class OverlappingLineSplitterTest {

    @Test
    void should_split_if_same_line_and_included() {
        OriLine existingLine = new OriLine(0, 0, 30, 30, OriLine.Type.VALLEY);
        OriLine newLine = new OriLine(20, 20, 25, 25, OriLine.Type.MOUNTAIN);

        List<OriLine> oriLines = splitLinesIfOverlap(existingLine, newLine);
        assertEquals(3, oriLines.size());
        assertTrue(oriLines.contains(new OriLine(0,0,20,20, OriLine.Type.VALLEY)));
        assertTrue(oriLines.contains(new OriLine(20,20,25,25, OriLine.Type.MOUNTAIN)));
        assertTrue(oriLines.contains(new OriLine(25,25,30,30, OriLine.Type.VALLEY)));
    }

    @Test
    void should_not_add_0_length_lines() {
        OriLine existingLine = new OriLine(0, 0, 30, 30, OriLine.Type.VALLEY);
        OriLine newLine = new OriLine(0, 0, 30, 30, OriLine.Type.MOUNTAIN);

        List<OriLine> oriLines = splitLinesIfOverlap(existingLine, newLine);
        assertEquals(1, oriLines.size());
        assertTrue(oriLines.contains(new OriLine(0,0,30,30, OriLine.Type.MOUNTAIN)));
    }

}