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

package oripa.domain.fold.halfedge;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

public class OriHalfedge {

	public OriHalfedge next = null;
	public OriHalfedge prev = null;
	public OriHalfedge pair = null;
	public OriEdge edge = null;
	public OriVertex vertex = null;
	public OriFace face = null;

	/**
	 * mountain/valley value
	 */
	public int type = 0;

	/**
	 * temporary position while folding
	 */
	public Vector2d tmpVec = new Vector2d();

	public Vector2d positionForDisplay = new Vector2d();
	public Vector2d positionAfterFolded = new Vector2d();
	public Vector3d vertexColor = new Vector3d();

	public OriHalfedge(final OriVertex v, final OriFace f) {
		vertex = v;
		face = f;
		tmpVec.set(v.p);
	}
}
