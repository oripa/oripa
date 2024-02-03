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

import java.util.ArrayList;
import java.util.List;

import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OrigamiModelFactory;

/**
 * @author OUCHI Koji
 *
 */
public class SplitFacesToSubFacesConverter {
	/**
	 *
	 * @param splitFaces
	 *            result of
	 *            {@link OrigamiModelFactory#buildOrigamiForSubfaces(java.util.Collection, double, double)}
	 *            where the parameter collection contains edges after fold.
	 * @return
	 */
	public List<SubFace> convertToSubFaces(final List<OriFace> splitFaces) {
		return new ArrayList<SubFace>(
				splitFaces.stream()
						.map(face -> new SubFace(face))
						.toList());
	}
}
