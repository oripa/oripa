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

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
@ExtendWith(MockitoExtension.class)
class OriVertexTest {
	final static double EPS = 1e-8;

	@Test
	void testAddEdge() {
		var vertex = new OriVertex(1, 1);

		var v1 = new OriVertex(2, 1); // 0 deg.
		var e1 = new OriEdge(vertex, v1, OriLine.Type.MOUNTAIN.toInt());

		var v2 = new OriVertex(2, 2); // 45 deg.
		var e2 = new OriEdge(vertex, v2, OriLine.Type.MOUNTAIN.toInt());

		var v3 = new OriVertex(0, 1 - 1e-8); // -180 deg.
		var e3 = new OriEdge(vertex, v3, OriLine.Type.MOUNTAIN.toInt());

		Stream.of(e1, e2, e3).forEach(vertex::addEdge);

		assertSame(e1, vertex.getEdge(0));
		assertSame(e2, vertex.getEdge(1));
		assertSame(e3, vertex.getEdge(2));

		var v4 = new OriVertex(1, 0); // -90 deg.
		// reversed direction
		var e4 = new OriEdge(v4, vertex, OriLine.Type.MOUNTAIN.toInt());

		vertex.addEdge(e4);

		assertSame(e1, vertex.getEdge(0));
		assertSame(e2, vertex.getEdge(1));
		assertSame(e3, vertex.getEdge(2));
		assertSame(e4, vertex.getEdge(3));
	}

	@Test
	void testGetAngleDifference_lessThanOrEqualToPi() {
		var vertex = new OriVertex(1, 1);

		var v1 = new OriVertex(2, 1); // 0 deg.
		var e1 = new OriEdge(vertex, v1, OriLine.Type.MOUNTAIN.toInt());

		var v2 = new OriVertex(2, 2); // 45 deg.
		var e2 = new OriEdge(vertex, v2, OriLine.Type.MOUNTAIN.toInt());

		var v3 = new OriVertex(0, 1 - EPS * 0.1); // -180 deg.
		var e3 = new OriEdge(vertex, v3, OriLine.Type.MOUNTAIN.toInt());

		Stream.of(e1, e2, e3).forEach(vertex::addEdge);

		assertEquals(Math.PI / 4, vertex.getAngleDifference(0), EPS);
		assertEquals(3 * Math.PI / 4, vertex.getAngleDifference(1), EPS);
		assertEquals(Math.PI, vertex.getAngleDifference(2), EPS);
	}

	@Test
	void testGetAngleDifference_largerThanPi() {
		var vertex = new OriVertex(1, 1);

		var v1 = new OriVertex(2, 1); // 0 deg.
		var e1 = new OriEdge(vertex, v1, OriLine.Type.MOUNTAIN.toInt());

		var v2 = new OriVertex(2, 2); // 45 deg.
		var e2 = new OriEdge(vertex, v2, OriLine.Type.MOUNTAIN.toInt());

		var v3 = new OriVertex(0, 2); // 135 deg.
		var e3 = new OriEdge(vertex, v3, OriLine.Type.MOUNTAIN.toInt());

		Stream.of(e1, e2, e3).forEach(vertex::addEdge);

		assertEquals(Math.PI / 4, vertex.getAngleDifference(0), EPS);
		assertEquals(Math.PI / 2, vertex.getAngleDifference(1), EPS);
		assertEquals(5 * Math.PI / 4, vertex.getAngleDifference(2), EPS);
	}

}
