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

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author OUCHI Koji
 *
 */
@ExtendWith(MockitoExtension.class)
class SplitFacesToSubFacesConverterTest {
	@InjectMocks
	private SplitFacesToSubFacesConverter converter;

	/**
	 * Test method for
	 * {@link oripa.domain.fold.subface.SplitFacesToSubFacesConverter#toSubFaces(java.util.List)}.
	 */
	@Test
	void testToSubFaces() {
		var splitFace1 = OriFaceFactoryForTest.create10PxSquareFace(0, 0);
		var splitFace2 = OriFaceFactoryForTest.create10PxSquareFace(10, 0);

		var splitFaces = List.of(splitFace1, splitFace2);

		var subFaces = converter.toSubFaces(splitFaces);

		for (int i = 0; i < subFaces.size(); i++) {
			assertSame(splitFaces.get(i), subFaces.get(i).outline);
		}
	}

}
