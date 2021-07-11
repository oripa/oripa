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
package oripa.domain.paint.linetype;

import java.util.List;

import oripa.domain.paint.PaintContext;
import oripa.util.Command;

/**
 * @author OUCHI Koji
 *
 */
public class LineTypeChangerCommand implements Command {
	private final PaintContext context;
	private final TypeForChangeGettable setting;

	public LineTypeChangerCommand(final PaintContext context, final TypeForChangeGettable setting) {
		this.context = context;
		this.setting = setting;
	}

	@Override
	public void execute() {
		var lines = List.copyOf(context.getPickedLines());
		context.clear(false);

		context.creasePatternUndo().pushUndoInfo();

		var painter = context.getPainter();
		painter.alterLineTypes(lines, setting.getTypeFrom(), setting.getTypeTo());
	}

}
