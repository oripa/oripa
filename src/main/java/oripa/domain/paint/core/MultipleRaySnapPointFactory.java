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
import java.util.stream.Collectors;

import javax.vecmath.Vector2d;

import oripa.domain.cptool.PseudoRayFactory;
import oripa.domain.paint.PaintContext;
import oripa.geom.GeomUtil;

/**
 * @author OUCHI Koji
 *
 */
public class MultipleRaySnapPointFactory {

	public Collection<Vector2d> createSnapPoints(
			final PaintContext context,
			final Vector2d v,
			final Collection<Double> angles) {

		var paperSize = context.getCreasePattern().getPaperSize();
		var snapPointFactory = new RaySnapPointFactory();
		var rayFactory = new PseudoRayFactory();

		return angles.stream()
				.map(angle -> rayFactory.create(v, angle, paperSize))
				.flatMap(ray -> snapPointFactory.createSnapPoints(context, ray).stream())
				.filter(point -> GeomUtil.distance(point, v) > GeomUtil.EPS)
				.collect(Collectors.toList());
	}
}
