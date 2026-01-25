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

import java.util.List;

import oripa.domain.creasepattern.CreasePattern;
import oripa.domain.fold.halfedge.OriEdge;
import oripa.domain.fold.halfedge.OriVertex;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
public class TargetOriVertexFactory {

	public OriVertex create(final CreasePattern creasePattern, final Vector2d target, final double pointEps) {
		var lines = findSelectedVertexLines(creasePattern, target, pointEps);
		return toOriVertex(target, lines, pointEps);
	}

	private List<OriLine> findSelectedVertexLines(final CreasePattern creasePattern, final Vector2d target,
			final double pointEps) {
		return creasePattern.stream()
				.filter(line -> line.pointStream()
						.anyMatch(v -> v.equals(target, pointEps)))
				.filter(line -> line.length() > pointEps)
				.toList();
	}

	private OriVertex toOriVertex(final Vector2d target, final List<OriLine> lines, final double pointEps) {
		var startVertex = new OriVertex(target);

		for (var line : lines) {
			var endVertex = new OriVertex(
					line.pointStream().filter(p -> !p.equals(target, pointEps)).findFirst().get());
			var edge = new OriEdge(startVertex, endVertex, line.getType().toInt());

			startVertex.addEdge(edge);
		}

		return startVertex;
	}
}
