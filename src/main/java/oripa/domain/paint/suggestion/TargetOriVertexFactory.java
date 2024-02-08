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
package oripa.domain.paint.suggestion;

import oripa.domain.creasepattern.CreasePattern;
import oripa.domain.fold.halfedge.OriVertex;
import oripa.domain.fold.halfedge.OrigamiModelFactory;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
public class TargetOriVertexFactory {

	private class NearestVertex {
		public OriVertex vertex;
		public double distance = Double.MAX_VALUE;
	}

	public OriVertex create(final CreasePattern creasePattern, final Vector2d target, final double pointEps) {
		var origamiModelFactory = new OrigamiModelFactory();
		var origamiModel = origamiModelFactory.createOrigamiModel(
				creasePattern,
				pointEps);

		var nearest = new NearestVertex();
		nearest.vertex = origamiModel.getVertices().get(0);

		for (var v : origamiModel.getVertices()) {
			var p = v.getPositionBeforeFolding();
			var distance = p.distance(target);
			if (distance < nearest.distance) {
				nearest.vertex = v;
				nearest.distance = distance;
			}
		}

		return nearest.vertex;
	}

}
