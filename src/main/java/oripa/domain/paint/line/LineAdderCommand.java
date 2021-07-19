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
package oripa.domain.paint.line;

import java.util.Optional;

import javax.vecmath.Vector2d;

import oripa.domain.cptool.Painter;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.ValidatablePaintCommand;
import oripa.geom.GeomUtil;
import oripa.geom.Segment;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class LineAdderCommand extends ValidatablePaintCommand {
	private final PaintContext context;

	public LineAdderCommand(final PaintContext context) {
		this.context = context;
	}

	@Override
	public void execute() {
		validateCounts(context, 2, 0);

		Vector2d p0, p1;
		p0 = context.getVertex(0);
		p1 = context.getVertex(1);

		double paperSize = context.getCreasePattern().getPaperSize();

		Vector2d dir = new Vector2d(p0.x - p1.x, p0.y - p1.y);
		dir.normalize();
		dir.scale(paperSize * 8);

		// create new line
		Segment line = new Segment(p0.x - dir.x, p0.y - dir.y,
				p0.x + dir.x, p0.y + dir.y);

		Optional<Segment> clippedOpt = GeomUtil.clipLine(line, context.getPaperDomain());
		clippedOpt.ifPresent(clippedSeg -> {
			// add new line to crease pattern
			context.creasePatternUndo().pushUndoInfo();
			Painter painter = context.getPainter();
			painter.addLine(new OriLine(clippedSeg, context.getLineTypeOfNewLines()));
		});

		context.clear(false);
	}

}
