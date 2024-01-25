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
import java.util.Optional;
import java.util.stream.Collectors;

import oripa.geom.GeomUtil;
import oripa.geom.Segment;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
public class RaySnapPointFactory {
	public Collection<Vector2d> createSnapPoints(final Collection<OriLine> creasePattern, final Segment ray,
			final double eps) {
		Collection<Vector2d> snapPoints = new ArrayList<>();

		// snap on cross points of line and creases.
		snapPoints.addAll(
				creasePattern.stream()
						.map(crease -> GeomUtil.getCrossPoint(ray, crease))
						.filter(Optional::isPresent)
						.map(Optional::get)
						.collect(Collectors.toList()));

		// snap on end points of overlapping creases.
		creasePattern.stream()
				.filter(crease -> overlapsEntirely(crease, ray, eps))
				.flatMap(crease -> crease.pointStream())
				.forEach(point -> {
					snapPoints.add(point);
				});

		return snapPoints;
	}

	private boolean sharesEndPoint(final Segment s1, final Segment s2, final double eps) {
		return findSharedEndPoint(s1, s2, eps).isPresent();
	}

	private boolean overlapsEntirely(final Segment crease, final Segment ray, final double eps) {
		if (!GeomUtil.isRelaxedOverlap(ray, crease, eps)) {
			return false;
		}

		return !sharesEndPoint(crease, ray, eps) || GeomUtil.distinguishLineSegmentsOverlap(ray, crease, eps) >= 3;
	}

	private Optional<Vector2d> findSharedEndPoint(final Segment s1, final Segment s2, final double eps) {
		return s1.pointStream()
				.filter(p -> s2.pointStream()
						.anyMatch(q -> GeomUtil.areEqual(p, q, eps)))
				.findFirst();
	}
}
