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
package oripa.domain.paint.outline;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.vecmath.Vector2d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.cptool.Painter;
import oripa.domain.paint.PaintContext;
import oripa.geom.GeomUtil;
import oripa.util.Command;

/**
 * @author OUCHI Koji
 *
 */
public class OutlineEditerCommand implements Command {
	private static final Logger logger = LoggerFactory.getLogger(OutlineEditerCommand.class);

	private final PaintContext context;
	private final CloseTempOutlineFactory closeTempOutlineFactory;

	public OutlineEditerCommand(final PaintContext context, final CloseTempOutlineFactory factory) {
		this.context = context;
		this.closeTempOutlineFactory = factory;
	}

	@Override
	public void execute() {
		logger.debug("# of picked vertices (before): " + context.getPickedVertices().size());

		Vector2d v = context.peekVertex();

		var pickedVertices = context.getPickedVertices().subList(0, context.getVertexCount() - 1).stream()
				.distinct()
				.collect(Collectors.toList());

		if (pickedVertices.stream()
				.anyMatch(tv -> GeomUtil.distance(v, tv) < 1)) {
			if (pickedVertices.size() > 2) {
				// finish editing
				context.creasePatternUndo().pushUndoInfo();
				closeTmpOutline(pickedVertices, context.getPainter());

				context.clear(false);
			}
		}

		logger.debug("# of picked vertices (after): " + context.getPickedVertices().size());
	}

	private void closeTmpOutline(final Collection<Vector2d> outlineVertices, final Painter painter) {
		closeTempOutlineFactory.create().execute(outlineVertices, painter);
	}
}
