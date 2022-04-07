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
package oripa.domain.paint.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.vecmath.Vector2d;

import oripa.domain.paint.PaintContext;
import oripa.geom.GeomUtil;
import oripa.geom.Segment;

/**
 * @author OUCHI Koji
 *
 */
public class SnapPointFactory {
	public Collection<Vector2d> createSnapPoints(final PaintContext context, final Segment line) {
		Collection<Vector2d> snapPoints = new ArrayList<>();

		// snap on cross points of line and creases.
		snapPoints.addAll(
				context.getCreasePattern().stream()
						.map(crease -> GeomUtil.getCrossPoint(line, crease))
						.filter(Objects::nonNull)
						.collect(Collectors.toList()));

		// snap on end points of overlapping creases.
		context.getCreasePattern().stream()
				.filter(crease -> GeomUtil.isLineSegmentsOverlap(
						line.getP0(), line.getP1(), crease.getP0(), crease.getP1()))
				.forEach(crease -> {
					snapPoints.add(crease.getP0());
					snapPoints.add(crease.getP1());
				});

		return snapPoints;
	}
}
