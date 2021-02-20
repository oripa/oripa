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

import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OriHalfedge;
import oripa.domain.fold.halfedge.OriVertex;

/**
 * @author OUCHI Koji
 *
 */
class OriFaceFactoryForTest {
	static OriFace create10PxSquareFace(final double left, final double bottom) {
		var face = new OriFace();
		var he1 = new OriHalfedge(new OriVertex(left + 0, bottom + 0), face);
		var he2 = new OriHalfedge(new OriVertex(left + 10, bottom + 0), face);
		var he3 = new OriHalfedge(new OriVertex(left + 10, bottom + 10), face);
		var he4 = new OriHalfedge(new OriVertex(left + 0, bottom + 10), face);
		he1.next = he2;
		he2.next = he3;
		he3.next = he4;
		he4.next = he1;
		face.halfedges.addAll(List.of(he1, he2, he3, he4));

		return face;
	}
}
