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
package oripa.domain.fold.halfedge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.geom.GeomUtil;
import oripa.value.OriLine;
import oripa.value.OriPoint;

/**
 * @author OUCHI Koji
 *
 */
public class OriVerticesFactory {
	private static final Logger logger = LoggerFactory.getLogger(OriVerticesFactory.class);

	public List<OriVertex> createOriVertices(final Collection<OriLine> creasePatternWithoutAux, final double pointEps) {
		var vertices = new ArrayList<OriVertex>();
		var verticesMap = new TreeMap<OriPoint, OriVertex>();

		int edgeCount = 0;

		for (OriLine l : creasePatternWithoutAux) {
			OriVertex sv = addAndGetVertexFromVVec(verticesMap, l.getOriPoint0(), pointEps);
			OriVertex ev = addAndGetVertexFromVVec(verticesMap, l.getOriPoint1(), pointEps);
			OriEdge eg = new OriEdge(sv, ev, l.getType().toInt());
			edgeCount++;
			sv.addEdge(eg);
			ev.addEdge(eg);
		}
		vertices.addAll(verticesMap.values());

		logger.debug("#vertex = " + vertices.size());
		logger.debug("#edge = " + edgeCount);

		return vertices;
	}

	private OriVertex addAndGetVertexFromVVec(
			final TreeMap<OriPoint, OriVertex> verticesMap, final OriPoint p, final double pointEps) {
		var boundMap = verticesMap
				.headMap(new OriPoint(p.getX() + pointEps, p.getY() + pointEps), true)
				.tailMap(new OriPoint(p.getX() - pointEps, p.getY() - pointEps));

		var neighbors = boundMap.keySet().stream()
				.filter(point -> GeomUtil.areEqual(point, p, pointEps))
				.toList();

		if (neighbors.isEmpty()) {
			var vtx = new OriVertex(p);
			verticesMap.put(p, vtx);
			return vtx;
		}

		return boundMap.get(neighbors.get(0));
	}

}
