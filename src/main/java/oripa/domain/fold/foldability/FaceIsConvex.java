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
package oripa.domain.fold.foldability;

import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OriHalfedge;
import oripa.geom.GeomUtil;
import oripa.util.rule.AbstractRule;

/**
 * @author Koji
 *
 */
public class FaceIsConvex extends AbstractRule<OriFace> {

	public FaceIsConvex() {
		super("convex");
	}

	@Override
	public boolean holds(final OriFace face) {

		if (face.halfedgeCount() == 3) {
			return true;
		}

		OriHalfedge baseHe = face.getHalfedge(0);
		boolean baseFlg = GeomUtil.isCCW(baseHe.getPrevious().getPosition(),
				baseHe.getPosition(), baseHe.getNext().getPosition());

		for (int i = 1; i < face.halfedgeCount(); i++) {
			OriHalfedge he = face.getHalfedge(i);
			if (GeomUtil.isCCW(he.getPrevious().getPosition(), he.getPosition(),
					he.getNext().getPosition()) != baseFlg) {
				return false;
			}

		}

		return true;
	}
}
