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
package oripa.fold.rule;

import oripa.fold.OriFace;
import oripa.fold.OriHalfedge;
import oripa.geom.GeomUtil;

/**
 * @author Koji
 *
 */
public class FaceIsConvex extends AbstractRule<OriFace> {


	public boolean holds(OriFace face) {

		if (face.halfedges.size() == 3) {
			return true;
		}

		OriHalfedge baseHe = face.halfedges.get(0);
		boolean baseFlg = GeomUtil.CCWcheck(baseHe.prev.vertex.p, 
				baseHe.vertex.p, baseHe.next.vertex.p);

		for (int i = 1; i < face.halfedges.size(); i++) {
			OriHalfedge he = face.halfedges.get(i);
			if (GeomUtil.CCWcheck(he.prev.vertex.p, he.vertex.p, he.next.vertex.p) != baseFlg) {
				return false;
			}

		}
		
		return true;
	}
}
