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

import java.util.List;

import org.junit.jupiter.api.Test;

import oripa.value.OriLine;
import oripa.value.OriLine.Type;

/**
 * @author OUCHI Koji
 *
 */
class LineMirrorTest {

    /**
     * Test method for
     * {@link oripa.domain.cptool.LineMirror#createMirroredLines(oripa.value.OriLine, java.util.Collection)}.
     */
    @Test
    void testCreateMirroredLines() {
        var baseLine = new OriLine(50, -100, 50, 100, OriLine.Type.AUX);
        var toBeCopied = List.of(
                new OriLine(0, 0, 10, 0, Type.MOUNTAIN),
                new OriLine(20, 20, 20, 0, Type.VALLEY),
                baseLine);

        var mirror = new LineMirror();

        var mirroredLines = mirror.createMirroredLines(baseLine, toBeCopied);

        assertEquals(2, mirroredLines.size());

        var mirroredLine1 = new OriLine(100, 0, 90, 0, Type.MOUNTAIN);
        assertTrue(mirroredLines.stream().anyMatch(l -> l.equals(mirroredLine1, 1e-8)));

        var mirroredLine2 = new OriLine(80, 20, 80, 0, Type.VALLEY);
        assertTrue(mirroredLines.stream().anyMatch(l -> l.equals(mirroredLine2, 1e-8)));
    }

}
