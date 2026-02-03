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
package oripa.domain.paint.test;

import static org.junit.jupiter.api.Assertions.*;

import oripa.domain.paint.ActionState;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.PaintContextFactory;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
public class InputStatesTestBase {
    protected PaintContext context;
    protected int cpLineCount;
    protected ActionState state;

    protected <FirstState extends ActionState> void setUp(final Class<FirstState> stateClass) {
        context = new PaintContextFactory().createContext();
        context.setLineTypeOfNewLines(OriLine.Type.MOUNTAIN);

        try {
            state = stateClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void doAction(final Vector2d candidate) {
        cpLineCount = context.getCreasePattern().size();
        context.setCandidateVertexToPick(candidate);
        state = state.doAction(context, false);
    }

    protected void doAction(final OriLine candidate) {
        cpLineCount = context.getCreasePattern().size();
        context.setCandidateLineToPick(candidate);
        state = state.doAction(context, false);

    }

    protected void assertSnapPointExists() {
        assertTrue(context.getSnapPoints().size() > 0);
    }

    protected void assertNewLineInput() {
        assertTrue(context.getCreasePattern().size() > cpLineCount);
    }

}
