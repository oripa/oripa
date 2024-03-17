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

import oripa.domain.fold.origeom.OriGeomUtil;
import oripa.domain.fold.origeom.OverlapRelation;

/**
 * @author OUCHI Koji
 *
 */
class OverlapRelationInterpolater {
	public OverlapRelation interpolate(final OverlapRelation overlapRelation, final List<Face> faces,
			final double eps) {
		var interpolatedOverlapRelation = overlapRelation.clone();
		boolean changed = false;

		do {
			changed = false;
			for (int i = 0; i < faces.size(); i++) {
				for (int j = 0; j < faces.size(); j++) {
					changed |= interpolate(interpolatedOverlapRelation, faces, i, j, eps);
				}
			}
		} while (changed);

		return interpolatedOverlapRelation;
	}

	private boolean interpolate(final OverlapRelation interpolatedOverlapRelation, final List<Face> faces, final int i,
			final int j, final double eps) {
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

		if (OriGeomUtil.isFaceOverlap(face_i.getConvertedFace(), face_j.getConvertedFace(), eps)) {

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
		}
		return false;
	}
}
