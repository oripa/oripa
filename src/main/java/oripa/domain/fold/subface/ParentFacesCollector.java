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
package oripa.domain.fold.subface;

import java.util.List;
import java.util.stream.Collectors;

import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.origeom.OriGeomUtil;

/**
 * @author OUCHI Koji
 *
 */
public class ParentFacesCollector {
	public List<OriFace> collect(final List<OriFace> faces, final SubFace sub,
			final double paperSize) {
		var innerPoint = sub.getInnerPoint();

		return faces.stream()
				.filter(face -> OriGeomUtil.isOnFoldedFace(face, innerPoint, paperSize / 1000))
				.collect(Collectors.toList());
	}
}
