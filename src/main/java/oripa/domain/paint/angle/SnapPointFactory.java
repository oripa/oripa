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
package oripa.domain.paint.angle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.vecmath.Vector2d;

import oripa.domain.paint.PaintContext;
import oripa.geom.GeomUtil;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class SnapPointFactory {
	public Collection<Vector2d> createSnapPoints(final PaintContext context) {
		var step = context.getAngleStep();
		var paperSize = context.getCreasePattern().getPaperSize();

		var crossPoints = new ArrayList<Vector2d>();

		var sp = context.peekVertex();
		for (int i = 0; i < step.getDivNum() * 2; i++) {
			double angle = i * step.getRadianStep();
			double dx = paperSize * 4 * Math.cos(angle);
			double dy = paperSize * 4 * Math.sin(angle);
			var line = new OriLine(sp.x, sp.y,
					sp.x + dx, sp.y + dy, OriLine.Type.AUX);

			// snap on cross points of angle line and creases.
			crossPoints.addAll(
					context.getCreasePattern().stream()
							.map(crease -> GeomUtil.getCrossPoint(line, crease))
							.filter(Objects::nonNull)
							.collect(Collectors.toList()));

			// snap on end points of overlapping creases.
			context.getCreasePattern().stream()
					.filter(crease -> GeomUtil.isLineSegmentsOverlap(
							line.p0, line.p1, crease.p0, crease.p1))
					.forEach(crease -> {
						crossPoints.add(crease.p0);
						crossPoints.add(crease.p1);
					});
		}

		return crossPoints;
	}
}
