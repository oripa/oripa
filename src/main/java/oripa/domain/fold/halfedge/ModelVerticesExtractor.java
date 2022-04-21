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

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import oripa.domain.fold.origeom.OriGeomUtil;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class ModelVerticesExtractor {

	public static class Result {
		private final List<OriVertex> vertices;
		private final List<OriLine> precreases;

		public Result(final List<OriVertex> vertices, final List<OriLine> precreases) {
			this.vertices = vertices;
			this.precreases = precreases;
		}

		public List<OriVertex> getVertices() {
			return vertices;
		}

		public List<OriLine> getPrecreases() {
			return precreases;
		}
	}

	public Result extractByBoundary(final List<OriVertex> wholeVertices,
			final Collection<OriLine> wholePrecreases,
			final OriFace boundaryFace) {

		var vertices = wholeVertices.stream()
				.filter(vertex -> boundaryFace.isOnFaceInclusively(vertex.getPosition()))
				.collect(Collectors.toList());

		var precreases = wholePrecreases.stream()
				.filter(p -> OriGeomUtil.isOriLineIncludedInFace(boundaryFace, p))
				.collect(Collectors.toList());

		return new Result(vertices, precreases);

	}
}
