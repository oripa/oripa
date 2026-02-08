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
package oripa.domain.paint.suggestion;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickedVerticesConnectionLineAdderCommand;
import oripa.domain.paint.core.PickingVertex;
import oripa.domain.suggestion.MaekawaTheoremSuggester;
import oripa.domain.suggestion.TargetOriVertexFactory;
import oripa.util.Command;

/**
 * @author OUCHI Koji
 *
 */
public class SelectingEndPoint extends PickingVertex {
    @Override
    protected void initialize() {
        setPreviousClass(SelectingStartPoint.class);
        setNextClass(SelectingStartPoint.class);
    }

    @Override
    protected void onResult(final PaintContext context, final boolean doSpecial) {

        if (context.getVertexCount() != 2) {
            throw new IllegalStateException("wrong state: impossible vertex selection.");
        }

        var vertex = new TargetOriVertexFactory().create(context.getCreasePattern(), context.getVertex(0),
                context.getPointEps());
        var typeOpt = new MaekawaTheoremSuggester().suggest(vertex);

        typeOpt.ifPresentOrElse(type -> {
            Command command = new PickedVerticesConnectionLineAdderCommand(context, type);
            command.execute();
        }, () -> context.clear(false));
    }

    @Override
    protected void undoAction(final PaintContext context) {
        context.popVertex();
        context.clearSnapPoints();
    }

}
