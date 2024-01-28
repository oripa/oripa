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
package oripa.domain.paint.deleteline;

import java.util.List;

import oripa.domain.cptool.Painter;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.ValidatablePaintCommand;

/**
 * Removes all picked lines from crease pattern.
 *
 * @author OUCHI Koji
 *
 */
public class LineDeleterCommand extends ValidatablePaintCommand {
	private final PaintContext context;

	public LineDeleterCommand(final PaintContext context) {
		this.context = context;
	}

	@Override
	public void execute() {
		validateThat(() -> context.getLineCount() > 0, "Wrong state. There should be one or more pickedLines.");

		var lines = List.copyOf(context.getPickedLines());

		context.clear(true);

		context.creasePatternUndo().pushUndoInfo();
		Painter painter = context.getPainter();
		painter.removeLines(lines);

		context.refreshCreasePattern();
	}
}
