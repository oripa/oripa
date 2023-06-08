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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.origeom.OverlapRelation;

/**
 * @author OUCHI Koji
 *
 */
public class OverlappingFaceIndexIntersectionFactory {

	@SuppressWarnings("unchecked")
	public List<Integer>[][] create(
			final List<OriFace> faces,
			final OverlapRelation overlapRelation) {
		List<Set<Integer>> indices = IntStream.range(0, faces.size())
				.mapToObj(i -> new HashSet<Integer>())
				.collect(Collectors.toList());

		// prepare pair indices of overlapping faces.
		for (var face : faces) {
			for (var other : faces) {
				var index_i = face.getFaceID();
				var index_j = other.getFaceID();
				if (!overlapRelation.isNoOverlap(index_i, index_j)) {
					indices.get(index_i).add(index_j);
				}
			}
		}

		// extract overlapping-face indices shared by face pair.
		var indexIntersections = new List[faces.size()][faces.size()];
		for (var face : faces) {
			for (var other : faces) {
				var index_i = face.getFaceID();
				var index_j = other.getFaceID();

				if (index_i == index_j) {
					continue;
				}

				var overlappingFaces_i = indices.get(index_i);
				var overlappingFaces_j = indices.get(index_j);

				indexIntersections[index_i][index_j] = overlappingFaces_i.stream()
						.filter(index -> overlappingFaces_j.contains(index))
						.collect(Collectors.toList());
			}
		}

		return indexIntersections;
	}

}
