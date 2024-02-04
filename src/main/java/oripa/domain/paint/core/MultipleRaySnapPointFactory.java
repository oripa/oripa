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

import java.util.Collection;
import java.util.function.Function;

import oripa.domain.creasepattern.CreasePattern;
import oripa.geom.GeomUtil;
import oripa.geom.Ray;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
public class MultipleRaySnapPointFactory {

	public Collection<Vector2d> createSnapPoints(
			final CreasePattern creasePattern,
			final Vector2d v,
			final Collection<Double> angles,
			final double pointEps) {

		var snapPointFactory = new RaySnapPointFactory();
		Function<Double, Ray> createRay = angle -> new Ray(v, angle);

		return angles.stream()
				.map(createRay)
				.flatMap(ray -> snapPointFactory.createSnapPoints(creasePattern, ray, pointEps)
						.stream())
				.filter(point -> !GeomUtil.areEqual(point, v, pointEps))
				.toList();
	}
}
