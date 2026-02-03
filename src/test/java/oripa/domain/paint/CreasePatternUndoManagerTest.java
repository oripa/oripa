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
package oripa.domain.paint;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class CreasePatternUndoManagerTest {
    private static final Logger logger = LoggerFactory
            .getLogger(CreasePatternUndoManagerTest.class);

    Collection<OriLine> createOriLines(final double x0, final double y0, final double x1,
            final double y1) {
        Collection<OriLine> lines = new ArrayList<>();
        lines.add(new OriLine(x0, y0, x1, y1, OriLine.Type.MOUNTAIN));
        return lines;
    }

    @Test
    public void testUndo() {
        var manager = new CreasePatternUndoManager();

        final int count = 3;

        for (int i = 0; i < count; i++) {
            manager.push(createOriLines(i, i, i, i));
        }

        var lines = createOriLines(count, count, count, count);
        for (int i = count - 1; i >= 0; i--) {
            lines = manager.undo(lines).get().getInfo();
            assertEquals(i, ((OriLine) lines.toArray()[0]).getP0().getX());
        }

        assertFalse(manager.canUndo());
    }

    @Test
    public void testRedo() {
        var manager = new CreasePatternUndoManager();

        final int count = 3;

        for (int i = 0; i < count; i++) {
            manager.push(createOriLines(i, i, i, i));
        }

        var lines = createOriLines(count, count, count, count);
        for (int i = count - 1; i >= 0; i--) {
            lines = manager.undo(lines).get().getInfo();
            var p0x = ((OriLine) lines.toArray()[0]).getP0().getX();
            logger.debug("undo result: " + p0x);
        }

        for (int i = 0; i < count; i++) {
            var l = manager.redo().get().getInfo();
            var p0x = ((OriLine) l.toArray()[0]).getP0().getX();
            assertEquals(i + 1, p0x);
            logger.debug("redo result: " + p0x);
        }

        assertFalse(manager.canRedo());
    }

    @Test
    public void testDiscardingOldRedo() {
        var manager = new CreasePatternUndoManager();

        for (int i = 0; i < 5; i++) {
            manager.push(createOriLines(i, i, i, i));
        }

        var lines = createOriLines(5, 5, 5, 5);
        for (int i = 2; i >= 0; i--) {
            lines = manager.undo(lines).get().getInfo();
//			assertTrue(manager.canRedo());
        }

        manager.push(createOriLines(9, 9, 9, 9));

        assertFalse(manager.canRedo());
        assertTrue(manager.redo().isEmpty());
    }
}
