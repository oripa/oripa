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
package oripa.domain.fold;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.vecmath.Vector2d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.cptool.LineAdder;
import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class SubFacesFactory {
	private static final Logger logger = LoggerFactory.getLogger(SubFacesFactory.class);

	private final CreasePatternFactory cpFactory = new CreasePatternFactory();
	private final OrigamiModelFactory modelFactory = new OrigamiModelFactory();
	private final LineAdder lineAdder = new LineAdder();

	/**
	 *
	 * @param faces
	 *            extracted from the drawn crease pattern. This method assumes
	 *            that the faces hold the coordinates after folding.
	 *
	 * @param paperSize
	 * @return subfaces.
	 */
	public List<SubFace> createSubFaces(
			final List<OriFace> faces, final double paperSize) {
		logger.debug("createSubFaces() start");

		var creasePattern = createFoldedEdgesAsCreasePattern(faces, paperSize);

		// By this construction, we get faces that are composed of the edges
		// after folding where the edges are split at cross points in the crease
		// pattern. (layering is not considered)
		// We call such face a subface hereafter.
		var foldedEdgesOrigamiModel = modelFactory.buildOrigami(creasePattern, paperSize);

		var subFaces = new ArrayList<SubFace>(
				foldedEdgesOrigamiModel.getFaces().stream()
						.map(face -> new SubFace(face))
						.collect(Collectors.toList()));

		// Stores the face reference of given crease pattern into the subface
		// that is contained in the face.
		for (SubFace sub : subFaces) {
			Vector2d innerPoint = sub.getInnerPoint();

			sub.faces.addAll(faces.stream()
					.filter(face -> OriGeomUtil.isOnFoldedFace(face, innerPoint, paperSize / 1000))
					.collect(Collectors.toList()));
		}

		// extract distinct subfaces by comparing face list's items.
		ArrayList<SubFace> distinctSubFaces = new ArrayList<>();
		for (SubFace sub : subFaces) {
			if (distinctSubFaces.stream()
					.noneMatch(s -> isSame(sub, s))) {
				distinctSubFaces.add(sub);
			}
		}

		logger.debug("createSubFaces() end");

		return distinctSubFaces;
	}

	/**
	 * construct edge structure after folding as a crease pattern for easy
	 * calculation.
	 *
	 * @param faces
	 * @param paperSize
	 * @return
	 */
	private CreasePatternInterface createFoldedEdgesAsCreasePattern(final List<OriFace> faces,
			final double paperSize) {
		CreasePatternInterface creasePattern = cpFactory.createCreasePattern(paperSize);
		logger.debug("createSubFaces(): construct edge structure after folding");
		creasePattern.clear();
		for (OriFace face : faces) {
			for (OriHalfedge he : face.halfedges) {
				OriLine line = new OriLine(he.positionAfterFolded, he.next.positionAfterFolded,
						OriLine.Type.MOUNTAIN);
				// make cross every time to divide the faces.
				// addLines() cannot make cross among given lines.
				lineAdder.addLine(line, creasePattern);
			}
		}
		creasePattern.cleanDuplicatedLines();

		return creasePattern;
	}

	private boolean isSame(final SubFace sub0, final SubFace sub1) {
		if (sub0.faces.size() != sub1.faces.size()) {
			return false;
		}

		return sub0.faces.stream()
				.allMatch(face -> sub1.faces.contains(face));
	}
}
