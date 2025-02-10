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
package oripa.domain.fold.subface.test;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OriHalfedge;
import oripa.domain.fold.halfedge.OriVertex;
import oripa.geom.Polygon;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
public class OriFaceFactoryForTest {
	/**
	 *
	 * @param left
	 *            smaller x coordinate of the square
	 * @param top
	 *            smaller y coordinate of the square
	 * @return
	 */
	public static OriFace create10PxSquareMock(final double left, final double top) {
		return createRectangleMock(left, top, left + 10, top + 10);
	}

	/**
	 *
	 * @param left
	 *            smaller x coordinate of the square
	 * @param top
	 *            smaller y coordinate of the square
	 * @param right
	 *            larger x coordinate of the square
	 * @param bottom
	 *            larger y coordinate of the square
	 * @return
	 */
	public static OriFace createRectangleMock(final double left, final double top,
			final double right, final double bottom) {
		var face = mock(OriFace.class);
		var he1 = createHalfEdgeSpy(left, bottom, face);
		var he2 = createHalfEdgeSpy(right, bottom, face);
		var he3 = createHalfEdgeSpy(right, top, face);
		var he4 = createHalfEdgeSpy(left, top, face);
		lenient().when(he1.getNext()).thenReturn(he2);
		lenient().when(he2.getNext()).thenReturn(he3);
		lenient().when(he3.getNext()).thenReturn(he4);
		lenient().when(he4.getNext()).thenReturn(he1);

		lenient().when(face.halfedgeCount()).thenReturn(4);

		var list = List.of(he1, he2, he3, he4);
		lenient().when(face.halfedgeIterable()).thenReturn(list);
		lenient().when(face.halfedgeStream()).thenAnswer(invocation -> list.stream());

		lenient().when(face.toPolygon(anyDouble())).thenReturn(
				new Polygon(List.of(new Vector2d(left, top), new Vector2d(right, top), new Vector2d(right, bottom),
						new Vector2d(left, bottom))));

		return face;
	}

	private static OriHalfedge createHalfEdgeSpy(final double x, final double y, final OriFace face) {
		var position = new Vector2d(x, y);
		var halfEdge = spy(new OriHalfedge(new OriVertex(position), face));

		lenient().when(halfEdge.getPosition()).thenReturn(position);
		lenient().when(halfEdge.getPositionWhileFolding()).thenReturn(position);

		return halfEdge;
	}

}
