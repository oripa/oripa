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
package oripa.domain.fold.halfedge;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.vecmath.Vector2d;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author OUCHI Koji
 *
 */
@ExtendWith(MockitoExtension.class)
class OriFaceTest {
	OriFace face;

	@BeforeEach
	void setupFace() {
		face = new OriFace();
		var he1 = createHalfEdgeSpy(0, 0, face);
		var he2 = createHalfEdgeSpy(10, 0, face);
		var he3 = createHalfEdgeSpy(5, 10, face);

		he1.setNext(he2);
		he2.setNext(he3);
		he3.setNext(he1);

		face.halfedges.add(he1);
		face.halfedges.add(he2);
		face.halfedges.add(he3);
	}

	private OriHalfedge createHalfEdgeSpy(final double x, final double y, final OriFace face) {
		var position = new Vector2d(x, y);
		var he = spy(new OriHalfedge(new OriVertex(position), face));

		lenient().doReturn(position).when(he).getPosition();
		lenient().doReturn(position).when(he).getPosition();

		return he;
	}

	/**
	 * Test method for
	 * {@link oripa.domain.fold.halfedge.OriFace#isOnFaceInclusively(javax.vecmath.Vector2d)}.
	 */
	@Test
	void testIsOnFaceInclusively() {
		assertTrue(face.isOnFaceInclusively(new Vector2d(5, 5)));
		assertTrue(face.isOnFaceInclusively(new Vector2d(5, 10 + 1e-8)));
		assertFalse(face.isOnFaceInclusively(new Vector2d(5, 10.1)));
	}

	/**
	 * Test method for
	 * {@link oripa.domain.fold.halfedge.OriFace#isOnFaceExclusively(javax.vecmath.Vector2d, double)}.
	 */
	@Test
	void testIsOnFoldedFaceExclusively() {
		final double EPS = 1e-6;
		assertTrue(face.isOnFaceExclusively(new Vector2d(5, 5), EPS));
		assertTrue(face.isOnFaceExclusively(new Vector2d(5, 9.9999), EPS));
		assertFalse(face.isOnFaceExclusively(new Vector2d(5, 10 + 1e-8), EPS));
		assertFalse(face.isOnFaceExclusively(new Vector2d(5, 10), EPS));

		verify(face.halfedges.get(0), atLeastOnce()).getPosition();
		verify(face.halfedges.get(1), atLeastOnce()).getPosition();
		verify(face.halfedges.get(2), atLeastOnce()).getPosition();
	}

}
