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

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OriHalfedge;
import oripa.domain.fold.origeom.OriGeomUtil;

/**
 * @author OUCHI Koji
 *
 */
public class FaceIndicesOnHalfEdgeFactory {
	private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public Map<OriHalfedge, Set<Integer>> create(
			final List<OriFace> faces,
			final double eps) {

		Map<OriHalfedge, Set<Integer>> indices = new HashMap<>();

		var halfedges = new ArrayList<OriHalfedge>();

		for (var face : faces) {
			for (var halfedge : face.halfedgeIterable()) {
				halfedges.add(halfedge);
				Set<Integer> indexSet = new HashSet<Integer>();
				indices.put(halfedge, indexSet);
			}
		}

		halfedges.parallelStream().forEach(halfedge -> {
			for (var face : faces) {
				var indexSet = indices.get(halfedge);
				if (OriGeomUtil.isHalfedgeCrossFace(face, halfedge, eps)) {
					indexSet.add(face.getFaceID());
				}
			}
		});

		int count = 0;
		for (var indexSet : indices.values()) {
			count += indexSet.size();
		}

		logger.debug("#faceOnHalfEdge = {}", count);

		return indices;
	}

}
