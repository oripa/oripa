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
package oripa.domain.suggestion;

import oripa.domain.fold.halfedge.OriVertex;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class MaekawaTheoremSuggester {
	/**
	 *
	 * @param vertex
	 * @return type to be used for new line. null if {@code vertex} is not at
	 *         inside of paper.
	 */
	public OriLine.Type suggest(final OriVertex vertex) {
		double edgeCount = vertex.edgeCount();

		if (edgeCount % 2 == 0) {
			return null;
		}

		if (vertex.edgeStream().anyMatch(edge -> edge.isBoundary())) {
			return null;
		}

		int mountainCount = 0;
		int valleyCount = 0;

		for (int i = 0; i < edgeCount; i++) {
			if (vertex.getEdge(i).isMountain()) {
				mountainCount++;
			} else {
				valleyCount++;
			}
		}
		int diff = mountainCount - valleyCount;
		switch (diff) {
		case 3:
			return OriLine.Type.VALLEY;
		case -3:
			return OriLine.Type.MOUNTAIN;
		case 1:
			return OriLine.Type.MOUNTAIN;
		case -1:
			return OriLine.Type.VALLEY;
		default:
			return null;
		}
	}

}
