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
package oripa.domain.paint.copypaste;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import oripa.domain.cptool.Painter;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.ValidatablePaintCommand;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
public class LinePasterCommand extends ValidatablePaintCommand {
	private final PaintContext context;
	private final SelectionOriginHolder originHolder;
	private final ShiftedLineFactory factory;

	public LinePasterCommand(final PaintContext context, final SelectionOriginHolder originHolder,
			final ShiftedLineFactory factory) {
		this.context = context;
		this.originHolder = originHolder;
		this.factory = factory;
	}

	@Override
	public void execute() {
		validateThat(() -> context.getVertexCount() == 1, "Wrong state. There should be 1 pickedVertices.");

		Vector2d v = context.popVertex().get();

		if (context.getLineCount() == 0) {
			return;
		}

		context.creasePatternUndo().pushUndoInfo();

		Vector2d origin = originHolder.getOrigin(context).orElseThrow();

		var offset = factory.createOffset(origin, v);

		Painter painter = context.getPainter();
		painter.addLines(
				shiftLines(context.getPickedLines(), offset.getX(), offset.getY()));

		context.refreshCreasePattern();
	}

	private List<OriLine> shiftLines(final Collection<OriLine> lines,
			final double diffX, final double diffY) {

		return lines.stream()
				.map(l -> factory.createShiftedLine(l, diffX, diffY))
				.collect(Collectors.toList());
	}
}
