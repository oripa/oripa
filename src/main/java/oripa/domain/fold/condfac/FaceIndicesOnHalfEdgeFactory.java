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
package oripa.domain.fold.condfac;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OriHalfedge;
import oripa.domain.fold.origeom.OriGeomUtil;

/**
 * @author OUCHI Koji
 *
 */
public class FaceIndicesOnHalfEdgeFactory {

	public Map<OriHalfedge, Set<Integer>> create(
			final List<OriFace> faces,
			final double eps) {

		Map<OriHalfedge, Set<Integer>> indices = new HashMap<>();

		for (var face : faces) {
			for (var halfedge : face.halfedgeIterable()) {
				Set<Integer> indexSet = new HashSet<Integer>();
				indices.put(halfedge, indexSet);
			}
		}
		for (var face : faces) {
			for (var halfedge : face.halfedgeIterable()) {
				var indexSet = indices.get(halfedge);
				for (var other : faces) {
					if (other == face) {
						continue;
					}
					if (OriGeomUtil.isHalfedgeCrossFace(other, halfedge, eps)) {
						indexSet.add(other.getFaceID());
					}
				}
			}
		}

		return indices;
	}

}
