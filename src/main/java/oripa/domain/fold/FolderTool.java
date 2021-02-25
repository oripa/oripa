/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2005-2009 Jun Mitani http://mitani.cs.tsukuba.ac.jp/

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

package oripa.domain.fold;

import java.util.List;
import java.util.stream.Collectors;

import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OriHalfedge;
import oripa.geom.RectangleDomain;

public class FolderTool {
	public RectangleDomain createDomainOfFoldedModel(final List<OriFace> faces) {
		var domain = new RectangleDomain();
		domain.enlarge(faces.stream()
				.flatMap(face -> face.halfedges.stream().map(he -> he.getPosition()))
				.collect(Collectors.toList()));

		return domain;
	}

	public void setFacesOutline(final List<OriFace> faces) {

		for (OriFace f : faces) {
			for (OriHalfedge he : f.halfedges) {
				he.getPositionForDisplay().set(he.getPosition());
			}
			f.buildOutline();
		}

		// not used.
//		if (isSlide) {
//			int minDepth = Integer.MAX_VALUE;
//			int maxDepth = -Integer.MAX_VALUE;
//			for (var f : faces) {
//				minDepth = Math.min(minDepth, f.z_order);
//				maxDepth = Math.max(minDepth, f.z_order);
//			}
//
//			double slideUnit = 10.0 / (maxDepth - minDepth);
//			for (OriVertex v : vertices) {
//				v.tmpFlg = false;
//				v.tmpVec.set(v.p);
//			}
//
//			for (OriFace f : faces) {
//				Vector2d faceCenter = f.getCentroid();
//				for (OriHalfedge he : f.halfedges) {
//					var vertex = he.getVertex();
//					if (vertex.tmpFlg) {
//						continue;
//					}
//					vertex.tmpFlg = true;
//
//					vertex.tmpVec.x += slideUnit * f.z_order;
//					vertex.tmpVec.y += slideUnit * f.z_order;
//
//					Vector2d dirToCenter = new Vector2d(faceCenter);
//					dirToCenter.sub(vertex.tmpVec);
//					dirToCenter.normalize();
//					dirToCenter.scale(6.0);
//					vertex.tmpVec.add(dirToCenter);
//				}
//			}
//
//			for (OriFace f : faces) {
//				for (OriHalfedge he : f.halfedges) {
//					he.positionForDisplay.set(he.getVertex().tmpVec);
//				}
//				f.buildOutline();
//			}
//		}
	}
}
