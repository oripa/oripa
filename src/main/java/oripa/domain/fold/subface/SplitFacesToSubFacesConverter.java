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

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OriVertex;
import oripa.domain.fold.halfedge.OrigamiModelFactory;

/**
 * @author OUCHI Koji
 *
 */
public class SplitFacesToSubFacesConverter {
	private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/**
	 *
	 * @param splitFaces
	 *            result of
	 *            {@link OrigamiModelFactory#buildOrigamiForSubfaces(java.util.Collection, double, double)}
	 *            where the parameter collection contains edges after fold.
	 * @return
	 */
	public List<SubFace> convertToSubFaces(final Collection<OriFace> splitFaces, final Collection<OriVertex> vertices,
			final double eps) {
		Collection<OriFace> faces = new ArrayList<>(
				splitFaces.stream()
						.map(face -> face.remove180degreeVertices())
						.map(face -> face.removeDuplicatedVertices(eps))
						.filter(face -> face.halfedgeCount() >= 3)
						.toList());

		removeOuterFace(faces, vertices, eps);

		return new ArrayList<SubFace>(
				faces.stream()
						.map(face -> new SubFace(face, eps))
						.toList());
	}

	private void removeOuterFace(final Collection<OriFace> faces, final Collection<OriVertex> vertices,
			final double eps) {

		logger.debug("try to remove outer face");
		logger.debug("before: {} faces", faces.size());

		var outerFaceOpt = faces.stream()
				.filter(face -> vertices.stream()
						.allMatch(v -> face.includesInclusively(v.getPosition(), eps * 5)))
				.findFirst();
		if (outerFaceOpt.isPresent()) {
			var outerFace = outerFaceOpt.get();
			logger.debug("remove {}", outerFace);
			faces.removeIf(face -> face == outerFace);
		}

		logger.debug("after: {} faces", faces.size());
	}

}
