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
package oripa.renderer.estimation;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import oripa.domain.fold.origeom.OriGeomUtil;
import oripa.domain.fold.origeom.OverlapRelation;
import oripa.util.IntPair;
import oripa.util.collection.CollectionUtil;

/**
 * @author OUCHI Koji
 *
 */
class OverlapRelationInterpolater {
    public OverlapRelation interpolate(final OverlapRelation overlapRelation, final List<Face> faces,
            final double eps) {
        var interpolatedOverlapRelation = overlapRelation.clone();
        var changed = false;

        Set<IntPair> newOverlaps = CollectionUtil.newConcurrentHashSet();

        for (var face : faces) {
            face.getConvertedFace().buildTriangles(eps);
        }

        // preparation
        IntStream.range(0, faces.size())
                .parallel()
                .forEach(i -> IntStream.range(0, faces.size())
                        .parallel()
                        .forEach(j -> {
                            var face_i = faces.get(i);
                            var index_i = face_i.getFaceID();

                            var face_j = faces.get(j);
                            var index_j = face_j.getFaceID();

                            // converted faces (= distorted faces) can overlap
                            // even if original faces don't overlap.
                            if (overlapRelation.isNoOverlap(index_i, index_j)) {
                                if (OriGeomUtil.isFaceOverlap(
                                        face_i.getConvertedFace(), face_j.getConvertedFace(), eps)) {
                                    newOverlaps.add(new IntPair(i, j));
                                }
                            }
                        }));

        do {
            // update overlap relation
            changed = newOverlaps.stream()
                    .anyMatch(pair -> interpolate(interpolatedOverlapRelation, faces,
                            pair.v1(), pair.v2(), eps));
        } while (changed);

        return interpolatedOverlapRelation;
    }

    private boolean interpolate(final OverlapRelation interpolatedOverlapRelation, final List<Face> faces,
            final int i, final int j, final double eps) {
        var face_i = faces.get(i);
        var index_i = face_i.getFaceID();

        var face_j = faces.get(j);
        var index_j = face_j.getFaceID();

        if (i == j) {
            return false;
        }

        if (!interpolatedOverlapRelation.isNoOverlap(index_i, index_j)) {
            return false;
        }

        for (var midFace : faces) {
            var index_mid = midFace.getFaceID();

            if (index_i == index_mid || index_j == index_mid) {
                continue;
            }

            if (interpolatedOverlapRelation.isUpper(index_i, index_mid)
                    && interpolatedOverlapRelation.isUpper(index_mid, index_j)) {

                interpolatedOverlapRelation.setUpper(index_i, index_j);
                return true;
            }
        }
        return false;
    }
}
