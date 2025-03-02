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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.cptool.ElementRemover;
import oripa.domain.cptool.LineAdder;
import oripa.domain.cptool.PointsMerger;
import oripa.domain.creasepattern.CreasePattern;
import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OriHalfedge;
import oripa.geom.RectangleDomain;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class FacesToCreasePatternConverter {
	private static final Logger logger = LoggerFactory
			.getLogger(FacesToCreasePatternConverter.class);

	private final CreasePatternFactory cpFactory;
	private final LineAdder lineAdder;
	private final ElementRemover elementRemover;
	private final PointsMerger pointMerger;

	/**
	 * Constructor
	 */
	public FacesToCreasePatternConverter(final CreasePatternFactory cpFactory,
			final LineAdder lineAdder, final ElementRemover elementRemover, final PointsMerger pointsMerger) {
		this.cpFactory = cpFactory;
		this.lineAdder = lineAdder;
		this.elementRemover = elementRemover;
		this.pointMerger = pointsMerger;
	}

	/**
	 * construct edge structure after folding as a crease pattern for easy
	 * calculation to obtain subfaces.
	 *
	 * @param faces
	 *            faces after fold without layer ordering.
	 * @return
	 */
	public CreasePattern convertToCreasePattern(final List<OriFace> faces, final double paperSize,
			final double pointEps) {
		logger.debug("toCreasePattern(): construct edge structure after folding");

		var lines = cpFactory.createCreasePattern(
				RectangleDomain.createFromPoints(
						faces.stream()
								.flatMap(OriFace::halfedgeStream)
								.map(OriHalfedge::getPosition)
								.toList()));
//		Collection<OriLine> lines = new ArrayList<OriLine>();

		var filteredFaces = faces.stream()
				.map(face -> face.remove180degreeVertices(pointEps))
				.map(face -> face.removeDuplicatedVertices(pointEps))
				.filter(face -> face.halfedgeCount() >= 3)
				.toList();

		for (OriFace face : filteredFaces) {
			var faceLines = face.halfedgeStream()
					.map(he -> new OriLine(he.getPosition(), he.getNext().getPosition(),
							OriLine.Type.MOUNTAIN))
					.toList();
			// make cross every time to divide the faces.
			lineAdder.addAll(faceLines, lines, pointEps);
		}

		lines = pointMerger.mergeClosePoints(lines, pointEps);

//		try {
//			var creasePattern = cpFactory.createCreasePattern(lines);
//			creasePattern.forEach(line -> logger.debug("{}", line));
//			new ExporterCP().export(Doc.forSaving(creasePattern, null), "debug.cp", null);
//		} catch (IllegalArgumentException | IOException e) {
//		}

		elementRemover.removeMeaninglessVertices(lines, pointEps);

		CreasePattern creasePattern = cpFactory.createCreasePattern(lines);

		logger.debug("toCreasePattern(): {} segments", creasePattern.size());

		return creasePattern;
	}

}
