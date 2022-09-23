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
package oripa.domain.paint.kawasaki;

import javax.vecmath.Vector2d;

import oripa.domain.creasepattern.CreasePattern;
import oripa.domain.fold.halfedge.OriVertex;
import oripa.domain.fold.halfedge.OrigamiModelFactory;
import oripa.geom.GeomUtil;
import oripa.gui.presenter.creasepattern.geometry.NearestPoint;

/**
 * @author OUCHI Koji
 *
 */
public class TargetOriVertexFactory {

	public OriVertex create(final CreasePattern creasePattern, final Vector2d target) {
		var origamiModelFactory = new OrigamiModelFactory();
		var origamiModel = origamiModelFactory.createOrigamiModel(creasePattern);

		var nearest = new NearestPoint();
		var vertex = origamiModel.getVertices().get(0);

		for (var v : origamiModel.getVertices()) {
			var p = v.getPositionBeforeFolding();
			var distance = GeomUtil.distance(p, target);
			if (distance < nearest.distance) {
				nearest.point = p;
				nearest.distance = distance;
				vertex = v;
			}
		}

		return vertex;
	}

}
