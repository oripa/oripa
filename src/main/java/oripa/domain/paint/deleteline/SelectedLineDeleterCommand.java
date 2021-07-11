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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.cptool.Painter;
import oripa.domain.paint.PaintContext;
import oripa.util.Command;

/**
 * Removes lines marked as selected state from crease pattern.
 *
 * @author OUCHI Koji
 *
 */
public class SelectedLineDeleterCommand implements Command {
	private static final Logger logger = LoggerFactory.getLogger(SelectedLineDeleterCommand.class);

	private final PaintContext context;

	public SelectedLineDeleterCommand(final PaintContext context) {
		this.context = context;
	}

	@Override
	public void execute() {
		context.creasePatternUndo().pushUndoInfo();

		Painter painter = context.getPainter();

		try {
			painter.removeSelectedLines();
		} catch (Exception ex) {
			logger.error("error when deleting selected lines", ex);
		}
		if (!context.isPasting()) {
			context.clear(false);
		}
	}
}
