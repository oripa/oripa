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
package oripa.domain.paint.byvalue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.cptool.Painter;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.ValidatablePaintCommand;
import oripa.geom.GeomUtil;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
public class LineByValueCommand extends ValidatablePaintCommand {
	private final static Logger logger = LoggerFactory.getLogger(LineByValueCommand.class);

	private final PaintContext context;
	private final ByValueContext valueSetting;

	public LineByValueCommand(final PaintContext context, final ByValueContext valueSetting) {
		this.context = context;
		this.valueSetting = valueSetting;
	}

	@Override
	public void execute() {
		final int correctVertexCount = 1;
		final int correctLineCount = 0;
		validateCounts(context, correctVertexCount, correctLineCount);

		Vector2d vertex = context.popVertex().get();

		try {
			var length = valueSetting.getLength();
			var angle = valueSetting.getAngle();

			logger.debug("length = " + length);

			if (length <= 0) {
				return;
			}

			OriLine vl = new OriLine(GeomUtil.getSegmentByValue(vertex, length, -angle),
					context.getLineTypeOfNewLines());

			context.creasePatternUndo().pushUndoInfo();

			Painter painter = context.getPainter();
			painter.addLine(vl);

			context.refreshCreasePattern();

		} catch (Exception ex) {
			logger.error("unknown error: ", ex);
		}
	}

}
